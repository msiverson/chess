package dataaccess;

import org.junit.jupiter.api.*;

import chess.ChessGame;
import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SQLGameDAO;
import model.GameData;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    @DisplayName("createGame() Positive")
    void createGamePositive() throws DataAccessException {

        ChessGame game = new ChessGame();

        int id = gameDAO.createGame(
                new GameData(0, null, null, "Test Game", game)
        );

        Assertions.assertTrue(id > 0);
    }

    @Test
    @DisplayName("createGame() Negative [null game name]")
    void createGameNegative() {

        ChessGame game = new ChessGame();

        Assertions.assertThrows(DataAccessException.class, () ->
                gameDAO.createGame(new GameData(
                        0, null, null, null, game))
        );
    }

    @Test
    @DisplayName("getGame() Positive")
    void getGamePositive() throws DataAccessException {

        ChessGame game = new ChessGame();

        int id = gameDAO.createGame(
                new GameData(0, null, null, "Game", game));

        GameData retrieved = gameDAO.getGame(id);

        Assertions.assertNotNull(retrieved);
    }

    @Test
    @DisplayName("getGame() Negative [invalid gameID]")
    void getGameNegative() throws DataAccessException {

        GameData game = gameDAO.getGame(999);

        Assertions.assertNull(game);
    }

    @Test
    @DisplayName("listGames() Positive")
    void listGamesPositive() throws DataAccessException {

        ChessGame game = new ChessGame();

        gameDAO.createGame(new GameData(0,null,null,"Game1",game));
        gameDAO.createGame(new GameData(0,null,null,"Game2",game));

        Assertions.assertEquals(2, gameDAO.listGames().size());
    }

    @Test
    @DisplayName("listGames() Negative [no games in database]")
    void listGamesNegative() throws DataAccessException {

        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @DisplayName("clear() Positive")
    void clearPositive() throws DataAccessException {

        ChessGame game = new ChessGame();

        gameDAO.createGame(new GameData(0, null, null, "Game1", game));
        gameDAO.createGame(new GameData(0, null, null, "Game2", game));

        gameDAO.clear();

        Assertions.assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    @DisplayName("clear() Negative [zero games after a clear]")
    void clearNegative() throws DataAccessException {

        gameDAO.clear();

        Assertions.assertEquals(0, gameDAO.listGames().size());
    }
}