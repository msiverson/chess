package service;

import java.util.UUID;

// Auth
import dataaccess.AlreadyExistsException;
import dataaccess.AuthDAO;

// User
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;
import model.AuthData;
import model.UserData;

public class UserService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public UserService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {

        // IllegalArgumentException
        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new IllegalArgumentException();
        }

        // Check if a user exists
        try {
            UserData userCheck = userDAO.getUser(registerRequest.username());
            if (userCheck != null) {
                throw new AlreadyExistsException("Already exists");
            }
        } catch (DataAccessException e) {
            throw new ServiceException("Server Error");
        }

        // Add userData
        try {
            UserData userData = new UserData(
                registerRequest.username(),
                registerRequest.password(),
                registerRequest.email()
            );
            userDAO.addUser(userData);
        } catch (DataAccessException e) {
            throw new ServiceException("Server Error");
        }

        // Add authData
        String authToken = UUID.randomUUID().toString();
        try {
            AuthData auth = new AuthData(
                    authToken,
                    registerRequest.username()
            );
            authDAO.addAuth(auth);
        } catch (DataAccessException e) {
            throw new ServiceException("Server Error");
        }

        return new RegisterResult(registerRequest.username(), authToken);
    }
}
