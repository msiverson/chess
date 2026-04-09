package websocket.handler;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import model.AuthData;
import model.GameData;
import websocket.service.GameplayService;
import websocket.Connection;
import websocket.ConnectionManager;
import websocket.commands.*;
import websocket.messages.*;

public class GameplayHandler {
    private final GameplayService gameplayService;
    private final ConnectionManager connectionManager;
    private final Gson gson = new Gson();

    public GameplayHandler(GameplayService gameplayService, ConnectionManager connectionManager) {
        this.gameplayService = gameplayService;
        this.connectionManager = connectionManager;
    }

    public void onConnect(WsContext ctx) {
        System.out.println("SERVER WS OPEN: " + ctx.sessionId());
    }

    public void onMessage(WsContext ctx, String json) {
        System.out.println("server received websocket message: " + json);
        try {
            UserGameCommand base = gson.fromJson(json, UserGameCommand.class);

            switch (base.getCommandType()) {
                case CONNECT -> handleConnect(ctx, gson.fromJson(json, ConnectCommand.class));
                case MAKE_MOVE -> handleMakeMove(ctx, gson.fromJson(json, MakeMoveCommand.class));
                case LEAVE -> handleLeave(ctx, gson.fromJson(json, LeaveCommand.class));
                case RESIGN -> handleResign(ctx, gson.fromJson(json, ResignCommand.class));
            }
        } catch (Exception e) {
            connectionManager.sendToOne(ctx, new ErrorMessage("Error: " + e.getMessage()));
        }
    }

    public void onClose(WsContext ctx) {
        System.out.println("SERVER WS CLOSED: " + ctx.sessionId());
        connectionManager.remove(ctx.sessionId());
    }

    private void handleConnect(WsContext ctx, ConnectCommand cmd) {
        System.out.println("handleConnect called for game " + cmd.getGameID());

        GameData gameData = gameplayService.connect(cmd.getAuthToken(), cmd.getGameID());
        AuthData authData = gameplayService.getAuth(cmd.getAuthToken());

        Connection connection = new Connection(
                cmd.getAuthToken(),
                authData.username(),
                cmd.getGameID(),
                ctx
        );

        connectionManager.add(ctx.sessionId(), connection);

        System.out.println("sending LoadGameMessage (connect)");
        connectionManager.sendToOne(ctx, new LoadGameMessage(gameData.game()));

        connectionManager.broadcastToGameExcept(
                cmd.getGameID(),
                ctx.sessionId(),
                new NotificationMessage(authData.username() + " connected")
        );
    }

    private void handleMakeMove(WsContext ctx, MakeMoveCommand cmd) {
        GameData updatedGame = gameplayService.makeMove(
                cmd.getAuthToken(),
                cmd.getGameID(),
                cmd.getMove()
        );

        AuthData authData = gameplayService.getAuth(cmd.getAuthToken());

        connectionManager.broadcastToGame(
                cmd.getGameID(),
                new LoadGameMessage(updatedGame.game())
        );

        connectionManager.broadcastToGame(
                cmd.getGameID(),
                new NotificationMessage(authData.username() + " moved " + cmd.getMove())
        );
    }

    private void handleLeave(WsContext ctx, LeaveCommand cmd) {
        AuthData authData = gameplayService.getAuth(cmd.getAuthToken());
        gameplayService.leave(cmd.getAuthToken(), cmd.getGameID());

        connectionManager.remove(ctx.sessionId());

        connectionManager.broadcastToGame(
                cmd.getGameID(),
                new NotificationMessage(authData.username() + " left the game")
        );
    }

    private void handleResign(WsContext ctx, ResignCommand cmd) {
        AuthData authData = gameplayService.getAuth(cmd.getAuthToken());
        gameplayService.resign(cmd.getAuthToken(), cmd.getGameID());

        connectionManager.broadcastToGame(
                cmd.getGameID(),
                new NotificationMessage(authData.username() + " resigned")
        );
    }
}
