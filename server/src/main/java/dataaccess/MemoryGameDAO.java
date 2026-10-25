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

    public void createGame(int gameID,String gameName) throws DataAccessException {
        // we are creating a new chess game so this parameter will just be a new chess game from the starting position
        if (Map_GameData.get(gameID) != null) {
            throw new DataAccessException("game ID taken");
        }
        Map_GameData.put(gameID, new GameData(gameID, null,null,gameName, new ChessGame()));
    }

    public GameData getGame(int GameID) throws DataAccessException{
        if (Map_GameData.get(GameID) != null) {
            return Map_GameData.get(GameID);
        }
        throw new DataAccessException("GameID not found");
    }

    public List listGame() throws DataAccessException{
        List<Integer> gameIDs = new ArrayList<>();
        if (Map_GameData.isEmpty()) {
            throw new DataAccessException("There are no games to list");
        }
        for (int gameID : Map_GameData.keySet()) {
            gameIDs.add(gameID);
        }
        return gameIDs;
    }
    public void updateChessGame(int gameID, ChessMove move)throws DataAccessException {
        if (Map_GameData.get(gameID) == null) {
            throw new DataAccessException("gameID not found");
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

    public boolean joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException{
        GameData pendigGameData = Map_GameData.get(gameID);
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (pendigGameData.whiteUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), newUsername, pendigGameData.blackUsername(), pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return true;
            }
            throw new DataAccessException("White UserName already taken");
        }
        else { // player color black
            if (pendigGameData.blackUsername() == null) {
                GameData newGameData = new GameData(pendigGameData.gameID(), pendigGameData.whiteUsername(), newUsername, pendigGameData.gameName(), pendigGameData.game());
                swapGameData(gameID, newGameData);
                return true;
            }
            throw new DataAccessException("Black UserName already taken");
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
