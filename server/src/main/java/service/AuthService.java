package service;

import dataaccess.DataAccessException;
import model.AuthData;

public interface AuthService {
    void clear() throws DataAccessException;
    void createAuth(String authToken, String username) throws DataAccessException;
    AuthData getAuth(String authToken, String username) throws DataAccessException;
    void DeleteAuth(String authToken) throws DataAccessException;
}
