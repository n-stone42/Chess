package service;

import chess.ChessGame;
import dataaccess.*;
import dataAccessException.DataAccessException;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;

import java.util.ArrayList;

import UserRecordsModels.*;
import GameRecordModels.*;

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

    public RegisterResult registerBob() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(userBob.username(), userBob.password(),
                userBob.email());
        return userService.register(request);
    }

    public RegisterResult registerTom() throws DataAccessException {
        RegisterRequest request = new RegisterRequest(userTom.username(), userTom.password(),
                userTom.email());
        return userService.register(request);
    }

    @Test
    public void testBobGetUsername() throws DataAccessException {
        RegisterResult result = registerBob();
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

    public CreateGameResult createGame1(
            RegisterResult bobRegisterResult) throws DataAccessException {
        return gameService.creatGame(bobRegisterResult.authToken(), new CreateGameRequest(testGameName1));
    }

    public CreateGameResult createGame2(
            RegisterResult tomRegisterResult) throws DataAccessException {
        return gameService.creatGame(tomRegisterResult.authToken(), new CreateGameRequest(testGameName2));
    }

    @Test
    public void createUserInMemory() {
        try {
            RegisterResult result = registerBob();
            String authToken = result.authToken();
            assertNotNull(authMemory.getAuth(authToken));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogin() {
        try {
            RegisterResult result = registerBob();
            LoginResult loginResult = userService.login(
                    new LoginRequest(userBob.username(), userBob.password()));
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
            RegisterResult result = registerBob();
            LoginResult loginResult = userService.login(
                    new LoginRequest(userBob.username(), userBob.password()));
            LogoutRequest logoutRequest = new LogoutRequest(loginResult.username(),
                    loginResult.authToken());
            assertEquals(userService.logout(logoutRequest), new LogoutResult());
            assertThrows(DataAccessException.class, () -> authMemory.getAuth(loginResult.authToken()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        RegisterResult bobRegisterResult = registerBob();
        String testGameName = "What's A good game Name?";
        CreateGameResult createGameResult = gameService.
                creatGame(bobRegisterResult.authToken(), new CreateGameRequest(testGameName));
        int gamedID = createGameResult.gameID();
        assertEquals(gameMemory.getGame(gamedID), new GameData(gamedID, null, null, testGameName, new ChessGame()));
    }

    @Test
    public void testBadCreateGame() throws DataAccessException {
        RegisterResult bobRegisterResult = registerBob();
        String testGameName = "What's A good game Name?";
        CreateGameResult createGameResult = gameService.
                creatGame(bobRegisterResult.authToken(), new CreateGameRequest(testGameName));
        int gamedID = createGameResult.gameID();
        assertThrows(DataAccessException.class, () -> gameMemory.createGame(gamedID, "badstring"));
    }

    @Test
    public void testBadPassword() throws DataAccessException {
        RegisterResult bobRegisterResult = registerBob();
        assertThrows(DataAccessException.class, () -> userMemory.matchPassword(userTom.username(), "bad password"));
    }

    @Test
    public void duplicateUser() throws DataAccessException {
        registerBob();
        assertThrows(DataAccessException.class, () -> registerBob());
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        RegisterResult bobRegisterResult = registerBob();
        RegisterResult tomRegisterResult = registerTom();
        String testGameName = "What's A good game Name?";
        CreateGameResult createGameResult = gameService.
                creatGame(bobRegisterResult.authToken(), new CreateGameRequest(testGameName));
        int gameID = createGameResult.gameID();
        JoinGameRequest whiteRequest = new JoinGameRequest("WHITE", gameID);
        gameService.joinGame(bobRegisterResult.authToken(), whiteRequest);
        assertEquals(new GameData(gameID, userBob.username(), null, testGameName, new ChessGame()),
                gameMemory.getGame(gameID));
        JoinGameRequest blackRequest = new JoinGameRequest("BLACK", gameID);
        gameService.joinGame(tomRegisterResult.authToken(), blackRequest);
        assertEquals(new GameData(gameID, userBob.username(), userTom.username(), testGameName, new ChessGame()),
                gameMemory.getGame(gameID));
    }

    @Test
    public void testGameList() throws DataAccessException {
        //Register users
        RegisterResult bobRegisterResult = registerBob();
        RegisterResult tomRegisterResult = registerTom();

        ArrayList<model.ModifiedGameData> expectedList = new ArrayList<model.ModifiedGameData>();

        CreateGameResult createGameResult1 = createGame1(tomRegisterResult);
        int gameID1 = createGameResult1.gameID();
        JoinGameRequest whiteRequest1 = new JoinGameRequest("WHITE", gameID1);
        JoinGameRequest blackRequest1 = new JoinGameRequest("BLACK", gameID1);
        gameService.joinGame(bobRegisterResult.authToken(), whiteRequest1);
        gameService.joinGame(tomRegisterResult.authToken(), blackRequest1);
        expectedList.add(new model.ModifiedGameData(gameID1, userBob.username(), userTom.username(), "Game1"));

        CreateGameResult createGameResult2 = createGame2(tomRegisterResult);
        int gameID2 = createGameResult2.gameID();
        JoinGameRequest whiteRequest2 = new JoinGameRequest("WHITE", gameID2);
        JoinGameRequest blackRequest2 = new JoinGameRequest("BLACK", gameID2);
        gameService.joinGame(tomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(bobRegisterResult.authToken(), blackRequest2);
        expectedList.add(new model.ModifiedGameData(gameID2, userTom.username(), userBob.username(), "Game2"));

        ListGameResult expectedResult = new ListGameResult(expectedList);
        assertEquals(expectedResult, gameService.listGames(tomRegisterResult.authToken()));
    }

    @Test
    public void testClearAll() throws DataAccessException {
        RegisterResult bobRegisterResult = registerBob();
        RegisterResult tomRegisterResult = registerTom();

        createGame1(bobRegisterResult);
        CreateGameResult createGameResult2 = createGame2(bobRegisterResult);
        int gameID2 = createGameResult2.gameID();
        JoinGameRequest whiteRequest2 = new JoinGameRequest("WHITE", gameID2);
        JoinGameRequest blackRequest2 = new JoinGameRequest("BLACK", gameID2);
        gameService.joinGame(tomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(bobRegisterResult.authToken(), blackRequest2);
        clearService.eraseAllData();
        assertThrows(DataAccessException.class, () -> userMemory.getUser(userBob.username()));
        assertThrows(DataAccessException.class, () -> userMemory.getUser(userTom.username()));
        assertThrows(DataAccessException.class, () -> gameService.listGames(tomRegisterResult.authToken()));
        assertThrows(DataAccessException.class, () -> gameService.
                creatGame(bobRegisterResult.authToken(), new CreateGameRequest("Game1")));

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
        assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest("bad username", "bad password")));
        assertThrows(DataAccessException.class, () -> userService.login(new LoginRequest(userBob.username(), "bad password")));
    }
}
