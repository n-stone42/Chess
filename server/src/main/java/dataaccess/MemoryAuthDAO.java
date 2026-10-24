package dataaccess;
import java.util.HashMap;
import java.util.Objects;

import model.*;

public class MemoryAuthDAO implements AuthDataAccess {
    public HashMap<String, AuthData> Map_AuthData = new HashMap<>();

    public void clear() {
        Map_AuthData.clear();
    }
    public void createAuth(String authToken, String username) throws DataAccessException{
        if (Map_AuthData.get(authToken) != null) {

        }
        Map_AuthData.put(authToken, new AuthData(authToken, username));
    }

    public AuthData getAuth(String authToken, String username) throws DataAccessException {
        if (Map_AuthData.get(authToken) == null) {
            throw new DataAccessException("Auth Token invalid");
        }
        return Map_AuthData.get(authToken);
    }

    public void DeleteAuth(String authToken) throws DataAccessException{
        if (Map_AuthData.get(authToken) == null) {
            throw new DataAccessException("Auth Token invalid");
        }
        Map_AuthData.remove(authToken);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        MemoryAuthDAO that = (MemoryAuthDAO) o;
        return Objects.equals(Map_AuthData, that.Map_AuthData);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(Map_AuthData);
    }
}
