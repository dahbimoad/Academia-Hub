//src/main/java/com/academiahub/schoolmanagement/DAO/UtilisateurDAO.java
package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Utilisateur;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UtilisateurDAO {
    private Connection connection;

    public UtilisateurDAO(Connection connection) {
        this.connection = connection;
    }

    public Utilisateur authenticate(String username, String password) {
        String query = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password); // In a real application, use password hashing

            System.out.println("Attempting authentication for user: " + username);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                System.out.println("Authentication successful. Role: " + user.getRole());
                return user;
            }
            System.out.println("Authentication failed for user: " + username);
        } catch (SQLException e) {
            System.out.println("Database error during authentication:");
            e.printStackTrace();
        }
        return null;
    }
}
