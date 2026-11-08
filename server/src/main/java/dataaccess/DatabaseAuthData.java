package dataaccess;

import model.AuthData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DatabaseAuthData implements AuthDataAccess{

    public void databaseAccess() throws DataAccessException {
        makeAuthDataTable();
    }


    private final String[] createAuthTableStatement = {
        """
        CREATE TABLE IF NOT EXISTS AuthData (
         `authToken` VARCHAR(255) NOT NULL,
         `username` VARCHAR(255) NOT NULL,
         PRIMARY KEY(`authToken`),
         INDEX(authToken)
        );
        """};

    public void deleteAuthTable() throws DataAccessException{
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("DROP TABLE AuthData")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error could not create clear AuthData table" + ex, DataAccessException.Code.OtherError);
        }
    }

    public void makeAuthDataTable() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createAuthTableStatement) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                } catch (SQLDataException exception) {
                    System.out.print(exception);
                    return;
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error could not create auth Table" + ex, DataAccessException.Code.OtherError);
        }
    }

    public void clear() throws DataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("TRUNCATE TABLE AuthData")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error could not create database" + ex, DataAccessException.Code.OtherError);
        }
    }

    public void createAuth(String authToken, String username) throws DataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO AuthData (authToken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
                return;

            } catch (SQLException ex) {
                throw new DataAccessException("Error could not add Auth Data " + ex, DataAccessException.Code.OtherError); }

        } catch (SQLException ex) {
            throw new DataAccessException("Error could not create database/ get connection" + ex, DataAccessException.Code.OtherError); }
    }

    public AuthData getAuth(String authToken) throws DataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM AuthData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()){
                        String username = resultSet.getString("username");
                        return new AuthData(authToken, username);
                    } throw new DataAccessException("Error auth Token not found", DataAccessException.Code.Unathurized);
                }

            } catch (SQLException ex) {
                throw new DataAccessException("Error could not get user (auth data)" + ex, DataAccessException.Code.Unathurized);}

        } catch (SQLException ex) {
            throw new DataAccessException("Error could not create table" + ex, DataAccessException.Code.OtherError);}
    }

    public void deleteAuth(String authToken) throws DataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("DELETE FROM AuthData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Error could clear Auth table" + ex, DataAccessException.Code.OtherError);
        }
    }


}
