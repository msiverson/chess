package ui;

import chess.ChessGame;
import chess.ChessMove;
import chess.ChessPiece;
import chess.ChessPosition;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ChessBoardUI {
    private final ChessUI ui = new ChessUI();

    private static final int BOARD_SIZE = 8;

    public void draw(
            ChessGame game,
            ChessGame.TeamColor perspective,
            Collection<ChessMove> highlightMoves,
            ChessMove lastMove,
            boolean isObserving
    ) {
        ui.clearScreen();

        System.out.println(ui.title("CHESS"));
        System.out.println(ui.info("Perspective: " + perspective));
        System.out.println();

        drawBoard(game, perspective, highlightMoves, lastMove);

        if (isObserving) {
            drawControlsObserving();
        } else {
            drawControlsPlaying();
        }

    }

    private void drawBoard(
            ChessGame game,
            ChessGame.TeamColor perspective,
            Collection<ChessMove> highlightMoves,
            ChessMove lastMove
    ) {
        Set<ChessPosition> highlightSquares = new HashSet<>();

        if (highlightMoves != null) {
            for (ChessMove move : highlightMoves) {
                highlightSquares.add(
                        move.getEndPosition()
                );
            }
        }

        printFiles(perspective);
        for (int r = 0; r < BOARD_SIZE; r++) {
            int row = perspective ==
                    ChessGame.TeamColor.WHITE
                    ? BOARD_SIZE - r
                    : r + 1;

            System.out.print(ui.info(" " + row + " "));

            for (int c = 0; c < BOARD_SIZE; c++) {
                int col = perspective ==
                        ChessGame.TeamColor.WHITE
                        ? c + 1
                        : BOARD_SIZE - c;

                ChessPosition pos = new ChessPosition(row, col);

                boolean light = (row + col) % 2 == 0;
                boolean highlight = highlightSquares.contains(pos);
                boolean last = isLastMoveSquare(pos, lastMove);

                drawSquare(game, pos, light, highlight, last);
            }

            System.out.println(ui.info(" " + row));
        }

        printFiles(perspective);

        System.out.println();
    }

    private void drawSquare(
            ChessGame game,
            ChessPosition pos,
            boolean light,
            boolean highlight,
            boolean lastMove
    ) {
        String bg;

        if (lastMove) {
            bg = ui.lastMoveSquare();
        } else if (highlight) {
            bg = ui.highlightSquare();
        } else if (light) {
            bg = ui.lightSquare();
        } else {
            bg = ui.darkSquare();
        }

        ChessPiece piece = game.getBoard().getPiece(pos);

        String content;

        if (piece == null) {
            content = ui.noPiece();
        } else {
            content = pieceString(piece);
        }
        System.out.print(bg + content + ui.resetBg() + ui.resetText());
    }

    private boolean isLastMoveSquare(ChessPosition pos, ChessMove move) {
        if (move == null) {
            return false;
        }

        return pos.equals(move.getStartPosition()) || pos.equals(move.getEndPosition());
    }

    private String pieceString(ChessPiece piece) {
        return switch (piece.getPieceType()) {
            case KING -> piece.getTeamColor() ==
                    ChessGame.TeamColor.WHITE ?
                    ui.whiteKing() :
                    ui.blackKing();
            case QUEEN -> piece.getTeamColor() ==
                    ChessGame.TeamColor.WHITE ?
                    ui.whiteQueen() :
                    ui.blackQueen();
            case ROOK -> piece.getTeamColor() ==
                    ChessGame.TeamColor.WHITE ?
                    ui.whiteRook() :
                    ui.blackRook();
            case BISHOP -> piece.getTeamColor() ==
                    ChessGame.TeamColor.WHITE ?
                    ui.whiteBishop() :
                    ui.blackBishop();
            case KNIGHT -> piece.getTeamColor() ==
                    ChessGame.TeamColor.WHITE ?
                    ui.whiteKnight() :
                    ui.blackKnight();
            case PAWN -> piece.getTeamColor() ==
                    ChessGame.TeamColor.WHITE ?
                    ui.whitePawn() :
                    ui.blackPawn();
        };
    }

    private void printFiles(ChessGame.TeamColor perspective) {
        System.out.print("   ");

        for (int i = 1; i <= BOARD_SIZE; i++) {
            char file = perspective ==
                    ChessGame.TeamColor.WHITE
                    ? (char) ('a' + i - 1)
                    : (char) ('h' - i + 1);

            System.out.print(ui.info(" \u200A" + file + "\u2009 "));
        }

        System.out.println();
    }

    private void drawControlsPlaying() {
        System.out.println(ui.divider(40));
        System.out.println(ui.info("Commands:"));
        System.out.println(ui.info("move") + "  make move (e2 e4)");
        System.out.println(ui.info("highlight") + "  show legal moves");
        System.out.println(ui.info("redraw") + "  refresh board");
        System.out.println(ui.info("leave") + "  exit game");
        System.out.println(ui.divider(40));
    }

    private void drawControlsObserving() {
        System.out.println(ui.divider(40));
        System.out.println(ui.info("Commands:"));
        System.out.println(ui.info("redraw") + "  refresh board");
        System.out.println(ui.info("leave") + "  exit game");
        System.out.println(ui.divider(40));
    }
}
