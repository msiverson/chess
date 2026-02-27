package dataaccess;
import model.AuthData;

public interface AuthDAO {
    // authToken
    public String getAuth() throws DataAccessException;
    public void addAuth(String authToken);
    public void deleteAuth();

    // username
    public String getUser() throws DataAccessException;
}

class MemoryAuthDAO implements AuthDAO {
    // authToken
    public String getAuth() throws DataAccessException {}
    public void addAuth(String authToken) {}
    public void deleteAuth() {}

    // username
    public String getUser() throws DataAccessException {}
}