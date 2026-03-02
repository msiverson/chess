package service;

import dataaccess.memory.MemoryUserDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.UserData;
import dto.session.*;

import org.junit.jupiter.api.*;
import service.exceptions.UnauthorizedException;

import static org.junit.jupiter.api.Assertions.*;

public class SessionServiceTests {

    private SessionService service;
    private MemoryUserDAO userDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new SessionService(userDAO, authDAO);
    }

    // =========================
    // LOGIN TESTS
    // =========================

    @Test
    public void loginPositive() throws Exception {
        // Arrange
        UserData user = new UserData("alice", "password", "email");
        userDAO.addUser(user);

        LoginRequest request = new LoginRequest("alice", "password");

        // Act
        LoginResult result = service.login(request);

        // Assert
        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
        assertNotEquals("", result.authToken());
    }

    @Test
    public void loginNegativeWrongPassword() throws Exception {
        // Arrange
        UserData user = new UserData("alice", "password", "email");
        userDAO.addUser(user);

        LoginRequest request = new LoginRequest("alice", "wrong");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> {
            service.login(request);
        });
    }

    // =========================
    // LOGOUT TESTS
    // =========================

    @Test
    public void logoutPositive() throws Exception {
        // Arrange
        UserData user = new UserData("alice", "password", "email");
        userDAO.addUser(user);

        LoginResult loginResult =
                service.login(new LoginRequest("alice", "password"));

        LogoutRequest logoutRequest =
                new LogoutRequest(loginResult.authToken());

        // Act
        service.logout(logoutRequest);

        // Assert
        assertNull(authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    public void logoutNegativeInvalidToken() {
        LogoutRequest request =
                new LogoutRequest("fake-token");

        assertThrows(UnauthorizedException.class, () -> {
            service.logout(request);
        });
    }
}