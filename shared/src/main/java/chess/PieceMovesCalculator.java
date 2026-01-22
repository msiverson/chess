package chess;

import java.sql.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

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

    }
}

final class QueenMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

    }
}

final class BishopMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

    }
}

final class KnightMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

    }
}

final class RookMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

    }
}

final class PawnMovesCalculator implements PieceMovesCalculator {
    @Override
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition position) {

    }
}
