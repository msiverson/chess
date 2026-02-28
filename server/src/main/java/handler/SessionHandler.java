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
        try {
            LoginRequest req = ctx.bodyAsClass(LoginRequest.class);
            LoginResult result = service.login(req);

            ctx.status(200).json(result);

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (ServiceException e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: internal error")));
        }
    }

    public void logout(Context ctx) {
        try {
            String token = ctx.header("authorization");
            service.logout(new LogoutRequest(token));

            ctx.status(200).json(Map.of());

        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (ServiceException e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
