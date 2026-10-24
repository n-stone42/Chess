package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDataAccess;
import model.*;
import java.util.UUID;

public class UserService {
    private final UserDataAccess UserData;
    private final AuthDataAccess AuthData;

    //list of methods on diagra, for service to dataaccess ,
    //getUser(username)
    //creterUser(userData)
    //createAuth(authData)
    //MatchPasswors(UserName, password)
    //CreateAuthDataToekn(AuthData)
    //GetAuth(AuthData)
    //deleat(AuthToken)
    //GEtGames(authToken)
    //GetData(authToken) -- gameData?
    //NewGaem(gameId)
    //Clear()

    //list of methods on diagram from handler to service for the user
    //register(RegisterRequest)
    //Login(LoginRequest)
    //Logout(LogoutRequest)

    public UserService(UserDataAccess UserData, AuthDataAccess AuthData) {
        this.UserData = UserData;
        this.AuthData = AuthData;
    }
    public record RegisterRequest(
            String username,
            String password,
            String email) {}

    public record RegisterResult(
            String username,
            String authToken) {}
    public record LoginRequest(
            String username,
            String password){}

    public record LoginResult(
            String username,
            String authToken) {}
    public record LogoutRequest(
            String username,
            String AuthToken) {}

    public record LogoutResult() {}


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        if (UserData.getUser(request.username()) !=null) { // username not in database
            UserData.createUser(request.username(), request.password(), request.email());
            String AuthToken = generateToken();
            AuthData.createAuth(AuthToken,request.username());
            return new RegisterResult(request.username(),AuthToken);
        }
        return null;
    }

    public LoginResult Login(LoginRequest request) throws DataAccessException {
        if (UserData.getUser(request.username()) !=null) {
            if (UserData.MatchPassword(request.username(), request.password())) {
                String AuthToken = generateToken();
                AuthData.createAuth(AuthToken,request.username());
                return new LoginResult(request.username(), AuthToken);
            }
        }
        return null;
    }

    public LogoutResult Logout(LogoutRequest request) throws DataAccessException {
        if (AuthData.getAuth(request.AuthToken(), request.username()) !=null) {
            AuthData.DeleteAuth(request.AuthToken());
            return new LogoutResult();
        }
        return null;
    }
}
