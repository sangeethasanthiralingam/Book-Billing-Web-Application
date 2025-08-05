package util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
    // Thread-safe Singleton with volatile keyword
    private static volatile DBConnection instance;
    private Connection connection;
    
    // Database configuration
    private static final String URL = "jdbc:mysql://localhost:3306/bookshop";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root@1234";
    
    // Private constructor to prevent instantiation
    private DBConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL Driver not found. Database connection will be simulated.");
            System.out.println("To enable real database connection, add mysql-connector-java to classpath.");
        }
    }
    
    // Thread-safe getInstance method with double-checked locking
    public static DBConnection getInstance() {
        if (instance == null) {
            synchronized (DBConnection.class) {
                if (instance == null) {
                    instance = new DBConnection();
                }
            }
        }
        return instance;
    }
    
    public Connection getConnection() {
        try {
            if (connection == null || connection.isClosed()) {
                // Try to create real connection
                try {
                    connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);
                } catch (SQLException e) {
                    System.out.println("Database connection failed: " + e.getMessage());
                    throw new RuntimeException("Database connection failed: " + e.getMessage(), e);
                }
            }
        } catch (Exception e) {
            System.out.println("Database connection error: " + e.getMessage());
            throw new RuntimeException("Database connection error: " + e.getMessage(), e);
        }
        return connection;
    }
    
    public void closeConnection() {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 