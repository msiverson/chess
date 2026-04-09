package websocket.service;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.AuthData;
import model.GameData;
import http.service.exceptions.ServiceException;
import http.service.exceptions.UnauthorizedException;

public class GameplayService {
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameplayService(AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public AuthData getAuth(String authToken) {
        return requireAuth(authToken);
    }

    public GameData connect(String authToken, int gameID) {
        requireAuth(authToken);
        return requireGame(gameID);
    }

    public GameData makeMove(String authToken, int gameID, ChessMove move) {
        AuthData auth = requireAuth(authToken);
        GameData gameData = requireGame(gameID);

        validatePlayerCanMove(auth, gameData);

        ChessGame game = gameData.game();

        try {
            game.makeMove(move);
        } catch (InvalidMoveException e) {
            throw new IllegalArgumentException("invalid move");
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

        return updatedGame;
    }

    public void leave(String authToken, int gameID) {
        requireAuth(authToken);
        requireGame(gameID);
    }

    public void resign(String authToken, int gameID) {
        AuthData auth = requireAuth(authToken);
        GameData gameData = requireGame(gameID);
        validatePlayerExists(auth, gameData);
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
            throw new UnauthorizedException("Observers cannot make moves");
        }

        ChessGame.TeamColor playerColor =
                isWhite ? ChessGame.TeamColor.WHITE : ChessGame.TeamColor.BLACK;

        if (gameData.game().getTeamTurn() != playerColor) {
            throw new UnauthorizedException("Not your turn");
        }
    }

    private void validatePlayerExists(AuthData auth, GameData gameData) {
        String username = auth.username();

        boolean isWhite = username.equals(gameData.whiteUsername());
        boolean isBlack = username.equals(gameData.blackUsername());

        if (!isWhite && !isBlack) {
            throw new UnauthorizedException("Observers cannot resign");
        }
    }
}
