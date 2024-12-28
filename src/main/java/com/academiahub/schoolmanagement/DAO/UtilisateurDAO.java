package com.academiahub.schoolmanagement.DAO;



import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class UtilisateurDAO {

    public void create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs(username, password, role) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, utilisateur.getPassword());
            pstmt.setString(3, utilisateur.getRole());
            pstmt.executeUpdate();

            try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    utilisateur.setId(generatedKeys.getInt(1));
                }
            }

            System.out.println("Utilisateur créé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    public Utilisateur read(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        Utilisateur user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }

    public void update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateurs SET username=?, password=?, role=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, utilisateur.getPassword());
            pstmt.setString(3, utilisateur.getRole());
            pstmt.setInt(4, utilisateur.getId());
            pstmt.executeUpdate();
            System.out.println("Utilisateur mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Utilisateur supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Utilisateur> findAll() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Utilisateur user = new Utilisateur(
                        rs.getInt("id"),
                        rs.getString("username"),
                        rs.getString("password"),
                        rs.getString("role")
                );
                liste.add(user);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public Utilisateur findByUsername(String username) {
        String sql = "SELECT * FROM utilisateurs WHERE username = ?";
        Utilisateur user = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, username);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    user = new Utilisateur(
                            rs.getInt("id"),
                            rs.getString("username"),
                            rs.getString("password"),
                            rs.getString("role")
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return user;
    }
}
