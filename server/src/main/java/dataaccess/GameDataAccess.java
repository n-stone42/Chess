package dataaccess;

import chess.ChessGame;
import model.GameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void clear() throws DataAccessException;

    void createGame(int gameID, String gameName) throws DataAccessException;

    ArrayList<MemoryGameDAO.ModifiedGameData> listGame() throws DataAccessException;

    GameData getGame(int gameID) throws DataAccessException;

    void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException;
}
