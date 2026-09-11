package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
            Collection<ChessMove> Lst = new ArrayList<>();
            //vectors [1,0] [1,1] [0,1] [-1,1], [-1,0], [-1,-1], [0,-1], [1,-1]
            int[][] moves = {
                    {1,0}, {1,1}, {0,1}, {-1,1}, {-1,0}, {-1,-1}, {0,-1}, {1,-1}
            };
            for (var move: moves) {
                if (can_move(board, myPosition, move[0], move[1])) {
                    int new_row = myPosition.getRow() + move[0];
                    int new_col = myPosition.getColumn() + move[1];
                    ChessPosition new_pos = new ChessPosition (new_row, new_col);
                    ChessMove new_move = new ChessMove(myPosition, new_pos, null);
                    Lst.add(new_move);
                }
            }
            return Lst;
        }

        else if (the_piece.getPieceType() == PieceType.KNIGHT) {
            Collection<ChessMove> Lst = new ArrayList<>();
            int[][] moves = {
                    {2,1}, {1,2}, {-1,2}, {-2,1}, {-2,-1}, {-1,-2}, {1,-2}, {2,-1}
            };
            for (var move: moves) {
                if (can_move(board, myPosition, move[0], move[1])) {
                    int new_row = myPosition.getRow() + move[0];
                    int new_col = myPosition.getColumn() + move[1];
                    ChessPosition new_pos = new ChessPosition (new_row, new_col);
                    ChessMove new_move = new ChessMove(myPosition, new_pos, null);
                    Lst.add(new_move);
                }
            }
            return Lst;
        }

        else if (the_piece.getPieceType() == PieceType.PAWN) {
            // do pawn stuff
            Collection<ChessMove> Lst = new ArrayList<>();
            if (the_piece.getTeamColor() == ChessGame.TeamColor.WHITE) {


                // see if we can move forward 1
                ChessPosition new_pos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                if (is_empty(board, new_pos)) {
//                  System.out.println("can move forward 1");
                    if (myPosition.getRow() == 7) {
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.ROOK));
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.BISHOP));
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.QUEEN));
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.KNIGHT));
                    }
                    else {
                        ChessMove new_move = new ChessMove(myPosition, new_pos, null);
                        Lst.add(new_move);
                    }

                    if (myPosition.getRow() == 2) { // move forward 2
                        ChessPosition new_pos2 = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                        if (is_empty(board, new_pos2)) {
                            ChessMove new_move2 = new ChessMove(myPosition, new_pos2, null);
                            Lst.add(new_move2);
                        }
                    }
                }

