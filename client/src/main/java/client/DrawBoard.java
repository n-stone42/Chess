package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;
import dataaccess.DataAccessException;

import static ui.EscapeSequences.*;

public class DrawBoard {
    public ChessBoard board;

    public DrawBoard(ChessBoard board) {
        this.board = board;
    }

    public String toString(ChessGame.TeamColor playerColor) {
        String returnString = "";
        //print row of letters
        for (int i=8; i >0; i--) {
            //print square number
            for (int j=8; j >0; j--) {
                returnString += squareColor(i,j);
                returnString += getObjectInSquare(i,j);
            }
            returnString += RESET_BG_COLOR ;
            returnString += "\n"; // print square number again

        }
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
                case PAWN -> WHITE_PAWN;
                case ROOK -> WHITE_ROOK;
                case BISHOP -> WHITE_BISHOP;
                case KNIGHT -> WHITE_KNIGHT;
                case KING -> WHITE_KING;
                case QUEEN -> WHITE_QUEEN;
            };
        }
        return switch (piece.getPieceType()) {
            case PAWN -> BLACK_PAWN;
            case ROOK -> BLACK_ROOK;
            case BISHOP -> BLACK_BISHOP;
            case KNIGHT -> BLACK_KNIGHT;
            case KING -> BLACK_KING;
            case QUEEN -> BLACK_QUEEN;
        };
    }
    private String squareColor(int row, int col) {
        if (isSquareBlack(row,col)) {
            return SET_BG_COLOR_BLUE;
        }
        return SET_BG_COLOR_WHITE;
    }

    private boolean isSquareBlack(int row, int col) {
        return (row % 2 != col % 2);
    }

}
