package dataaccess.sql;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import dataaccess.DataAccessException;
import dataaccess.GameDAO;
import model.GameData;

import java.util.List;

public class SQLGameDAO extends SQLDAO implements GameDAO {
    @Override
    public List<GameData> listGames() throws DataAccessException {
        return List.of();
    }

    @Override
    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    @Override
    public void createGame(GameData gameData) throws DataAccessException {

    }

    @Override
    public void updateGame(int gameID, GameData updatedGame) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
