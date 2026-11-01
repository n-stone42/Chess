package service;

import dataaccess.*;

public class ClearService {

    private final UserDataAccess userData;
    private final GameDataAccess gameData;
    private final AuthDataAccess authData;

    public ClearService(UserDataAccess userData, GameDataAccess gameData, AuthDataAccess authData) {
        this.userData = userData;
        this.gameData = gameData;
        this.authData = authData;
    }

    public void eraseAllData() throws DataAccessException {
        userData.clear();
        gameData.clear();
        authData.clear();

    }

}
