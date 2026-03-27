package dto.game;

public record CreateGameRequest(String authToken, String gameName) {}