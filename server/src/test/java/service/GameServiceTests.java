package service;

import java.util.List;

import http.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import static org.junit.jupiter.api.Assertions.*;

import dataaccess.memory.MemoryGameDAO;
import dataaccess.memory.MemoryAuthDAO;
import dto.game.CreateGameRequest;
import dto.game.JoinGameRequest;
import dto.game.ListGamesRequest;
import dto.game.ListGamesResult;
import model.GameData;
import model.AuthData;

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
        AuthData authData = new AuthData("validToken", "validUsername");
        authDAO.addAuth(authData);
    }

    // =========================
    // CREATE GAME TESTS
    // =========================

    @Test
    @DisplayName("createGame() Positive")
    public void createGamePositive() throws Exception {
        var result = service.createGame(
                new CreateGameRequest("validToken", "TestGame")
        );

        assertTrue(result.gameID() >= 0);

        List<GameData> games = gameDAO.listGames();
        assertEquals(1, games.size());
        assertEquals("TestGame", games.get(0).gameName());
    }

    @Test
    @DisplayName("createGame() Negative [Null name]")
    public void createGameNegativeNullName() {
        assertThrows(RuntimeException.class, () ->
                service.createGame(new CreateGameRequest("validToken", null))
        );
    }

    // =========================
    // LIST GAMES TESTS
    // =========================

    @Test
    @DisplayName("listGames() Positive [Empty list]")
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
    @DisplayName("listGames() Negative [Invalid authToken]")
    public void listGamesInvalidAuth() {
        ListGamesRequest request =
                new ListGamesRequest("invalidToken");

        assertThrows(RuntimeException.class, () ->
                service.listGames(request)
        );
    }

    // =========================
    // JOIN GAME TESTS
    // =========================

    @Test
    @DisplayName("joinGame() Positive")
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

        assertEquals("validUsername", game.whiteUsername());
    }

    @Test
    @DisplayName("joinGame() Negative [Invalid gameID]")
    public void joinGameNegativeInvalidGameID() {
        assertThrows(RuntimeException.class, () ->
                service.joinGame(
                        new JoinGameRequest("validToken", "WHITE", 9999)
                )
        );
    }
}