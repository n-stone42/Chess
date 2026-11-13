package dataaccess;

import java.util.HashMap;
import java.util.Objects;

import dataAccessException.dataAccessException;
import model.*;

public class MemoryUserDAO implements UserDataAccess {
    private final HashMap<String, UserData> mapUserData = new HashMap<>();

    public void clear() {
        mapUserData.clear();
    }

    public UserData getUser(String username) throws dataAccessException {
        if (mapUserData.get(username) != null) {
            return mapUserData.get(username);
        }
        throw new dataAccessException("Error: Username not found MU19", dataAccessException.Code.Unathurized);
    }

    public void createUser(String username, String password, String email) throws dataAccessException {
        if (mapUserData.get(username) == null) { //oppsite of get User Check
            mapUserData.put(username, new UserData(username, password, email));
            return;
        }
        throw new dataAccessException("Error: Duplicate Username MU26", dataAccessException.Code.AlreadyTaken);
    }

    public boolean matchPassword(String username, String password) throws dataAccessException {
        if (mapUserData.containsKey(username)) {
            return Objects.equals(mapUserData.get(username).password(), password);
        }
        throw new dataAccessException("Error: Password did not match", dataAccessException.Code.BadRequest);
    }
}
