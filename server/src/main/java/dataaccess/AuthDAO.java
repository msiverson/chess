package dataaccess;

import model.AuthData;

public interface AuthDAO {
    public AuthData getAuth(String authToken) throws DataAccessException;
    public void addAuth(AuthData authData) throws DataAccessException;
    public void deleteAuth(String authToken) throws DataAccessException;

    void clear() throws DataAccessException;
}