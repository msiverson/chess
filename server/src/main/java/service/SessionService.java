package service;

import java.util.UUID;

// Auth
import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;
// User
import dataaccess.UserDAO;
import model.UserData;
// Exceptions
import service.exceptions.ServiceException;
import service.exceptions.UnauthorizedException;
// DTO
import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.session.LogoutRequest;

public class SessionService {
    private final UserDAO userDAO;
    private final AuthDAO authDAO;

    public SessionService(UserDAO userDAO, AuthDAO authDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
    }

    public LoginResult login(LoginRequest loginRequest) {
        // IllegalArgumentException
        if (loginRequest.username() == null || loginRequest.password() == null) {
            throw new IllegalArgumentException();
        }

        try {
            UserData user = userDAO.getUser(loginRequest.username());

            // Retrieve User
            if (user == null) {
                throw new UnauthorizedException("Invalid username");
            }

            // Verify password
            if (!user.password().equals(loginRequest.password())) {
                throw new UnauthorizedException("Invalid password");
            }

            // Add authData
            String authToken = UUID.randomUUID().toString();
            AuthData authData = new AuthData(authToken, loginRequest.username());
            authDAO.addAuth(authData);

            return new LoginResult(loginRequest.username(), authToken);

        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }

    public void logout(LogoutRequest logoutRequest) {
        if (logoutRequest.authToken() == null) {
            throw new UnauthorizedException("Missing auth token");
        }

        try {
            AuthData auth = authDAO.getAuth(logoutRequest.authToken());
            if (auth == null) {
                throw new UnauthorizedException("Invalid auth token");
            }
            authDAO.deleteAuth(logoutRequest.authToken());
        } catch (DataAccessException e) {
            throw new ServiceException("Server error");
        }
    }
}
