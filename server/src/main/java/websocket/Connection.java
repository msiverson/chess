package websocket;

import io.javalin.websocket.WsContext;

public class Connection {
    private final String authToken;
    private final String username;
    private final int gameID;
    private final WsContext ctx;

    public Connection(String authToken, String username, int gameID, WsContext ctx) {
        this.authToken = authToken;
        this.username = username;
        this.gameID = gameID;
        this.ctx = ctx;
    }

    public String getAuthToken() {
        return authToken;
    }

    public String getUsername() {
        return username;
    }

    public int getGameID() {
        return gameID;
    }

    public WsContext getCtx() {
        return ctx;
    }
}
