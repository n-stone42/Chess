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
    public  MemoryUserDAO userMemory = new MemoryUserDAO();
    public MemoryAuthDAO authMemory = new MemoryAuthDAO();
    public MemoryGameDAO gameMemory = new MemoryGameDAO();
    public UserService userService = new UserService(userMemory, authMemory);
    public GameService gameService = new GameService(userMemory, gameMemory, authMemory);
    public ClearService clearService = new ClearService(userMemory, gameMemory, authMemory);

    UserData User_Bob = new UserData("Bob", "password", "BobTheGOAT67@gmail.com");
    UserData User_Tom = new UserData("Tom", "banana", "TomTheM0M@gmail.com");

    @BeforeEach
    public void reset() {
        userMemory.clear();
        authMemory.clear();
        gameMemory.clear();
    }

    public UserService.RegisterResult registerBob() throws DataAccessException{
        UserService.RegisterRequest request= new UserService.RegisterRequest(User_Bob.username(), User_Bob.password(),User_Bob.email());
        return userService.register(request);
    }

    public UserService.RegisterResult registerTom() throws DataAccessException{
        UserService.RegisterRequest request= new UserService.RegisterRequest(User_Tom.username(), User_Tom.password(),User_Tom.email());
        return userService.register(request);
    }

    @Test
    public void CreateUserInMemory() {
        try {
            UserService.RegisterResult result = registerBob();
            String authToken = result.authToken();
            assertNotNull(authMemory.getAuth(authToken));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testLogin() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(new UserService.LoginRequest(User_Bob.username(), User_Bob.password()));
            assertTrue(loginResult.username() == User_Bob.username() && loginResult.authToken() != result.authToken());
            assertEquals(authMemory.getAuth(loginResult.authToken()), new AuthData(loginResult.authToken(),User_Bob.username()));
        }
        catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testLogout() {
        try {
            UserService.RegisterResult result = registerBob();
            UserService.LoginResult loginResult = userService.login(new UserService.LoginRequest(User_Bob.username(), User_Bob.password()));
            UserService.LogoutRequest logoutRequest = new UserService.LogoutRequest(loginResult.username(), loginResult.authToken());
            assertEquals(userService.logout(logoutRequest), new UserService.LogoutResult());
            assertThrows(DataAccessException.class,() -> authMemory.getAuth(loginResult.authToken()));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    public void testCreateGame() throws DataAccessException{
        UserService.RegisterResult BobRegisterResult = registerBob();
        String TestGameName = "What's A good game Name?";
        service.GameService.CreateGameResult  createGameResult= gameService.creatGame(BobRegisterResult.authToken(), TestGameName);
        int GameID = createGameResult.GameId();
        assertEquals(gameMemory.getGame(GameID), new GameData(GameID,null,null,TestGameName,new ChessGame()));
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName = "What's A good game Name?";
        service.GameService.CreateGameResult  createGameResult = gameService.creatGame(BobRegisterResult.authToken(), TestGameName);
        int GameID = createGameResult.GameId();
        GameService.JoinGameRequest whiteRequest = new GameService.JoinGameRequest("WHITE",GameID);
        gameService.joinGame(BobRegisterResult.authToken(), whiteRequest);
        assertEquals(new GameData(GameID, User_Bob.username(), null,TestGameName,new ChessGame()), gameMemory.getGame(GameID));
        GameService.JoinGameRequest blackRequest = new GameService.JoinGameRequest("BLACK",GameID);
        gameService.joinGame(TomRegisterResult.authToken(), blackRequest);
        assertEquals(new GameData(GameID, User_Bob.username(), User_Tom.username(), TestGameName,new ChessGame()), gameMemory.getGame(GameID));
    }
    // can test errors like if white username is already filled, game ID is wrong  ect

    @Test
    public void testGameList() throws DataAccessException {
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName1 = "Game1";
        String TestGameName2 = "Game2";

        //Create 1st game
        service.GameService.CreateGameResult  createGameResult1 = gameService.creatGame(BobRegisterResult.authToken(), TestGameName1);
        int GameID1 = createGameResult1.GameId();
        GameService.JoinGameRequest whiteRequest1 = new GameService.JoinGameRequest("WHITE",GameID1);
        GameService.JoinGameRequest blackRequest1 = new GameService.JoinGameRequest("BLACK",GameID1);
        gameService.joinGame(BobRegisterResult.authToken(), whiteRequest1 );
        gameService.joinGame(TomRegisterResult.authToken(), blackRequest1);
        GameData expectedGameData1 = new GameData(GameID1, User_Bob.username(), User_Tom.username(), TestGameName1,new ChessGame());

        //Create 2nd game
        service.GameService.CreateGameResult  createGameResult2 = gameService.creatGame(TomRegisterResult.authToken(), TestGameName2);
        int GameID2 = createGameResult2.GameId();
        GameService.JoinGameRequest whiteRequest2 = new GameService.JoinGameRequest("WHITE",GameID2);
        GameService.JoinGameRequest blackRequest2 = new GameService.JoinGameRequest("BLACK",GameID2);
        gameService.joinGame(TomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(BobRegisterResult.authToken(), blackRequest2);
        GameData expectedGameData2 = new GameData(GameID2, User_Tom.username(), User_Bob.username(), TestGameName2,new ChessGame());

        ArrayList<GameData> expectedList = new ArrayList<GameData>();
        expectedList.add(expectedGameData1);
        expectedList.add(expectedGameData2);
        GameService.ListGameResult expectedResult = new GameService.ListGameResult(expectedList);
        assertEquals(expectedResult, gameService.listGames(TomRegisterResult.authToken()));
    }

    @Test
    public void testClearAll() throws DataAccessException{
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName1 = "Game1";
        String TestGameName2 = "Game2";

        //Create 1st game
        service.GameService.CreateGameResult  createGameResult1 = gameService.creatGame(BobRegisterResult.authToken(), TestGameName1);
        int GameID1 = createGameResult1.GameId();
        GameService.JoinGameRequest whiteRequest1 = new GameService.JoinGameRequest("WHITE",GameID1);
        GameService.JoinGameRequest blackRequest1 = new GameService.JoinGameRequest("BLACK",GameID1);
        gameService.joinGame(BobRegisterResult.authToken(), whiteRequest1);
        gameService.joinGame(TomRegisterResult.authToken(), blackRequest1);
        GameData expectedGameData1 = new GameData(GameID1, User_Bob.username(), User_Tom.username(), TestGameName1,new ChessGame());

        //Create 2nd game
        service.GameService.CreateGameResult  createGameResult2 = gameService.creatGame(TomRegisterResult.authToken(), TestGameName2);
        int GameID2 = createGameResult2.GameId();
        GameService.JoinGameRequest whiteRequest2 = new GameService.JoinGameRequest("WHITE",GameID2);
        GameService.JoinGameRequest blackRequest2 = new GameService.JoinGameRequest("BLACK",GameID2);
        gameService.joinGame(TomRegisterResult.authToken(), whiteRequest2);
        gameService.joinGame(BobRegisterResult.authToken(), blackRequest2);
        clearService.eraseAllData();
        assertThrows(DataAccessException.class,() -> userMemory.getUser(User_Bob.username()));
        assertThrows(DataAccessException.class,() -> userMemory.getUser(User_Tom.username()));
        assertThrows(DataAccessException.class,() -> gameService.listGames(TomRegisterResult.authToken()));
        assertThrows(DataAccessException.class,() -> gameService.creatGame(BobRegisterResult.authToken(), "Game1"));

    }


}
