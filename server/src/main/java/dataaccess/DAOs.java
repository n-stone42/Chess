/// this was the old implemations, its is still hear incase the new one runs into problems


//package dataaccess;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Objects;
//
//import chess.ChessGame;
//import chess.ChessMove;
//import chess.InvalidMoveException;
//import model.*;
//public class DAOs implements DataAccess {
//
//    //search by username
//    public HashMap<String, UserData> Map_UserData  = new HashMap<>();
//    // search by gameID
//    public HashMap<String, GameData> Map_GameData = new HashMap<>();
//    // search by auth Token
//    public HashMap<String, AuthData> Map_AuthData = new HashMap<>();
//
//    // list of methods Create, Read, Update, Delete
//    // UserData
//    public void clear() {
//        Map_UserData.clear();
//        Map_GameData.clear();
//        Map_AuthData.clear();
//    }
//
//    public UserData getUser(String username) throws DataAccessException{
//        if (Map_UserData.get(username) != null) {
//            return Map_UserData.get(username);
//        }
//        throw new DataAccessException("UserName not found");
//    }
//    public void createUser(String username, String password, String email) throws DataAccessException {
//        if (Map_UserData.get(username) == null) { //oppsite of get User Check
//            Map_UserData.put(username, new UserData(username, password, email));
//        }
//        throw new DataAccessException("duplicate Username");
//    }
//
//    public void createGame(int gameID, String whiteUsername, String blackUsername, String gameName) throws DataAccessException {
//        // we are creating a new chess game so this parameter will just be a new chess game from the starting position
//        if (Map_GameData.get(gameID) != null) {
//            throw new DataAccessException("game ID taken");
//        }
//        Map_GameData.put(gameName, new GameData(gameID, whiteUsername, blackUsername, blackUsername, new ChessGame()));
//
//    }
//
//    public List listGame() throws DataAccessException{
//        List<String> gameIDs = new ArrayList<>();
//        if (Map_GameData.isEmpty()) {
//            throw new DataAccessException("There are no games to list");
//        }
//        for (String gameID : Map_GameData.keySet()) {
//            gameIDs.add(gameID);
//        }
//        return gameIDs;
//    }
//
//    public void updateGame(String gameName, ChessMove move)throws DataAccessException {
//        if (Map_GameData.get(gameName) == null) {
//            throw new DataAccessException("gameID not found");
//        }
//
//        GameData oldGamedata = Map_GameData.get(gameName);
//        ChessGame game = oldGamedata.game();
//
//        try {
//            // make the move on the game
//            game.makeMove(move);
//            // remove the old record
//            Map_GameData.remove(oldGamedata.gameName());
//            // make a new record with  the new game
//            Map_GameData.put(oldGamedata.gameName(),new GameData(oldGamedata.gameID(), oldGamedata.whiteUsername(),
//                    oldGamedata.blackUsername(), oldGamedata.gameName(), game));
//        }
//        catch (InvalidMoveException e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    public void createAuth(String authToken, String username) throws DataAccessException{
//        if (Map_AuthData.get(authToken) != null) {
//
//        }
//        Map_AuthData.put(authToken, new AuthData(authToken, username));
//    }
//
//    public AuthData getAuth(String authToken, String username) throws DataAccessException {
//        if (Map_AuthData.get(authToken) == null) {
//            throw new DataAccessException("Auth Token invalid");
//        }
//        return Map_AuthData.get(authToken);
//    }
//
//    public void DeleteAuth(String authToken) throws DataAccessException{
//        if (Map_AuthData.get(authToken) == null) {
//            throw new DataAccessException("Auth Token invalid");
//        }
//        Map_AuthData.remove(authToken);
//    }
//
//    public boolean MatchPassword(String username, String password ) {
//        if (Map_UserData.containsKey(username)) {
//            return Objects.equals(Map_UserData.get(username).password(), password);
//        }
//        return false;
//    }
//
//
//
//
//
//
//
////    public void DeleteUser(String username) throws DataAccessException {
////        if (Map_UserData.get(username) != null) {
////            Map_UserData.remove(username);
////        }
////        throw new DataAccessException("UserName nor found");
////    }
////
////    public void UpdateUser(
//}
