package http.handler;

import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.Context;

import dto.user.RegisterRequest;
import dto.user.RegisterResult;

import http.service.UserService;
import http.service.exceptions.AlreadyExistsException;
import http.service.exceptions.ServiceException;

public class UserHandler {

    private final UserService service;
    private final Gson gson = new Gson();

    public UserHandler(UserService service) {
        this.service = service;
    }

    public void register(Context ctx) {
        try {
            // Extract body into RegisterRequest
            RegisterRequest registerRequest =  gson.fromJson(ctx.body(), RegisterRequest.class);

            // Service operation
            RegisterResult result = service.register(registerRequest);

            // Success response
            ctx.status(200);
            ctx.result(gson.toJson(result));

        // Failure responses
        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (AlreadyExistsException e) {
            ctx.status(403);
            ctx.result(gson.toJson(Map.of("message", "Error: already taken")));
        } catch (ServiceException e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: internal error")));
        }
    }
}
