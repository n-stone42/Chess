package server;

import com.google.gson.Gson;
import dataaccess.*;
import io.javalin.*;
import io.javalin.http.Context;
import org.jetbrains.annotations.NotNull;
import handlers.*;
import service.ClearService;
import service.GameService;
import service.UserService;


public class Server {

    // the DAOs
//    MemoryAuthDAO authDAO = new MemoryAuthDAO();
//    MemoryGameDAO gameDAO = new MemoryGameDAO();
//    MemoryUserDAO userDAO = new MemoryUserDAO();
    DatabaseAuthData authData = new DatabaseAuthData();
    DatabaseGameData gameData = new DatabaseGameData();
    DatabaseUserData userData = new DatabaseUserData();

    //the Serevices
    ClearService clearService = new ClearService(userData, gameData, authData);
    GameService gameService = new GameService(userData, gameData, authData);
    UserService userService = new UserService(userData, authData);


    private final Javalin javalin;
    private final ClearHandler clearHandler = new ClearHandler(clearService);


    public Server() {

        javalin = Javalin.create(config -> config.staticFiles.add("web"));
        javalin.post("/user", this::registerUser);
        javalin.post("/session", this::login);
        javalin.delete("/session", this::logout);
        javalin.get("/game", this::listGame);
        javalin.post("/game", this::createGame);
        javalin.put("/game", this::joinGame);
        javalin.delete("/db", this::clearDb);
        javalin.exception(DataAccessException.class, this::exceptionHandler);

    }

    public void clearDb(@NotNull Context context) throws DataAccessException {
        clearHandler.clear();
        context.status(200);
    }

    public void registerUser(@NotNull Context context) throws DataAccessException {
        UserService.RegisterRequest request = new Gson().fromJson(context.body(), UserService.RegisterRequest.class);
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("Error: Username or password is null", DataAccessException.Code.BadRequest);
        }
        UserService.RegisterResult result = userService.register(request);
        context.result(new Gson().toJson(result));
    }

    public void login(@NotNull Context context) throws DataAccessException {
        UserService.LoginRequest request = new Gson().fromJson(context.body(), UserService.LoginRequest.class);
        if (request.username() == null || request.password() == null) {
            throw new DataAccessException("Error: Username or password is null", DataAccessException.Code.BadRequest);
        }
        UserService.LoginResult result = userService.login(request);
        context.result(new Gson().toJson(result));
    }

    public void logout(@NotNull Context context) throws DataAccessException {
        String authToken = context.header("authorization");
        String username = authData.getAuth(authToken).username();
        UserService.LogoutRequest logoutRequest = new UserService.LogoutRequest(username, authToken);

        UserService.LogoutResult result = userService.logout(logoutRequest);
        context.result(new Gson().toJson(result));
    }

    public void createGame(@NotNull Context context) throws DataAccessException {
        String authToken = context.header("authorization");
        GameService.CreateGameRequest request = new Gson().fromJson(context.body(),
                GameService.CreateGameRequest.class);
        if (request.gameName() == null) {
            throw new DataAccessException("Error: Game name is null", DataAccessException.Code.BadRequest);
        }
        GameService.CreateGameResult result = gameService.creatGame(authToken,
                new Gson().fromJson(context.body(), GameService.CreateGameRequest.class));
        context.result(new Gson().toJson(result));
    }

    public void listGame(@NotNull Context context) throws DataAccessException {
        String authToken = context.header("authorization");
        GameService.ListGameResult result = gameService.listGames(authToken);
        context.result(new Gson().toJson(result));
    }

    public void joinGame(@NotNull Context context) throws DataAccessException {
//        UserService.LoginRequest request = new Gson().fromJson(context.body(), UserService.LoginRequest.class);
        String authToken = context.header("authorization");
        GameService.JoinGameRequest request = new Gson().fromJson(context.body(), GameService.JoinGameRequest.class);
        if (request.playerColor() == null) {
            throw new DataAccessException("Error: player color in null", DataAccessException.Code.BadRequest);
        }
        GameService.JoinGameResult result = gameService.joinGame(authToken, request);
        context.result(new Gson().toJson(result));
    }

    public void exceptionHandler(DataAccessException exception, Context context) {
        context.status(exception.toHttpStatuscode());
        context.result(exception.toJson());
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
