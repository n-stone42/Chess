package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {

    private final ChessGame.TeamColor pieceColor;
    private final PieceType type;

//    @Override
//    public boolean equals(Object o) {
//        return true;
//    }

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        // takes in the board and the position of the piece
        // returns the moves

        ChessPiece the_piece = board.getPiece(myPosition);
        if (the_piece.getPieceType() == PieceType.BISHOP){
//            return List.of(new ChessMove(new ChessPosition(5,4), new ChessPosition(1,8), null));
            Collection<ChessMove> List_1 = move_itter(board, myPosition, 1,1);
            Collection<ChessMove> List_2 = move_itter(board, myPosition, -1,1);
            Collection<ChessMove> List_3 = move_itter(board, myPosition, -1,-1);
            Collection<ChessMove> List_4 = move_itter(board, myPosition, 1,-1);
            List_1.addAll(List_2);
            List_1.addAll(List_3);
            List_1.addAll(List_4);
            return List_1;
        }

        else if (the_piece.getPieceType() == PieceType.KING){

        }

        else if (the_piece.getPieceType() == PieceType.KNIGHT) {
            //do knight stuff
        }

        else if (the_piece.getPieceType() == PieceType.PAWN) {
            // do pawn stuff
        }

        else if (the_piece.getPieceType() == PieceType.QUEEN) {
            // do queen stuff
        }

        else if (the_piece.getPieceType() == PieceType.ROOK) {
            // do rook stuff
        }

        else {
            return null;} // there was
        return null;
    }

    private boolean is_empty(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition) == null) {
//            System.out.println("returning is empty");
            return true;
        }
        else {
//            System.out.println("returning is NOT empty");
            return false;
        }
    }

    private boolean in_bounds(int row, int col) {
        if ( 0 < row && row < 9 && 0 < col && col < 9){
//            System.out.printf("row %d col %d is in bounds   ", row, col);
            return true;
        }
        else {
//            System.out.printf("row %d col %d is in OUT of bounds   ", row, col);
            return false;
        }
    }

    private boolean can_take(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {

//        System.out.printf("Old Piece type %s   New Piece type%s",board.getPiece(myPosition).getTeamColor(), board.getPiece(newPosition).getTeamColor());
        if (board.getPiece(myPosition).getTeamColor() == board.getPiece(newPosition).getTeamColor()) {
            System.out.printf("can't take %d, %d", newPosition.getRow(), newPosition.getColumn());
            System.out.println();
            return false; //can't take, same color
        }
        System.out.printf("CAN take %d, %d", newPosition.getRow(), newPosition.getColumn());
        System.out.println();
        return true; //different colors, can take
    }
    private List<ChessMove> move_itter(ChessBoard board, ChessPosition myPosition, int row_scalar, int col_scalar) {

        List<ChessMove> moves = new ArrayList<>();
//        System.out.printf("row scalar is %d   ", row_scalar);
//        System.out.printf("col scalar is %d   ", col_scalar);
        int new_row = myPosition.getRow() + row_scalar;
        int new_col = myPosition.getColumn() + col_scalar;
        while (in_bounds(new_row, new_col)) {
//            System.out.printf("Finding new move. Row %d col %d  ",new_row, new_col);
            ChessPosition new_pos = new ChessPosition (new_row, new_col);

            if (is_empty(board, new_pos)) {
                ChessMove new_move = new ChessMove(myPosition, new_pos, null);
                moves.add(new_move);
//                System.out.printf("adding the following move %d %d   ", new_row, new_col);
            }
            else if (can_take(board, myPosition, new_pos)) { // next piece can be taken, add it to list then return
                ChessMove new_move = new ChessMove(myPosition, new_pos, null);
                moves.add(new_move);
                return moves;
            }
            else { //we have hit another piece that can't be taken, return the list
                return moves;
            }

            new_row = new_row + row_scalar;
            new_col = new_col + col_scalar;
        }
        return moves;
    }
}
