package server;

import dataaccess.DataAccessException;
import io.javalin.Javalin;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;
import dataaccess.UserDAO;
import dataaccess.sql.SQLAuthDAO;
import dataaccess.sql.SQLGameDAO;
import dataaccess.sql.SQLUserDAO;
import static dataaccess.sql.DatabaseManager.configureDatabase;
import static dataaccess.sql.DatabaseManager.createDatabase;

import http.handler.DBHandler;
import http.handler.GameHandler;
import http.handler.SessionHandler;
import http.handler.UserHandler;

import http.service.DBService;
import http.service.GameService;
import http.service.SessionService;
import http.service.UserService;

public class Server {

    private final Javalin javalin;

    public Server() {
        javalin = Javalin.create(config -> config.staticFiles.add("web"));

        try {
            createDatabase();
            configureDatabase();
        } catch (DataAccessException e) {
            throw new RuntimeException();
        }

        // DAO (SQL Implementation)
        UserDAO userDAO = new SQLUserDAO();
        AuthDAO authDAO = new SQLAuthDAO();
        GameDAO gameDAO = new SQLGameDAO();

        // DB endpoint
        DBService dbService = new DBService(userDAO, authDAO, gameDAO);
        DBHandler dbHandler = new DBHandler(dbService);
        javalin.delete("/db", dbHandler::clear);

        // User endpoints
        UserService userService = new UserService(userDAO, authDAO);
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

        // WebSocket Endpoint
        // GameplayService gameplayService = new GameplayService(authDAO, gameDAO);
        // GameplayHandler gameplayHandler = new GameplayHandler(gameplayService);

    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}