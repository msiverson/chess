package chess;

import java.util.ArrayList;
import java.util.Collection;

public interface PieceMovesCalculator {
    Collection<ChessMove> pieceMoves (ChessBoard board, ChessPosition position);

    static PieceMovesCalculator typeSelector (ChessPiece.PieceType type) {
        return switch (type) {
            case KING -> new KingMovesCalculator();
            case QUEEN -> new QueenMovesCalculator();
            case BISHOP -> new BishopMovesCalculator();
            case KNIGHT -> new KnightMovesCalculator();
            case ROOK -> new RookMovesCalculator();
            case PAWN -> new PawnMovesCalculator();
        };
    }
}

final class KingMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] kingDirections = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                                    { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }};
        return MajorMovesCalculator.calculateMoves(board, position, 1, kingDirections);
    }
}

final class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] queenDirections = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                                    { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }};
        return MajorMovesCalculator.calculateMoves(board, position, 8, queenDirections);
    }
}

final class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] bishopDirections = {{ 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }};
        return MajorMovesCalculator.calculateMoves(board, position, 8, bishopDirections);
    }
}

final class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] knightDirections = {{ 2, -1 }, { 2, 1 }, { 1, 2 }, { -1, 2 },
                                    { -2, 1 }, { -2, -1 }, { -1, -2 }, { 1, -2 }};
        return MajorMovesCalculator.calculateMoves(board, position, 1, knightDirections);
    }
}

final class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] rookDirections = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }};
        return MajorMovesCalculator.calculateMoves(board, position, 8, rookDirections);
    }
}

final class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        throw new RuntimeException("Not implemented");
    }
}

class MoveCheck {
    static boolean moveChecker(
            Collection<ChessMove> moves, ChessBoard board, ChessPosition piecePosition, ChessPosition positionToCheck) {
        if ((positionToCheck.getColumn() <= 8 && positionToCheck.getColumn() >= 1) &&
                (positionToCheck.getRow() <= 8 && positionToCheck.getRow() >= 1)) {
            if (board.getPiece(positionToCheck) == null) {
                ChessMove newMove = new ChessMove(piecePosition, positionToCheck, null);
                return moves.add(newMove);
            } else if (board.getPiece(piecePosition).getTeamColor() != board.getPiece(positionToCheck).getTeamColor()) {
                ChessMove newMove = new ChessMove(piecePosition, positionToCheck, null);
                return !moves.add(newMove);
            }
        }
        return false;
    }
}

class MajorMovesCalculator {
    static Collection<ChessMove> calculateMoves(ChessBoard board, ChessPosition position, int maxSteps, int[][] direct) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] d : direct) {
            int currR = d[0], currC = d[1];
            for (int step = 1; step <= maxSteps; step++) {
                ChessPosition currMove = new ChessPosition(
                        position.getRow()+(currR*step), position.getColumn()+(currC*step));
                boolean continueDirection = MoveCheck.moveChecker(moves, board, position, currMove);
                if (!continueDirection) break;
            }
        }
        return moves;
    }
}
