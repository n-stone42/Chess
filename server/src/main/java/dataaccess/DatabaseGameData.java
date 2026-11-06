package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.*;
import java.util.ArrayList;

public class DatabaseGameData implements GameDataAccess{

    public void dataBaseAccess() throws DataAccessException {
        makeGameDataTables();
    }

    private final String[] createGameTableStatement = {
        """
        CREATE TABLE IF NOT EXISTS GameData (
         `gameID` int NOT NULL,
         `whiteUser` VARCHAR(255),
         `blackUser` VARCHAR(255),
         `gameName` VARCHAR(255) NOT NULL,
         `chessGame` VARCHAR(4095) NOT NULL,
         PRIMARY KEY(`gameID`),
         INDEX(gameID),
         INDEX(whiteUser),
         INDEX(blackUser),
         INDEX(gameName)
        );
        """

    };

    public String chessGameToString(ChessGame chessGame) {
        Gson serializer = new Gson();
        return serializer.toJson(chessGame);
    }

    public ChessGame JSONtoChessGame(String json) {
        Gson serializer = new Gson();
        return serializer.fromJson(json, ChessGame.class);
    }

    public void makeGameDataTables() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createGameTableStatement) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                } catch (SQLDataException exception) {
                    System.out.print(exception);
                    return;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("could not create database" + ex, DataAccessException.Code.OtherError);
        }
    }
    public void clear() throws DataAccessException {
        makeGameDataTables();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("TRUNCATE TABLE GameData")) {
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new DataAccessException("could not clear Chess game data table " + ex, DataAccessException.Code.OtherError);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("could not create Chess game data table " + ex, DataAccessException.Code.OtherError);
        }
    }

    public void deleteGameTable() throws DataAccessException{
        makeGameDataTables();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("DROP TABLE GameData")) {
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new DataAccessException("could not delete Game Table" + ex, DataAccessException.Code.OtherError);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("could not delete Game Table" + ex, DataAccessException.Code.OtherError);
        }
    }

    public void createGame(int gameID, String gameName) throws DataAccessException {
        makeGameDataTables();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "INSERT INTO GameData (gameID, whiteUser, blackUser, gameName, chessGame) VALUES(?,?,?,?,?)")) {
                preparedStatement.setInt(1, gameID);
                preparedStatement.setString(2, null);
                preparedStatement.setString(3, null);
                preparedStatement.setString(4, gameName);
                preparedStatement.setString(5, chessGameToString(new ChessGame()));

                preparedStatement.executeUpdate();
        }
        }catch (SQLException exception) {
        throw new DataAccessException("could not create game " + exception, DataAccessException.Code.OtherError); }

    }

    public ArrayList<MemoryGameDAO.ModifiedGameData> listGame() throws DataAccessException {
        return  null;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        makeGameDataTables();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement(
                    "SELECT whiteUser, blackUser, gameName, chessGame FROM GameData WHERE gameID=?")) {
                preparedStatement.setInt(1, gameID);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()){
                        String whiteUser = resultSet.getString("whiteUser");
                        String blackUser = resultSet.getString("blackUser");
                        String gameName = resultSet.getString("gameName");
                        ChessGame chessGame = JSONtoChessGame(resultSet.getString("chessGame"));
                        return new GameData(gameID,whiteUser, blackUser, gameName, chessGame);
                    } throw new DataAccessException("could not find chess game ID= " + gameID, DataAccessException.Code.OtherError);
                }

            } catch (SQLException ex) {
                throw new DataAccessException("could not get user" + ex, DataAccessException.Code.OtherError);}

        } catch (SQLException ex) {
            throw new DataAccessException("could not create database" + ex, DataAccessException.Code.OtherError);}
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException {
        GameData pendingGame = getGame(gameID);
        if (playerColor == ChessGame.TeamColor.WHITE) {
            if (pendingGame.whiteUsername() == null) {
                try (Connection connection = DatabaseManager.getConnection()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(
                            "UPDATE GameData SET whiteUser =(?) WHERE gameID = (?)")) {
                        preparedStatement.setString(1, newUsername);
                        preparedStatement.setInt(2, gameID);

                        preparedStatement.executeUpdate();
                        return;
                        }
                } catch (SQLException ex) {
                    throw new DataAccessException("could not create database" + ex, DataAccessException.Code.OtherError);}

            } throw new DataAccessException("username taken", DataAccessException.Code.AlreadyTaken);
        }

        else if (playerColor == ChessGame.TeamColor.BLACK) {
            if (pendingGame.blackUsername() == null) {
                try (Connection connection = DatabaseManager.getConnection()) {
                    try (PreparedStatement preparedStatement = connection.prepareStatement(
                            "INSERT INTO GameData (blackUser) VALUES=(?)")) {
                        preparedStatement.setString(1, pendingGame.blackUsername());
                        return;
                    }
                }
                catch (SQLException ex) {
                    throw new DataAccessException("could not create database" + ex, DataAccessException.Code.OtherError);}
            }
            throw new DataAccessException("username taken", DataAccessException.Code.AlreadyTaken);
        } throw new DataAccessException("could not match color on pending join game request", DataAccessException.Code.OtherError);
    }
}
