package service;

import dataaccess.AuthDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.memory.MemoryUserDAO;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import service.exceptions.AlreadyExistsException;

import static org.junit.jupiter.api.Assertions.*;

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
    void registerSuccess() throws Exception {
        RegisterRequest request =
                new RegisterRequest("alice", "password", "email");

        RegisterResult result = service.register(request);

        assertEquals("alice", result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerAlreadyExists() throws Exception {
        RegisterRequest request =
                new RegisterRequest("bob", "pass", "email");

        service.register(request);

        assertThrows(
                AlreadyExistsException.class,
                () -> service.register(request)
        );
    }
}
