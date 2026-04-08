package http.handler;

import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.javalin.http.Context;

import dto.game.CreateGameRequest;
import dto.game.CreateGameResult;
import dto.game.JoinGameRequest;
import dto.game.ListGamesRequest;
import dto.game.ListGamesResult;

import http.service.GameService;
import http.service.exceptions.AlreadyTakenException;
import http.service.exceptions.UnauthorizedException;

public class GameHandler {

    private final GameService service;
    private final Gson gson = new Gson();

    public GameHandler (GameService service) {
        this.service = service;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");

            ListGamesResult result = service.listGames(new ListGamesRequest(authToken));

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            JsonObject json = JsonParser.parseString(ctx.body()).getAsJsonObject();

            if (json.get("gameName") == null) {
                throw new IllegalArgumentException();
            }

            String gameName = json.get("gameName").getAsString();
            CreateGameResult result = service.createGame(new CreateGameRequest(authToken, gameName));

            ctx.status(200);
            ctx.result(gson.toJson(result));

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            JsonObject json = JsonParser.parseString(ctx.body()).getAsJsonObject();

            if (json.get("playerColor") == null || json.get("gameID") == null) {
                throw new IllegalArgumentException();
            }
            String playerColor = json.get("playerColor").getAsString();
            int gameID = json.get("gameID").getAsInt();

            service.joinGame(new JoinGameRequest(token, playerColor, gameID));

            ctx.status(200);

        } catch (IllegalArgumentException e) {
            ctx.status(400);
            ctx.result(gson.toJson(Map.of("message", "Error: bad request")));
        } catch (UnauthorizedException e) {
            ctx.status(401);
            ctx.result(gson.toJson(Map.of("message", "Error: unauthorized")));
        } catch (AlreadyTakenException e) {
            ctx.status(403);
            ctx.result(gson.toJson(Map.of("message", "Error: already taken")));
        } catch (Exception e) {
            ctx.status(500);
            ctx.result(gson.toJson(Map.of("message", "Error: " + e.getMessage())));
        }
    }
}
