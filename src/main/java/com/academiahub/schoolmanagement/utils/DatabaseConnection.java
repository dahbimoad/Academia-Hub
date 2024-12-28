//src/main/java/com/academiahub/schoolmanagement/utils/DatabaseConnection.java
package com.academiahub.schoolmanagement.utils;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnection {
    private static final String URL = "jdbc:postgresql://localhost:5432/academiahub";
    private static final String USER = "postgres";  // Replace with your PostgreSQL username
    private static final String PASSWORD = "1234";  // Replace with your PostgreSQL password

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}