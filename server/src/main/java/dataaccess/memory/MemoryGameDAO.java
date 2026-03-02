package dataaccess.memory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;

import model.GameData;

public class MemoryGameDAO implements GameDAO {
    private final Map<Integer, GameData> gameDataDB = new HashMap<>();

    @Override
    public List<GameData> listGames() throws DataAccessException {
        return new ArrayList<>(gameDataDB.values());
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return gameDataDB.get(gameID);
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {
        gameDataDB.put(gameData.gameID(), gameData);
    }

    @Override
    public void updateGame(int gameID, GameData gameData) throws DataAccessException {
        gameDataDB.put(gameID, gameData);
    }

    @Override
    public void clear() throws DataAccessException {
        gameDataDB.clear();
    }
}
