package service;

import dataaccess.*;

public class ClearService {

    private final UserDataAccess userData;
    private final GameDataAccess gameData;
    private final AuthDataAccess authData;

    public ClearService(UserDataAccess UserData, GameDataAccess GameData, AuthDataAccess AuthData) {
        userData = UserData;
        gameData = GameData;
        authData = AuthData;
    }

    public void eraseAllData() throws DataAccessException{
        userData.clear();
        gameData.clear();
        authData.clear();

    }

}
