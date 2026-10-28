package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import java.util.Map;
import Handlers.*;
import service.ClearService;
import service.GameService;
import service.UserService;

public class Server {

    // the DAOs
    MemoryAuthDAO authDAO = new MemoryAuthDAO();
    MemoryGameDAO gameDAO = new MemoryGameDAO();
    MemoryUserDAO UserDAO = new MemoryUserDAO();

    //the Serevices
    ClearService clearService = new ClearService(UserDAO, gameDAO, authDAO);
    GameService gameService = new GameService(UserDAO, gameDAO, authDAO);
    UserService userService = new UserService(UserDAO, authDAO);


    private final Javalin javalin;
    private final ClearHandler clearHandler = new ClearHandler(clearService);
    private final UserHandler userHandler = new UserHandler(userService);
    private final GameHandler gameHandler = new GameHandler(gameService);
//    private final Javalin httpHandler;


    public Server () {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
            javalin.delete("/db", this::clearDb);
//        javalin.exception(DataAccessException.class, this::exceptionHandler);
        // Register your endpoints and exception handlers here.
    }

    public void clearDb(@NotNull Context context) throws DataAccessException { // there is no context
        clearHandler.clear();
//        System.out.print("called clearDB in Server");
        context.status(200);
    }

    private void handle(Context ctx) {
        var r = Map.of();
        var json = new Gson();
        ctx.json(json.toJson(r));
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

    public int port() {
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
