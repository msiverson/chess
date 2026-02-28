package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
   private final Map<String, AuthData> authDataDB = new HashMap<>();

   @Override
    public AuthData getAuth(String authToken) throws DataAccessException {
        return authDataDB.get(authToken);
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {
        authDataDB.put(authData.authToken(), authData);
    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {
        authDataDB.remove(authToken);
    }

    @Override
    public void clear() throws DataAccessException {
        authDataDB.clear();
    }
}