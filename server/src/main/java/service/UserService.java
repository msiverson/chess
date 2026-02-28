package service;

import java.util.UUID;

import dto.user.RegisterRequest;
import dto.user.RegisterResult;
import handler.AlreadyExistsException;

public class UserService {
    public RegisterResult register(RegisterRequest registerRequest) {

        if (registerRequest.username() == null ||
                registerRequest.password() == null ||
                registerRequest.email() == null) {
            throw new IllegalArgumentException();
        }
        // Generate authToken
        String authToken = UUID.randomUUID().toString();
    }
}
