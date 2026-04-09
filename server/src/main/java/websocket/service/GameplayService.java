package websocket.service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import http.service.exceptions.ServiceException;
import http.service.exceptions.UnauthorizedException;
import model.AuthData;
import model.GameData;

public class GameplayService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameplayService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public record MoveResult(GameData gameData, boolean checkmate, boolean stalemate) {
    }

    public AuthData getAuth(String authToken) {
        return requireAuth(authToken);
    }

    public GameData connect(String authToken, int gameID) {
        requireAuth(authToken);
        return requireGame(gameID);
    }

    public MoveResult makeMove(String authToken, int gameID, ChessMove move) {
        AuthData auth = requireAuth(authToken);
        GameData gameData = requireGame(gameID);

        ChessGame game = gameData.game();

        if (game.isGameOver()) {
            throw new IllegalArgumentException("game is over");
        }

        validatePlayerCanMove(auth, gameData);
        validatePlayerOwnsPiece(auth, gameData, move);

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new IllegalArgumentException("invalid move");
        }

        ChessGame.TeamColor nextTurn = game.getTeamTurn();
        boolean checkmate = game.isInCheckmate(nextTurn);
        boolean stalemate = game.isInStalemate(nextTurn);

        if (checkmate || stalemate) {
            game.setGameOver(true);
        }

        GameData updatedGame = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );

        try {
            gameDAO.updateGame(gameID, updatedGame);
        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }

        return new MoveResult(updatedGame, checkmate, stalemate);
    }

    public void leave(String authToken, int gameID) {
        AuthData auth = requireAuth(authToken);
        GameData gameData = requireGame(gameID);

        String username = auth.username();
        String whiteUsername = gameData.whiteUsername();
        String blackUsername = gameData.blackUsername();

        if (username.equals(whiteUsername)) {
            whiteUsername = null;
        } else if (username.equals(blackUsername)) {
            blackUsername = null;
        }

        GameData updatedGame = new GameData(
                gameData.gameID(),
                whiteUsername,
                blackUsername,
                gameData.gameName(),
                gameData.game()
        );

        try {
            gameDAO.updateGame(gameID, updatedGame);
        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }

    public void resign(String authToken, int gameID) {
        AuthData auth = requireAuth(authToken);
        GameData gameData = requireGame(gameID);

        validatePlayerExists(auth, gameData);

        ChessGame game = gameData.game();
        if (game.isGameOver()) {
            throw new IllegalArgumentException("game is over");
        }

        game.setGameOver(true);

        GameData updatedGame = new GameData(
                gameData.gameID(),
                gameData.whiteUsername(),
                gameData.blackUsername(),
                gameData.gameName(),
                game
        );

        try {
            gameDAO.updateGame(gameID, updatedGame);
        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }

    private AuthData requireAuth(String authToken) {
        if (authToken == null) {
            throw new UnauthorizedException("Missing auth token");
        }

        try {
            AuthData auth = authDAO.getAuth(authToken);
            if (auth == null) {
                throw new UnauthorizedException("Invalid auth token");
            }
            return auth;
        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }

    private GameData requireGame(int gameID) {
        try {
            GameData game = gameDAO.getGame(gameID);
            if (game == null) {
                throw new IllegalArgumentException("game not found");
            }
            return game;
        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }

    private void validatePlayerCanMove(AuthData auth, GameData gameData) {
        String username = auth.username();

        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if (!isWhite && !isBlack) {
            throw new UnauthorizedException("observers cannot make moves");
        }

        ChessGame.TeamColor playerColor =
                isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        if (gameData.game().getTeamTurn() != playerColor) {
            throw new UnauthorizedException("not your turn");
        }
    }

    private void validatePlayerOwnsPiece(AuthData auth, GameData gameData, ChessMove move) {
        var piece = gameData.game().getBoard().getPiece(move.getStartPosition());

        if (piece == null) {
            throw new IllegalArgumentException("invalid move");
        }

        String username = auth.username();
        ChessGame.TeamColor expectedColor =
                username.equals(gameData.whiteUsername())
                        ? ChessGame.TeamColor.WHITE
                        : ChessGame.TeamColor.BLACK;

        if (piece.getTeamColor() != expectedColor) {
            throw new UnauthorizedException("cannot move opponent piece");
        }
    }

    private void validatePlayerExists(AuthData auth, GameData gameData) {
        String username = auth.username();

        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if (!isWhite && !isBlack) {
            throw new UnauthorizedException("observers cannot resign");
        }
    }
}