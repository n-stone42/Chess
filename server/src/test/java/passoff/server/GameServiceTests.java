package passoff.server;
import chess.ChessGame;
import dataaccess.*;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.ClearService;
import service.GameService;
import service.UserService;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static passoff.server.UserServiceTests.userService;

public class GameServiceTests {
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
    public void testCreateGame() throws DataAccessException{
        UserService.RegisterResult BobRegisterResult = registerBob();
        String TestGameName = "What's A good game Name?";
        int GameID = gameService.creatGame(BobRegisterResult.authToken(), TestGameName);
        assertEquals(gameMemory.getGame(GameID), new GameData(GameID,null,null,TestGameName,new ChessGame()));
    }

    @Test
    public void testJoinGame() throws DataAccessException {
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName = "What's A good game Name?";
        int GameID = gameService.creatGame(BobRegisterResult.authToken(), TestGameName);
        gameService.joinGame(BobRegisterResult.authToken(), ChessGame.TeamColor.WHITE, GameID);
        assertEquals(new GameData(GameID, User_Bob.username(), null,TestGameName,new ChessGame()), gameMemory.getGame(GameID));

        gameService.joinGame(TomRegisterResult.authToken(), ChessGame.TeamColor.BLACK, GameID);
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
        int GameID1 = gameService.creatGame(BobRegisterResult.authToken(), TestGameName1);
        gameService.joinGame(BobRegisterResult.authToken(), ChessGame.TeamColor.WHITE, GameID1);
        gameService.joinGame(TomRegisterResult.authToken(), ChessGame.TeamColor.BLACK, GameID1);
        GameData expectedGameData1 = new GameData(GameID1, User_Bob.username(), User_Tom.username(), TestGameName1,new ChessGame());

        //Create 2nd game
        int GameID2 = gameService.creatGame(TomRegisterResult.authToken(), TestGameName2);
        gameService.joinGame(TomRegisterResult.authToken(), ChessGame.TeamColor.WHITE, GameID2);
        gameService.joinGame(BobRegisterResult.authToken(), ChessGame.TeamColor.BLACK, GameID2);
        GameData expectedGameData2 = new GameData(GameID2, User_Tom.username(), User_Bob.username(), TestGameName2,new ChessGame());

        ArrayList<GameData> expectedResult = new ArrayList<GameData>();
        expectedResult.add(expectedGameData1);
        expectedResult.add(expectedGameData2);
        assertEquals(expectedResult, gameService.listGames(TomRegisterResult.authToken()));
    }

    @Test
    public void testClearAll() throws DataAccessException{
        UserService.RegisterResult BobRegisterResult = registerBob();
        UserService.RegisterResult TomRegisterResult = registerTom();
        String TestGameName1 = "Game1";
        String TestGameName2 = "Game2";

        //Create 1st game
        int GameID1 = gameService.creatGame(BobRegisterResult.authToken(), TestGameName1);
        gameService.joinGame(BobRegisterResult.authToken(), ChessGame.TeamColor.WHITE, GameID1);
        gameService.joinGame(TomRegisterResult.authToken(), ChessGame.TeamColor.BLACK, GameID1);
        GameData expectedGameData1 = new GameData(GameID1, User_Bob.username(), User_Tom.username(), TestGameName1,new ChessGame());

        //Create 2nd game
        int GameID2 = gameService.creatGame(TomRegisterResult.authToken(), TestGameName2);
        gameService.joinGame(TomRegisterResult.authToken(), ChessGame.TeamColor.WHITE, GameID2);
        gameService.joinGame(BobRegisterResult.authToken(), ChessGame.TeamColor.BLACK, GameID2);
        clearService.eraseAllData();
        assertThrows(DataAccessException.class,() -> userMemory.getUser(User_Bob.username()));
        assertThrows(DataAccessException.class,() -> userMemory.getUser(User_Tom.username()));
        assertThrows(DataAccessException.class,() -> gameService.listGames(TomRegisterResult.authToken()));
        assertThrows(DataAccessException.class,() -> gameService.creatGame(BobRegisterResult.authToken(), "Game1"));

    }


}
