package service;

import chess.ChessMove;
import dataaccess.DataAccessException;

import java.util.List;

public interface GameService {
    void clear() throws DataAccessException;
    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException;
    List listGame() throws DataAccessException;

    void updateGame(String gameName, ChessMove move)throws DataAccessException;
}
