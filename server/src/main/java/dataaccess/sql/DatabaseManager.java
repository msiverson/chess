package dataaccess.sql;

import dataaccess.DataAccessException;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Properties;

public class DatabaseManager {
    private static String databaseName;
    private static String dbUsername;
    private static String dbPassword;
    private static String connectionUrl;

    private static final String[] CREATE_STATEMENTS = {
            """
        CREATE TABLE IF NOT EXISTS users (
            username VARCHAR(50) PRIMARY KEY,
            password VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL UNIQUE
        ) ENGINE=InnoDB
        """,

            """
        CREATE TABLE IF NOT EXISTS games (
            game_id INT AUTO_INCREMENT PRIMARY KEY,
            white_username VARCHAR(50),
            black_username VARCHAR(50),
            game_name VARCHAR(100),
            game_state TEXT,
        
            FOREIGN KEY (white_username)
                REFERENCES users(username)
                ON DELETE SET NULL,
        
            FOREIGN KEY (black_username)
                REFERENCES users(username)
                ON DELETE SET NULL
        ) ENGINE=InnoDB
        """,

            """
        CREATE TABLE IF NOT EXISTS auth_tokens (
            auth_token VARCHAR(255) PRIMARY KEY,
            username VARCHAR(50) NOT NULL
        
            FOREIGN KEY (username)
                REFERENCES users(username)
                ON DELETE CASCADE
        ) ENGINE=InnoDB
        """
    };

    /*
     * Static Initialization (Initializes the fields of DatabaseManager on first reference to it)
     * Load the database information for the db.properties file.
     */
    static {
        loadPropertiesFromResources();
    }

    private static void loadPropertiesFromResources() {
        try (var propStream = Thread.currentThread().getContextClassLoader().getResourceAsStream("db.properties")) {
            if (propStream == null) {
                throw new Exception("Unable to load db.properties");
            }
            Properties props = new Properties();
            props.load(propStream);
            loadProperties(props);
        } catch (Exception ex) {
            throw new RuntimeException("unable to process db.properties", ex);
        }
    }

    private static void loadProperties(Properties props) {
        databaseName = props.getProperty("db.name");
        dbUsername = props.getProperty("db.user");
        dbPassword = props.getProperty("db.password");

        var host = props.getProperty("db.host");
        var port = Integer.parseInt(props.getProperty("db.port"));
        connectionUrl = String.format("jdbc:mysql://%s:%d", host, port);
    }

    /**
     * Creates the database if it does not already exist.
     */
    static public void createDatabase() throws DataAccessException {
        var statement = "CREATE DATABASE IF NOT EXISTS " + databaseName;
        try (var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
             var preparedStatement = conn.prepareStatement(statement)) {
            preparedStatement.executeUpdate();
        } catch (SQLException ex) {
            throw new DataAccessException("failed to create database", ex);
        }
    }

    public static void configureDatabase() throws DataAccessException {
        try (Connection conn = getConnection()) {
            for (String statement : CREATE_STATEMENTS) {
                try (PreparedStatement ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (SQLException e) {
            throw new DataAccessException("Database initialization failed", e);
        }
    }

    /**
     * WRAPPER FOR JAVA SQL getConnection()
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
            var conn = DriverManager.getConnection(connectionUrl, dbUsername, dbPassword);
            conn.setCatalog(databaseName);
            return conn;
        } catch (SQLException ex) {
            throw new DataAccessException("failed to get connection", ex);
        }
    }

}
