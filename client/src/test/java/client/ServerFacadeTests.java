package client;

import client.ServerFacade;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.UserService;
import UserRecordsModels.*;


import javax.xml.crypto.Data;

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
    @Test
    public void negativeLogin() { // login before register
        assertThrows(DataAccessException.class, () -> serverFacade.login(new LoginRequest(userBob.username(), userBob.password())));
    }

    @Test
    public void positiveRegister() {
        try {
            RegisterResult expectedResult = new RegisterResult("userBob", "someAuthToken");
            RegisterResult result = serverFacade.registerUser(new RegisterRequest(userBob.username(), userBob.password(), userBob.email()));
            System.out.println(result);
            assertEquals(expectedResult.username(), result.username());
            assertNotNull(result.authToken());
        }
        catch (DataAccessException exception) {
            assertFalse(true, "test failed, threw Error: +" + exception);
        }
    }

//    @Test
//    public void positiveLogin() throws DataAccessException {
//        assertEquals("some string",serverFacade.login(new LoginRequest(userBob.username(), userBob.password())));
//    }

}
