package server;

import dataaccess.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
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

        // DAO (Memory Implementation)
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();

        //DB endpoint
//        Service
//        DBHandler dbHandler = new DBHandler();
//        javalin.delete("/db", dbHandler::clear);

        // User endpoints
        UserService userService = new UserService(userDAO, authDAO); // Create services
        UserHandler userHandler = new UserHandler(userService);
        javalin.post("/user", userHandler::register);

        // Session endpoints
//        Service
//        SessionHandler sessionHandler = new SessionHandler();
//        javalin.post("/session", sessionHandler::login);
//        javalin.delete("/session", sessionHandler::logout);


        // Game endpoints
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