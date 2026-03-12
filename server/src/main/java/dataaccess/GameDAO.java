package dataaccess;

import java.util.List;

import model.GameData;

public interface GameDAO {
    public List<GameData> listGames() throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public int createGame(GameData gameData) throws DataAccessException;
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException;

    void clear() throws DataAccessException;
}
