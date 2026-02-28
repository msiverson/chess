package server;

import io.javalin.Javalin;

// DB imports
import handler.DBHandler;

// User imports
import handler.UserHandler;
import service.UserService;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryUserDAO;

// Session imports
import handler.SessionHandler;

// Game imports
import handler.GameHandler;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        //DB endpoint
//        DAO
//        Service
//        DBHandler dbHandler = new DBHandler();
//        javalin.delete("/db", dbHandler::clear);

        // User endpoints
        UserDAO userDAO = new MemoryUserDAO(); // Create DAO implementations
        UserService userService = new UserService(userDAO); // Create services
        UserHandler userHandler = new UserHandler(userService); // Create handler
        javalin.post("/user", userHandler::register); // Create endpoint

        // Session endpoints
//        DAO
//        Service
//        SessionHandler sessionHandler = new SessionHandler();
//        javalin.post("/session", sessionHandler::login);
//        javalin.delete("/session", sessionHandler::logout);


        // Game endpoints
//        DAO
//        Service
//        GameHandler gameHandler = new GameHandler();
//        javalin.get("/game", gameHandler::listGames);
//        javalin.post("/game", gameHandler::createGame);
//        javalin.put("/game", gameHandler::joinGame);
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}


//    private final HashSet<String> validTokens = new HashSet<>(Set.of("secret1", "secret2"));

//    private boolean authorized(Context ctx) {
//        String authToken = ctx.header("authorization");
//        if (!validTokens.contains(authToken)) {
//            ctx.contentType("application/json");
//            ctx.status(401);
//            ctx.result(new Gson().toJson(Map.of("msg", "invalid authorization")));
//            return false;
//        }
//        return true;
//    }