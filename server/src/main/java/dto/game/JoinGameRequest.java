package dto.game;

public record JoinGameRequest(String authToken, String playerColor, int gameID) {}