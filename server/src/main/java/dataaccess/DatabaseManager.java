package dataaccess;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    /*
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
        try {
            try (InputStream in = DatabaseManager.class.getClassLoader().getResourceAsStream("db.properties")) {
                Properties props = new Properties();
                props.load(in);
                databaseName = props.getProperty("db.name");
                dbUsername = props.getProperty("db.user");
                dbPassword = props.getProperty("db.password");

                String host = props.getProperty("db.host");
                var port = Integer.parseInt(props.getProperty("db.port"));
                connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);

            }
        } catch (Exception ex) {
            throw new RuntimeException("Error unable to process db.properties. " + ex.getMessage());
        }
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        String result = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (Connection connection = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             PreparedStatement preparedStatement = connection.prepareStatement(result)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("Error could not create database", ex);
        }
    }

    /**
     * Create a connection to the database and sets the catalog based upon the
     * properties specified in db.properties. Connections to the database should
     * be short-lived, and you must close the connection when you are done with it.
     * The easiest way to do that is with a try-with-resource block.
     * <br/>
     * <code>
     * try (var conn = DatabaseManager.getConnection()) {
     * // execute SQL statements.
     * }
     * </code>
     */
    static Connection getConnection() throws DataAccessException {
        try {
            //do not wrap the following line with a try-with-resources
            Connection connection = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            connection.setCatalog(databaseName);
            return connection;
        } catch (SQLException ex) {
            throw new DataAccessException("Error failed to get connection", ex);
        }
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Error Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("Error unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        String host = props.getProperty("db.host");
        int port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }
}
