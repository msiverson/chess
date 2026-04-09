package client;

import Facades.ServerFacade;
import org.junit.jupiter.api.*;

import server.Server;

import dto.game.*;
import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade facade;

    private String username;
    private String password;
    private String authToken;

    /*
        Start server once for entire test suite
     */
    @BeforeAll
    static void init() {

        server = new Server();

        int port = server.run(0);

        System.out.println("Started test HTTP server on port " + port);

        facade = new ServerFacade("http://localhost:" + port);
    }

    /*
        Reset DB and create fresh user before each test
     */
    @BeforeEach
    void setup() throws Exception {

        facade.clear();

        username = "user_" + System.currentTimeMillis();
        password = "password";
        String email = username + "@test.com";

        RegisterResult registerResult =
                facade.register(
                        new RegisterRequest(
                                username,
                                password,
                                email
                        )
                );

        authToken = registerResult.authToken();
    }

    /*
        Stop server after all tests complete
     */
    @AfterAll
    static void stopServer() {

        server.stop();
    }

    /*
        REGISTER
     */

    @Test
    void registerPositive() throws Exception {

        String newUser = "user_" + System.currentTimeMillis();
        String newEmail = newUser + "@test.com";

        RegisterResult result =
                facade.register(
                        new RegisterRequest(
                                newUser,
                                "password",
                                newEmail
                        )
                );

        assertNotNull(result);
        assertEquals(newUser, result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void registerNegativeDuplicateUser() throws Exception {

        facade.register(
                new RegisterRequest(
                        "duplicateUser",
                        "pass",
                        "duplicateUser@test.com"
                )
        );

        assertThrows(RuntimeException.class, () ->
                facade.register(
                        new RegisterRequest(
                                "duplicateUser",
                                "pass",
                                "anotherDuplicateUser@test.com"
                        )
                )
        );
    }

    /*
        LOGIN
     */

    @Test
    void loginPositive() throws Exception {

        LoginResult result =
                facade.login(
                        new LoginRequest(
                                username,
                                password
                        )
                );

        assertNotNull(result);
        assertEquals(username, result.username());
        assertNotNull(result.authToken());
    }

    @Test
    void loginNegativeWrongPassword() {

        assertThrows(RuntimeException.class, () ->
                facade.login(
                        new LoginRequest(
                                username,
                                "wrongPassword"
                        )
                )
        );
    }

    /*
        LOGOUT
     */

    @Test
    void logoutPositive() throws Exception {

        assertDoesNotThrow(() ->
                facade.logout(authToken)
        );
    }

    @Test
    void logoutNegativeInvalidToken() {

        assertThrows(RuntimeException.class, () ->
                facade.logout("invalidToken")
        );
    }

    /*
        CREATE GAME
     */

    @Test
    void createGamePositive() throws Exception {

        CreateGameResult result =
                facade.createGame(
                        new CreateGameRequest(
                                authToken,
                                "game1"
                        )
                );

        assertNotNull(result);
        assertTrue(result.gameID() > 0);
    }

    @Test
    void createGameNegativeInvalidAuthToken() {

        assertThrows(RuntimeException.class, () ->
                facade.createGame(
                        new CreateGameRequest(
                                "badToken",
                                "game"
                        )
                )
        );
    }

    /*
        LIST GAMES
     */

    @Test
    void listGamesPositive() throws Exception {

        facade.createGame(
                new CreateGameRequest(
                        authToken,
                        "game1"
                )
        );

        ListGamesResult result =
                facade.listGames(
                        new ListGamesRequest(authToken)
                );

        assertNotNull(result);
        assertFalse(result.games().isEmpty());
    }

    @Test
    void listGamesNegativeInvalidToken() {

        assertThrows(RuntimeException.class, () ->
                facade.listGames(
                        new ListGamesRequest("badToken")
                )
        );
    }

    /*
        JOIN GAME
     */

    @Test
    void joinGamePositive() throws Exception {

        CreateGameResult game =
                facade.createGame(
                        new CreateGameRequest(
                                authToken,
                                "game1"
                        )
                );

        assertDoesNotThrow(() ->
                facade.joinGame(
                        new JoinGameRequest(
                                authToken,
                                "WHITE",
                                game.gameID()
                        )
                )
        );
    }

    @Test
    void joinGameNegativeInvalidGameID() {

        assertThrows(RuntimeException.class, () ->
                facade.joinGame(
                        new JoinGameRequest(
                                authToken,
                                "WHITE",
                                999999
                        )
                )
        );
    }
}