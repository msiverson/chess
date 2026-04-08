package websocket.service;

import dataaccess.AuthDAO;
import dataaccess.GameDAO;


public class GameplayService {

    private final AuthDAO authDAO;
    private final GameDAO gameDAO;

    public GameplayService (AuthDAO authDAO, GameDAO gameDAO) {
        this.authDAO = authDAO;
        this.gameDAO = gameDAO;
    }


}
