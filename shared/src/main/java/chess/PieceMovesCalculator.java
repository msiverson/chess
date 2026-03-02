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
        return CalculateMoves.calculateMoves(board, position, 1, kingDirections);
    }
}

final class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] queenDirections = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 },
                                    { 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }};
        return CalculateMoves.calculateMoves(board, position, 8, queenDirections);
    }
}

final class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] bishopDirections = {{ 1, 1 }, { 1, -1 }, { -1, 1 }, { -1, -1 }};
        return CalculateMoves.calculateMoves(board, position, 8, bishopDirections);
    }
}

final class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] knightDirections = {{ 2, -1 }, { 2, 1 }, { 1, 2 }, { -1, 2 },
                                    { -2, 1 }, { -2, -1 }, { -1, -2 }, { 1, -2 }};
        return CalculateMoves.calculateMoves(board, position, 1, knightDirections);
    }
}

final class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] rookDirections = {{ 1, 0 }, { -1, 0 }, { 0, 1 }, { 0, -1 }};
        return CalculateMoves.calculateMoves(board, position, 8, rookDirections);
    }
}

final class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {
        int[][] whitePawnDirections = {{1,0}};
        int[][] whitePawnCapture = {{1,-1}, {1,1}};
        int[][] blackPawnDirections = {{-1,0}};
        int[][] blackPawnCapture = {{-1,-1}, {-1,1}};
        Collection<ChessMove> moves = new ArrayList<>();
        ChessPiece currPiece = board.getPiece(position);
        if (currPiece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            if (position.getRow() == 2) {
                moves = CalculateMoves.calculateMoves(board, position, 2, whitePawnDirections);
            } else {
                moves = CalculateMoves.calculateMoves(board, position, 1, whitePawnDirections);
            }
            moves.addAll(CalculateMoves.calculateMoves(board, position, 1, whitePawnCapture));
        } else if (currPiece.getTeamColor() == ChessGame.TeamColor.BLACK) {
            if (position.getRow() == 7) {
                moves = CalculateMoves.calculateMoves(board, position, 2, blackPawnDirections);
            } else {
                moves = CalculateMoves.calculateMoves(board, position, 1, blackPawnDirections);
            }
            moves.addAll(CalculateMoves.calculateMoves(board, position, 1, blackPawnCapture));
        }
        return moves;
    }
}
class PawnMoveCheck {

    static boolean pawnMoveChecker(
            Collection<ChessMove> moves,
            ChessBoard board,
            ChessPosition piecePosition,
            ChessPosition positionToCheck) {

        // 1️⃣ Bounds check first
        if (!isInBounds(positionToCheck)) {
            return false;
        }

        ChessPiece movingPiece = board.getPiece(piecePosition);
        ChessPiece targetPiece = board.getPiece(positionToCheck);

        boolean sameColumn = piecePosition.getColumn() == positionToCheck.getColumn();
        boolean isPromotionRow = positionToCheck.getRow() == 1 || positionToCheck.getRow() == 8;

        // 2️⃣ Forward move (no capture)
        if (sameColumn) {
            if (targetPiece != null) return false;

            return addPawnMove(moves, piecePosition, positionToCheck, isPromotionRow);
        }

        // 3️⃣ Diagonal capture
        if (targetPiece == null) return false;

        if (movingPiece.getTeamColor() == targetPiece.getTeamColor()) {
            return false;
        }

        return addPawnMove(moves, piecePosition, positionToCheck, isPromotionRow);
    }

    static boolean isInBounds(ChessPosition pos) {
        return pos.getRow() >= 1 && pos.getRow() <= 8 &&
                pos.getColumn() >= 1 && pos.getColumn() <= 8;
    }

    private static boolean addPawnMove(
            Collection<ChessMove> moves,
            ChessPosition from,
            ChessPosition to,
            boolean isPromotion) {

        if (!isPromotion) {
            return moves.add(new ChessMove(from, to, null));
        }

        ChessPiece.PieceType[] promotions = {
                ChessPiece.PieceType.QUEEN,
                ChessPiece.PieceType.ROOK,
                ChessPiece.PieceType.BISHOP,
                ChessPiece.PieceType.KNIGHT
        };

        for (ChessPiece.PieceType type : promotions) {
            moves.add(new ChessMove(from, to, type));
        }

        return true;
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

class CalculateMoves {
    static Collection<ChessMove> calculateMoves(
            ChessBoard board, ChessPosition position, int maxSteps, int[][] direct) {
        Collection<ChessMove> moves = new ArrayList<>();

        for (int[] d : direct) {
            int currR = d[0], currC = d[1];
            for (int step = 1; step <= maxSteps; step++) {
                ChessPosition currMove = new ChessPosition(
                        position.getRow()+(currR*step), position.getColumn()+(currC*step));
                boolean continueDirection;
                if (board.getPiece(position).getPieceType() == ChessPiece.PieceType.PAWN) {
                    continueDirection = PawnMoveCheck.pawnMoveChecker(moves, board, position, currMove);
                } else {
                    continueDirection = MoveCheck.moveChecker(moves, board, position, currMove);
                }
                if (!continueDirection) {
                    break;
                }
            }
        }
        return moves;
    }
}
