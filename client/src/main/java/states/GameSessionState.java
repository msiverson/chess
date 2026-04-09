package states;

import chess.*;
import client.ClientContext;
import client.ClientState;
import facades.ServerFacade;
import ui.ChessBoardUI;
import ui.ChessUI;
import websocket.commands.LeaveCommand;
import websocket.commands.MakeMoveCommand;
import websocket.commands.ResignCommand;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Scanner;

import static client.ClientState.POST_LOGIN;

public class GameSessionState {

    private final ServerFacade server;
    private final Scanner scanner;
    private final ChessUI ui = new ChessUI();
    private final ChessBoardUI boardUI = new ChessBoardUI();

    private ChessMove lastMove = null;
    private ChessMove pendingMove = null;

    public GameSessionState(ServerFacade server, Scanner scanner) {
        this.server = server;
        this.scanner = scanner;
    }

    public ClientState run(ClientContext context) {
        drawBoard(context, null);

        boolean inGame = true;
        while (inGame) {
            System.out.print(ui.prompt(">>> "));
            String input = scanner.nextLine().trim();

            if (input.isBlank()) {
                continue;
            }

            try {
                String[] parts = input.split("\\s+");
                String command = parts[0].toLowerCase();

                if (context.getObserving()) {
                    inGame = handleObserverCommand(context, command, parts);
                } else {
                    inGame = handlePlayerCommand(context, command, parts);
                }

            } catch (Exception e) {
                System.out.println(ui.error("Error: " + e.getMessage()));
            }
        }

        return POST_LOGIN;
    }

    private boolean handlePlayerCommand(ClientContext context, String command, String[] parts) throws IOException {
        switch (command) {
            case "help" -> printPlayerHelp();
            case "redraw" -> drawBoard(context, null);
            case "highlight" -> handleHighlight(context, parts);
            case "move" -> handleMove(context, parts);
            case "resign" -> handleResign(context);
            case "leave" -> {
                handleLeave(context);
                return false;
            }
            default -> System.out.println(ui.warning("unknown command"));
        }
        return true;
    }

    private boolean handleObserverCommand(ClientContext context, String command, String[] parts) throws IOException {
        switch (command) {
            case "help" -> printObserverHelp();
            case "redraw" -> drawBoard(context, null);
            case "highlight" -> handleHighlight(context, parts);
            case "leave" -> {
                handleLeave(context);
                return false;
            }
            default -> System.out.println(ui.warning("unknown command"));
        }
        return true;
    }

    private void drawBoard(ClientContext context, Collection<ChessMove> highlights) {
        ChessGame game = context.getCurrentGame();

        if (game == null) {
            System.out.println(ui.warning("Game is still loading"));
            return;
        }

        ChessGame.TeamColor perspective =
                context.getObserving()
                        ? ChessGame.TeamColor.WHITE
                        : context.getTeamColor();

        boardUI.draw(
                game,
                perspective,
                highlights,
                lastMove,
                context.getObserving()
        );
    }

    private void handleHighlight(ClientContext context, String[] parts) {
        if (parts.length != 2) {
            throw new IllegalArgumentException("usage: highlight e2");
        }

        ChessGame game = context.getCurrentGame();
        if (game == null) {
            throw new IllegalArgumentException("game not loaded");
        }

        ChessPosition start = parsePosition(parts[1]);
        Collection<ChessMove> moves = game.validMoves(start);

        if (moves == null) {
            throw new IllegalArgumentException("no piece at that square");
        }

        Collection<ChessMove> highlighted = new ArrayList<>(moves);
        highlighted.add(new ChessMove(start, start, null)); // optionally include origin square

        drawBoard(context, highlighted);
    }

    private void handleMove(ClientContext context, String[] parts) throws IOException {
        if (parts.length != 3 && parts.length != 4) {
            throw new IllegalArgumentException("usage: move e2 e4 [promotion]");
        }

        ChessPosition from = parsePosition(parts[1]);
        ChessPosition to = parsePosition(parts[2]);
        ChessPiece.PieceType promotion = null;

        if (parts.length == 4) {
            promotion = parsePromotion(parts[3]);
        }

        ChessMove move = new ChessMove(from, to, promotion);
        pendingMove = move;

        context.getGameSocket().sendCommand(
                new MakeMoveCommand(
                        context.getAuthToken(),
                        context.getGameId(),
                        move
                )
        );
    }

    private void handleResign(ClientContext context) throws IOException {
        System.out.print(ui.warning("Type yes to confirm resignation: "));
        String confirm = scanner.nextLine().trim();

        if (confirm.equalsIgnoreCase("yes")) {
            context.getGameSocket().sendCommand(
                    new ResignCommand(
                            context.getAuthToken(),
                            context.getGameId()
                    )
            );
        }
    }

    private void handleLeave(ClientContext context) throws IOException {
        if (context.getGameSocket() != null) {
            context.getGameSocket().sendCommand(
                    new LeaveCommand(
                            context.getAuthToken(),
                            context.getGameId()
                    )
            );
            context.getGameSocket().close();
            context.setGameSocket(null);
        }

        context.setCurrentGame(null);
        context.setGameId(null);
        context.setTeamColor(null);
        context.setObserving(false);
    }

    private ChessPosition parsePosition(String input) {
        if (input == null || input.length() != 2) {
            throw new IllegalArgumentException("invalid square");
        }

        char file = Character.toLowerCase(input.charAt(0));
        char rank = input.charAt(1);

        if (file < 'a' || file > 'h' || rank < '1' || rank > '8') {
            throw new IllegalArgumentException("invalid square");
        }

        int column = file - 'a' + 1;
        int row = rank - '0';

        return new ChessPosition(row, column);
    }

    private ChessPiece.PieceType parsePromotion(String input) {
        return switch (input.toLowerCase()) {
            case "queen" -> ChessPiece.PieceType.QUEEN;
            case "rook" -> ChessPiece.PieceType.ROOK;
            case "bishop" -> ChessPiece.PieceType.BISHOP;
            case "knight" -> ChessPiece.PieceType.KNIGHT;
            default -> throw new IllegalArgumentException("invalid promotion piece");
        };
    }

    private void printPlayerHelp() {
        System.out.println(ui.info("""
                Commands:
                * help
                * redraw
                * highlight <square>
                * move <from> <to> [promotion]
                * resign
                * leave
                """));
    }

    private void printObserverHelp() {
        System.out.println(ui.info("""
                Commands:
                * help
                * redraw
                * highlight <square>
                * leave
                """));
    }

    public void updateGame(ClientContext context, ChessGame game) {
        context.setCurrentGame(game);

        if (pendingMove != null) {
            lastMove = pendingMove;
            pendingMove = null;
        }

        System.out.println();
        drawBoard(context, null);
    }

    public void rejectPendingMove() {
        pendingMove = null;
    }
}