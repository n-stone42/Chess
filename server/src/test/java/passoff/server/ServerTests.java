//package passoff.server;
//
//import dataaccess.DataAccessException;
//import org.junit.jupiter.api.AfterAll;
//import org.junit.jupiter.api.BeforeAll;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import server.Server;
//
//import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
//
//public class ServerTests {
//
//    public Server ChessServer;
////    static ServerFacade Server;
//
//    @BeforeAll
//    public void StartServer() throws DataAccessException {
//        ChessServer = new Server();
//        ChessServer.run(0);
//        var url = "http://localhost:" + ChessServer.port();
//    }
//
//    @AfterAll
//    public void stopServer() {
//        ChessServer.stop();
//    }
//
//    @BeforeEach
//    void clear() {
//        assertDoesNotThrow(() ->)
//    }
//}
