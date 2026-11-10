package service;

import UserRecordsModels.*;
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


    public static String generateToken() {
        return UUID.randomUUID().toString();
    }

    public RegisterResult register(RegisterRequest request) throws DataAccessException {
        try {
            userData.getUser(request.username()); // username not in database
            throw new DataAccessException("Error: Username already exists", DataAccessException.Code.AlreadyTaken);

        } catch (Exception e) {
            userData.createUser(request.username(), request.password(), request.email());
            String authToken = generateToken();
            authData.createAuth(authToken, request.username());
            return new RegisterResult(request.username(), authToken);
        }
    }

    public LoginResult login(LoginRequest request) throws DataAccessException {
        if (userData.getUser(request.username()) != null) {
            if (userData.matchPassword(request.username(), request.password())) {
                String authToken = generateToken();
                authData.createAuth(authToken, request.username());
                return new LoginResult(request.username(), authToken);
            }
            throw new DataAccessException("Error: Password does not match",
                    DataAccessException.Code.Unathurized); //change
        }
        throw new DataAccessException("Error: Username not found", DataAccessException.Code.Unathurized); // change
    }

    public LogoutResult logout(LogoutRequest request) throws DataAccessException {
        if (authData.getAuth(request.authToken()) != null) {
            authData.deleteAuth(request.authToken());
            return new LogoutResult();
        }
        return null;
    }
}
