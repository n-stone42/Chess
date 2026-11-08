package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;

import java.util.ArrayList;

public class GameService {
    private UserDataAccess userData;
    private GameDataAccess gameData;
    private AuthDataAccess authData;

    private int gameIDCounter = 1;

    public GameService(UserDataAccess userData, GameDataAccess gameData, AuthDataAccess authData) {
        this.userData = userData;
        this.gameData = gameData;
        this.authData = authData;
    }

    public record CreateGameRequest(String gameName) {
    }

    ;

    public record CreateGameResult(
            int gameID) {
    }


    public record ListGameResult(
            ArrayList<MemoryGameDAO.ModifiedGameData> games) {
    }

    public record JoinGameRequest(
            String playerColor,
            int gameID
    ) {
    }

    public record JoinGameResult() {
    }

    public Boolean verifyAuthToken(String authToken) {
        try {
            if (authData.getAuth(authToken) != null) {
                return true;
            }
        } catch (Exception e) {
        }
        return false;
    }

    private int createGameID() {
        int newID = gameIDCounter;
        gameIDCounter++;
        return newID;
    }

    public CreateGameResult creatGame(String authToken,
                                      CreateGameRequest request) throws DataAccessException {// returns the game ID
        System.out.println("in createGame in GameService--------------------");
        if (verifyAuthToken(authToken)) {
            int newGameID = createGameID();
            gameData.createGame(newGameID, request.gameName());
            return new CreateGameResult(newGameID);
        }
        throw new DataAccessException("Error: authToken not verfied", DataAccessException.Code.OtherError);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws DataAccessException {
        if (gameData.getGame(request.gameID()) != null) {
            AuthData userAuthData = authData.getAuth(authToken);
            String username = userAuthData.username();
            ChessGame.TeamColor color;
            if (request.playerColor.equals("WHITE")) {
                gameData.joinGame(request.gameID(), ChessGame.TeamColor.WHITE, username);
                return new JoinGameResult();
            } else if (request.playerColor.equals("BLACK")) {
                gameData.joinGame(request.gameID(), ChessGame.TeamColor.BLACK, username);
                return new JoinGameResult();
            } else {
                throw new DataAccessException("Error: Player color not found", DataAccessException.Code.BadRequest);
            }
        }
        throw new DataAccessException("Error: Game ID not found", DataAccessException.Code.BadRequest);
    }

    public ListGameResult listGames(String authToken) throws DataAccessException {
        if (verifyAuthToken(authToken)) {
            return new ListGameResult(gameData.listGame());
        }
        throw new DataAccessException("Error: authToken not verfied", DataAccessException.Code.Unathurized);
    }
}