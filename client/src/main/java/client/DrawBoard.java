package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class DrawBoard {
    public ChessBoard board;

    public DrawBoard(ChessBoard board) {
        this.board = board;
    }

    public String toString(ChessGame.TeamColor playerColor) {
        if (playerColor == ChessGame.TeamColor.WHITE) {
            String returnString = "";
            String rowOfLettersForward = RESET_TEXT_COLOR + "   a   b   c  d   e   f  g   h" + "\n";
            returnString += rowOfLettersForward;
            for (int i = 8; i > 0; i--) {
                returnString += printRow(i, true);
            }
            returnString += rowOfLettersForward;
            return returnString;
        }
        else {
            String returnString = "";
            String rowOfLettersBackwards = RESET_TEXT_COLOR + "   h   g   f  e   d   c  b   a " + "\n";
            returnString += rowOfLettersBackwards;
            for (int i = 1; i <= 8; i++) {
                returnString += printRow(i, false);
            }
            returnString += rowOfLettersBackwards;
            return returnString;
        }
    }

    private String printRow(int row, boolean forward) {
        String returnString = "";
        returnString += RESET_TEXT_COLOR +row + " ";
        if (forward) {
            for (int j = 1; j <= 8; j++) {
                returnString += squareColor(row, j);
                returnString += getObjectInSquare(row, j);
            }
        }
        else {
        for (int j = 8; j > 0; j--) {
            returnString += squareColor(row, j);
            returnString += getObjectInSquare(row, j);
            }
        }

        returnString += RESET_BG_COLOR ;
        returnString += " " + RESET_TEXT_COLOR +row;
        returnString += "\n";
        return returnString;
    }


    private String getObjectInSquare(int row, int col)  {
        board.resetBoard();
        ChessPiece piece = board.getPiece(new ChessPosition(row,col));
        if (board.getPiece(new ChessPosition(row,col)) == null) {
            return EMPTY;
        }


        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            return switch (piece.getPieceType()) {
                case PAWN -> SET_TEXT_COLOR_DARK_GREY   + WHITE_PAWN;
                case ROOK -> SET_TEXT_COLOR_DARK_GREY  + WHITE_ROOK;
                case BISHOP -> SET_TEXT_COLOR_DARK_GREY  + WHITE_BISHOP;
                case KNIGHT -> SET_TEXT_COLOR_DARK_GREY  + WHITE_KNIGHT;
                case KING -> SET_TEXT_COLOR_DARK_GREY + WHITE_KING;
                case QUEEN -> SET_TEXT_COLOR_DARK_GREY  + WHITE_QUEEN;
            };
        }
        return switch (piece.getPieceType()) {
            case PAWN -> SET_TEXT_COLOR_BLACK + BLACK_PAWN;
            case ROOK -> SET_TEXT_COLOR_BLACK + BLACK_ROOK;
            case BISHOP -> SET_TEXT_COLOR_BLACK + BLACK_BISHOP;
            case KNIGHT -> SET_TEXT_COLOR_BLACK + BLACK_KNIGHT;
            case KING -> SET_TEXT_COLOR_BLACK + BLACK_KING;
            case QUEEN -> SET_TEXT_COLOR_BLACK + BLACK_QUEEN;
        };
    }
    private String squareColor(int row, int col) {
        if (isSquareBlack(row,col)) {
            return SET_BG_COLOR_BLUE;
        }
        return SET_BG_COLOR_WHITE;
    }

    private boolean isSquareBlack(int row, int col) {
        return (row % 2 == col % 2);
    }

}