//                 check pawn taking logic
                ChessPosition take_left = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() -1);
                if (can_take(board, myPosition,take_left)) {
                    if (myPosition.getRow() == 7) { //promotion takes
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.ROOK));
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.BISHOP));
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.QUEEN));
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.KNIGHT));
                    }
                    else {
                        ChessMove left = new ChessMove(myPosition, take_left, null);
                        Lst.add(left);
                    }
                }
                ChessPosition take_right = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() +1);
                if (can_take(board, myPosition, take_right)) {
                    if (myPosition.getRow() == 7) { //promotion takes
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.ROOK));
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.BISHOP));
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.QUEEN));
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.KNIGHT));
                    }
                    else {
                        ChessMove right = new ChessMove(myPosition, take_right, null);
                        Lst.add(right);
                    }
                }

            }

            if (the_piece.getTeamColor() == ChessGame.TeamColor.BLACK) {

                if (can_move(board, myPosition, -1,0 )) { // move forward 1

                    ChessPosition new_pos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());

                    if (myPosition.getRow() == 2) {
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.ROOK));
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.BISHOP));
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.QUEEN));
                        Lst.add(new ChessMove(myPosition, new_pos,PieceType.KNIGHT));
                    }

                    else {
                        ChessMove new_move = new ChessMove(myPosition, new_pos, null);
                        Lst.add(new_move);
                    }

                    if (myPosition.getRow() == 7) { // move forward 2
                        ChessPosition new_pos2 = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                        if (is_empty(board, new_pos2)) {
                            ChessMove new_move2 = new ChessMove(myPosition, new_pos2, null);
                            Lst.add(new_move2);
                        }
                    }
                }

                // check pawn taking logic
                ChessPosition take_left = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() +1);
                if (can_take(board, myPosition,take_left)) {

                    if (myPosition.getRow() == 2) { //promotion takes
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.ROOK));
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.BISHOP));
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.QUEEN));
                        Lst.add(new ChessMove(myPosition, take_left, PieceType.KNIGHT));
                    }
                    else {
                        ChessMove left = new ChessMove(myPosition, take_left, null);
                        Lst.add(left);
                    }
                }
                ChessPosition take_right = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() -1);
                if (can_take(board, myPosition, take_right)) {

                    if (myPosition.getRow() == 2) { //promotion takes
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.ROOK));
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.BISHOP));
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.QUEEN));
                        Lst.add(new ChessMove(myPosition, take_right, PieceType.KNIGHT));
                    }

                    else {
                        ChessMove right = new ChessMove(myPosition, take_right, null);
                        Lst.add(right);
                    }
                }

            }

            return Lst;

        }

        else if (the_piece.getPieceType() == PieceType.QUEEN) {
            // Rook like moves
            Collection<ChessMove> List_1 = move_itter(board, myPosition, 1,0);
            Collection<ChessMove> List_2 = move_itter(board, myPosition, 0,1);
            Collection<ChessMove> List_3 = move_itter(board, myPosition, -1,0);
            Collection<ChessMove> List_4 = move_itter(board, myPosition, 0,-1);
            List_1.addAll(List_2);
            List_1.addAll(List_3);
            List_1.addAll(List_4);

            //Bishop like moves
            Collection<ChessMove> List_5 = move_itter(board, myPosition, 1,1);
            Collection<ChessMove> List_6 = move_itter(board, myPosition, -1,1);
            Collection<ChessMove> List_7 = move_itter(board, myPosition, -1,-1);
            Collection<ChessMove> List_8 = move_itter(board, myPosition, 1,-1);
            List_1.addAll(List_5);
            List_1.addAll(List_6);
            List_1.addAll(List_7);
            List_1.addAll(List_8);

            return List_1;
        }

        else if (the_piece.getPieceType() == PieceType.ROOK) {
            Collection<ChessMove> List_1 = move_itter(board, myPosition, 1,0);
            Collection<ChessMove> List_2 = move_itter(board, myPosition, 0,1);
            Collection<ChessMove> List_3 = move_itter(board, myPosition, -1,0);
            Collection<ChessMove> List_4 = move_itter(board, myPosition, 0,-1);
            List_1.addAll(List_2);
            List_1.addAll(List_3);
            List_1.addAll(List_4);
            return List_1;
        }

        else {
            return null;} // there wa
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
        if (!in_bounds(newPosition.getRow(), newPosition.getColumn())) {
            return false;
        }

        if (board.getPiece(newPosition) == null) { // make sure there is a piece there
            return false;
        }
//        System.out.printf("Old Piece type %s   New Piece type%s",board.getPiece(myPosition).getTeamColor(), board.getPiece(newPosition).getTeamColor());
        else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(newPosition).getTeamColor()) {
//            System.out.printf("can't take %d, %d", newPosition.getRow(), newPosition.getColumn());
//            System.out.println();
            return false; //can't take, same color
        }
//        System.out.printf("CAN take %d, %d", newPosition.getRow(), newPosition.getColumn());
//        System.out.println();
        return true; //different colors, can take
    }

    private boolean can_move(ChessBoard board, ChessPosition myPosition, int row_scalar, int col_scalar) { // takes the other 3 helper function to see if a piece can move so a given position
        int new_row = myPosition.getRow() + row_scalar;
        int new_col = myPosition.getColumn() + col_scalar;
        ChessPosition new_pos = new ChessPosition (new_row, new_col);
        if (in_bounds(new_row, new_col)) { // move in bounds to empty square
            if (is_empty(board, new_pos)) {
                return true;
            }
            else if (can_take(board, myPosition, new_pos)) {
                return true;
            }
            return false; // can't take
        }
        return false; //out of bounds
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

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
