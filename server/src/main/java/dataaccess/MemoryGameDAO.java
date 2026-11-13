package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;

import chess.ChessGame;
import dataAccessException.dataAccessException;
import model.*;

public class MemoryGameDAO implements GameDataAccess {
    public HashMap<Integer, GameData> mapGameData = new HashMap<>();

    public void clear() {
        mapGameData.clear();
    }


    public void createGame(int gameID, String gameName) throws dataAccessException {
        // we are creating a new chess game so this parameter will just be a new chess game from the starting position
        if (mapGameData.get(gameID) != null) {
            throw new dataAccessException("Error: Game ID taken", dataAccessException.Code.AlreadyTaken);
        }
        mapGameData.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
    }

    public GameData getGame(int gameID) throws dataAccessException {
        if (mapGameData.get(gameID) != null) {
            return mapGameData.get(gameID);
        }
        throw new dataAccessException("Error: GameID not found", dataAccessException.Code.BadRequest);
    }

    public ArrayList<ModifiedGameData> listGame() throws dataAccessException {
        ArrayList<ModifiedGameData> games = new ArrayList<ModifiedGameData>();
        for (int gameID : mapGameData.keySet()) {
            GameData gameData = mapGameData.get(gameID);
            ModifiedGameData mofGameData = new ModifiedGameData(gameData.gameID(), gameData.whiteUsername(),
                    gameData.blackUsername(), gameData.gameName());

            games.add(mofGameData);
        }
        return games;
    }

    public void swapGameData(int gameID, GameData newGameData) {
        GameData oldGamedata = mapGameData.get(gameID);
        mapGameData.remove(oldGamedata.gameID());
        mapGameData.put(oldGamedata.gameID(), newGameData);

    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws dataAccessException {
        GameData pendigGameData = mapGameData.get(gameID);
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (pendigGameData.whiteUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), newUsername,
                        pendigGameData.blackUsername(), pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return;
            }
            throw new dataAccessException("Error: White UserName already taken", dataAccessException.Code.AlreadyTaken);
        } else { // player color black
            if (pendigGameData.blackUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), pendigGameData.whiteUsername(),
                        newUsername, pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return;
            }
            throw new dataAccessException("Error: Black UserName already taken", dataAccessException.Code.AlreadyTaken);
        }
    }
}
