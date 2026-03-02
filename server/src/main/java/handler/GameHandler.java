package handler;

import java.util.Map;

import com.google.gson.Gson;
import io.javalin.http.Context;

import dto.game.CreateGameRequest;
import dto.game.CreateGameResult;
import dto.game.JoinGameRequest;
import dto.game.ListGamesRequest;
import dto.game.ListGamesResult;

import service.GameService;
import service.exceptions.AlreadyTakenException;
import service.exceptions.UnauthorizedException;

public class GameHandler {

    private final GameService service;
    private final Gson gson = new Gson();

    public GameHandler (GameService service) {
        this.service = service;
    }

    public void listGames(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            ListGamesRequest listGamesRequest = new ListGamesRequest(authToken);
            ListGamesResult result = service.listGames(listGamesRequest);
            ctx.status(200).json(result);

        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void createGame(Context ctx) {
        try {
            String authToken = ctx.header("authorization");
            String req = ""; //= ctx.bodyAsClass(CreateGameRequest.class);

            CreateGameResult result = service.createGame(new CreateGameRequest(authToken, req));

            ctx.status(200).json(result);

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }

    public void joinGame(Context ctx) {
        try {
            String token = ctx.header("authorization");
            String req = "";//= ctx.bodyAsClass(JoinGameRequest.class);

            //service.joinGame(new JoinGameRequest(req, token));

            ctx.status(200).json(Map.of());

        } catch (IllegalArgumentException e) {
            ctx.status(400).json(Map.of("message", "Error: bad request"));
        } catch (UnauthorizedException e) {
            ctx.status(401).json(Map.of("message", "Error: unauthorized"));
        } catch (AlreadyTakenException e) {
            ctx.status(403).json(Map.of("message", "Error: already taken"));
        } catch (Exception e) {
            ctx.status(500).json(Map.of("message", "Error: " + e.getMessage()));
        }
    }
}
