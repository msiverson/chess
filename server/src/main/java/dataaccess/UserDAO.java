package dataaccess;

public interface UserDAO {
    // username
    public String getUser() throws DataAccessException;
    public void addUser(String username);

    // password
    public String getPass() throws DataAccessException;
    public void addPass(String password);

    // email
    public String getEmail() throws DataAccessException;
    public void addEmail(String email);
}