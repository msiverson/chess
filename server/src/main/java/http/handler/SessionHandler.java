package http.handler;

import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.Context;

import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.session.LogoutRequest;

import http.service.SessionService;
import http.service.exceptions.ServiceException;
import http.service.exceptions.UnauthorizedException;

public class SessionHandler {

    private final SessionService service;
    private final Gson gson = new Gson();

    public SessionHandler(SessionService service) {
        this.service = service;
    }

    public void login(Context ctx) {
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
            System.out.println("401");
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (ServiceException e) {
            System.out.println("500");
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
