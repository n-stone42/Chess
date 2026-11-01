package passoff.server;

import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

public class ServiceTests {
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
        UserService.RegisterRequest request = new UserService.RegisterRequest(userBob.username(), userBob.password(), userBob.email());
        return userService.register(request);
    }

    public UserService.RegisterResult registerTom() throws DataAccessException {
        UserService.RegisterRequest request = new UserService.RegisterRequest(userTom.username(), userTom.password(), userTom.email());
        return userService.register(request);
    }

    public service.GameService.CreateGameResult createGame1(UserService.RegisterResult BobRegisterResult, UserService.RegisterResult TomRegisterResult) throws DataAccessException {
        return gameService.creatGame(BobRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName1));
    }

    public service.GameService.CreateGameResult createGame2(UserService.RegisterResult BobRegisterResult, UserService.RegisterResult TomRegisterResult) throws DataAccessException {
        return gameService.creatGame(TomRegisterResult.authToken(), new GameService.CreateGameRequest(testGameName2));
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
            UserService.LoginResult loginResult = userService.login(new UserService.LoginRequest(userBob.username(), userBob.password()));
            assertTrue(loginResult.username() == userBob.username() && loginResult.authToken() != result.authToken());
            assertEquals(authMemory.getAuth(loginResult.authToken()), new AuthData(loginResult.authToken(), userBob.username()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testLogout() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(new UserService.LoginRequest(userBob.username(), userBob.password()));
            UserService.LogoutRequest logoutRequest = new UserService.LogoutRequest(loginResult.username(), loginResult.authToken());
            assertEquals(userService.logout(logoutRequest), new UserService.LogoutResult());
            assertThrows(DataAccessException.class, () -> authMemory.getAuth(loginResult.authToken()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    public void testCreateGame() throws DataAccessException {
        UserService.RegisterResult BobRegisterResult = registerBob();
        String TestGameName = "What's A good game Name?";
        service.GameService.CreateGameResult createGameResult = gameService.creatGame(BobRegisterResult.authToken(), new GameService.CreateGameRequest(TestGameName));
        int GameID = createGameResult.gameID();
        assertEquals(gameMemory.getGame(GameID), new GameData(GameID, null, null, TestGameName, new ChessGame()));
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName = "What's A good game Name?";
        service.GameService.CreateGameResult createGameResult = gameService.creatGame(BobRegisterResult.authToken(), new GameService.CreateGameRequest(TestGameName));
        int GameID = createGameResult.gameID();
        GameService.JoinGameRequest whiteRequest = new GameService.JoinGameRequest("WHITE", GameID);
        gameService.joinGame(BobRegisterResult.authToken(), whiteRequest);
        assertEquals(new GameData(GameID, userBob.username(), null, TestGameName, new ChessGame()), gameMemory.getGame(GameID));
        GameService.JoinGameRequest blackRequest = new GameService.JoinGameRequest("BLACK", GameID);
        gameService.joinGame(TomRegisterResult.authToken(), blackRequest);
        assertEquals(new GameData(GameID, userBob.username(), userTom.username(), TestGameName, new ChessGame()), gameMemory.getGame(GameID));
    }

    @Test
    public void testGameList() throws DataAccessException {
        //Register users
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();

        ArrayList<MemoryGameDAO.ModifiedGameData> expectedList = new ArrayList<MemoryGameDAO.ModifiedGameData>();

        GameService.CreateGameResult createGameResult1 = createGame1(BobRegisterResult, TomRegisterResult);
        int GameID1 = createGameResult1.gameID();
        GameService.JoinGameRequest whiteRequest1 = new GameService.JoinGameRequest("WHITE", GameID1);
        GameService.JoinGameRequest blackRequest1 = new GameService.JoinGameRequest("BLACK", GameID1);
        gameService.joinGame(BobRegisterResult.authToken(), whiteRequest1);
        gameService.joinGame(TomRegisterResult.authToken(), blackRequest1);
        expectedList.add(new MemoryGameDAO.ModifiedGameData(GameID1, userBob.username(), userTom.username(), "Game1"));

        GameService.CreateGameResult createGameResult2 = createGame2(BobRegisterResult, TomRegisterResult);
        int GameID2 = createGameResult2.gameID();
        GameService.JoinGameRequest whiteRequest2 = new GameService.JoinGameRequest("WHITE", GameID2);
        GameService.JoinGameRequest blackRequest2 = new GameService.JoinGameRequest("BLACK", GameID2);
        gameService.joinGame(TomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(BobRegisterResult.authToken(), blackRequest2);
        expectedList.add(new MemoryGameDAO.ModifiedGameData(GameID2, userTom.username(), userBob.username(), "Game2"));

        GameService.ListGameResult expectedResult = new GameService.ListGameResult(expectedList);
        assertEquals(expectedResult, gameService.listGames(TomRegisterResult.authToken()));
    }

    @Test
    public void testClearAll() throws DataAccessException {
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName1 = "Game1";
        String TestGameName2 = "Game2";

        createGame1(BobRegisterResult, TomRegisterResult);
        service.GameService.CreateGameResult createGameResult2 = createGame2(BobRegisterResult, TomRegisterResult);
        int GameID2 = createGameResult2.gameID();
        GameService.JoinGameRequest whiteRequest2 = new GameService.JoinGameRequest("WHITE", GameID2);
        GameService.JoinGameRequest blackRequest2 = new GameService.JoinGameRequest("BLACK", GameID2);
        gameService.joinGame(TomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(BobRegisterResult.authToken(), blackRequest2);
        clearService.eraseAllData();
        assertThrows(DataAccessException.class, () -> userMemory.getUser(userBob.username()));
        assertThrows(DataAccessException.class, () -> userMemory.getUser(userTom.username()));
        assertThrows(DataAccessException.class, () -> gameService.listGames(TomRegisterResult.authToken()));
        assertThrows(DataAccessException.class, () -> gameService.creatGame(BobRegisterResult.authToken(), new GameService.CreateGameRequest("Game1")));

    }

    @Test
    public void testDeleteAuthWithBadAuth() {

    }

}
