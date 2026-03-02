package dataaccess.memory;

import dataaccess.GameDAO;
import model.GameData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataaccess.DataAccessException;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> gameDataDB = new HashMap<>();

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return null;
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void updateGame(int gameID) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {
        gameDataDB.clear();
    }
}
