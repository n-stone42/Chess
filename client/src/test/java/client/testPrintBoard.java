package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import org.junit.jupiter.api.Test;

public class testPrintBoard {

    DrawBoard drawBoard = new DrawBoard();
    ChessBoard board = new ChessBoard();
    @Test
    public void testPrintBoardWhite() {
        board.resetBoard();
        System.out.print(drawBoard.toString(board, ChessGame.TeamColor.WHITE));
    }
    @Test
    public void testPrintBoardBlack() {
        board.resetBoard();
        System.out.print(drawBoard.toString(board, ChessGame.TeamColor.BLACK));
    }
}
