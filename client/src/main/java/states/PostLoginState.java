package states;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.ChessGame;
import client.ClientContext;
import client.ClientState;
import Facades.GameWebSocketFacade;
import Facades.ServerFacade;
import dto.game.*;
import ui.ChessUI;
import websocket.commands.ConnectCommand;
import websocket.messages.ErrorMessage;
import websocket.messages.LoadGameMessage;
import websocket.messages.NotificationMessage;

import static client.ClientState.*;

public class PostLoginState {


    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessUI ui = new ChessUI();

    private final GameSessionState gameSessionState;

    private boolean firstRun = true;
    private List<GameInfo> gamesList = new ArrayList<>();

//    public PostLoginState(ServerFacade server, Scanner scanner) {
//        this.server = server;
//        this.scanner = scanner;
//    }
public PostLoginState(ServerFacade server, Scanner scanner, GameSessionState gameSessionState) {
    this.server = server;
    this.scanner = scanner;
    this.gameSessionState = gameSessionState;
}

    public ClientState run(ClientContext context) {
        if (firstRun) {
            help();
            firstRun = false;
        }

        System.out.print(ui.prompt("[LOGGED IN] >>> "));

        String input = scanner.nextLine().trim().toLowerCase();
        try {
            return switch (input) {
                case "help" -> help();
                case "logout" -> logout(context);
                case "create game" -> createGame(context);
                case "list games" -> listGames(context);
                case "play game" -> joinGame(context, false);
                case "observe game" -> joinGame(context, true);
                default -> {
                    System.out.println(ui.warning("Unknown command. Type help to list available commands"));

                    yield POST_LOGIN;
                }
            };
        } catch (Exception e) {
            System.out.println(e.getMessage());

            return POST_LOGIN;
        }
    }

    private ClientState help() {
        System.out.println(ui.info("""
                        Commands:
                        * help (lists available commands)
                        * logout (log user out)
                        * create game (creates new game)
                        * list games (lists created games, their game numbers, and current players)
                        * play game (play created games with game number (can be found with list games)
                        * observe game (watch an ongoing game)
                        """
                )
        );
        return POST_LOGIN;
    }

    private ClientState logout(ClientContext context) throws Exception {
        server.logout(context.getAuthToken());
        firstRun = true;

        return PRE_LOGIN;
    }

    private ClientState createGame(ClientContext context) throws Exception {
        System.out.print(ui.prompt("game name: "));

        String name = scanner.nextLine();

        server.createGame(new CreateGameRequest(context.getAuthToken(), name));

        System.out.println(ui.success("Created game: " + name));

        return POST_LOGIN;
    }

    private ClientState listGames(ClientContext context) throws Exception {
        ListGamesResult result = server.listGames(new ListGamesRequest(context.getAuthToken()));
        gamesList = result.games();

        if (gamesList.isEmpty()) {
            System.out.println(ui.warning(
                    "No games have been created yet. Use create game first"));
        } else {
            int i = 1;
            for (GameInfo game : gamesList) {
                System.out.printf("%d. %s | white:%s | black:%s%n",
                        i++,
                        game.gameName(),
                        game.whiteUsername(),
                        game.blackUsername()
                );
            }
        }
        return POST_LOGIN;
    }

    private ClientState joinGame(ClientContext context, boolean isObserving) throws Exception {
        ListGamesResult listResult = server.listGames(new ListGamesRequest(context.getAuthToken()));
        gamesList = listResult.games();

        if (gamesList.isEmpty()) {
            System.out.println(ui.warning("No games have been created yet. Use create game first"));
            return POST_LOGIN;
        }

        // Check for valid index input
        System.out.print(ui.prompt("game number: "));
        int gameIndex;

        try {
            gameIndex = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println(ui.error("invalid number"));
            return POST_LOGIN;
        }

        if (gameIndex < 1 || gameIndex > gamesList.size()) {
            System.out.println(ui.error("game number out of range"));
            return POST_LOGIN;
        }

        // Get game info from with valid game index
        GameInfo game = gamesList.get(gameIndex - 1);

        // Check if observing
        if (isObserving) {
            context.setObserving(true);
            context.setGameId(game.gameID());
            context.setTeamColor(ChessGame.TeamColor.WHITE);

            // Open WebSocket connection to view game
            openGameSocket(context);

            System.out.println(ui.success("Observing game"));
            return GAME_SESSION;
        }

        // Select team color
        System.out.print(ui.prompt("color (white/black): "));
        ChessGame.TeamColor teamColor;

        try {
            teamColor = ChessGame.TeamColor.valueOf(scanner.nextLine().trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            System.out.println(ui.error("Color must be white or black"));
            return POST_LOGIN;
        }

        boolean whiteTaken = game.whiteUsername() != null;
        boolean blackTaken = game.blackUsername() != null;

        if (teamColor == ChessGame.TeamColor.WHITE && whiteTaken) {
            if (!blackTaken) {
                System.out.println(ui.error("White is already taken. Choose black."));
            } else {
                System.out.println(ui.error("Both colors are already taken."));
                return POST_LOGIN;
            }
            return POST_LOGIN;
        }

        if (teamColor == ChessGame.TeamColor.BLACK && blackTaken) {
            if (!whiteTaken) {
                System.out.println(ui.error("Black is already taken. Choose white."));
            } else {
                System.out.println(ui.error("Both colors are already taken."));
                return POST_LOGIN;
            }
            return POST_LOGIN;
        }

        // Make HTTP request to claim player slot
        server.joinGame(new JoinGameRequest(
                context.getAuthToken(),
                teamColor.name(),
                game.gameID())
        );

        context.setObserving(false);
        context.setGameId(game.gameID());
        context.setTeamColor(teamColor);

        // Open WebSocket connection for gameplay
        openGameSocket(context);

        System.out.println(ui.success("Joined game"));

        return GAME_SESSION;
    }

    private void openGameSocket(ClientContext context) throws Exception {
        GameWebSocketFacade socket = makeGameSocket(context);
        context.setGameSocket(socket);

        socket.connect(server.getServerUrl());
        System.out.println("after connect: " + socket.isConnected());

        socket.sendCommand(new ConnectCommand(
                context.getAuthToken(),
                context.getGameId()
        ));
        System.out.println("after CONNECT cmd: " + socket.isConnected());
    }

    private GameWebSocketFacade makeGameSocket(ClientContext context) {
        return new GameWebSocketFacade(new GameWebSocketFacade.NotificationHandler() {
            @Override
            public void onLoadGame(LoadGameMessage message) {
                gameSessionState.updateGame(context, message.getGame());
            }

            @Override
            public void onNotification(NotificationMessage message) {
                System.out.println(ui.info(message.getNotificationMessage()));
            }

            @Override
            public void onError(ErrorMessage message) {
                System.out.println(ui.error(message.getErrorMessage()));
            }
        });
    }
}
