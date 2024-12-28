package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesseurDAO {

    public void create(Professeur professeur) {
        String sql = "INSERT INTO professeurs(nom, prenom, specialite, user_id) VALUES (?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, professeur.getNom());
            pstmt.setString(2, professeur.getPrenom());
            pstmt.setString(3, professeur.getSpecialite());
            pstmt.setInt(4, professeur.getUserId()); // Ajout de userId
            pstmt.executeUpdate();
            System.out.println("Professeur créé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Professeur read(int id) {
        String sql = "SELECT * FROM professeurs WHERE id = ?";
        Professeur prof = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prof = new Professeur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getInt("user_id") // Lecture de userId
                    );
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return prof;
    }

    public void update(Professeur professeur) {
        String sql = "UPDATE professeurs SET nom=?, prenom=?, specialite=?, user_id=? WHERE id=?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, professeur.getNom());
            pstmt.setString(2, professeur.getPrenom());
            pstmt.setString(3, professeur.getSpecialite());
            pstmt.setInt(4, professeur.getUserId()); // Mise à jour de userId
            pstmt.setInt(5, professeur.getId());
            pstmt.executeUpdate();
            System.out.println("Professeur mis à jour avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM professeurs WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            pstmt.executeUpdate();
            System.out.println("Professeur supprimé avec succès !");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<Professeur> findAll() {
        List<Professeur> liste = new ArrayList<>();
        String sql = "SELECT * FROM professeurs";
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                Professeur prof = new Professeur(
                        rs.getInt("id"),
                        rs.getString("nom"),
                        rs.getString("prenom"),
                        rs.getString("specialite"),
                        rs.getInt("user_id") // Lecture de userId
                );
                liste.add(prof);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }
}
