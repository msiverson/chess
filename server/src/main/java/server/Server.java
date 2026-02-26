package server;

import com.google.gson.Gson;
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

        // Clear application
        javalin.delete("/db", context -> deleteName(context));
        // Register user
        javalin.post("/user", )
        //

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }

    private void deleteName(Context ctx) {

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
