package chess;

import java.util.Collection;
import java.util.Objects;

/**
 * For a class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor teamTurn;
    private ChessBoard gameBoard = new ChessBoard();

    public ChessGame() {
        gameBoard.resetBoard();
        teamTurn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return teamTurn;
    }

    /**
     * Set's which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        teamTurn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets a valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece currPiece = gameBoard.getPiece(startPosition);
        if (currPiece == null) {
            return null;
        } else {
            Collection<ChessMove> movesToCheck = currPiece.pieceMoves(gameBoard, startPosition);
            ChessBoard testBoard = new ChessBoard();
            for (int i = 0; i < movesToCheck.size(); i++) {
                testBoard = gameBoard;

            }

                // King Check Checker
//                // Update a test board with each of the moves
//                    // For each move, run "isInCheck" method
//                        // If true, discard move
//                        // If false, do nothing
        }
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in a chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        if (gameBoard.getPiece(move.getStartPosition()).getTeamColor() != teamTurn) {

        }
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;

        FindKing:
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition currPosition = new ChessPosition(i, j);
                if (gameBoard.getPiece(currPosition).getPieceType() == ChessPiece.PieceType.KING &&
                        gameBoard.getPiece(currPosition).getTeamColor() == teamColor) {
                    kingPosition = currPosition;
                    break FindKing;
                }
            }
        }


        int[][] cardinalDirections = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }};
        ChessPiece.PieceType[] cardinalTypes = {
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK
        };
        int[][] blackDiagonalDirections = {{1,-1}, {1,1}};
        int[][] whiteDiagonalDirections = {{-1,-1}, {-1,1}};
        ChessPiece.PieceType[] diagonalTypes = {
                ChessPiece.PieceType.KING,
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.PAWN
        };
        int[][] knightDirections = {{ 2, -1 }, { 2, 1 }, { 1, 2 }, { -1, 2 },
                { -2, 1 }, { -2, -1 }, { -1, -2 }, { 1, -2 }};
        ChessPiece.PieceType[] knightType = {
                ChessPiece.PieceType.KNIGHT
        };

        int[][][] directionSet = {
            cardinalDirections,
            blackDiagonalDirections,
            whiteDiagonalDirections,
            knightDirections
        };

        ChessPiece.PieceType[][] typeSet = {
            cardinalTypes,
            diagonalTypes,
            knightType
        };

        for (int i = 0; i < 4; i++) {
            if (typeSet[i][0] == ChessPiece.PieceType.KNIGHT) {
                if (checkFromDirection(kingPosition, directionSet[i], 1, typeSet[i])) {
                    return true;
                }
            } else {
                if (checkFromDirection(kingPosition, directionSet[i], 8, typeSet[i])) {
                    return true;
                }
            }
        }
        return false;



    }

    private boolean checkFromDirection(ChessPosition kingPosition, int[][]directions, int maxSteps,
                                       ChessPiece.PieceType[] pieceTypes) {
        ChessPiece kingPiece = gameBoard.getPiece(kingPosition);
        for (int[] d : directions) {
            int currR = d[0], currC = d[1];
            for (int step = 1; step <= maxSteps; step++) {
                ChessPosition currPosition = new ChessPosition(
                        kingPosition.getRow()+(currR*step), kingPosition.getColumn()+(currC*step));
                if ((currPosition.getColumn() <= 8 && currPosition.getColumn() >= 1) &&
                        (currPosition.getRow() <= 8 && currPosition.getRow() >= 1)) {
                    ChessPiece currPiece = gameBoard.getPiece(currPosition);
                    if (currPiece != null) {
                        if (currPiece.getTeamColor() != kingPiece.getTeamColor()) {
                            for (ChessPiece.PieceType currType : pieceTypes) {
                                if (currType == ChessPiece.PieceType.PAWN && step == 1) {
                                    if (kingPiece.getTeamColor() == TeamColor.WHITE && currR > 0) {
                                        return true;
                                    } else if (kingPiece.getTeamColor() == TeamColor.BLACK && currR < 0) {
                                        return true;
                                    }
                                } else if (currPiece.getPieceType() == currType &&
                                        currType != ChessPiece.PieceType.PAWN) {
                                    return true;
                                }
                            }
                        }
                    }
                }
            }
        }
        return false;

    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard with a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        gameBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return gameBoard;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return teamTurn == chessGame.teamTurn && Objects.equals(gameBoard, chessGame.gameBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(teamTurn, gameBoard);
    }

    @Override
    public String toString() {
        return "ChessGame{" +
                "teamTurn=" + teamTurn +
                ", gameBoard=" + gameBoard +
                '}';
    }
}
