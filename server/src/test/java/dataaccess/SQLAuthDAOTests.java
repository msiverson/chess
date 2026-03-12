package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLUserDAO;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.*;

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
    void addAuthPositive() throws DataAccessException {

        userDAO.addUser(new UserData("alice", "password", "email"));

        AuthData auth = new AuthData("token123", "alice");

        authDAO.addAuth(auth);

        AuthData retrieved = authDAO.getAuth("token123");

        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("alice", retrieved.username());
    }

    @Test
    void getAuthNegative() throws DataAccessException {

        AuthData auth = authDAO.getAuth("badToken");

        Assertions.assertNull(auth);
    }
}
