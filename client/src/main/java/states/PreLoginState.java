package states;

import java.util.Scanner;

import client.ClientContext;
import client.ClientState;
import client.ServerFacade;
import dto.session.LoginRequest;
import dto.session.LoginResult;
import dto.user.RegisterRequest;
import dto.user.RegisterResult;
import ui.ChessUI;

import static client.ClientState.PRE_LOGIN;
import static client.ClientState.QUIT;

public class PreLoginState {

    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessUI ui = new ChessUI();

    private boolean firstRun = true;

    public PreLoginState(ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
    }

    public ClientState run(ClientContext context) {
        if (firstRun) {
            help();
            firstRun = false;
        }

        System.out.print(ui.prompt("[LOGGED OUT] >>> "));

        String input = scanner.nextLine().trim().toLowerCase();
        try {
            return switch (input) {
                case "help" -> help();
                case "login" -> login(context);
                case "register" -> register(context);
                case "quit" -> QUIT;
                default -> {
                    ui.clearScreen();
                    System.out.println(ui.warning("Unknown command. Type help to list available commands"));
                    yield PRE_LOGIN;
                }
            };
        } catch (Exception e) {
            ui.clearScreen();
            System.out.println(ui.error(e.getMessage()));
            return PRE_LOGIN;
        }
    }

    private ClientState help() {
        ui.clearScreen();
        System.out.println(ui.info(
                        """
                                Commands:
                                * help (lists available commands)
                                * login (login a registered user)
                                * register (create an account and login)
                                * quit (close the program)
                                """
                )
        );
        return PRE_LOGIN;
    }

    private ClientState login(ClientContext context) throws Exception {

        System.out.print(ui.prompt("username: "));
        String username = scanner.nextLine();

        System.out.print(ui.prompt("password: "));
        String password = scanner.nextLine();

        LoginResult result = server.login(new LoginRequest(username, password));

        ui.clearScreen();
        System.out.println(ui.success("Welcome back " + result.username()));

        context.setAuthToken(result.authToken());

        return ClientState.POST_LOGIN;
    }

    private ClientState register(ClientContext context) throws Exception {

        System.out.print(ui.prompt("username: "));
        String username = scanner.nextLine().trim();

        System.out.print(ui.prompt("password: "));
        String password = scanner.nextLine();

        System.out.print(ui.prompt("email: "));
        String email = scanner.nextLine().trim();

        if (username.isBlank() || password.isBlank() || email.isBlank()) {
            System.out.println(ui.error("username, password, and email are required"));
            return ClientState.PRE_LOGIN;
        }

        try {
            RegisterResult result = server.register(
                    new RegisterRequest(username, password, email)
            );

            context.setAuthToken(result.authToken());

            ui.clearScreen();
            System.out.println(ui.success("New user " + result.username() + " registered and logged in"));

            return ClientState.POST_LOGIN;

        } catch (RuntimeException e) {
            String message = e.getMessage();

            if (message != null) {
                String lower = message.toLowerCase();

                if (lower.contains("already taken") || lower.contains("already exists")) {
                    System.out.println(ui.error("username is already taken"));
                    return ClientState.PRE_LOGIN;
                }

                if (lower.contains("email")) {
                    System.out.println(ui.error("email is already in use"));
                    return ClientState.PRE_LOGIN;
                }

                if (lower.contains("internal error")) {
                    System.out.println(ui.error("registration failed; username or email may already be in use"));
                    return ClientState.PRE_LOGIN;
                }
            }

            System.out.println(ui.error(message != null ? message : "registration failed"));
            return ClientState.PRE_LOGIN;
        }
    }
}