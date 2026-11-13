package dataaccess;

import dataAccessException.dataAccessException;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;

public class DatabaseUserData implements UserDataAccess {

    private final String[] createUserTableStatement = {
        """
        CREATE TABLE IF NOT EXISTS User (
         `Username` VARCHAR(255) NOT NULL,
         `Password` VARCHAR(255) NOT NULL,
         `Email` VARCHAR(255) NOT NULL,
         PRIMARY KEY(`Username`),
         INDEX(Username),
         INDEX(Password),
         INDEX(Email)
        );
        """};

    public String hashPassoword(String password) {
        return BCrypt.hashpw(password, BCrypt.gensalt());
    }

    public void makeUserTable() throws dataAccessException {
        DatabaseManager.createDatabase();
        try (Connection connection = DatabaseManager.getConnection()) {
            for (String statement : createUserTableStatement) {
                try (var preparedStatement = connection.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                } catch (SQLDataException exception) {
                    return;
                }
            }
        } catch (SQLException ex) {throw new dataAccessException("could not create database" + ex, dataAccessException.Code.OtherError);
        }
    }


    public void clear() throws dataAccessException {
        makeUserTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (var preparedStatement = connection.prepareStatement("TRUNCATE TABLE User")) {
                preparedStatement.executeUpdate();
            }
        } catch (SQLException ex) {throw new dataAccessException("Error could not create database" + ex, dataAccessException.Code.OtherError);
        }
    }

    public UserData getUser(String username) throws dataAccessException {
        makeUserTable();
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("SELECT Password, Email FROM User WHERE Username=?")) {
                preparedStatement.setString(1, username);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()){
                        String password = resultSet.getString("Password");
                        String email = resultSet.getString("Email");
                        return new UserData(username, password, email);
                    } throw new dataAccessException("Error could not get user 1 ", dataAccessException.Code.Unathurized);
                }

            } catch (SQLException ex) {throw new dataAccessException("Error could not get user 2" + ex, dataAccessException.Code.Unathurized);}

        } catch (SQLException ex) { throw new dataAccessException("Error could not create database" + ex, dataAccessException.Code.OtherError);}
    }

    public void createUser(String username, String password, String email) throws dataAccessException {
        makeUserTable();
        String hashedPassword = hashPassoword(password);
        try (Connection connection = DatabaseManager.getConnection()) {
            try (PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO User (Username, Password, Email) VALUES(?, ?, ?)")) {
                preparedStatement.setString(1, username);
                preparedStatement.setString(2, hashedPassword);
                preparedStatement.setString(3, email);

                preparedStatement.executeUpdate();

            } catch (SQLException ex) { throw new dataAccessException("Error could not create user" + ex, dataAccessException.Code.AlreadyTaken); }

        } catch (SQLException ex) { throw new dataAccessException(
                "Error could not create database/ get connection" + ex.getMessage(), dataAccessException.Code.OtherError); }
    }


    public boolean matchPassword(String username, String password) throws dataAccessException {
        UserData userData = getUser(username);
        return BCrypt.checkpw(password, userData.password());
    }
}
