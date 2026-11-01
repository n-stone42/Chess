package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;

import chess.ChessGame;
import model.*;

public class MemoryGameDAO implements GameDataAccess {
    public HashMap<Integer, GameData> mapGameData = new HashMap<>();

    public void clear() {
        mapGameData.clear();
    }

    public record ModifiedGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {
    }

    public void createGame(int gameID, String gameName) throws DataAccessException {
        // we are creating a new chess game so this parameter will just be a new chess game from the starting position
        if (mapGameData.get(gameID) != null) {
            throw new DataAccessException("Error: Game ID taken", DataAccessException.Code.AlreadyTaken);
        }
        mapGameData.put(gameID, new GameData(gameID, null, null, gameName, new ChessGame()));
    }

    public GameData getGame(int gameID) throws DataAccessException {
        if (mapGameData.get(gameID) != null) {
            return mapGameData.get(gameID);
        }
        throw new DataAccessException("Error: GameID not found", DataAccessException.Code.BadRequest);
    }

    public ArrayList<ModifiedGameData> listGame() throws DataAccessException {
        ArrayList<ModifiedGameData> games = new ArrayList<ModifiedGameData>();
        for (int gameID : mapGameData.keySet()) {
            GameData gameData = mapGameData.get(gameID);
            ModifiedGameData mofGameData = new ModifiedGameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());

            games.add(mofGameData);
        }
        return games;
    }

    public void swapGameData(int gameID, GameData newGameData) {
        GameData oldGamedata = mapGameData.get(gameID);
        mapGameData.remove(oldGamedata.gameID());
        mapGameData.put(oldGamedata.gameID(), newGameData);

    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException {
        GameData pendigGameData = mapGameData.get(gameID);
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (pendigGameData.whiteUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), newUsername, pendigGameData.blackUsername(), pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return;
            }
            throw new DataAccessException("Error: White UserName already taken", DataAccessException.Code.AlreadyTaken);
        } else { // player color black
            if (pendigGameData.blackUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), pendigGameData.whiteUsername(), newUsername, pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return;
            }
            throw new DataAccessException("Error: Black UserName already taken", DataAccessException.Code.AlreadyTaken);
        }
    }
}
