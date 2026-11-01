package dataaccess;

import java.util.HashMap;
import java.util.Objects;

import model.*;

public class MemoryUserDAO implements UserDataAccess {
    private final HashMap<String, UserData> mapUserData = new HashMap<>();

    public void clear() {
        mapUserData.clear();
    }

    public UserData getUser(String username) throws DataAccessException {
        if (mapUserData.get(username) != null) {
            return mapUserData.get(username);
        }
        throw new DataAccessException("Error: Username not found MU19", DataAccessException.Code.Unathurized);
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        if (mapUserData.get(username) == null) { //oppsite of get User Check
            mapUserData.put(username, new UserData(username, password, email));
            return;
        }
        throw new DataAccessException("Error: Duplicate Username MU26", DataAccessException.Code.AlreadyTaken);
    }

    public boolean matchPassword(String username, String password) throws DataAccessException {
        if (mapUserData.containsKey(username)) {
            return Objects.equals(mapUserData.get(username).password(), password);
        }
        throw new DataAccessException("Error: Password did not match", DataAccessException.Code.BadRequest);
    }
}
