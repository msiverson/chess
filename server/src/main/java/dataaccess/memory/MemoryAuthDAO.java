package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import model.AuthData;



public class MemoryAuthDAO implements AuthDAO {
   private final Map<String, AuthData> authDataDB = new HashMap<>();

    // authToken
    @Override
    public String getAuth() throws DataAccessException {}

    @Override
    public void addAuth(String authToken) {}

    @Override
    public void deleteAuth() {}

    // username
    @Override
    public String getUser() throws DataAccessException {}
}