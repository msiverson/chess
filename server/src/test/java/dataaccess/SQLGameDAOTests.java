package dataaccess;

import chess.ChessGame;
import dataaccess.DataAccessException;
import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SQLGameDAO;
import model.GameData;
import org.junit.jupiter.api.*;

import java.util.List;

public class SQLGameDAOTests {

    private SQLGameDAO gameDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        gameDAO = new SQLGameDAO();
        gameDAO.clear();
    }

    @Test
    void createGamePositive() throws DataAccessException {

        ChessGame game = new ChessGame();

        int id = gameDAO.createGame(
                new GameData(0, null, null, "Test Game", game)
        );

        GameData retrieved = gameDAO.getGame(id);

        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("Test Game", retrieved.gameName());
    }

    @Test
    void getGameNegative() throws DataAccessException {

        GameData game = gameDAO.getGame(999);

        Assertions.assertNull(game);
    }

    @Test
    void listGamesPositive() throws DataAccessException {

        ChessGame game = new ChessGame();

        gameDAO.createGame(new GameData(0, null, null, "Game1", game));
        gameDAO.createGame(new GameData(0, null, null, "Game2", game));

        List<GameData> games = gameDAO.listGames();

        Assertions.assertEquals(2, games.size());
    }
}