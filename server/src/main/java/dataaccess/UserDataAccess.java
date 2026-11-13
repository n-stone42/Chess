package dataaccess;

import dataAccessException.dataAccessException;
import model.UserData;

public interface UserDataAccess {
    void clear() throws dataAccessException;

    UserData getUser(String username) throws dataAccessException;

    void createUser(String username, String password, String email) throws dataAccessException;

    boolean matchPassword(String username, String password) throws dataAccessException;
}
