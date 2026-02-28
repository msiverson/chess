package dataaccess;

import model.UserData;

public interface UserDAO {
    // username
    public UserData getUser(String username) throws DataAccessException;
    public void addUser(UserData userData) throws DataAccessException;

    void clear() throws DataAccessException;
}