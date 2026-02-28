package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;

public class MemoryAuthDAO implements AuthDAO {
   private final Map<String, AuthData> authDataDB = new HashMap<>();

    public AuthDAO getAuth(String authToken) throws DataAccessException {
        return null;
    }

    @Override
    public void addAuth(AuthData authData) throws DataAccessException {

    }

    @Override
    public void deleteAuth(String authToken) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}