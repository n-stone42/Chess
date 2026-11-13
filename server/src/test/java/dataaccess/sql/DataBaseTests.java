package dataaccess.sql;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import dataAccessException.dataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public static int gameID = 1;
    private int newGameID() {
        return gameID++;
    }
    public DatabaseUserData databaseUserData = new DatabaseUserData();
    public DatabaseAuthData databaseAuthData = new DatabaseAuthData();
    public DatabaseGameData databaseGameData = new DatabaseGameData();
    public DatabaseManager manger = new DatabaseManager();

    public static UserData userBob = new UserData("Bob", "password", "B0BTheGoat67@gmail.com");
    public static UserData userTom = new UserData("Tom", "words", "TomTheM0M@hotmainl.com");
    public AuthData testAuthData = new AuthData("token", userBob.username());

    void addUser(UserData user) throws dataAccessException {
        databaseUserData.createUser(user.username(), user.password(), user.email());
    }

    void addAuthData() throws dataAccessException {
        databaseAuthData.createAuth(testAuthData.authToken(), userBob.username());
    }

    Integer addChessGame() throws dataAccessException {
        int gameID = newGameID();
        databaseGameData.createGame(gameID, "testGame");
        return gameID;
    }

    void insertPlayerToGame(Integer gameID, ChessGame.TeamColor color, UserData user) throws dataAccessException {
        databaseGameData.joinGame(gameID, color, user.username());
    }

    @BeforeEach
    void setup()  throws dataAccessException {
    clearTables();

    }

    public void clearTables() throws dataAccessException {
        databaseUserData.clear();
        databaseAuthData.clear();
        databaseGameData.clear();
    }

    @Test
    void testCreateTables() {
        assertDoesNotThrow(() -> {
            databaseUserData.makeUserTable();
        });
        assertDoesNotThrow(() -> {
            databaseAuthData.makeAuthDataTable();
        });
        assertDoesNotThrow(() -> {
            databaseGameData.makeGameDataTables();
        });
    }

    @Test
    void testCreateDBWithoutError() {
        assertDoesNotThrow(() -> {
            manger.createDatabase();
            }, "making database failed " );
    }

    @Test
    void createUserWithoutError() {
        assertDoesNotThrow( () -> {
            addUser(userBob);
        });
    }

    @Test
    void testGetUserData() throws dataAccessException {
        addUser(userBob);
        assertDoesNotThrow( () -> {
            assertEquals(userBob.username(), databaseUserData.getUser(userBob.username()).username());
            assertEquals(userBob.email(), databaseUserData.getUser(userBob.username()).email());
        });
    }

    @Test
    void testMatchPassword()  throws dataAccessException {
        addUser(userBob);
        assertDoesNotThrow( () -> {
            assertTrue(databaseUserData.matchPassword(userBob.username(), userBob.password()));
        });
        assertDoesNotThrow( () -> {
            assertFalse(databaseUserData.matchPassword(userBob.username(), "badPassword"));
        });
    }

    @Test
    void testGetAuthData() {
        assertDoesNotThrow( () -> {
            addAuthData();
        });
        assertDoesNotThrow( () -> {
            assertEquals(databaseAuthData.getAuth("token"), testAuthData);
        });
    }
    @Test
    void testClearTables() throws dataAccessException {
        databaseUserData.clear();
        databaseAuthData.clear();
        databaseGameData.clear();
        assertThrows(dataAccessException.class, () -> databaseUserData.getUser(userBob.username()));
        assertThrows(dataAccessException.class, () -> databaseAuthData.getAuth(testAuthData.authToken()));
    }

    @Test
    void testDeleteAuth() throws dataAccessException {
        addAuthData();
        assertDoesNotThrow( () -> {
            databaseAuthData.deleteAuth(testAuthData.authToken());
        });
        assertThrows(dataAccessException.class, () -> databaseAuthData.getAuth(testAuthData.authToken()));
    }

    @Test
    void testGameCerilization() throws dataAccessException {
        ChessGame testGame = new ChessGame();
        String json = databaseGameData.chessGameToString(testGame);
        assertEquals(testGame, databaseGameData.JSONtoChessGame(json));
    }


    @Test
    void testCreateGame() throws dataAccessException {

        int gameID = addChessGame();
        ChessGame expectedGame = new ChessGame();
        GameData expectedGameData = new GameData(gameID, null, null, "testGame", expectedGame);
        assertEquals(expectedGameData, databaseGameData.getGame(gameID));
    }

    @Test
    void testJoinGame() throws dataAccessException {
        addUser(userBob);
        int gameID = addChessGame();
        insertPlayerToGame(gameID, ChessGame.TeamColor.WHITE, userBob);

        ChessGame expectedGame = new ChessGame();
        GameData expectedGameData = new GameData(gameID, userBob.username(), null, "testGame", expectedGame);
        assertEquals(expectedGameData, databaseGameData.getGame(gameID));

        addUser(userTom);
        insertPlayerToGame(gameID, ChessGame.TeamColor.BLACK, userTom);
        GameData expectedGameData2 = new GameData(gameID, userBob.username(), userTom.username(), "testGame", expectedGame);
        assertEquals(expectedGameData2, databaseGameData.getGame(gameID));
    }

    @Test
    void testListGame() throws dataAccessException {
        addUser(userBob);
        addUser(userTom);
        int gameID1 = addChessGame();
        insertPlayerToGame(gameID1, ChessGame.TeamColor.WHITE, userBob);
        insertPlayerToGame(gameID1, ChessGame.TeamColor.BLACK, userTom);
        model.ModifiedGameData expectedGameData1 = new
                model.ModifiedGameData(gameID1, userBob.username(), userTom.username(), "testGame");

        int gameID2 = addChessGame();
        insertPlayerToGame(gameID2, ChessGame.TeamColor.WHITE, userTom);
        assertThrows(dataAccessException.class, () -> insertPlayerToGame(gameID2, ChessGame.TeamColor.WHITE, userBob));
        insertPlayerToGame(gameID2, ChessGame.TeamColor.BLACK, userBob);
        model.ModifiedGameData expectedGameData2 = new
                model.ModifiedGameData(gameID2, userTom.username(), userBob.username(), "testGame");

        ArrayList<model.ModifiedGameData> expectedGameList = new ArrayList<>();
        expectedGameList.add(expectedGameData1);
        expectedGameList.add(expectedGameData2);

        assertEquals(expectedGameList, databaseGameData.listGame());
    }


    @Test
    void toHTTP() {
        var badRequest = new dataAccessException("test 400", dataAccessException.Code.BadRequest);
        var unathurized = new dataAccessException("test 401", dataAccessException.Code.Unathurized);
        var alreadyTaken = new dataAccessException("test 403", dataAccessException.Code.AlreadyTaken);
        var otherError = new dataAccessException("test 500", dataAccessException.Code.OtherError);
        assertEquals(badRequest.toHttpStatuscode(), 400);
        assertEquals(unathurized.toHttpStatuscode(), 401);
        assertEquals(alreadyTaken.toHttpStatuscode(), 403);
        assertEquals(otherError.toHttpStatuscode(), 500);
    }
    @Test
    public void testDataAccessacception() throws dataAccessException {
        dataAccessException test = new dataAccessException("test", dataAccessException.Code.OtherError);
        String testString = test.toJson();
        assertEquals(testString, new Gson().toJson(Map.of("message", "test", "status", dataAccessException.Code.OtherError)));
    }
}
