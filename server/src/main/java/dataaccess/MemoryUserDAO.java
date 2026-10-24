package dataaccess;

import java.util.HashMap;
import java.util.Objects;

import model.*;

public class MemoryUserDAO implements UserDataAccess {
    private final HashMap<String, UserData> Map_UserData  = new HashMap<>();

    public void clear() {
        Map_UserData.clear();
    }

    public UserData getUser(String username) throws DataAccessException{
        if (Map_UserData.get(username) != null) {
            return Map_UserData.get(username);
        }
        throw new DataAccessException("UserName not found");
    }
    public void createUser(String username, String password, String email) throws DataAccessException {
        if (Map_UserData.get(username) == null) { //oppsite of get User Check
            Map_UserData.put(username, new UserData(username, password, email));
            return;
        }
        throw new DataAccessException("duplicate Username");
    }
    public boolean MatchPassword(String username, String password ) {
        if (Map_UserData.containsKey(username)) {
            return Objects.equals(Map_UserData.get(username).password(), password);
        }
        return false;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MemoryUserDAO that = (MemoryUserDAO) o;
        return Objects.equals(Map_UserData, that.Map_UserData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Map_UserData);
    }
}
