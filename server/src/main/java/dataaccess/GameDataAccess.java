package dataaccess;

import chess.ChessGame;
import chess.ChessMove;
import model.GameData;

import java.util.ArrayList;
import java.util.List;

public interface GameDataAccess {
    void clear() throws DataAccessException;
    void createGame(int gameID,  String gameName) throws DataAccessException;
    ArrayList<GameData> listGame() throws DataAccessException;

    void updateChessGame(int gameID, ChessMove move)throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    boolean joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException;
}
