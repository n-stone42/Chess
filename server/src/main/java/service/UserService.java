package service;

import dataaccess.AuthDataAccess;
import dataaccess.DataAccessException;
import dataaccess.UserDataAccess;

import java.util.UUID;

public class UserService {
    private UserDataAccess userData;
    private AuthDataAccess authData;

    public UserService(UserDataAccess userData, AuthDataAccess authData) {
        this.userData = userData;
        this.authData = authData;
    }

    public record RegisterRequest(
            String username,
            String password,
            String email) {
    }

    public record RegisterResult(
            String username,
            String authToken) {
    }

    public record LoginRequest(
            String username,
            String password) {
    }

    public record LoginResult(
            String username,
            String authToken) {
    }

    public record LogoutRequest(
            String username,
            String authToken) {
    }

    public record LogoutResult() {
    }


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        try {
            userData.getUser(request.username()); // username not in database
            throw new DataAccessException("Error: Username already exists US49", DataAccessException.Code.AlreadyTaken);

        } catch (Exception e) {
            userData.createUser(request.username(), request.password(), request.email());
            String AuthToken = generateToken();
            authData.createAuth(AuthToken, request.username());
            return new RegisterResult(request.username(), AuthToken);
        }
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (userData.getUser(request.username()) != null) {
            if (userData.matchPassword(request.username(), request.password())) {
                String AuthToken = generateToken();
                authData.createAuth(AuthToken, request.username());
                return new LoginResult(request.username(), AuthToken);
            }
            throw new DataAccessException("Error: Password does not match US67", DataAccessException.Code.Unathurized); //change
        }
        throw new DataAccessException("Error: Usename not found US69", DataAccessException.Code.Unathurized); // change
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        if (authData.getAuth(request.authToken()) != null) {
            authData.deleteAuth(request.authToken());
            return new LogoutResult();
        }
        return null;
    }
}
