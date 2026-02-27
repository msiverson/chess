package handler;

import dto.*;
import dto.LoginRequest;
import dto.LoginResult;
import dto.LogoutRequest;
import io.javalin.http.Context;
import service.SessionService;

import java.util.Map;

public class SessionHandler {

    private final SessionService service = new SessionService();

    public void login(Context ctx) {
        try {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
            LoginResult result = service.login(req);

            ctx.status(200).json(result);

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void logout(Context ctx) {
        try {
            String token = ctx.header("authorization");
            service.logout(new LogoutRequest(token));

            ctx.status(200).json(Map.of());

        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
