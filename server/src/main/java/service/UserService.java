package service;

import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import model.UserData;

public interface UserService {
    void clear() throws DataAccessException;

    UserData getUser(String username) throws DataAccessException;

    void createUser(String username, String password, String email) throws DataAccessException;
    boolean MatchPassword(String username, String password )throws DataAccessException;
}
