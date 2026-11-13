package dataaccess;

import chess.ChessGame;
import dataAccessException.dataAccessException;
import model.GameData;
import model.ModifiedGameData;

import java.util.ArrayList;

public interface GameDataAccess {
    void clear() throws dataAccessException;

    void createGame(int gameID, String gameName) throws dataAccessException;

    ArrayList<ModifiedGameData> listGame() throws dataAccessException;

    GameData getGame(int gameID) throws dataAccessException;

    void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws dataAccessException;
}
