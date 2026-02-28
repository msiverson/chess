package dataaccess.memory;

import java.util.HashMap;
import java.util.Map;

import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import model.UserData;

public class MemoryUserDAO implements UserDAO {
    private final Map<String, UserData> userDataDB = new HashMap<>();

    // username
    @Override
    public String getUser() throws DataAccessException {
        return "";
    }

    @Override
    public void addUser(String username) {

    }

    // password
    @Override
    public String getPass() throws DataAccessException {
        return "";
    }

    @Override
    public void addPass(String password) {

    }

    // email
    @Override
    public String getEmail() throws DataAccessException {
        return "";
    }

    @Override
    public void addEmail(String email) {

    }
}