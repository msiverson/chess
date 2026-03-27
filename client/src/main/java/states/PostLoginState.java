package states;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import chess.ChessGame;
import client.ClientContext;
import client.ClientState;
import client.ServerFacade;
import dto.game.*;
import ui.ChessUI;

import static client.ClientState.*;

public class PostLoginState {


    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessUI ui = new ChessUI();

    private boolean firstRun = true;
    private List<GameInfo> gamesList = new ArrayList<>();

    public PostLoginState(ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
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
                    ui.clearScreen();
                    System.out.println(ui.warning("Unknown command. Type help to list available commands"));

                    yield POST_LOGIN;
                }
            };
        } catch (Exception e) {
            ui.clearScreen();
            System.out.println(e.getMessage());

            return POST_LOGIN;
        }
    }

    private ClientState help() {
        ui.clearScreen();
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

        CreateGameResult result = server.createGame(new CreateGameRequest(context.getAuthToken(), name));

        //context.setGameId(result.gameID());

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
        context.setObserving(isObserving);

        ListGamesResult listResult = server.listGames(new ListGamesRequest(context.getAuthToken()));
        gamesList = listResult.games();

        if (gamesList.isEmpty()) {
            System.out.println(ui.warning("No games have been created yet. Use create game first"));
            return POST_LOGIN;
        }

        // Check for valid index input
        int index;
        GameInfo game;
        while (true) {
            System.out.print(ui.prompt("game number: "));

            try {
                index = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println(ui.error("invalid number"));
                continue;
            }

            if (index < 1 || index > gamesList.size()) {
                System.out.println(
                        ui.error("game number out of range")
                );
                continue;
            }

            // Get selected game
            game = gamesList.get(index - 1);

            // Check if client is observing game
            if (isObserving) {
                context.setObserving(true);
                context.setGameId(game.gameID());
                return GAME_SESSION;
            }
            // Check if client has joined a game before
            else if (context.getTeamColor() == null) {
                break;
            }
            // Check if client is rejoining a game
            else if (context.getGameId() != null && game.gameID() == context.getGameId()) {
                    context.setObserving(false);
                    context.setGameId(game.gameID());
                    return GAME_SESSION;
            }
            // Can't join a game if a part of another game and not observing
            else {
                System.out.println(ui.warning("Can't join a game if a part of another game and not observing"));
                return POST_LOGIN;
            }
        }

        // Check for valid player color input
        ChessGame.TeamColor teamColor;
        while (true) {
            System.out.print(ui.prompt("color (white/black): "));

            String colorInput = scanner.nextLine().trim().toUpperCase();

            try {
                teamColor = ChessGame.TeamColor.valueOf(colorInput);
            } catch (IllegalArgumentException e) {
                System.out.println(ui.error("Color must be white or black"));
                continue;
            }

            if (teamColor == ChessGame.TeamColor.WHITE && game.whiteUsername() != null ||
                    teamColor == ChessGame.TeamColor.BLACK && game.blackUsername() != null) {
                System.out.println(ui.error("Color already taken. Choose another"));
                continue;
            }
            break;
        }

        // Joining game for first time
        server.joinGame(new JoinGameRequest(
                context.getAuthToken(),
                teamColor.name(),
                game.gameID())
        );

        // Set client context fields for this game
        context.setObserving(false);
        context.setGameId(game.gameID());
        context.setTeamColor(teamColor);

        System.out.println(ui.success("Joined game"));

        return GAME_SESSION;
    }
}
