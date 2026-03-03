package service;

import chess.ChessGame;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryUserDAO;

import model.AuthData;
import model.GameData;
import model.UserData;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.ServiceException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DBServiceTests {

    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;
    private MemoryGameDAO gameDAO;
    private DBService dbService;

    @BeforeEach
    public void setup() throws Exception {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        gameDAO = new MemoryGameDAO();

        dbService = new DBService(userDAO, authDAO, gameDAO);

        // Add sample data
        userDAO.addUser(new UserData("alice", "password", "email"));
        authDAO.addAuth(new AuthData("token", "alice"));
        gameDAO.createGame(new GameData(
            1, null, null, "testGame", new ChessGame()
        ));
    }

    @Test
    public void clearPositive() throws Exception {
        // Act
        dbService.clear();

        // Assert
        assertEquals(0, gameDAO.listGames().size());
    }

    @Test
    public void clearNegative() {
        // Arrange
        UserDAO failingUserDAO = new FailingUserDAO();

        DBService failingService =
                new DBService(failingUserDAO, authDAO, gameDAO);

        // Act & Assert
        assertThrows(ServiceException.class, failingService::clear);
    }
}

class FailingUserDAO implements UserDAO {

    @Override
    public UserData getUser(String username) throws DataAccessException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {}

    @Override
    public void clear() throws DataAccessException {
        throw new DataAccessException("DB Failure");
    }
}

