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
        if (the_piece.getPieceType() == PieceType.BISHOP) {
            Collection<ChessMove> chessMoveList1 = moveItter(board, myPosition, 1, 1);
            Collection<ChessMove> chessMoveList2 = moveItter(board, myPosition, -1, 1);
            Collection<ChessMove> chessMoveList3 = moveItter(board, myPosition, -1, -1);
            Collection<ChessMove> chessMoveList4 = moveItter(board, myPosition, 1, -1);
            chessMoveList1.addAll(chessMoveList2);
            chessMoveList1.addAll(chessMoveList3);
            chessMoveList1.addAll(chessMoveList4);
            return chessMoveList1;
        } else if (the_piece.getPieceType() == PieceType.KING) {
            Collection<ChessMove> moves1 = new ArrayList<>();
            int[][] moves = {
                    {1, 0}, {1, 1}, {0, 1}, {-1, 1}, {-1, 0}, {-1, -1}, {0, -1}, {1, -1}
            };
            for (var move : moves) {
                if (canMove(board, myPosition, move[0], move[1])) {
                    int new_row = myPosition.getRow() + move[0];
                    int new_col = myPosition.getColumn() + move[1];
                    ChessPosition newPos = new ChessPosition(new_row, new_col);
                    ChessMove new_move = new ChessMove(myPosition, newPos, null);
                    moves1.add(new_move);
                }
            }
            return moves1;
        } else if (the_piece.getPieceType() == PieceType.KNIGHT) {
            Collection<ChessMove> moves2 = new ArrayList<>();
            int[][] moves = {
                    {2, 1}, {1, 2}, {-1, 2}, {-2, 1}, {-2, -1}, {-1, -2}, {1, -2}, {2, -1}
            };
            for (var move : moves) {
                if (canMove(board, myPosition, move[0], move[1])) {
                    int newRow = myPosition.getRow() + move[0];
                    int newCol = myPosition.getColumn() + move[1];
                    ChessPosition newPos = new ChessPosition(newRow, newCol);
                    ChessMove new_move = new ChessMove(myPosition, newPos, null);
                    moves2.add(new_move);
                }
            }
            return moves2;
        } else if (the_piece.getPieceType() == PieceType.PAWN) {
            // do pawn stuff
            Collection<ChessMove> moves3 = new ArrayList<>();
            if (the_piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
                // see if we can move forward 1
                ChessPosition newPos = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn());
                if (isEmpty(board, newPos)) {
//                  System.out.println("can move forward 1");
                    if (myPosition.getRow() == 7) {
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
                    } else {
                        ChessMove newMove = new ChessMove(myPosition, newPos, null);
                        moves3.add(newMove);
                    }
                    if (myPosition.getRow() == 2) { // move forward 2
                        ChessPosition newPos2 = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn());
                        if (isEmpty(board, newPos2)) {
                            ChessMove newMove2 = new ChessMove(myPosition, newPos2, null);
                            moves3.add(newMove2);
                        }
                    }
                }

