package service;

// Auth
import dataaccess.AuthDAO;
// User
import dataaccess.UserDAO;
// Exceptions
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

    public LoginResult login(LoginRequest loginRequest)  {

    }

    public void logout(LogoutRequest logoutRequest) {

    }
}
