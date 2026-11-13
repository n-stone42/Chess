package dataaccess;

import dataAccessException.dataAccessException;
import model.AuthData;

import java.sql.*;

public class DatabaseAuthData implements AuthDataAccess {

    private final String[] createAuthTableStatement = {
        """
        CREATE TABLE IF NOT EXISTS AuthData (
         `authToken` VARCHAR(255) NOT NULL,
         `username` VARCHAR(255) NOT NULL,
         PRIMARY KEY(`authToken`),
         INDEX(authToken),
         INDEX(username)
        );
        """};

    public void makeAuthDataTable() throws dataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createAuthTableStatement) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {throw new dataAccessException("Error could not create auth Table" + ex, dataAccessException.Code.OtherError);
        }
    }

    public void clear() throws dataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("TRUNCATE TABLE AuthData")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {throw new dataAccessException("Error could not create database" + ex, dataAccessException.Code.OtherError);
        }
    }

    public void createAuth(String authToken, String username) throws dataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO AuthData (authToken, username) VALUES(?, ?)")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.setString(2, username);
                preparedStatement.executeUpdate();
                return;
            }
        } catch (SQLException ex) { throw new dataAccessException(
                "Error could not create database/ get connection" + ex.getMessage(), dataAccessException.Code.OtherError); }
    }

    public AuthData getAuth(String authToken) throws dataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT username FROM AuthData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()){
                        String username = resultSet.getString("username");
                        return new AuthData(authToken, username);
                    }
                } throw new dataAccessException("Error auth Token not found---", dataAccessException.Code.Unathurized);
            }
        } catch (SQLException ex) {throw new dataAccessException("Error could not create table" + ex, dataAccessException.Code.Unathurized);}
    }

    public void deleteAuth(String authToken) throws dataAccessException {
        makeAuthDataTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("DELETE FROM AuthData WHERE authToken=?")) {
                preparedStatement.setString(1, authToken);
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) { throw new dataAccessException("Error could clear Auth table" + ex, dataAccessException.Code.OtherError);
        }
    }


}
