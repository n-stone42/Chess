package client;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import org.junit.jupiter.api.Test;

public class testPrintBoard {

    DrawBoard drawBoard = new DrawBoard(new ChessBoard());
    @Test
    public void testPrintBoard() {
        System.out.print(drawBoard.toString(ChessGame.TeamColor.WHITE));
    }
}
