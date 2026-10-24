package dataaccess;

import chess.ChessMove;

import java.util.List;

public interface GameDataAccess {
    void clear() throws DataAccessException;
    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException;
    List listGame() throws DataAccessException;

    void updateGame(String gameName, ChessMove move)throws DataAccessException;
}
