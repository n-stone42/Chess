package dataaccess;

import model.UserData;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLDataException;
import java.sql.SQLException;

public class DatabaseUserData implements UserDataAccess{

    public void databaseAccess() throws DataAccessException {
        makeUserTable();
    }

    private final String[] createBaiscDatabase = {
        """
        CREATE DATABASE IF NOT EXISTS Chess;
        """
    };

    private final String[] createUserTableStatment = {
        """
        CREATE TABLE IF NOT EXISTS User (
         `Username` VARCHAR(255) NOT NULL,
         `Password` VARCHAR(255) NOT NULL,
         PRIMARY KEY(`Username`),
         INDEX(Username),
         INDEX(Password)
        );
         """};


    public void makeUserTable() throws DataAccessException {

        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createUserTableStatment) {
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
            try (var preparedStatement = connection.prepareStatement("TRUNCATE TABLE User")) {
                preparedStatement.executeUpdate();
            } catch (SQLException ex) {
                throw new DataAccessException("could not clear DataBase" + ex, DataAccessException.Code.OtherError);
            }
        } catch (SQLException ex) {
            throw new DataAccessException("could not create database" + ex, DataAccessException.Code.OtherError);
        }
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
