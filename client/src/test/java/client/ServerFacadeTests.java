package java.client;

import client.ServerFacade;
import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import service.UserService;
import UserRecordsModels.*;


import static org.junit.jupiter.api.Assertions.assertThrows;


public class ServerFacadeTests {
    //bulid serverFacade first then build client around it
    // must have a positive and negative test for every method in serveFacade
    private static Server server;
    static ServerFacade serverFacade = new ServerFacade("8080");

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
//        assertThrows(DataAccessException.class, () -> ServerFacade.login(new LoginRequest(userBob.username(), userBob.password())));

    }

}
