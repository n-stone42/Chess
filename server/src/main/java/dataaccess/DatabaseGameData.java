package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.GameData;

import java.sql.Connection;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.ArrayList;

public class DatabaseGameData implements GameDataAccess{

    public void dataBaseAccess() throws DataAccessException {
        makeGameDataTables();
    }

    private final String[] createGameTableStatement = {
        """
        CREATE TABLE IF NOT EXISTS GameData (
         `gameID` int NOT NULL,
         `whiteUser` VARCHAR(255) NOT NULL,
         `blackUser` VARCHAR(255) NOT NULL,
         `gameName` VARCHAR(255) NOT NULL,
         `chessGame` VARCHAR(1023) NOT NULL,
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
        DatabaseManager.createDatabase();
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
        DatabaseManager.createDatabase();
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
        return;
    }

    public ArrayList<MemoryGameDAO.ModifiedGameData> listGame() throws DataAccessException {
        return  null;
    }

    public GameData getGame(int gameID) throws DataAccessException {
        return null;
    }

    public void joinGame(int gameID, ChessGame.TeamColor playerColor, String newUsername) throws DataAccessException {
        return;
    }
}
