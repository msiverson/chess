package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userDataDB = new HashMap<>();

    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataDB.get(username);
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        userDataDB.put(userData.username(), userData);
    }

    @Override
    public void clear() throws DataAccessException {
        userDataDB.clear();
    }
}