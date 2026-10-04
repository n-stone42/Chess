package chess;


import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;


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
        this.turn = turn;
        this.myBoard = myBoard;
        myBoard.resetBoard();
        turn = TeamColor.WHITE;
    }

    public ChessGame(ChessBoard board, TeamColor newTurn) {
        this.myBoard = board;
        this.turn = newTurn;
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

    public ChessPosition findKing(TeamColor color) {
        for (int i = 1; i <= 8; i++) {
            for (int j = 1; j <= 8; j++) {
                ChessPosition testPosition = new ChessPosition(i, j);
                if (myBoard.getPiece(testPosition) != null) {
                    if (myBoard.getPiece(testPosition).getPieceType() == ChessPiece.PieceType.KING && myBoard.getPiece(testPosition).getTeamColor() == color) {
                        return testPosition;
                    }

                }
            }
        }
        System.out.print("King NOT found");
        return null;
    }
    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {

        if (this.myBoard.getPiece(move.getStartPosition()) == null) {
            throw new InvalidMoveException("No Piece");
        }

        // creat new board, make move there and see if the move results in the king being in check
        ChessBoard boardCopy =  new ChessBoard(myBoard);
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece startPiece = boardCopy.getPiece(startPosition);

        // testing move logic -- seeing if the move follows the movement logic of the piece
        Collection<ChessMove> pieceMoves = startPiece.pieceMoves(boardCopy, startPosition);
        if (!pieceMoves.contains(move)) {
            throw new InvalidMoveException("Not a legal move");
        }

        if (startPiece.getTeamColor() != getTeamTurn()) {
            throw new InvalidMoveException("movement out of turn");
        }

        // see if the move was taking a pice. Maybe  taken piece was putting us in check
        if (boardCopy.getPiece(endPosition) != null) {
            if (startPiece.getTeamColor() != boardCopy.getPiece(endPosition).getTeamColor()) { // we can take
                boardCopy.removePiece(endPosition);
            }
            else { // it's the same piece color
                throw new InvalidMoveException("Can't make move, can't take a piece of your own color");
            }
        }

        // add the piece to its end square -- it should be empty now
        if (move.getPromotionPiece() == null) { //it's not a pawn
            boardCopy.addPiece(endPosition, startPiece);
        }
        
        else { // it's a pawn
            ChessPiece newPiece = new ChessPiece(startPiece.getTeamColor(), move.getPromotionPiece());
            boardCopy.addPiece(endPosition, newPiece);
        }

        // remove piece from the start position
        boardCopy.removePiece(startPosition);

        ChessGame testGame = new ChessGame(boardCopy, turn);
        if (testGame.isInCheck(turn)) {
            throw new InvalidMoveException("Can't make move, would put King in Check");
        }

        // the move is valid, make the move on the real chess board
//        System.out.print(move.toString());
//        System.out.println("move is valid");
        myBoard = boardCopy;
        advanceTurn();
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition King_position = findKing(teamColor);
//        System.out.print("king Position");
//        System.out.println(King_position);
        // create a copy of the board, remove the king and add a queen then a Knight then a pawn
        //if these pieces can take something check the piece type and if it matches, we are in check
        ChessBoard boardCopy =  new ChessBoard(myBoard); //deep copy of the chess board

        // iterate though the chess board
        for (int i=1; i <= 8; i++) {
            for (int j=1; j <= 8; j++) {
                // create and test new chess position
                ChessPosition test_pos =  new ChessPosition(i,j);
                if (boardCopy.getPiece(test_pos) != null) {
                    // somehow it can't find the enemey king
                    // if we have a new chess piece there
                    ChessPiece testPiece = boardCopy.getPiece(test_pos);
//                    System.out.println(testPiece.getPieceType());
                    if (testPiece.getTeamColor() != teamColor) {
//                        System.out.println("enemy piece found");

                        // get that pieces moves and see if it can take the king
                        for (ChessMove testMove : testPiece.pieceMoves(boardCopy, test_pos)) {
//                            System.out.print(testPiece.getPieceType().toString());
//                            System.out.println(testMove.toString());
                            if (testMove.getEndPosition().getRow() == King_position.getRow() &&
                                    testMove.getEndPosition().getColumn() == King_position.getColumn()) {
//                                System.out.println("IS IN Check");
//                                System.out.println();
                                return true;
                            }
                        }
                    }
                }
            }
        }
//        System.out.println("not in Check");
        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {

        // else return true
        if (this.isInCheck(teamColor)) {     // if in check
            Collection<ChessMove> allValidMoves = new ArrayList<>();

            // iterate through pieces on the chess board of our color
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition testPosition = new ChessPosition(i, j);
                    if (myBoard.getPiece(testPosition) != null) {
                        if (myBoard.getPiece(testPosition).getTeamColor() == teamColor) {
                            ChessPiece newPiece = myBoard.getPiece(testPosition);
                            // add all valid moves
                            allValidMoves.addAll(newPiece.pieceMoves(myBoard, testPosition));
                        }

                    }
                }
            }
            // implement those moves and if any result in a position not in check, return false
            for (ChessMove move : allValidMoves) {

                ChessBoard boardCopy =  new ChessBoard(myBoard);
                ChessGame testGame = new ChessGame(boardCopy, turn);
                try {
//                    System.out.print("trying new move ");
//                    System.out.print(move);
                    testGame.makeMove(move);
                    if (!testGame.isInCheck(teamColor)) {
                        System.out.println("SUCCESS");
                        return false;
                    }
                    return false;
                } catch (InvalidMoveException e) {
                    System.out.println(e);
                }
            }
        return true;
        }
    return false;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (!this.isInCheck(teamColor)) {     // if in check
            Collection<ChessMove> allValidMoves = new ArrayList<>();

            // iterate through pieces on the chess board of our color
            for (int i = 1; i <= 8; i++) {
                for (int j = 1; j <= 8; j++) {
                    ChessPosition testPosition = new ChessPosition(i, j);
                    if (myBoard.getPiece(testPosition) != null) {
                        if (myBoard.getPiece(testPosition).getTeamColor() == teamColor) {
                            ChessPiece newPiece = myBoard.getPiece(testPosition);
                            // add all valid moves
                            allValidMoves.addAll(newPiece.pieceMoves(myBoard, testPosition));
                        }

                    }
                }
            }
            // implement those moves and if any result in a position not in check, return false
            for (ChessMove move : allValidMoves) {

                ChessBoard boardCopy = new ChessBoard(myBoard);
                ChessGame testGame = new ChessGame(boardCopy, turn);
                try {
//                    System.out.print("trying new move ");
//                    System.out.print(move);
                    testGame.makeMove(move);
                    if (!testGame.isInCheck(teamColor)) {
                        System.out.println("SUCCESS");
                        return false;
                    }
                    return false;
                } catch (InvalidMoveException e) {
                    System.out.println(e);
                }
            }
            return true;
        }
        return false;
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

    private void advanceTurn() {
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
            return;
        }
        turn = TeamColor.WHITE;
    }

    private void SetTurn(TeamColor newTurn) {
        turn = newTurn;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        ChessGame chessGame = (ChessGame) o;
        return turn == chessGame.turn && Objects.equals(myBoard, chessGame.myBoard);
    }

    @Override
    public int hashCode() {
        return Objects.hash(turn, myBoard);
    }
}