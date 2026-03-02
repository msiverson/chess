package service;

import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryAuthDAO;
import dto.game.CreateGameRequest;
import dto.game.JoinGameRequest;
import dto.game.ListGamesRequest;
import dto.game.ListGamesResult;
import model.GameData;
import model.AuthData;
import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameServiceTests {

    private GameService service;
    private MemoryGameDAO gameDAO;
    private MemoryAuthDAO authDAO;

    @BeforeEach
    public void setup() throws Exception{
        gameDAO = new MemoryGameDAO();
        authDAO = new MemoryAuthDAO();
        service = new GameService(authDAO, gameDAO);

        // Insert valid auth token
        AuthData authData = new AuthData("validToken", "alice");
        authDAO.addAuth(authData);
    }



    // =========================
    // CREATE GAME TESTS
    // =========================

    @Test
    public void createGamePositive() throws Exception {
        var result = service.createGame(
                new CreateGameRequest("validToken", "TestGame")
        );

        assertTrue(result.gameID() > 0);

        List<GameData> games = gameDAO.listGames();
        assertEquals(1, games.size());
        assertEquals("TestGame", games.get(0).gameName());
    }

    @Test
    public void createGameNegativeNullName() {
        assertThrows(RuntimeException.class, () ->
                service.createGame(
                        new CreateGameRequest("validToken", null)
                )
        );
    }

    // =========================
    // LIST GAMES TESTS
    // =========================

    @Test
    public void listGamesEmpty() throws Exception {
        // Arrange
        ListGamesRequest request =
                new ListGamesRequest("validToken");

        // Act
        ListGamesResult result = service.listGames(request);

        // Assert
        assertNotNull(result.games());
        assertEquals(0, result.games().size());
    }

    @Test
    public void listGamesMultiple() throws Exception {
        // Arrange
        service.createGame(new CreateGameRequest("validToken", "Game1"));
        service.createGame(new CreateGameRequest("validToken", "Game2"));

        ListGamesRequest request =
                new ListGamesRequest("validToken");

        // Act
        ListGamesResult result = service.listGames(request);

        // Assert
        assertEquals(2, result.games().size());
    }

    @Test
    public void listGamesInvalidAuth() {
        ListGamesRequest request =
                new ListGamesRequest("badToken");

        assertThrows(RuntimeException.class, () ->
                service.listGames(request)
        );
    }

    // =========================
    // JOIN GAME TESTS
    // =========================

    @Test
    public void joinGamePositive() throws Exception {
        var createResult =
                service.createGame(new CreateGameRequest("validToken", "Game1"));

        service.joinGame(
                new JoinGameRequest("validToken",
                        "WHITE",
                        createResult.gameID()
                        )
        );

        GameData game = gameDAO.listGames().get(0);

        assertEquals("alice", game.whiteUsername());
    }

    @Test
    public void joinGameNegativeInvalidGameID() {
        assertThrows(RuntimeException.class, () ->
                service.joinGame(
                        new JoinGameRequest("validToken", "WHITE", 9999)
                )
        );
    }
}