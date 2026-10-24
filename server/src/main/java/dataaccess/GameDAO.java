package dataaccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.*;
import service.GameService;

public class GameDAO implements GameService {
    public HashMap<String, GameData> Map_GameData = new HashMap<>();

    public void clear() {
        Map_GameData.clear();
    }

    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException {
        // we are creating a new chess game so this parameter will just be a new chess game from the starting position
        if (Map_GameData.get(gameID) != null) {
            throw new DataAccessException("game ID taken");
        }
        Map_GameData.put(gameName, new GameData(gameID, whiteUsername, blackUsername, blackUsername, new ChessGame()));
    }
    public List listGame() throws DataAccessException{
        List<String> gameIDs = new ArrayList<>();
        if (Map_GameData.isEmpty()) {
            throw new DataAccessException("There are no games to list");
        }
        for (String gameID : Map_GameData.keySet()) {
            gameIDs.add(gameID);
        }
        return gameIDs;
    }
    public void updateGame(String gameName, ChessMove move)throws DataAccessException {
        if (Map_GameData.get(gameName) == null) {
            throw new DataAccessException("gameID not found");
        }
        GameData oldGamedata = Map_GameData.get(gameName);
        ChessGame game = oldGamedata.game();
        try {
            // make the move on the game
            game.makeMove(move);
            // remove the old record
            Map_GameData.remove(oldGamedata.gameName());
            // make a new record with  the new game
            Map_GameData.put(oldGamedata.gameName(),new GameData(oldGamedata.gameID(), oldGamedata.whiteUsername(),
                    oldGamedata.blackUsername(), oldGamedata.gameName(), game));
        }
        catch (InvalidMoveException e) {
            throw new RuntimeException(e);
        }
    }
}
