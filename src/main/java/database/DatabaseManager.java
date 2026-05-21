package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseManager {

    private static final String URL =
            "jdbc:sqlite:"
            + System.getProperty("user.dir")
            + "/data/app.db";

    public static Connection getConnection()
            throws SQLException {

        return DriverManager.getConnection(URL);
    }
}