//                 check pawn taking logic
                ChessPosition takeLeft = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 1);
                if (differentColors(board, myPosition, takeLeft)) {
                    if (myPosition.getRow() == 7) { //promotion takes
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.ROOK));
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.BISHOP));
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.QUEEN));
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.KNIGHT));
                    } else {
                        ChessMove left = new ChessMove(myPosition, takeLeft, null);
                        moves3.add(left);
                    }
                }
                ChessPosition takeRight = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 1);
                if (differentColors(board, myPosition, takeRight)) {
                    if (myPosition.getRow() == 7) { //promotion takes
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.ROOK));
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.BISHOP));
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.QUEEN));
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.KNIGHT));
                    } else {
                        ChessMove right = new ChessMove(myPosition, takeRight, null);
                        moves3.add(right);
                    }
                }

            }

            if (the_piece.getTeamColor() == ChessGame.TeamColor.BLACK) {

                if (canMove(board, myPosition, -1, 0)) { // move forward 1

                    ChessPosition newPos = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn());

                    if (myPosition.getRow() == 2) {
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.ROOK));
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.BISHOP));
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.QUEEN));
                        moves3.add(new ChessMove(myPosition, newPos, PieceType.KNIGHT));
                    } else {
                        ChessMove new_move = new ChessMove(myPosition, newPos, null);
                        moves3.add(new_move);
                    }

                    if (myPosition.getRow() == 7) { // move forward 2
                        ChessPosition newPos2 = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn());
                        if (isEmpty(board, newPos2)) {
                            ChessMove new_move2 = new ChessMove(myPosition, newPos2, null);
                            moves3.add(new_move2);
                        }
                    }
                }

                // check pawn taking logic
                ChessPosition takeLeft = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 1);
                if (differentColors(board, myPosition, takeLeft)) {

                    if (myPosition.getRow() == 2) { //promotion takes
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.ROOK));
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.BISHOP));
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.QUEEN));
                        moves3.add(new ChessMove(myPosition, takeLeft, PieceType.KNIGHT));
                    } else {
                        ChessMove left = new ChessMove(myPosition, takeLeft, null);
                        moves3.add(left);
                    }
                }
                ChessPosition takeRight = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 1);
                if (differentColors(board, myPosition, takeRight)) {

                    if (myPosition.getRow() == 2) { //promotion takes
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.ROOK));
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.BISHOP));
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.QUEEN));
                        moves3.add(new ChessMove(myPosition, takeRight, PieceType.KNIGHT));
                    } else {
                        ChessMove right = new ChessMove(myPosition, takeRight, null);
                        moves3.add(right);
                    }
                }
            }
            return moves3;
        } else if (the_piece.getPieceType() == PieceType.QUEEN) {
            // Rook like moves
            Collection<ChessMove> chessMovesList1 = moveItter(board, myPosition, 1, 0);
            Collection<ChessMove> chessMovesList2 = moveItter(board, myPosition, 0, 1);
            Collection<ChessMove> chessMovesList3 = moveItter(board, myPosition, -1, 0);
            Collection<ChessMove> chessMovesList4 = moveItter(board, myPosition, 0, -1);
            chessMovesList1.addAll(chessMovesList2);
            chessMovesList1.addAll(chessMovesList3);
            chessMovesList1.addAll(chessMovesList4);

            //Bishop like moves
            Collection<ChessMove> chessMovesList5 = moveItter(board, myPosition, 1, 1);
            Collection<ChessMove> chessMovesList6 = moveItter(board, myPosition, -1, 1);
            Collection<ChessMove> chessMovesList7 = moveItter(board, myPosition, -1, -1);
            Collection<ChessMove> chessMovesList8 = moveItter(board, myPosition, 1, -1);
            chessMovesList1.addAll(chessMovesList5);
            chessMovesList1.addAll(chessMovesList6);
            chessMovesList1.addAll(chessMovesList7);
            chessMovesList1.addAll(chessMovesList8);

            return chessMovesList1;
        } else if (the_piece.getPieceType() == PieceType.ROOK) {
            Collection<ChessMove> chessMovesList1 = moveItter(board, myPosition, 1, 0);
            Collection<ChessMove> chessMovesList2 = moveItter(board, myPosition, 0, 1);
            Collection<ChessMove> chessMovesList3 = moveItter(board, myPosition, -1, 0);
            Collection<ChessMove> chessMovesList4 = moveItter(board, myPosition, 0, -1);
            chessMovesList1.addAll(chessMovesList2);
            chessMovesList1.addAll(chessMovesList3);
            chessMovesList1.addAll(chessMovesList4);
            return chessMovesList1;
        } else {
            return null;
        }
    }

    private boolean isEmpty(ChessBoard board, ChessPosition myPosition) {
        if (board.getPiece(myPosition) == null) {
            return true;
        } else {
            return false;
        }
    }

    public boolean inBounds(int row, int col) {
        if (0 < row && row < 9 && 0 < col && col < 9) {
            return true;
        } else {
            return false;
        }
    }

    public boolean differentColors(ChessBoard board, ChessPosition myPosition, ChessPosition newPosition) {
        if (!inBounds(newPosition.getRow(), newPosition.getColumn())) {
            return false;
        }

        if (board.getPiece(newPosition) == null) { // make sure there is a piece there
            return false;
        } else if (board.getPiece(myPosition).getTeamColor() == board.getPiece(newPosition).getTeamColor()) {
            return false; //can't take, same color
        }
        return true; //different colors, can take
    }

    private boolean canMove(ChessBoard board, ChessPosition myPosition, int rowScalar, int colScalar) { // takes the other 3 helper function to see if a piece can move so a given position
        int new_row = myPosition.getRow() + rowScalar;
        int new_col = myPosition.getColumn() + colScalar;
        ChessPosition new_pos = new ChessPosition(new_row, new_col);
        if (inBounds(new_row, new_col)) { // move in bounds to empty square
            if (isEmpty(board, new_pos)) {
                return true;
            } else if (differentColors(board, myPosition, new_pos)) {
                return true;
            }
            return false; // can't take
        }
        return false; //out of bounds
    }

    private List<ChessMove> moveItter(ChessBoard board, ChessPosition myPosition, int rowScalar, int colScalar) {

        List<ChessMove> moves = new ArrayList<>();

        int newRow = myPosition.getRow() + rowScalar;
        int newCol = myPosition.getColumn() + colScalar;
        while (inBounds(newRow, newCol)) {
            ChessPosition newPos = new ChessPosition(newRow, newCol);

            if (isEmpty(board, newPos)) {
                ChessMove new_move = new ChessMove(myPosition, newPos, null);
                moves.add(new_move);
            } else if (differentColors(board, myPosition, newPos)) { // next piece can be taken, add it to list then return
                ChessMove new_move = new ChessMove(myPosition, newPos, null);
                moves.add(new_move);
                return moves;
            } else { //we have hit another piece that can't be taken, return the list
                return moves;
            }
            newRow = newRow + rowScalar;
            newCol = newCol + colScalar;
        }
        return moves;
    }

    @Override
    public String toString() {
        return "ChessPiece{" +
                "pieceColor=" + pieceColor +
                ", type=" + type +
                '}';
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
