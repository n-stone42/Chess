package java.client;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.UserService;


import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {
    //bulid serverFacade first then build client around it
    // must have a positive and negative test for every method in serveFacade

    private static Server server;


    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(8080);
        System.out.println("Started test HTTP server on " + port);
    }


    @AfterAll
    static void stopServer() {
        server.stop();
    }

    UserData userBob = new UserData("Bob", "password", "BobTheGOAT67Gmail.com");
    @Test
    public void negativeLogin() { // login before register
//        assertThrows(DataAccessException.class, () -> UserService.LoginRequest loginRequest = new UserService.LoginRequest(userBob.username(), userBob.password()));

    }

}
