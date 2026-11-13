package client;

import chess.*;
import exception.DataAccessException;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);
        try {
            ChessClient client = new ChessClient("http://localhost:8080");
            client.read();

        }
        catch (DataAccessException exception) {
            System.out.print("can't start server, RIP" + exception);
        }
    }
}
