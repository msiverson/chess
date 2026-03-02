package service;

import java.util.List;

import chess.ChessGame;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.GameDAO;

import dto.game.CreateGameRequest;
import dto.game.CreateGameResult;
import dto.game.GameInfo;
import dto.game.JoinGameRequest;
import dto.game.ListGamesRequest;
import dto.game.ListGamesResult;

import model.AuthData;
import model.GameData;

import service.exceptions.AlreadyTakenException;
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;
    private Integer gameIDCount = 1;

    public GameService (AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        // IllegalArgumentException
        if (listGamesRequest.authToken() == null) {
            throw new IllegalArgumentException();
        }

        try {
            AuthData auth = authDAO.getAuth(listGamesRequest.authToken());
            if (auth == null) {
                throw new UnauthorizedException("Invalid auth token");
            }

            List<GameInfo> gameInfos = gameDAO.listGames()
                    .stream()
                    .map(g -> new GameInfo(
                            g.gameID(),
                            g.whiteUsername(),
                            g.blackUsername(),
                            g.gameName()
                    ))
                    .toList();

            return new ListGamesResult(gameInfos);

        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }

    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        if (createGameRequest.authToken() == null || createGameRequest.gameName() == null) {
            throw new IllegalArgumentException();
        }

        try {
            AuthData authData = authDAO.getAuth(createGameRequest.authToken());

            // Validate authToken
            if (authData == null) {
                throw new UnauthorizedException("Invalid auth token");
            }

            // Get username from authData
            int newGameID = gameIDCount++;
            ChessGame newGame = new ChessGame();

            gameDAO.createGame(
                new GameData(
                    newGameID,
                    null,
                    null,
                    createGameRequest.gameName(),
                    newGame
                )
            );

            return new CreateGameResult(newGameID);

        } catch (DataAccessException e) {
           throw new ServiceException("Server error");
        }

    }
    public void joinGame(JoinGameRequest joinGameRequest) {
        if (joinGameRequest.authToken() == null || joinGameRequest.playerColor() == null ||
            joinGameRequest.gameID() == null) {
            throw new IllegalArgumentException();
        }

        try {
            AuthData authData = authDAO.getAuth(joinGameRequest.authToken());

            // Validate authToken
            if (authData == null) {
                throw new UnauthorizedException("Invalid auth token");
            }

            GameData oldGame = gameDAO.getGame(joinGameRequest.gameID());
            GameData updatedGame;

            if (joinGameRequest.playerColor().equals("WHITE")) {
                if (oldGame.whiteUsername() != null) {
                    throw new AlreadyTakenException("White taken");
                }
                updatedGame = new GameData(
                    oldGame.gameID(),
                    authData.username(),
                    oldGame.blackUsername(),
                    oldGame.gameName(),
                    oldGame.game()
                );
            } else if (joinGameRequest.playerColor().equals("BLACK")) {
                if (oldGame.blackUsername() != null) {
                    throw new AlreadyTakenException("Black taken");
                }
                updatedGame = new GameData(
                    oldGame.gameID(),
                    oldGame.whiteUsername(),
                    authData.username(),
                    oldGame.gameName(),
                    oldGame.game()
                );
            } else {
                throw new IllegalArgumentException();
            }

            gameDAO.updateGame(joinGameRequest.gameID(), updatedGame);

        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }
}
