package states;

import chess.*;
import client.ClientContext;
import client.ClientState;
import client.ServerFacade;
import ui.ChessBoardUI;
import ui.ChessUI;

import java.util.Scanner;

import static client.ClientState.POST_LOGIN;

public class GameSessionState {

    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessUI ui = new ChessUI();
    private final ChessBoardUI boardUI = new ChessBoardUI();

    private static final int BOARD_SIZE = 8;

    public GameSessionState (ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
    }

    public ClientState run(ClientContext context) {
        ChessGame chessGame = context.getCurrentGame();
        boardUI.draw(chessGame, context.getTeamColor(), null, null, context.getObserving());

        boolean inGame = true;
        while (inGame) {
            System.out.print(ui.prompt(">>> "));
            String command = scanner.nextLine();
            if (!context.getObserving()) {
                switch (command) {
                    case "move" -> //TODO
                    case "highlight" -> //TODO
                    case "leave" -> inGame = false;
                    case "redraw" -> boardUI.draw(
                            chessGame,
                            context.getTeamColor(),
                            null,
                            null,
                            context.getObserving()
                    );
                    default -> System.out.println(ui.warning("unknown command")
                    );
                }
            } else {
                switch (command) {
                    case "leave" -> inGame = false;
                    // redrawing will happen automatically
//                    case "redraw" -> boardUI.draw(chessGame,
//                            context.getTeamColor(),
//                            null,
//                            null,
//                            context.getObserving()
//                    );
                    default -> System.out.println(ui.warning("unknown command")
                    );
                }
            }
        }

        return POST_LOGIN;
    }
}