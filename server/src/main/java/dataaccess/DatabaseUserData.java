package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class DatabaseUserData implements UserDataAccess{

    public void databaseAccess() throws DataAccessException {
        makeDatabase();
    }

    private final String[] createBaiscDatabase = {
        """
        CREATE DATABASE IF NOT EXISTS Chess;
        """
    };

    private final String[] createDatabase = {
        """
        CREATE DATABASE Chess;
        USE Chess;
        
        CREATE TABLE User (
        `Username` VARCHAR(255) NOT NULL,
        `Password` VARCHAR(255) NOT NULL,
        PRIMARY KEY(Username)
        
        );
    
        INSERT INTO User (Username, Password) Values ('Test User', 'Test Password');
         """};


    public void makeDatabase() throws DataAccessException {

        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createBaiscDatabase) {
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
        return;
    }

    public UserData getUser(String username) throws DataAccessException {
        return null;
    }

    public void createUser(String username, String password, String email) throws DataAccessException {
        return;
    }

    public boolean matchPassword(String username, String password) throws DataAccessException {
        return false;
    }
}
