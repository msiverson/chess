package ui;

import static ui.FullColorEscapeSequences.*;

public class ChessUI {
    
    // UI Theme Colors
    private final FullColorEscapeSequences.RGBValue titleGold = new FullColorEscapeSequences.RGBValue(212, 175, 55);
    private final FullColorEscapeSequences.RGBValue promptBlueText = new FullColorEscapeSequences.RGBValue(120, 170, 255);

    private final FullColorEscapeSequences.RGBValue successGreenText = new FullColorEscapeSequences.RGBValue(100, 200, 120);
    private final FullColorEscapeSequences.RGBValue successGreenBG = new FullColorEscapeSequences.RGBValue(30, 60, 40);
    private final FullColorEscapeSequences.RGBValue warningOrangeText = new FullColorEscapeSequences.RGBValue(255, 190, 90);
    private final FullColorEscapeSequences.RGBValue warningOrangeBG = new FullColorEscapeSequences.RGBValue(70,55,25);
    private final FullColorEscapeSequences.RGBValue errorRedText = new FullColorEscapeSequences.RGBValue(255, 110, 110);
    private final FullColorEscapeSequences.RGBValue errorRedBG = new FullColorEscapeSequences.RGBValue(75,30,30);

    private final FullColorEscapeSequences.RGBValue infoGray = new FullColorEscapeSequences.RGBValue(170, 170, 170);
    private final FullColorEscapeSequences.RGBValue dividerGray = new FullColorEscapeSequences.RGBValue(90, 90, 90);

    // Chess Board Colors
    private final FullColorEscapeSequences.RGBValue lightSquare = new FullColorEscapeSequences.RGBValue(200, 180, 160);
    private final FullColorEscapeSequences.RGBValue darkSquare = new FullColorEscapeSequences.RGBValue(181, 136, 99);
    private final FullColorEscapeSequences.RGBValue highlightSquare = new FullColorEscapeSequences.RGBValue(246, 246, 105);
    private final FullColorEscapeSequences.RGBValue lastMoveSquare = new FullColorEscapeSequences.RGBValue(186, 202, 68);
    private final FullColorEscapeSequences.RGBValue whitePieceColor = new FullColorEscapeSequences.RGBValue(255, 255, 255);
    private final FullColorEscapeSequences.RGBValue blackPieceColor = new FullColorEscapeSequences.RGBValue(30, 30, 30);

    // Utility Functions
    public void clearScreen() {
        System.out.println(ERASE_SCREEN);
        System.out.flush();
    }

    // Text Style Helpers
    public String title(String text) {
        return BOLD + setTextRGB(titleGold) + text + RESET;
    }

    public String prompt(String text) {
        return BOLD + setTextRGB(promptBlueText) + text + RESET;
    }

    public String info(String text) {
        return setTextRGB(infoGray) + text + RESET;
    }

    public String success(String text) {
        return setTextRGB(successGreenText) + setBackgroundRGB(successGreenBG) + text + RESET;
    }

    public String warning(String text) {
        return setTextRGB(warningOrangeText) + setBackgroundRGB(warningOrangeBG) + text + RESET;
    }

    public String error(String text) {
        return setTextRGB(errorRedText) + setBackgroundRGB(errorRedBG) + text + RESET;
    }

    public String divider(int length) {
        return setTextRGB(dividerGray) + "-".repeat(length) + RESET;
    }

    // Chess Piece Helpers
    public String whiteKing() {
        return setTextRGB(whitePieceColor) + KING;
    }
    public String whiteQueen() {
        return setTextRGB(whitePieceColor) + QUEEN;
    }
    public String whiteBishop() {
        return setTextRGB(whitePieceColor) + BISHOP;
    }
    public String whiteKnight() {
        return setTextRGB(whitePieceColor) + KNIGHT;
    }
    public String whiteRook() {
        return setTextRGB(whitePieceColor) + ROOK;
    }
    public String whitePawn() {
        return setTextRGB(whitePieceColor) + PAWN;
    }
    public String blackKing() {
        return setTextRGB(blackPieceColor) + KING;
    }
    public String blackQueen() {
        return setTextRGB(blackPieceColor) + QUEEN;
    }
    public String blackBishop() {
        return setTextRGB(blackPieceColor) + BISHOP;
    }
    public String blackKnight() {
        return setTextRGB(blackPieceColor) + KNIGHT;
    }
    public String blackRook() {
        return setTextRGB(blackPieceColor) + ROOK;
    }
    public String blackPawn() {
        return setTextRGB(blackPieceColor) + PAWN;
    }

    // Chess Board Helpers
    public String lightSquare() {
        return setBackgroundRGB(lightSquare);
    }
    public String darkSquare() {
        return setBackgroundRGB(darkSquare);
    }
    public String highlightSquare() {
        return setBackgroundRGB(highlightSquare);
    }
    public String lastMoveSquare() {
        return setBackgroundRGB(lastMoveSquare);
    }
    public String noPiece() {
        return EMPTY;
    }
    public String resetBg() {
        return RESET_BG_COLOR;
    }
    public String resetText() {
        return RESET_TEXT_COLOR;
    }
}

