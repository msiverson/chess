package service;

import dto.RegisterRequest;
import dto.RegisterResult;

import java.util.UUID;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {

        // Generate authToken
        String authToken = UUID.randomUUID().toString();
    }
}
