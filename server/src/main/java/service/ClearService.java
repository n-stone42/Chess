package service;

import dataaccess.*;

public class ClearService {

    private final UserDataAccess userData;
    private final GameDataAccess gameData;
    private final AuthDataAccess authData;

    public ClearService(UserDataAccess UserData, GameDataAccess GameData, AuthDataAccess AuthData) {
        this.userData = UserData;
        this.gameData = GameData;
        this.authData = AuthData;
    }

    public void eraseAllData() throws DataAccessException{
        userData.clear();
        gameData.clear();
        authData.clear();

    }

}
