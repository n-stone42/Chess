package passoff.server;
import chess.ChessGame;
import dataaccess.*;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import service.GameService;
import service.UserService;
import static org.junit.jupiter.api.Assertions.*;
import static passoff.server.UserServiceTests.userService;

public class GameServiceTests {
    public  MemoryUserDAO userMemory = new MemoryUserDAO();
    public MemoryAuthDAO authMemory = new MemoryAuthDAO();
    public MemoryGameDAO gameMemory = new MemoryGameDAO();
     public UserService userService = new UserService(userMemory, authMemory);
    public GameService gameService = new GameService(userMemory, gameMemory, authMemory);

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

}
