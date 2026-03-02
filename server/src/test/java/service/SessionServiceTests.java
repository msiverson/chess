package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import dataaccess.memory.MemoryUserDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.UserData;
import dto.session.*;
import service.exceptions.UnauthorizedException;

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
    @DisplayName("login() Positive")
    public void loginPositive() throws Exception {
        // Arrange
        UserData user = new UserData("validUsername", "validPassword", "validEmail");
        userDAO.addUser(user);

        LoginRequest request = new LoginRequest("validUsername", "validPassword");

        // Act
        LoginResult result = service.login(request);

        // Assert
        assertEquals("validUsername", result.username());
        assertNotNull(result.authToken());
        assertNotEquals("", result.authToken());
    }

    @Test
    @DisplayName("login() Negative [Wrong Password]")
    public void loginNegativeWrongPassword() throws Exception {
        // Arrange
        UserData user = new UserData("validUsername", "validPassword", "validEmail");
        userDAO.addUser(user);

        LoginRequest request = new LoginRequest("validUsername", "wrong");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> service.login(request));
    }

    // =========================
    // LOGOUT TESTS
    // =========================

    @Test
    @DisplayName("logout() Positive")
    public void logoutPositive() throws Exception {
        // Arrange
        UserData user = new UserData("validUsername", "validPassword", "validEmail");
        userDAO.addUser(user);

        LoginResult loginResult =
                service.login(new LoginRequest("validUsername", "validPassword"));

        LogoutRequest logoutRequest = new LogoutRequest(loginResult.authToken());

        // Act
        service.logout(logoutRequest);

        // Assert
        assertNull(authDAO.getAuth(loginResult.authToken()));
    }

    @Test
    @DisplayName("logout() Negative [Invalid Token]")
    public void logoutNegativeInvalidToken() {
        LogoutRequest request =
                new LogoutRequest("invalidToken");

        // Act & Assert
        assertThrows(UnauthorizedException.class, () -> service.logout(request));
    }
}