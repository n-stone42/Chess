package client;

import client.ServerFacade;
import dataaccess.DataAccessException;
import dataaccess.sql.DataBaseTests;
import io.javalin.config.Key;
import io.javalin.http.Context;
import io.javalin.http.HandlerType;
import io.javalin.http.HttpStatus;
import io.javalin.json.JsonMapper;
import io.javalin.plugin.ContextPlugin;
import io.javalin.security.RouteRole;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.UserData;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.jupiter.api.*;
import server.Server;
import service.UserService;
import UserRecordsModels.*;


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

    public RegisterResult registerUser(UserData user) throws DataAccessException{
        return serverFacade.registerUser(new RegisterRequest(user.username(), user.password(), user.email()));
    }

    public LoginResult loginUser(UserData user) throws DataAccessException{
        return serverFacade.login(new LoginRequest(user.username(), user.password()));
    }


    @Test
    public void negativeLogin() throws DataAccessException{ // login before register
        serverFacade.clear();
        assertThrows(DataAccessException.class, () -> serverFacade.login(new LoginRequest(userBob.username(), userBob.password())));
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
        catch (DataAccessException exception) {
            assertFalse(true, "test failed, threw Error: " + exception);
        }
    }

    @Test
    public void positiveLogin() throws DataAccessException {
        serverFacade.clear();
        serverFacade.login(new LoginRequest(userBob.username(), userBob.password()));
        serverFacade.clear();
    }

}
