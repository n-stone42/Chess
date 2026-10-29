package dataaccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.*;

public class MemoryGameDAO implements GameDataAccess {
    public HashMap<Integer, GameData> Map_GameData = new HashMap<>();

    public void clear() {
        Map_GameData.clear();
    }

    public record modifiedGameData(int gameID, String whiteUsername, String blackUsername, String gameName) {};



    public void createGame(int gameID, String gameName) throws DataAccessException {
        // we are creating a new chess game so this parameter will just be a new chess game from the starting position
        if (Map_GameData.get(gameID) != null) {
            throw new DataAccessException("Error: Game ID taken", DataAccessException.Code.AlreadyTaken);
        }
        Map_GameData.put(gameID, new GameData(gameID, null,null,gameName, new ChessGame()));
    }

    public GameData getGame(int gameID) throws DataAccessException{
        if (Map_GameData.get(gameID) != null) {
            return Map_GameData.get(gameID);
        }
        throw new DataAccessException("Error: GameID not found", DataAccessException.Code.BadRequest);
    }

    public ArrayList listGame() throws DataAccessException{
        ArrayList<modifiedGameData> games = new ArrayList<modifiedGameData>();
        for (int gameID : Map_GameData.keySet()) {
            GameData gameData = Map_GameData.get(gameID);
            modifiedGameData mofGameData = new modifiedGameData(gameData.gameID(), gameData.whiteUsername(), gameData.blackUsername(), gameData.gameName());

            games.add(mofGameData);
        }
        return games;
    }
    public void updateChessGame(int gameID, ChessMove move)throws DataAccessException {
        if (Map_GameData.get(gameID) == null) {
            throw new DataAccessException("Error: GameID not found", DataAccessException.Code.BadRequest);
        }
        GameData oldGamedata = Map_GameData.get(gameID);
        ChessGame game = oldGamedata.game();
        try {
            game.makeMove(move);
            GameData newGameData = new GameData(oldGamedata.gameID(), oldGamedata.whiteUsername(),
                    oldGamedata.blackUsername(), oldGamedata.gameName(), game);
            // remove the old record
            swapGameData(gameID, newGameData);
        }
        catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }

    public void swapGameData(int gameID, GameData newGameData ) {
        GameData oldGamedata = Map_GameData.get(gameID);
        Map_GameData.remove(oldGamedata.gameID());
        Map_GameData.put(oldGamedata.gameID(), newGameData);

    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException{
        GameData pendigGameData = Map_GameData.get(gameID);
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (pendigGameData.whiteUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), newUsername, pendigGameData.blackUsername(), pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return;
            }
            throw new DataAccessException("Error: White UserName already taken", DataAccessException.Code.AlreadyTaken);
        }
        else { // player color black
            if (pendigGameData.blackUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), pendigGameData.whiteUsername(), newUsername, pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return;
            }
            throw new DataAccessException("Error: Black UserName already taken", DataAccessException.Code.AlreadyTaken);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MemoryGameDAO that = (MemoryGameDAO) o;
        return Objects.equals(Map_GameData, that.Map_GameData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Map_GameData);
    }
}
