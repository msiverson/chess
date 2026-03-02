package service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;
import service.exceptions.AlreadyExistsException;

public class UserServiceTests {

    private UserService service;
    private UserDAO userDAO;
    private AuthDAO authDAO;

    @BeforeEach
    void setup() {
        userDAO = new MemoryUserDAO();
        authDAO = new MemoryAuthDAO();
        service = new UserService(userDAO, authDAO);
    }

    @Test
    @DisplayName("register() Positive")
    void registerSuccess() throws Exception {
        RegisterRequest request =
                new RegisterRequest("Mark", "password", "email");

        RegisterResult result = service.register(request);

        assertEquals("Mark", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    @DisplayName("register() Negative [User Already Exists]")
    void registerAlreadyExists() throws Exception {
        RegisterRequest request =
                new RegisterRequest("EvilMark", "pass", "email");

        service.register(request);

        assertThrows(AlreadyExistsException.class, () -> service.register(request));
    }
}
