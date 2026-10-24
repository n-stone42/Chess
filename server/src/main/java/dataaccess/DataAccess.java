//package dataaccess;
//
//import chess.ChessMove;
//import model.AuthData;
//import model.UserData;
//
//import java.util.List;
//
//public interface DataAccess {
//
//    void clear() throws DataAccessException;
//
//    UserData getUser(String username) throws DataAccessException;
//
//    void createUser(String username, String password, String email) throws DataAccessException;
//    void createGame(int gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException;
//    List listGame() throws DataAccessException;
//
//    void updateGame(String gameName, ChessMove move)throws DataAccessException;
//    void createAuth(String authToken, String username) throws DataAccessException;
//    AuthData getAuth(String authToken, String username) throws DataAccessException;
//    void DeleteAuth(String authToken) throws DataAccessException;
//    boolean MatchPassword(String username, String password )throws DataAccessException;
//}
