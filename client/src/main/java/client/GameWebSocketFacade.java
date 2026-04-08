package client;

import com.google.gson.Gson;
import websocket.commands.*;
import websocket.messages.*;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.WebSocket;
import java.util.concurrent.CompletionStage;

public class GameWebSocketFacade implements WebSocket.Listener {
    private final Gson gson = new Gson();
    private final NotificationHandler handler;
    private WebSocket webSocket;

    public interface NotificationHandler {
        void onLoadGame(LoadGameMessage message);
        void onNotification(NotificationMessage message);
        void onError(ErrorMessage message);
    }

    public GameWebSocketFacade(NotificationHandler handler) {
        this.handler = handler;
    }

    public void connect(String httpServerUrl) {
        String wsUrl = httpServerUrl
                .replaceFirst("^http://", "ws://")
                .replaceFirst("^https://", "wss://")
                + "/ws";

        HttpClient client = HttpClient.newHttpClient();

        this.webSocket = client.newWebSocketBuilder()
                .buildAsync(URI.create(wsUrl), this)
                .join();
    }

    public void sendCommand(UserGameCommand command) {
        String json = gson.toJson(command);
        webSocket.sendText(json, true);
    }

    public void close() {
        if (webSocket != null) {
            webSocket.sendClose(WebSocket.NORMAL_CLOSURE, "bye");
        }
    }

    @Override
    public void onOpen(WebSocket webSocket) {
        WebSocket.Listener.super.onOpen(webSocket);
    }

    @Override
    public CompletionStage<?> onText(WebSocket webSocket, CharSequence data, boolean last) {
        String json = data.toString();

        ServerMessage base = gson.fromJson(json, ServerMessage.class);

        switch (base.getServerMessageType()) {
            case LOAD_GAME -> {
                LoadGameMessage msg = gson.fromJson(json, LoadGameMessage.class);
                handler.onLoadGame(msg);
            }
            case NOTIFICATION -> {
                NotificationMessage msg = gson.fromJson(json, NotificationMessage.class);
                handler.onNotification(msg);
            }
            case ERROR -> {
                ErrorMessage msg = gson.fromJson(json, ErrorMessage.class);
                handler.onError(msg);
            }
        }

        return WebSocket.Listener.super.onText(webSocket, data, last);
    }

    @Override
    public void onError(WebSocket webSocket, Throwable error) {
        handler.onError(new ErrorMessage("Error: " + error.getMessage()));
    }
}
