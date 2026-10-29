package service;

import chess.ChessGame;
import dataaccess.*;
import model.*;
import org.junit.jupiter.api.Assertions.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class GameService {
    private  UserDataAccess UserData;
    private GameDataAccess GameData;
    private AuthDataAccess AuthData;

    private int GameIDcounter;

    public GameService(UserDataAccess UserData, GameDataAccess GameData, AuthDataAccess AuthData) {
        this.UserData = UserData;
        this.GameData = GameData;
        this.AuthData = AuthData;
    }

    public record CreateGameResult(
            int GameId ) {}
    public record ListGameResult(
            ArrayList<GameData> Games) {}

    public record JoinGameRequest(
            String PlayerColor,
            int gameID
    ) {}
    public record JoinGameResult() {}

    public Boolean verifyAuthToken(String authToken) {
        try {
            if (AuthData.getAuth(authToken) != null) {
                return true;
            }
        } catch (Exception e) {}
        return false;
    }
    private int createGameID() {
        int newID = GameIDcounter;
        GameIDcounter++;
        return newID;
    }

    public CreateGameResult creatGame(String authToken, String gameName) throws DataAccessException {// returns the game ID
        if (verifyAuthToken(authToken)) {
            int NewGameID =createGameID();
            GameData.createGame(NewGameID, gameName);
            return new CreateGameResult(NewGameID);
        }
    throw new DataAccessException("Error: AuthToken not verfied", DataAccessException.Code.Unathurized);
    }

    public JoinGameResult joinGame(String authToken, JoinGameRequest request) throws DataAccessException{
        if (GameData.getGame(request.gameID()) != null) {
            AuthData userAuthData = AuthData.getAuth(authToken);
            String username = userAuthData.username();
            ChessGame.TeamColor color;
            if (request.PlayerColor == "WHITE") {
                GameData.joinGame(request.gameID(), ChessGame.TeamColor.WHITE, username);
                return new JoinGameResult();
            }
            else if (request.PlayerColor == "BLACK") {
                GameData.joinGame(request.gameID(), ChessGame.TeamColor.BLACK, username);
                return new JoinGameResult();
            }
        }
        throw new DataAccessException("Error: Game ID not found", DataAccessException.Code.BadRequest);
    }

    public ListGameResult listGames(String authToken) throws DataAccessException {
        if (verifyAuthToken(authToken)) {
            return new ListGameResult(GameData.listGame());
        }
        throw new DataAccessException("Error: AuthToken not verfied", DataAccessException.Code.Unathurized);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        GameService that = (GameService) o;
        return GameIDcounter == that.GameIDcounter && Objects.equals(UserData, that.UserData) && Objects.equals(GameData, that.GameData) && Objects.equals(AuthData, that.AuthData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UserData, GameData, AuthData, GameIDcounter);
    }
}
