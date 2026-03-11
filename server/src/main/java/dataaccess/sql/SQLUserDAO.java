package dataaccess.sql;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class SQLUserDAO implements UserDAO {
    @Override
    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    @Override
    public void addUser(UserData userData) throws DataAccessException {

    }

    @Override
    public void clear() throws DataAccessException {

    }
}
