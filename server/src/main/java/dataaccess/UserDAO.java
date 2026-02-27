package dataaccess;
import model.UserData;

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

class MemoryUserDAO implements UserDAO {

    @Override
    public String getUser() throws DataAccessException {
        return "";
    }

    @Override
    public void addUser(String username) {

    }

    @Override
    public String getPass() throws DataAccessException {
        return "";
    }

    @Override
    public void addPass(String password) {

    }

    @Override
    public String getEmail() throws DataAccessException {
        return "";
    }

    @Override
    public void addEmail(String email) {

    }
}