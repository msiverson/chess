package handler;

import java.util.Map;

import io.javalin.http.Context;
import com.google.gson.Gson;

import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.session.LogoutRequest;
import service.SessionService;
// Exceptions
import service.exceptions.UnauthorizedException;
import service.exceptions.ServiceException;

public class SessionHandler {

    private final SessionService service;
    private final Gson gson = new Gson();

    public SessionHandler(SessionService service) {
        this.service = service;
    }

    public void login(Context ctx) {
        System.out.println("Session Handler");

        try {
            LoginRequest req = gson.fromJson(ctx.body(), LoginRequest.class);
            LoginResult result = service.login(req);

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (ServiceException e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    public void logout(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            service.logout(new LogoutRequest(authToken));

            ctx.status(200);

        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (ServiceException e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
