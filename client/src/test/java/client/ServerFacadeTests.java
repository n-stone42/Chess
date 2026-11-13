package client;

import gameRecordModels.*;
import dataAccessException.dataAccessException;
import model.ModifiedGameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import userRecordsModels.*;
import java.util.ArrayList;
import static org.junit.jupiter.api.Assertions.*;


public class ServerFacadeTests {
    //bulid serverFacade first then build client around it
    // must have a positive and negative test for every method in serveFacade
    private static Server server;
    static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        int port = server.run(8080);
        String url = "http://localhost:" + port;
        serverFacade = new ServerFacade(url);
        System.out.println("Started test HTTP server on " + port);
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }


    UserData userBob = new UserData("Bob", "password", "BobTheGOAT67Gmail.com");

    public RegisterResult registerUser(UserData user) throws dataAccessException {
        return serverFacade.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));
    }

    public LoginResult loginUser(UserData user) throws dataAccessException {
        return serverFacade.login(new LoginRequest(user.username(), user.password()));
    }


    @Test
    public void negativeLogin() throws dataAccessException { // login before register
        serverFacade.clear();
        assertThrows(dataAccessException.class, () -> serverFacade.login(new LoginRequest(userBob.username(), userBob.password())));
    }

    @Test
    public void positiveRegister() {
        try {
            serverFacade.clear();
            RegisterResult expectedResult = new RegisterResult(userBob.username(), "someAuthToken");
            RegisterResult result = serverFacade.registerUser(new RegisterRequest(userBob.username(), userBob.password(), userBob.email()));
            System.out.println(result);
            assertEquals(expectedResult.username(), result.username());
            assertNotNull(result.authToken());
        }
        catch (dataAccessException exception) {
            assertFalse(true, "test failed, threw Error: " + exception);
        }
    }

    @Test
    public void positiveLogin() throws dataAccessException {
        serverFacade.clear();
        serverFacade.registerUser(new RegisterRequest(userBob.username(), userBob.password(), userBob.email()));
        serverFacade.login(new LoginRequest(userBob.username(), userBob.password()));
    }

    @Test
    public void positiveLogout() throws dataAccessException {
        serverFacade.clear();
        serverFacade.registerUser(new RegisterRequest(userBob.username(), userBob.password(), userBob.email()));
        LoginResult loginResult = serverFacade.login(new LoginRequest(userBob.username(), userBob.password()));
        System.out.println("now trying logout");
        serverFacade.logout(loginResult.authToken());
        assertThrows(dataAccessException.class, () -> serverFacade.logout(loginResult.authToken() ) );
    }

    @Test
    public void badLogout() throws dataAccessException { //logout before login
        serverFacade.clear();
        assertThrows(dataAccessException.class, () -> serverFacade.logout("bad auth token" ) );
    }

    @Test
    public void testMakeGame() throws dataAccessException {
        serverFacade.clear();
        registerUser(userBob);
        LoginResult loginResult = loginUser(userBob);
        serverFacade.createGame(new CreateGameRequest("testgame"), loginResult.authToken());
    }

    @Test
    public void badMakeGame() throws dataAccessException {
        serverFacade.clear();
        registerUser(userBob);
        LoginResult loginResult = loginUser(userBob);
        assertThrows(dataAccessException.class, () -> serverFacade.createGame(new CreateGameRequest(null), loginResult.authToken()));
    }

    @Test
    public void listGame() throws dataAccessException {
        serverFacade.clear();
        registerUser(userBob);
        LoginResult loginResult = loginUser(userBob);
        CreateGameResult result = serverFacade.createGame(new CreateGameRequest("testgame"), loginResult.authToken());
        ArrayList<ModifiedGameData> expectedGameList = new ArrayList<>();
        ModifiedGameData expectedGameData = new ModifiedGameData(result.gameID(), null,null, "testgame");
        expectedGameList.add(expectedGameData);
        ListGameResult expectedResult = new ListGameResult(expectedGameList);
        assertEquals(expectedResult, serverFacade.listGame(loginResult.authToken()));
    }

    @Test
    public void  badListGame() throws dataAccessException {
        serverFacade.clear();
        assertThrows(dataAccessException.class, () -> serverFacade.listGame("bad auth token") );
        registerUser(userBob);
    }

    @Test
    public void joinGameTest() throws dataAccessException {
        serverFacade.clear();
        registerUser(userBob);
        LoginResult loginResult = loginUser(userBob);
        CreateGameResult createGameResult = serverFacade.createGame(new CreateGameRequest("testgame"), loginResult.authToken());
        JoinGameResult joinGameResult = serverFacade.joinGame(loginResult.authToken(), "WHITE",
                createGameResult.gameID());
        ArrayList<ModifiedGameData> expectedGameList = new ArrayList<>();
        ModifiedGameData expectedGameData = new ModifiedGameData(createGameResult.gameID(), userBob.username(), null, "testgame");
        expectedGameList.add(expectedGameData);
        ListGameResult expectedResult = new ListGameResult(expectedGameList);
        assertEquals(expectedResult, serverFacade.listGame(loginResult.authToken()));
    }

    @Test
    public void negativeJoinGame() throws dataAccessException {
        serverFacade.clear();
        registerUser(userBob);
        LoginResult loginResult = loginUser(userBob);
        CreateGameResult createGameResult = serverFacade.createGame(new CreateGameRequest("testgame"), loginResult.authToken());
        assertThrows(dataAccessException.class, () -> serverFacade.joinGame("bad auth token", "Banana", createGameResult.gameID()));
        assertThrows(dataAccessException.class, () -> serverFacade.joinGame(loginResult.authToken(), "Banana", createGameResult.gameID()));
        assertThrows(dataAccessException.class, () -> serverFacade.joinGame(loginResult.authToken(), "WHITE", 5));
        JoinGameResult joinGameResult = serverFacade.joinGame(loginResult.authToken(), "WHITE",
                createGameResult.gameID());
        assertThrows(dataAccessException.class, () -> serverFacade.joinGame(loginResult.authToken(), "WHITE", createGameResult.gameID()));
    }

}
