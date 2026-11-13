package dataaccess;

import dataAccessException.dataAccessException;
import model.AuthData;

public interface AuthDataAccess {
    void clear() throws dataAccessException;

    void createAuth(String authToken, String username) throws dataAccessException;

    AuthData getAuth(String authToken) throws dataAccessException;

    void deleteAuth(String authToken) throws dataAccessException;
}
