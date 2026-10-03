package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.lang.classfile.Attributes.record;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {

    private TeamColor turn;
    private ChessBoard myBoard = new ChessBoard();

    public ChessGame() {
        turn = TeamColor.WHITE;
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return turn;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        turn = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition King_position = myBoard.getKing(teamColor);

        int test_col;
        int test_row;
        int king_row = King_position.getRow();
        int king_col = King_position.getColumn();
        List<int[]> vectors = new ArrayList<>();

        vectors.add(new int[]{1, -1}); vectors.add(new int[]{1, 0}); vectors.add(new int[]{1, 1});
        vectors.add(new int[]{0, 1}); vectors.add(new int[]{0, -1});
        vectors.add(new int[]{-1, -1}); vectors.add(new int[]{-1, 0}); vectors.add(new int[]{-1, 1});

        for (int[] vector : vectors) {
            System.out.print("testing vector");
            test_row = king_row + vector[0];
            test_col = king_col + vector[1];

            while ( 0 < test_row && test_row < 9 && 0 < test_col && test_col < 9) { // make sure what were are testing is in bounds
                ChessPosition new_pos = new ChessPosition(test_row, test_col);

                if (myBoard.getPiece(new_pos) != null &&
                myBoard.getPiece(new_pos).getTeamColor() != teamColor) {
                    if (myBoard.getPiece(new_pos).can_take(myBoard, new_pos, King_position)) {
                        // we have spotted a piece than can take the king, we are in check
                        System.out.println("we are in Check");
                        return true;
                    }
                    break; //this means we have hit out own team color. We can break our while loop,
                    // and go back into our for loop for the next vector
                }
                // we have not hit another piece, continue the while loop
                test_row += vector[0];
                test_col += vector[1];
            }
        }
        List<int[]> Knight_moves = new ArrayList<>();
        Knight_moves.add(new int []{1,2}); Knight_moves.add(new int []{2,1});
        Knight_moves.add(new int []{1,-2}); Knight_moves.add(new int []{2,-1});
        Knight_moves.add(new int []{-1,-2}); Knight_moves.add(new int []{-2,-1});
        Knight_moves.add(new int []{-1,2}); Knight_moves.add(new int []{-2,1});

        for (int[] vector : Knight_moves ) {
            test_row = king_row + vector[0];
            test_col = king_col + vector[1];
            while ( 0 < test_row && test_row < 9 && 0 < test_col && test_col < 9) { // make sure what were are testing is in bounds
                ChessPosition new_pos = new ChessPosition(test_row, test_col);

                if (myBoard.getPiece(new_pos) != null &&
                        myBoard.getPiece(new_pos).getTeamColor() != teamColor) {
                    if (myBoard.getPiece(new_pos).getPieceType() == ChessPiece.PieceType.KNIGHT) {
                        // the only piece we need to check is the knight, it does not matter if its pinned and we already know its the right color
                        // we have spotted a piece than can take the king, we are in check
                        System.out.println("we are in Check by a KNIGHT");
                        return true;
                    }
                    break;
                }
                // we have not hit another piece, continue the while loop
                test_row += vector[0];
                test_col += vector[0];
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        myBoard = board;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return myBoard;
    }


}