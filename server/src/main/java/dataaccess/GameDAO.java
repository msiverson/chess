package dataaccess;
import model.GameData;

public interface GameDAO {
    public GameData listGames() throws DataAccessException;
    public GameData getGame(int gameID) throws DataAccessException;
    public void createGame(GameData gameData) throws DataAccessException;
    public void updateGame(int gameID) throws DataAccessException;

    void clear() throws DataAccessException;
}
