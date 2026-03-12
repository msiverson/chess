package dataaccess;

import org.junit.jupiter.api.*;

import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;

public class SQLAuthDAOTests {

    private SQLAuthDAO authDAO;
    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        authDAO = new SQLAuthDAO();
        userDAO = new SQLUserDAO();
        authDAO.clear();
        userDAO.clear();
    }

    @Test
    @DisplayName("addAuth() Positive")
    void addAuthPositive() throws DataAccessException {
        userDAO.addUser(new UserData("validUsername", "validPassword", "validEmail"));
        AuthData auth = new AuthData("validToken", "validUsername");
        authDAO.addAuth(auth);
        AuthData retrieved = authDAO.getAuth("validToken");

        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("validUsername", retrieved.username());
    }

    @Test
    @DisplayName("addAuth() Negative [add auth to nonexistent user]")
    void addAuthNegative() {
        AuthData auth = new AuthData("validToken", "invalidUsername");

        Assertions.assertThrows(DataAccessException.class, () -> authDAO.addAuth(auth));
    }

    @Test
    @DisplayName("getAuth() Positive")
    void getAuthPositive() throws DataAccessException {
        userDAO.addUser(new UserData("validUsername", "validPassword", "validEmail"));
        authDAO.addAuth(new AuthData("validToken", "validUsername"));
        AuthData retrieved = authDAO.getAuth("validToken");

        Assertions.assertNotNull(retrieved);
    }

    @Test
    @DisplayName("getAuth() Negative [invalid token provided]")
    void getAuthNegative() throws DataAccessException {
        AuthData auth = authDAO.getAuth("invalidToken");

        Assertions.assertNull(auth);
    }

    @Test
    @DisplayName("deleteAuth() Positive")
    void deleteAuthPositive() throws DataAccessException {
        userDAO.addUser(new UserData("validUsername", "validPassword", "validEmail"));
        authDAO.addAuth(new AuthData("validToken", "validUsername"));
        authDAO.deleteAuth("validToken");

        Assertions.assertNull(authDAO.getAuth("validToken"));
    }

    @Test
    @DisplayName("deleteAuth() Negative [delete nonexistent token]")
    void deleteAuthNegative() throws DataAccessException {
        authDAO.deleteAuth("invalidToken");

        Assertions.assertNull(authDAO.getAuth("invalidToken"));
    }

    @Test
    @DisplayName("clear() Positive")
    void clearPositive() throws DataAccessException {
        userDAO.addUser(new UserData("validUsername", "validPassword", "validEmail"));
        authDAO.addAuth(new AuthData("validToken", "validUsername"));
        authDAO.clear();

        Assertions.assertNull(authDAO.getAuth("validToken"));
    }

    @Test
    @DisplayName("clear() Negative [getting auth after clear]")
    void clearNegative() throws DataAccessException {
        authDAO.clear();

        Assertions.assertNull(authDAO.getAuth("invalidAuth"));
    }
}
