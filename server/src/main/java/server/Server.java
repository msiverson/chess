package server;

import com.google.gson.Gson;
import handler.DBHandler;
import handler.GameHandler;
import handler.SessionHandler;
import handler.UserHandler;
import io.javalin.Javalin;
import io.javalin.http.Context;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Server {

    private final Javalin javalin;
    private final HashSet<String> validTokens = new HashSet<>(Set.of("secret1", "secret2"));

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        DBHandler dbHandler = new DBHandler();
        GameHandler gameHandler = new GameHandler();
        SessionHandler sessionHandler = new SessionHandler();
        UserHandler userHandler = new UserHandler();

        javalin.delete("/db", dbHandler::clear);

        javalin.post("/user", userHandler::register);
        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);

        javalin.get("/game", gameHandler::listGames);
        javalin.post("/game", gameHandler::createGame);
        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private boolean authorized(Context ctx) {
        String authToken = ctx.header("authorization");
        if (!validTokens.contains(authToken)) {
            ctx.contentType("application/json");
            ctx.status(401);
            ctx.result(new Gson().toJson(Map.of("msg", "invalid authorization")));
            return false;
        }
        return true;
    }
}
