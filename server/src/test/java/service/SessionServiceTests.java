package service;

import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.session.LogoutRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import dataaccess.memory.MemoryUserDAO;
import dataaccess.memory.MemoryAuthDAO;
import model.UserData;
import service.exceptions.UnauthorizedException;

import org.mindrot.jbcrypt.BCrypt;

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

        String hashedPassword = BCrypt.hashpw("validPassword", BCrypt.gensalt());

        UserData user = new UserData(
                "validUsername",
                hashedPassword,
                "validEmail"
        );

        userDAO.addUser(user);

        LoginRequest request =
                new LoginRequest("validUsername", "validPassword");

        LoginResult result = service.login(request);

        assertEquals("validUsername", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("login() Negative [Wrong Password]")
    public void loginNegativeWrongPassword() throws Exception {

        String hashedPassword = BCrypt.hashpw("validPassword", BCrypt.gensalt());

        UserData user = new UserData(
                "validUsername",
                hashedPassword,
                "validEmail"
        );

        userDAO.addUser(user);

        LoginRequest request =
                new LoginRequest("validUsername", "wrong");

        assertThrows(
                UnauthorizedException.class,
                () -> service.login(request)
        );
    }

    // =========================
    // LOGOUT TESTS
    // =========================

    @Test
    @DisplayName("logout() Positive")
    public void logoutPositive() throws Exception {

        String hashedPassword = BCrypt.hashpw("validPassword", BCrypt.gensalt());

        UserData user = new UserData(
                "validUsername",
                hashedPassword,
                "validEmail"
        );

        userDAO.addUser(user);

        LoginResult loginResult =
                service.login(
                        new LoginRequest("validUsername", "validPassword")
                );

        service.logout(
                new LogoutRequest(loginResult.authToken())
        );

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