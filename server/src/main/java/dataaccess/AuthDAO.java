package dataaccess;

public interface AuthDAO {
    // authToken
    public String getAuth() throws DataAccessException;
    public void addAuth(String authToken);
    public void deleteAuth();

    // username
    public String getUser() throws DataAccessException;
}