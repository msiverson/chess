package ui;

/**
 * ANSI UNICODE_ESCAPE Sequences with FULL RGB support
 */
public class FullColorEscapeSequences {

    // Unicode Escape
    static final String UNICODE_ESCAPE = "\u001b";

    // Utility
    final static String ERASE_SCREEN = UNICODE_ESCAPE + "[H" + UNICODE_ESCAPE + "[2J";
    final static String ERASE_LINE = UNICODE_ESCAPE + "[2K";
    final static String RESET = UNICODE_ESCAPE + "[0m";

    // Text Modifiers
    final static String BOLD = UNICODE_ESCAPE + "[1m";
    final static String FAINT = UNICODE_ESCAPE + "[2m";
    final static String ITALIC = UNICODE_ESCAPE + "[3m";
    final static String UNDERLINE = UNICODE_ESCAPE + "[4m";
    final static String BLINK = UNICODE_ESCAPE + "[5m";

    // RGB Color Record
    record RGBValue(int r, int g, int b) {};

    // Text Color
    static final String SET_TEXT_COLOR = UNICODE_ESCAPE + "[38;2;";
    final static String RESET_TEXT_COLOR = UNICODE_ESCAPE + "[39m";

     static String setTextRGB(RGBValue color) {
        return SET_TEXT_COLOR +
                color.r + ";" +
                color.g + ";" +
                color.b + "m";
    }

    // Background Color
    final static String SET_BG_COLOR = UNICODE_ESCAPE + "[48;2;";
    final static String RESET_BG_COLOR = UNICODE_ESCAPE + "[49m";

    static String setBackgroundRGB(RGBValue color) {
        return SET_BG_COLOR +
                color.r + ";" +
                color.g + ";" +
                color.b + "m";
    }

    // Chess Pieces
    final static String KING = " ♚ ";
    final static String QUEEN = " ♛ ";
    final static String BISHOP = " ♝ ";
    final static String KNIGHT = " ♞ ";
    final static String ROOK = " ♜ ";
    final static String PAWN = " ♟ ";
    final static String EMPTY = " \u2003 ";
    final static String FILE_LEFT_SPACE = " \u200A";
    final static String FILE_RIGHT_SPACE = "\u2009 ";
}