package service;

import gameRecordModels.CreateGameRequest;
import gameRecordModels.CreateGameResult;
import gameRecordModels.JoinGameRequest;
import gameRecordModels.ListGameResult;
import chess.ChessGame;
import dataaccess.*;
import dataAccessException.dataAccessException;
import model.*;

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

    public record JoinGameResult() {
    }

    public Boolean verifyAuthToken(String authToken) throws dataAccessException {

        if (authData.getAuth(authToken) != null) {
            return true;
        }
        throw new dataAccessException("verifyAuthToken function failed", dataAccessException.Code.Unathurized);
    }

    private int createGameID() {
        int newID = gameIDCounter;
        gameIDCounter++;
        return newID;
    }

    public CreateGameResult creatGame(String authToken,
                                      CreateGameRequest request) throws dataAccessException {// returns the game ID
        if (verifyAuthToken(authToken)) {
            int newGameID = createGameID();
            gameData.createGame(newGameID, request.gameName());
            return new CreateGameResult(newGameID);
        }
        throw new dataAccessException("Error: authToken not verfied", dataAccessException.Code.Unathurized);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws dataAccessException {
        if (gameData.getGame(request.gameID()) != null) {
            AuthData userAuthData = authData.getAuth(authToken);
            String username = userAuthData.username();
            String playerColor = request.playerColor().toLowerCase();
            if (playerColor.equals("white")) {
                gameData.joinGame(request.gameID(), ChessGame.TeamColor.WHITE, username);
                return new JoinGameResult();
            } else if (playerColor.equals("black")) {
                gameData.joinGame(request.gameID(), ChessGame.TeamColor.BLACK, username);
                return new JoinGameResult();
            } else {
                throw new dataAccessException("Error: Player color not found", dataAccessException.Code.BadRequest);
            }
        }
        throw new dataAccessException("Error: Game ID not found", dataAccessException.Code.BadRequest);
    }

    public ListGameResult listGames(String authToken) throws dataAccessException {
        if (verifyAuthToken(authToken)) {
            return new ListGameResult(gameData.listGame());
        }
        throw new dataAccessException("Error: authToken not verfied", dataAccessException.Code.OtherError); // changed 11-7
    }
}