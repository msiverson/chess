package service;
import dto.LoginRequest;
import dto.LoginResult;
import dto.LogoutRequest;

import java.util.UUID;

public class SessionService {
    public LoginResult login(LoginRequest loginRequest)  {


        // Generate authToken
        String authToken = UUID.randomUUID().toString();
    }

    public void logout(LogoutRequest logoutRequest) {

    }
}
