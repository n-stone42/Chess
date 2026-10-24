package dataaccess;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.*;
import service.UserService;

public class UserDAO implements UserService {
    public HashMap<String, UserData> Map_UserData  = new HashMap<>();

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
        }
        throw new DataAccessException("duplicate Username");
    }
    public boolean MatchPassword(String username, String password ) {
        if (Map_UserData.containsKey(username)) {
            return Objects.equals(Map_UserData.get(username).password(), password);
        }
        return false;
    }
}
