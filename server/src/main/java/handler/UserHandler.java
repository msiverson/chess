package handler;

import dto.*;
import dto.RegisterRequest;
import dto.RegisterResult;
import io.javalin.http.Context;
import service.UserService;

import java.util.Map;

public class UserHandler {

    private final UserService service = new UserService();

    public void register(Context ctx) {
        try {
            RegisterRequest req = ctx.bodyAsClass(RegisterRequest.class);
            RegisterResult result = service.register(req);

            ctx.status(200).json(result);

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (AlreadyExistsException e) {
            ctx.status(403).json(Map.of("message", "Error: already taken"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
