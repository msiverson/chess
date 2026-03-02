package service;

import java.util.UUID;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;

import dto.user.RegisterRequest;
import dto.user.RegisterResult;

import model.AuthData;
import model.UserData;

import service.exceptions.AlreadyExistsException;
import service.exceptions.ServiceException;

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

        try {
            // Check if a user exists
            UserData userCheck = userDAO.getUser(registerRequest.username());
            if (userCheck != null) {
                throw new AlreadyExistsException("Already exists");
            }

            // Add userData
            UserData userData = new UserData(
                    registerRequest.username(),
                    registerRequest.password(),
                    registerRequest.email()
            );
            userDAO.addUser(userData);

            // Add authData
            String authToken = UUID.randomUUID().toString();
            AuthData auth = new AuthData(
                    authToken,
                    registerRequest.username()
            );
            authDAO.addAuth(auth);

            return new RegisterResult(registerRequest.username(), authToken);

        } catch (DataAccessException e) {
            throw new ServiceException("Server Error");
        }
    }
}
