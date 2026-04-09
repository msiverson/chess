package client;

import java.util.Scanner;

import facades.ServerFacade;
import states.*;

public class Client {

    private final PreLoginState preLoginState;
    private final PostLoginState postLoginState;
    private final GameSessionState gameSessionState;

    private final ClientContext context;

    public Client(String serverUrl) {

        ServerFacade server = new ServerFacade(serverUrl);
        Scanner scanner = new Scanner(System.in);

        gameSessionState = new GameSessionState(server, scanner);
        preLoginState = new PreLoginState(server, scanner);
        postLoginState = new PostLoginState(server, scanner, gameSessionState);

        context = new ClientContext();
    }

    public void run() {
        ClientState state = ClientState.PRE_LOGIN;

        while (state != ClientState.QUIT) {
            switch (state) {
                case PRE_LOGIN -> state = preLoginState.run(context);
                case POST_LOGIN -> state = postLoginState.run(context);
                case GAME_SESSION -> state = gameSessionState.run(context);
                case null, default -> throw new RuntimeException("Error in client state machine");
            }
        }
    }
}