package dataaccess;

import dataAccessException.DataAccessException;
import model.UserData;

public interface UserDataAccess {
    void clear() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;

    boolean matchPassword(String username, String password) throws DataAccessException;
}
