package client;

import chess.ChessGame;

public class ClientContext {
    private String authToken = null;
    private Integer gameId = null;
    private ChessGame.TeamColor teamColor = null;
    private boolean observing;

    public String getAuthToken() { return authToken; }
    public void setAuthToken(String authToken) { this.authToken = authToken; }

    public Integer getGameId() { return gameId; }
    public void setGameId(Integer gameId) { this.gameId = gameId; }

    public ChessGame.TeamColor getTeamColor() { return teamColor; }
    public void setTeamColor(ChessGame.TeamColor teamColor) { this.teamColor = teamColor; }

    public boolean getObserving() { return observing; }
    public void setObserving(boolean observing) { this.observing = observing; }
}
