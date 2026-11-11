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
    @Test
    public void negativeLogin() { // login before register
        assertThrows(DataAccessException.class, () -> serverFacade.login(new LoginRequest(userBob.username(), userBob.password())));
    }

    @Test
    public void positiveRegister() {
        try {
            server.clear();
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

//    @Test
//    public void positiveLogin() throws DataAccessException {
//        assertEquals("some string",serverFacade.login(new LoginRequest(userBob.username(), userBob.password())));
//    }

            // some randon auto generated code
//            @NotNull
//            @Override
//            public HttpServletRequest req() {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public HttpServletResponse res() {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public HandlerType handlerType() {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public String matchedPath() {
//                return "";
//            }
//
//            @NotNull
//            @Override
//            public String endpointHandlerPath() {
//                return "";
//            }
//
//            @Override
//            public <T> T appData(@NotNull Key<T> key) {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public JsonMapper jsonMapper() {
//                return null;
//            }
//
//            @Override
//            public <T> T with(@NotNull Class<? extends ContextPlugin<?, T>> aClass) {
//                return null;
//            }
//
//            @Override
//            public boolean strictContentTypes() {
//                return false;
//            }
//
//            @NotNull
//            @Override
//            public String pathParam(@NotNull String s) {
//                return "";
//            }
//
//            @NotNull
//            @Override
//            public Map<String, String> pathParamMap() {
//                return Map.of();
//            }
//
//            @NotNull
//            @Override
//            public ServletOutputStream outputStream() {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public Context minSizeForCompression(int i) {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public Context result(@NotNull InputStream inputStream) {
//                return null;
//            }
//
//            @Nullable
//            @Override
//            public InputStream resultInputStream() {
//                return null;
//            }
//
//            @Override
//            public void future(@NotNull Supplier<? extends CompletableFuture<?>> supplier) {
//
//            }
//
//            @Override
//            public void redirect(@NotNull String s, @NotNull HttpStatus httpStatus) {
//
//            }
//
//            @Override
//            public void writeJsonStream(@NotNull Stream<?> stream) {
//
//            }
//
//            @NotNull
//            @Override
//            public Context skipRemainingHandlers() {
//                return null;
//            }
//
//            @NotNull
//            @Override
//            public Set<RouteRole> routeRoles() {
//                return Set.of();
//            }
//        });
//    }
}
