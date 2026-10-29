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

import javax.xml.crypto.Data;

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


    public Server () {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
            javalin.post("/user",this::RegisterUsesr);
            javalin.post("/session",this::Login);
            javalin.delete("/session",this::Logout);
            javalin.get("/game", this::listGame);
            javalin.post("/game", this::createGame);
            javalin.put("/game", this::joinGame);
            javalin.delete("/db", this::clearDb);
            javalin.exception(DataAccessException.class, this::exceptionHandler);

        // Register your endpoints and exception handlers here.
    }

    public void clearDb(@NotNull Context context) throws DataAccessException { // there is no context
        clearHandler.clear();
        context.status(200);
    }

    public void RegisterUsesr(@NotNull Context context) throws DataAccessException {
        UserService.RegisterRequest request = new Gson().fromJson(context.body(), UserService.RegisterRequest.class);
        if (request.username() == null || request.password() == null ) {
            throw new DataAccessException("Error: Username or password is null", DataAccessException.Code.BadRequest);
        }
        UserService.RegisterResult result = userService.register(request);
        context.result(new Gson().toJson(result));
//        userHandler.RegisterUser(handle(context));
    }

    public void Login(@NotNull Context context) throws DataAccessException {
        UserService.LoginRequest request = new Gson().fromJson(context.body(), UserService.LoginRequest.class);
        if (request.username() == null || request.password() == null ) {
            throw new DataAccessException("Error: Username or password is null", DataAccessException.Code.BadRequest);
        }
        UserService.LoginResult result = userService.login(request);
        context.result(new Gson().toJson(result));
    }

    public void Logout(@NotNull Context context) throws DataAccessException {
        String authToken = context.header("authorization");
        String username = authDAO.getUsername(authToken);
        UserService.LogoutRequest logoutRequest = new UserService.LogoutRequest(username, authToken);

        UserService.LogoutResult result = userService.logout(logoutRequest);
        context.result(new Gson().toJson(result));
    }

    public void createGame(@NotNull Context context) throws DataAccessException {
        String authToken = context.header("authorization");
        if (context.body().length() <= 2) {
            throw new DataAccessException("Error: Game name is null", DataAccessException.Code.BadRequest);
        }

        GameService.CreateGameResult result = gameService.creatGame(authToken, new Gson().fromJson(context.body(), GameService.CreateGameRequest.class));
        context.result(new Gson().toJson(result));
    }

    public void listGame(@NotNull Context context) throws DataAccessException {
        String authToken = context.header("authorization");
        GameService.ListGameResult result = gameService.listGames(authToken);
        System.out.println("list game result: ");
        System.out.println(new Gson().toJson(result));
        context.result(new Gson().toJson(result));
    }

    public void joinGame(@NotNull Context context) throws DataAccessException {
//        UserService.LoginRequest request = new Gson().fromJson(context.body(), UserService.LoginRequest.class);
        String authToken = context.header("authorization");
        GameService.JoinGameRequest request = new Gson().fromJson(context.body(), GameService.JoinGameRequest.class);
        GameService.JoinGameResult result = gameService.joinGame(authToken, request);
        context.result(new Gson().toJson(result));
    }


//    private void handle(Context ctx) {
//        var r = Map.of();
//        var json = new Gson();
//        ctx.json(json.toJson(r));
//    }

    public void exceptionHandler(DataAccessException exception, Context context) {
        context.status(exception.toHttpStatuscode());
        context.result(exception.toJson());
    }

    public int run(int desiredPort) {
        javalin.start(desiredPort);
        return javalin.port();
    }

//    private void exceptionHandler(DataAccessException exception, Context context) {
//        contest.status(exception.toHttp)
//    }

    public int port() {
        return javalin.port();
    }

    public void stop() {
        javalin.stop();
    }
}
