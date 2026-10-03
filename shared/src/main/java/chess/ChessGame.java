package chess;


import java.util.Collection;


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

    public ChessGame(ChessGame other) {
        this.turn = other.turn;
        this.myBoard = other.myBoard;
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
        
        // creat new board, make move there and see if the move results in the king being in check 
        ChessBoard boardCopy = new ChessBoard(myBoard);
        ChessPosition startPosition = move.getStartPosition();
        ChessPosition endPosition = move.getEndPosition();
        ChessPiece startPiece = boardCopy.getPiece(startPosition);

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
            ChessPiece newPiece = new ChessPiece(startPiece.getTeamColor(), startPiece.getPieceType());
            boardCopy.addPiece(endPosition, newPiece);
        }

        // remove piece from the start position
        boardCopy.removePiece(startPosition);

        ChessGame testGame = new ChessGame();
        if (testGame.isInCheck(turn)) {
            System.out.print("thowing in Check error ");
            throw new InvalidMoveException("Can't make move, would put King in Check");
        }

        // the move is valid, make the move on the real chess board
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
        System.out.println(myBoard);
        ChessPosition King_position = findKing(teamColor);
        // create a copy of the board, remove the king and add a queen then a Knight then a pawn
        //if these pieces can take something check the piece type and if it matches, we are in check
        ChessBoard boardCopy = new ChessBoard(myBoard); //deep copy of the chess board
        for (int i=1; i <=8; i++) {
            for (int j=1; j <=8; j++) {
                ChessPosition test_pos =  new ChessPosition(i,j);
                if (boardCopy.getPiece(test_pos) != null) {
                    ChessPiece testPiece = boardCopy.getPiece(test_pos);
                    for (ChessMove testMove: testPiece.pieceMoves(boardCopy, test_pos)) {
                        if (testMove.getEndPosition().getRow() == King_position.getRow() &&
                                testMove.getEndPosition().getColumn() == King_position.getColumn()) {
                            System.out.println("is in check, returning true");
                            return true;
                        }
                    }
                }
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

    private void advanceTurn() {
        if (turn == TeamColor.WHITE) {
            turn = TeamColor.BLACK;
            return;
        }
        turn = TeamColor.WHITE;
    }


}