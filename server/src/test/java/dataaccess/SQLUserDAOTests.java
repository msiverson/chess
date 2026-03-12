package dataaccess;

import dataaccess.DataAccessException;
import dataaccess.sql.DatabaseManager;
import dataaccess.sql.SQLUserDAO;
import model.UserData;
import org.junit.jupiter.api.*;

public class SQLUserDAOTests {

    private SQLUserDAO userDAO;

    @BeforeEach
    void setup() throws DataAccessException {
        DatabaseManager.configureDatabase();
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    void addUserPositive() throws DataAccessException {

        UserData user = new UserData("alice", "password", "email");

        userDAO.addUser(user);

        UserData retrieved = userDAO.getUser("alice");

        Assertions.assertNotNull(retrieved);
        Assertions.assertEquals("alice", retrieved.username());
    }

    @Test
    void addUserDuplicateNegative() throws DataAccessException {

        UserData user = new UserData("alice", "password", "email");

        userDAO.addUser(user);

        Assertions.assertThrows(DataAccessException.class, () -> {
            userDAO.addUser(user);
        });
    }
}
