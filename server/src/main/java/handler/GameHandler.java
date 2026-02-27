package handler;

import dto.*;
import dto.CreateGameRequest;
import dto.CreateGameResult;
import dto.JoinGameRequest;
import dto.ListGamesRequest;
import dto.ListGamesResult;
import io.javalin.http.Context;
import service.GameService;

import java.util.Map;

public class GameHandler {

    private final GameService service = new GameService();

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
            String token = ctx.header("authorization");
            String req = ctx.bodyAsClass(CreateGameRequest.class);


            CreateGameResult result = service.createGame(req, token);

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
            JoinGameRequest req = ctx.bodyAsClass(JoinGameRequest.class);

            service.joinGame(req, token);

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
