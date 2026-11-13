package dataaccess;

import java.util.HashMap;


import dataAccessException.dataAccessException;
import model.*;


public class MemoryAuthDAO implements AuthDataAccess {
    public HashMap<String, AuthData> mapAuthData = new HashMap<>();

    public void clear() {
        mapAuthData.clear();
    }

    public void createAuth(String authToken, String username) throws dataAccessException {
        if (mapAuthData.get(authToken) != null) {

        }
        mapAuthData.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken) throws dataAccessException {
        if (mapAuthData.get(authToken) == null) {
            throw new dataAccessException("Error: Auth Token invalid", dataAccessException.Code.Unathurized);
        }
        return mapAuthData.get(authToken);
    }

    public String getUsername(String authToken) throws dataAccessException {
        if (mapAuthData.get(authToken) == null) {
            throw new dataAccessException("Error: Auth Token invalid", dataAccessException.Code.Unathurized);
        }
        return mapAuthData.get(authToken).username();
    }

    public void deleteAuth(String authToken) throws dataAccessException {
        if (mapAuthData.get(authToken) == null) {
            throw new dataAccessException("Auth Token invalid", dataAccessException.Code.Unathurized);
        }
        mapAuthData.remove(authToken);
    }
}
