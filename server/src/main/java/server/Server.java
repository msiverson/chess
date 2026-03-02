package server;

import io.javalin.Javalin;

// DB imports
import handler.DBHandler;
import service.DBService;
// User Path imports
import handler.UserHandler;
import service.GameService;
import service.UserService;
// Session Path imports
import handler.SessionHandler;
import service.SessionService;
// Game Path imports
import handler.GameHandler;
import service.GameService;

// Data access
import dataaccess.AuthDAO;
import dataaccess.memory.MemoryAuthDAO;
import dataaccess.UserDAO;
import dataaccess.memory.MemoryUserDAO;
import dataaccess.GameDAO;
import dataaccess.memory.MemoryGameDAO;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        // DAO (Memory Implementation)
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        GameDAO gameDAO = new MemoryGameDAO();

        //DB endpoint
        DBService dbService = new DBService(userDAO, authDAO, gameDAO);
        DBHandler dbHandler = new DBHandler(dbService);
        javalin.delete("/db", dbHandler::clear);

        // User endpoints
        UserService userService = new UserService(userDAO, authDAO); // Create services
        UserHandler userHandler = new UserHandler(userService);
        javalin.post("/user", userHandler::register);

        // Session endpoints
        SessionService sessionService = new SessionService(userDAO, authDAO);
        SessionHandler sessionHandler = new SessionHandler(sessionService);
        javalin.post("/session", sessionHandler::login);
        javalin.delete("/session", sessionHandler::logout);

        // Game endpoints
        GameService gameService = new GameService(authDAO, gameDAO);
        GameHandler gameHandler = new GameHandler(gameService);
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
}