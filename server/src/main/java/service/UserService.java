package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryUserDAO;
import dataaccess.UserDataAccess;
import model.*;
import org.junit.jupiter.api.Assertions.*;
import java.util.Objects;
import java.util.UUID;

public class UserService {
    private  UserDataAccess UserData;
    private AuthDataAccess AuthData;

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
        try {
            UserData.getUser(request.username()); // username not in database
            throw new DataAccessException("Error: Username already exists US49", DataAccessException.Code.AlreadyTaken);

        }
        catch (Exception e){
            UserData.createUser(request.username(), request.password(), request.email());
            String AuthToken = generateToken();
            AuthData.createAuth(AuthToken, request.username());
            return new RegisterResult(request.username(), AuthToken);
            }
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (UserData.getUser(request.username()) !=null) {
            if (UserData.MatchPassword(request.username(), request.password())) {
                String AuthToken = generateToken();
                AuthData.createAuth(AuthToken,request.username());
                return new LoginResult(request.username(), AuthToken);
            }
            throw new DataAccessException("Error: Password does not match US67",DataAccessException.Code.Unathurized); //change
        }
        throw new DataAccessException("Error: Usename not found US69",DataAccessException.Code.Unathurized ); // change
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        if (AuthData.getAuth(request.AuthToken()) !=null) {
            AuthData.DeleteAuth(request.AuthToken());
            return new LogoutResult();
        }
        return null;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        UserService that = (UserService) o;
        return Objects.equals(UserData, that.UserData) && Objects.equals(AuthData, that.AuthData);
    }

    @Override
    public int hashCode() {
        return Objects.hash(UserData, AuthData);
    }
}
