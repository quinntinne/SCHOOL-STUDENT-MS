

import java.sql.Connection;   
import java.sql.DriverManager; 
import java.sql.SQLException;  

public class DatabaseConnection {


    private static final String DB_URL =
        "jdbc:mysql://localhost:3306/chuka_university_db?useSSL=false&serverTimezone=UTC";

    private static final String DB_USER = "root"; 

    private static final String DB_PASSWORD = "fooders2000";  


    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD);
    }


    public static void closeConnection(Connection connection) {
        if (connection != null) { // Only try to close if the connection actually exists
            try {
                connection.close();
            } catch (SQLException e) {
                // If closing fails, print the error but don't crash the program
                System.err.println("Error closing database connection: " + e.getMessage());
            }
        }
    }
}