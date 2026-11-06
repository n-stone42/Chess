package dataaccess;

import chess.ChessGame;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.eclipse.jetty.server.Authentication;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.xml.crypto.Data;

import static org.junit.jupiter.api.Assertions.*;


public class DataBaseTests {
    public DatabaseUserData databaseUserData = new DatabaseUserData();
    public DatabaseAuthData databaseAuthData = new DatabaseAuthData();
    public DatabaseGameData databaseGameData = new DatabaseGameData();
    public DatabaseManager manger = new DatabaseManager();

    public static UserData userBob = new UserData("Bob", "password", "B0BTheGoat67@gmail.com");

    public AuthData testAuthData = new AuthData("token", userBob.username());
    void addUserBob() throws DataAccessException{
        databaseUserData.createUser(userBob.username(), userBob.password(), userBob.email());
    }

    void addAuthData() throws DataAccessException{
        databaseAuthData.createAuth(testAuthData.authToken(), userBob.username());
    }

    void addChessGame() throws  DataAccessException{
        databaseGameData.createGame(1, "testGame");
    }

    void bobJoinsGame() throws DataAccessException{
        databaseGameData.joinGame(1, ChessGame.TeamColor.WHITE, userBob.username());
    }

    @BeforeEach
    void setup()  throws DataAccessException {
    try {
        clearTables(); }
    catch (DataAccessException exception) {}
    createTables();

    }

    private void clearTables() throws DataAccessException{
        databaseUserData.clear();
        databaseAuthData.clear();
        databaseGameData.clear();
    }

    private void createTables() throws DataAccessException {
        databaseUserData.makeUserTable();
        databaseAuthData.makeAuthDataTable();
        databaseGameData.makeGameDataTables();

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
            addUserBob();
        });
    }

    @Test
    void testGetUserData() throws DataAccessException{
        addUserBob();
        assertDoesNotThrow( () -> {
            assertEquals(userBob.username(), databaseUserData.getUser(userBob.username()).username());
            assertEquals(userBob.email(), databaseUserData.getUser(userBob.username()).email());
        });
    }

    @Test
    void testMatchPassword()  throws DataAccessException{
        addUserBob();
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
    void testClearTables() throws DataAccessException{
        databaseUserData.clear();
        databaseAuthData.clear();
        databaseGameData.clear();
        assertThrows(DataAccessException.class, () -> databaseUserData.getUser(userBob.username()));
        assertThrows(DataAccessException.class, () -> databaseAuthData.getAuth(testAuthData.authToken()));
    }

    @Test
    void testDeleteAuth() throws DataAccessException{
        addAuthData();
        assertDoesNotThrow( () -> {
            databaseAuthData.deleteAuth(testAuthData.authToken());
        });
        assertThrows(DataAccessException.class, () -> databaseAuthData.getAuth(testAuthData.authToken()));
    }

    @Test
    void testGameCerilization() throws DataAccessException {
        ChessGame testGame = new ChessGame();
        String json = databaseGameData.chessGameToString(testGame);
        assertEquals(testGame, databaseGameData.JSONtoChessGame(json));
    }

    @Test
    void deleteGameTable() {
        assertDoesNotThrow( () -> {
            databaseGameData.deleteGameTable();
        });
    }

    @Test
    void testCreateGame() throws DataAccessException{
        assertDoesNotThrow( () -> {
            addChessGame();
        });
        ChessGame expectedGame = new ChessGame();
        GameData expectedGameData = new GameData(1, null, null, "testGame", expectedGame);
        assertEquals(expectedGameData, databaseGameData.getGame(1));
    }

    @Test
    void testJoingGame() throws DataAccessException{
        addUserBob();
        assertDoesNotThrow( () -> {
            addChessGame();
        });
        assertDoesNotThrow( () -> {
            bobJoinsGame();
        });
        ChessGame expectedGame = new ChessGame();
        GameData expectedGameData = new GameData(1, userBob.username(), null, "testGame", expectedGame);
        assertEquals(expectedGameData, databaseGameData.getGame(1));
    }

}
