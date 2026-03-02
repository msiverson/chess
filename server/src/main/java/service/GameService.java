package service;

// Game
import dataaccess.GameDAO;
import model.GameData;
// Auth
import dataaccess.AuthDAO;
import model.AuthData;
// Exceptions
import dataaccess.DataAccessException;
// DTO
import dto.game.CreateGameRequest;
import dto.game.CreateGameResult;
import dto.game.JoinGameRequest;
import dto.game.ListGamesRequest;
import dto.game.ListGamesResult;

public class GameService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameService (AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public ListGamesResult listGames(ListGamesRequest listGamesRequest) {
        return null;
    }
    public CreateGameResult createGame(CreateGameRequest createGameRequest) {
        return null;
    }
    public void joinGame(JoinGameRequest joinGameRequest) {

    }
}
