package dataaccess;

import model.AuthData;

public interface AuthDataAccess {
    void clear() throws DataAccessException;
    void createAuth(String authToken, String username) throws DataAccessException;
    AuthData getAuth(String authToken) throws DataAccessException;
    void DeleteAuth(String authToken) throws DataAccessException;
}
