package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.AlreadyExistsException;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userDataDB = new HashMap<>();

    // username
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return userDataDB.get(username);
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {
        if (userDataDB.containsKey(userData.username())) {
            throw new AlreadyExistsException("User exists");
        }
        userDataDB.put(userData.username(), userData);
    }

    @Override
    public void clear() throws DataAccessException {

    }


}