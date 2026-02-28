package service;

import java.util.UUID;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;

import javax.xml.crypto.Data;

public class UserService {

    private final UserDAO userDAO;

    public UserService(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) {

        // If any field is missing
        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new IllegalArgumentException();
        }

        // Check if user already exists in db
        try {
            userDAO.getUser(registerRequest.username());
        } catch (DataAccessException e) {
            throw new ServiceException("Server Error");
        }

        // Generate authToken
        String authToken = UUID.randomUUID().toString();

        return new RegisterResult(registerRequest.username(), authToken);
    }
}
