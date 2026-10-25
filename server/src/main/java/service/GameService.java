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

    public int creatGame(String authToken, String gameName) throws DataAccessException {// returns the game ID
        if (verifyAuthToken(authToken)) {
            int NewGameID =createGameID();
            GameData.createGame(NewGameID, gameName);
            return NewGameID;
        }
    throw new DataAccessException("AuthToken not verfied");
    }

    public boolean joinGame(String authToken, ChessGame.TeamColor PlayerColor, int GameID) throws DataAccessException{
        if (verifyAuthToken(authToken)) {
            if (GameData.getGame(GameID) != null) {
                AuthData userAuthData = AuthData.getAuth(authToken);
                String username = userAuthData.username();
                return GameData.joinGame(GameID, PlayerColor, username);
            }
            throw new DataAccessException("Game ID not found");
        }
        return false;
    }

    public List listGames(String authToken) throws DataAccessException {
        if (verifyAuthToken(authToken)) {
            return GameData.listGame();
        }
        throw new DataAccessException("AuthToken not verfied");
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
