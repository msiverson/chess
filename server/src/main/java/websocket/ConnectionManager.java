package websocket;

import com.google.gson.Gson;
import io.javalin.websocket.WsContext;
import websocket.messages.ServerMessage;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConnectionManager {
    private final Map<String, Connection> connections = new ConcurrentHashMap<>();
    private final Gson gson = new Gson();

    public void add(String sessionId, Connection connection) {
        connections.put(sessionId, connection);
    }

    public void remove(String sessionId) {
        connections.remove(sessionId);
    }

    public Connection get(String sessionId) {
        return connections.get(sessionId);
    }

    public void sendToOne(WsContext ctx, ServerMessage message) {
        ctx.send(gson.toJson(message));
    }

    public void broadcastToGame(int gameID, ServerMessage message) {
        String json = gson.toJson(message);

        for (Connection connection : connections.values()) {
            if (connection.getGameID() == gameID) {
                connection.getCtx().send(json);
            }
        }
    }

    public void broadcastToGameExcept(int gameID, String excludedSessionId, ServerMessage message) {
        String json = gson.toJson(message);

        for (Map.Entry<String, Connection> entry : connections.entrySet()) {
            String sessionId = entry.getKey();
            Connection connection = entry.getValue();

            if (connection.getGameID() == gameID && !sessionId.equals(excludedSessionId)) {
                connection.getCtx().send(json);
            }
        }
    }
}