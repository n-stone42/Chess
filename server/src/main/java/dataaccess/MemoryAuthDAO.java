package dataaccess;

import java.util.HashMap;


import model.*;


public class MemoryAuthDAO implements AuthDataAccess {
    public HashMap<String, AuthData> Map_AuthData = new HashMap<>();

    public void clear() {
        Map_AuthData.clear();
    }

    public void createAuth(String authToken, String username) throws DataAccessException {
        if (Map_AuthData.get(authToken) != null) {

        }
        Map_AuthData.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        if (Map_AuthData.get(authToken) == null) {
            throw new DataAccessException("Error: Auth Token invalid", DataAccessException.Code.Unathurized);
        }
        return Map_AuthData.get(authToken);
    }

    public String getUsername(String authToken) throws DataAccessException {
        if (Map_AuthData.get(authToken) == null) {
            throw new DataAccessException("Error: Auth Token invalid", DataAccessException.Code.Unathurized);
        }
        return Map_AuthData.get(authToken).username();
    }

    public void DeleteAuth(String authToken) throws DataAccessException {
        if (Map_AuthData.get(authToken) == null) {
            throw new DataAccessException("Auth Token invalid", DataAccessException.Code.Unathurized);
        }
        Map_AuthData.remove(authToken);
    }
}
