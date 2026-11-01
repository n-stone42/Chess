package dataaccess;

import java.util.HashMap;


import model.*;


public class MemoryAuthDAO implements AuthDataAccess {
    public HashMap<String, AuthData> mapAuthData = new HashMap<>();

    public void clear() {
        mapAuthData.clear();
    }

    public void createAuth(String authToken, String username) throws DataAccessException {
        if (mapAuthData.get(authToken) != null) {

        }
        mapAuthData.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (mapAuthData.get(authToken) == null) {
            throw new DataAccessException("Error: Auth Token invalid", DataAccessException.Code.Unathurized);
        }
        return mapAuthData.get(authToken);
    }

    public String getUsername(String authToken) throws DataAccessException {
        if (mapAuthData.get(authToken) == null) {
            throw new DataAccessException("Error: Auth Token invalid", DataAccessException.Code.Unathurized);
        }
        return mapAuthData.get(authToken).username();
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        if (mapAuthData.get(authToken) == null) {
            throw new DataAccessException("Auth Token invalid", DataAccessException.Code.Unathurized);
        }
        mapAuthData.remove(authToken);
    }
}
