package handler;

import java.util.Map;

import io.javalin.http.Context;
import com.google.gson.Gson;

import dto.*;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;
import service.UserService;

public class UserHandler {

    private final UserService service = new UserService();
    private final Gson gson = new Gson();

    public void register(Context ctx) {
        try {
            // Extract body into RegisterRequest
            RegisterRequest registerRequest =  gson.fromJson(ctx.body(), RegisterRequest.class);
            RegisterResult result = service.register(registerRequest);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (AlreadyExistsException e) {
            ctx.status(403);
            ctx.result(gson.toJson(Map.of("message", "Error: already taken")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: already taken")));
        }
    }
}
