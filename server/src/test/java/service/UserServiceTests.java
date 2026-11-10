package service;

import chess.ChessGame;
import com.google.gson.Gson;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import javax.xml.crypto.Data;
import java.util.ArrayList;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    public MemoryUserDAO userMemory = new MemoryUserDAO();
    public MemoryAuthDAO authMemory = new MemoryAuthDAO();
    public MemoryGameDAO gameMemory = new MemoryGameDAO();
    public UserService userService = new UserService(userMemory, authMemory);
    public GameService gameService = new GameService(userMemory, gameMemory, authMemory);
    public ClearService clearService = new ClearService(userMemory, gameMemory, authMemory);

    UserData userBob = new UserData("Bob", "password", "BobTheGOAT67@gmail.com");
    UserData userTom = new UserData("Tom", "banana", "TomTheM0M@gmail.com");
    String testGameName1 = "Game1";
    String testGameName2 = "Game2";

    @BeforeEach
    public void reset() {
        userMemory.clear();
        authMemory.clear();
        gameMemory.clear();
    }

    public UserService.RegisterResult registerBob() throws DataAccessException {
        UserService.RegisterRequest request = new UserService.RegisterRequest(userBob.username(), userBob.password(),
                userBob.email());
        return userService.register(request);
    }

    public UserService.RegisterResult registerTom() throws DataAccessException {
        UserService.RegisterRequest request = new UserService.RegisterRequest(userTom.username(), userTom.password(),
                userTom.email());
        return userService.register(request);
    }

    @Test
    public void testBobGetUsername() throws DataAccessException {
        UserService.RegisterResult result = registerBob();
        String authToken = result.authToken();
        assertEquals(authMemory.getUsername(authToken), userBob.username());

    }

    @Test
    public void testBadUsername() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authMemory.getUsername("Bad auth"));
    }

    @Test
    public void testBadDeleteAuth() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> authMemory.deleteAuth("bad auth"));
    }

    @Test
    public void testBadGetGame() throws DataAccessException {
        assertThrows(DataAccessException.class, () -> gameMemory.getGame(67));
    }

    public GameService.CreateGameResult createGame1(
            UserService.RegisterResult bobRegisterResult) throws DataAccessException {
        return gameService.creatGame(bobRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName1));
    }

    public GameService.CreateGameResult createGame2(
            UserService.RegisterResult tomRegisterResult) throws DataAccessException {
        return gameService.creatGame(tomRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName2));
    }

    @Test
    public void createUserInMemory() {
        try {
            UserService.RegisterResult result = registerBob();
            String authToken = result.authToken();
            assertNotNull(authMemory.getAuth(authToken));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogin() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(
                    new UserService.LoginRequest(userBob.username(), userBob.password()));
            assertTrue(loginResult.username() == userBob.username() && loginResult.authToken() != result.authToken());
            assertEquals(authMemory.getAuth(loginResult.authToken()),
                    new AuthData(loginResult.authToken(), userBob.username()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogout() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(
                    new UserService.LoginRequest(userBob.username(), userBob.password()));
            UserService.LogoutRequest logoutRequest = new UserService.LogoutRequest(loginResult.username(),
                    loginResult.authToken());
            assertEquals(userService.logout(logoutRequest), new UserService.LogoutResult());
            assertThrows(DataAccessException.class, () -> authMemory.getAuth(loginResult.authToken()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        UserService.RegisterResult bobRegisterResult = registerBob();
        String testGameName = "What's A good game Name?";
        GameService.CreateGameResult createGameResult = gameService.
                creatGame(bobRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName));
        int gamedID = createGameResult.gameID();
        assertEquals(gameMemory.getGame(gamedID), new GameData(gamedID, null, null, testGameName, new ChessGame()));
    }

    @Test
    public void testBadCreateGame() throws DataAccessException {
        UserService.RegisterResult bobRegisterResult = registerBob();
        String testGameName = "What's A good game Name?";
        GameService.CreateGameResult createGameResult = gameService.
                creatGame(bobRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName));
        int gamedID = createGameResult.gameID();
        assertThrows(DataAccessException.class, () -> gameMemory.createGame(gamedID, "badstring"));
    }

    @Test
    public void testBadPassword() throws DataAccessException {
        UserService.RegisterResult bobRegisterResult = registerBob();
        assertThrows(DataAccessException.class, () -> userMemory.matchPassword(userTom.username(), "bad password"));
    }

    @Test
    public void duplicateUser() throws DataAccessException {
        registerBob();
        assertThrows(DataAccessException.class, () -> registerBob());
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        UserService.RegisterResult bobRegisterResult = registerBob();
        UserService.RegisterResult tomRegisterResult = registerTom();
        String testGameName = "What's A good game Name?";
        GameService.CreateGameResult createGameResult = gameService.
                creatGame(bobRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName));
        int gameID = createGameResult.gameID();
        GameService.JoinGameRequest whiteRequest = new GameService.JoinGameRequest("WHITE", gameID);
        gameService.joinGame(bobRegisterResult.authToken(), whiteRequest);
        assertEquals(new GameData(gameID, userBob.username(), null, testGameName, new ChessGame()),
                gameMemory.getGame(gameID));
        GameService.JoinGameRequest blackRequest = new GameService.JoinGameRequest("BLACK", gameID);
        gameService.joinGame(tomRegisterResult.authToken(), blackRequest);
        assertEquals(new GameData(gameID, userBob.username(), userTom.username(), testGameName, new ChessGame()),
                gameMemory.getGame(gameID));
    }

    @Test
    public void testGameList() throws DataAccessException {
        //Register users
        UserService.RegisterResult bobRegisterResult = registerBob();
        UserService.RegisterResult tomRegisterResult = registerTom();

        ArrayList<model.ModifiedGameData> expectedList = new ArrayList<model.ModifiedGameData>();

        GameService.CreateGameResult createGameResult1 = createGame1(tomRegisterResult);
        int gameID1 = createGameResult1.gameID();
        GameService.JoinGameRequest whiteRequest1 = new GameService.JoinGameRequest("WHITE", gameID1);
        GameService.JoinGameRequest blackRequest1 = new GameService.JoinGameRequest("BLACK", gameID1);
        gameService.joinGame(bobRegisterResult.authToken(), whiteRequest1);
        gameService.joinGame(tomRegisterResult.authToken(), blackRequest1);
        expectedList.add(new model.ModifiedGameData(gameID1, userBob.username(), userTom.username(), "Game1"));

        GameService.CreateGameResult createGameResult2 = createGame2(tomRegisterResult);
        int gameID2 = createGameResult2.gameID();
        GameService.JoinGameRequest whiteRequest2 = new GameService.JoinGameRequest("WHITE", gameID2);
        GameService.JoinGameRequest blackRequest2 = new GameService.JoinGameRequest("BLACK", gameID2);
        gameService.joinGame(tomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(bobRegisterResult.authToken(), blackRequest2);
        expectedList.add(new model.ModifiedGameData(gameID2, userTom.username(), userBob.username(), "Game2"));

        GameService.ListGameResult expectedResult = new GameService.ListGameResult(expectedList);
        assertEquals(expectedResult, gameService.listGames(tomRegisterResult.authToken()));
    }

    @Test
    public void testClearAll() throws DataAccessException {
        UserService.RegisterResult bobRegisterResult = registerBob();
        UserService.RegisterResult tomRegisterResult = registerTom();

        createGame1(bobRegisterResult);
        GameService.CreateGameResult createGameResult2 = createGame2(bobRegisterResult);
        int gameID2 = createGameResult2.gameID();
        GameService.JoinGameRequest whiteRequest2 = new GameService.JoinGameRequest("WHITE", gameID2);
        GameService.JoinGameRequest blackRequest2 = new GameService.JoinGameRequest("BLACK", gameID2);
        gameService.joinGame(tomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(bobRegisterResult.authToken(), blackRequest2);
        clearService.eraseAllData();
        assertThrows(DataAccessException.class, () -> userMemory.getUser(userBob.username()));
        assertThrows(DataAccessException.class, () -> userMemory.getUser(userTom.username()));
        assertThrows(DataAccessException.class, () -> gameService.listGames(tomRegisterResult.authToken()));
        assertThrows(DataAccessException.class, () -> gameService.
                creatGame(bobRegisterResult.authToken(), new GameService.CreateGameRequest("Game1")));

    }

    @Test
    public void testToHttpStatusCode() throws DataAccessException {
        var badRequest = new DataAccessException("test 400", DataAccessException.Code.BadRequest);
        var unathurized = new DataAccessException("test 401", DataAccessException.Code.Unathurized);
        var alreadyTaken = new DataAccessException("test 403", DataAccessException.Code.AlreadyTaken);
        var otherError = new DataAccessException("test 500", DataAccessException.Code.OtherError);
        assertEquals(badRequest.toHttpStatuscode(), 400);
        assertEquals(unathurized.toHttpStatuscode(), 401);
        assertEquals(alreadyTaken.toHttpStatuscode(), 403);
        assertEquals(otherError.toHttpStatuscode(), 500);
    }

    @Test
    public void testBadLogins() throws  DataAccessException {
        registerBob();
        assertThrows(DataAccessException.class, () -> userService.login(new UserService.LoginRequest("bad username", "bad password")));
        assertThrows(DataAccessException.class, () -> userService.login(new UserService.LoginRequest(userBob.username(), "bad password")));
    }
}
