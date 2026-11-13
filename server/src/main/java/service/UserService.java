package service;

import userRecordsModels.*;
import dataaccess.AuthDataAccess;
import dataAccessException.dataAccessException;
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

    public RegisterResult register(RegisterRequest request) throws dataAccessException {
        try {
            userData.getUser(request.username()); // username not in database
            System.out.print("username already exists");
            throw new dataAccessException("Error: Username already exists", dataAccessException.Code.AlreadyTaken);

        } catch (Exception e) {
            userData.createUser(request.username(), request.password(), request.email());
            String authToken = generateToken();

            authData.createAuth(authToken, request.username());
            return new RegisterResult(request.username(), authToken);
        }
    }

    public LoginResult login(LoginRequest request) throws dataAccessException {
        if (userData.getUser(request.username()) != null) {
            if (userData.matchPassword(request.username(), request.password())) {
                String authToken = generateToken();
                authData.createAuth(authToken, request.username());
                return new LoginResult(request.username(), authToken);
            }
            System.out.println("password no match");
            throw new dataAccessException("Error: Password does not match",
                    dataAccessException.Code.Unathurized); //change
        }
        throw new dataAccessException("Error: Username not found", dataAccessException.Code.Unathurized); // change
    }

    public LogoutResult logout(LogoutRequest request) throws dataAccessException {
        System.out.println("logout request in userService: " + request);
        if (authData.getAuth(request.authToken()) != null) {
            authData.deleteAuth(request.authToken());
            return new LogoutResult();
        }
        return null;

    }
}
