package dataaccess;

import org.junit.jupiter.api.*;

import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SQLUserDAO;
import model.UserData;


public class SQLUserDAOTests {

    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    @DisplayName("addUser() Positive")
    void addUserPositive() throws DataAccessException {

        UserData user = new UserData("validUsername", "validPassword", "validEmail");

        userDAO.addUser(user);

        UserData retrieved = userDAO.getUser("validUsername");

        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("validUsername", retrieved.username());
    }

    @Test
    @DisplayName("addUser() Negative [Duplicate Username]")
    void addUserDuplicateNegative() throws DataAccessException {

        UserData user = new UserData("validUsername", "validPassword", "validEmail");

        userDAO.addUser(user);

        Assertions.assertThrows(DataAccessException.class, () -> userDAO.addUser(user));
    }

    @Test
    @DisplayName("getUser() Positive")
    void getUserPositive() throws DataAccessException {

        UserData user = new UserData("validUsername", "validPassword", "validEmail");

        userDAO.addUser(user);

        UserData retrieved = userDAO.getUser("validUsername");

        Assertions.assertNotNull(retrieved);
    }

    @Test
    @DisplayName("getUser() Negative [retrieve nonexistent username]")
    void getUserNegative() throws DataAccessException {

        UserData retrieved = userDAO.getUser("invalidUsername");

        Assertions.assertNull(retrieved);
    }

    @Test
    @DisplayName("clear() Positive")
    void clearPositive() throws DataAccessException {

        userDAO.addUser(new UserData("validUsername", "validPassword", "validEmail"));

        userDAO.clear();

        Assertions.assertNull(userDAO.getUser("a"));
    }

    @Test
    @DisplayName("clear() Negative [getting username after clear]")
    void clearNegative() throws DataAccessException {

        userDAO.clear();

        Assertions.assertNull(userDAO.getUser("invalidUsername"));
    }
}
