package dataaccess;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import chess.ChessGame;
import chess.ChessMove;
import chess.InvalidMoveException;
import model.*;
import service.AuthService;

public class AuthDAO implements AuthService {
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
}
