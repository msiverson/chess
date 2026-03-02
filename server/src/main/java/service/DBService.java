package service;

import dataaccess.*;
import service.exceptions.ServiceException;

public class DBService {

    private final UserDAO userDAO;
    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public DBService(UserDAO userDAO,
                     AuthDAO authDAO,
                     GameDAO gameDAO) {
        this.userDAO = userDAO;
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }

    public void clear() {
        try {
            userDAO.clear();
            authDAO.clear();
            gameDAO.clear();
        } catch (DataAccessException e) {
            throw new ServiceException("Server Error");
        }
    }
}
