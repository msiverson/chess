package facades;

import java.io.IOException;
import java.net.URI;

import com.google.gson.Gson;
import jakarta.websocket.*;

import websocket.commands.UserGameCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;
import websocket.messages.ServerMessage;

@ClientEndpoint
public class GameWebSocketFacade {

    public interface NotificationHandler {
        void onLoadGame(LoadGameMessage message);
        void onNotification(NotificationMessage message);
        void onError(ErrorMessage message);
    }

    private final Gson gson = new Gson();
    private final NotificationHandler handler;
    private Session session;

    public GameWebSocketFacade(NotificationHandler handler) {
        this.handler = handler;
    }

    public void connect(String httpServerUrl) throws Exception {
        String wsUrl = httpServerUrl
                .replaceFirst("^http://", "ws://")
                .replaceFirst("^https://", "wss://")
                + "/ws";

        WebSocketContainer container = ContainerProvider.getWebSocketContainer();
        container.setDefaultMaxSessionIdleTimeout(0); // never timeout
        this.session = container.connectToServer(this, URI.create(wsUrl));
    }

    public void sendCommand(UserGameCommand command) throws IOException {
        if (session == null || !session.isOpen()) {
            throw new IllegalStateException("WebSocket is not connected");
        }
        session.getBasicRemote().sendText(gson.toJson(command));
    }

    public void close() throws IOException {
        if (session != null && session.isOpen()) {
            session.close();
        }
    }

    @OnMessage
    public void onMessage(String json) {
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
    }

    @OnOpen
    public void onOpen(Session session) {
        this.session = session;
        System.out.println("CLIENT WS OPEN");
        System.out.println("idle timeout = " + session.getMaxIdleTimeout());
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        System.out.println("CLIENT WS CLOSED: " + reason);
        this.session = null;
    }

    @OnError
    public void onError(Session session, Throwable throwable) {
        System.out.println("CLIENT WS ERROR: " + throwable.getMessage());
        handler.onError(new ErrorMessage("Error: " + throwable.getMessage()));
    }

    public boolean isConnected() {
        return session != null && session.isOpen();
    }
}
