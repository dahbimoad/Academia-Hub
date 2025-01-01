package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    private Connection connection;

    public UtilisateurDAO(Connection connection) {
        this.connection = connection;
    }

    public UtilisateurDAO() {

    }

    public Utilisateur authenticate(String username, String password) {
    String query = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";
    try (Connection conn = DatabaseConnection.getConnection();
         PreparedStatement stmt = conn.prepareStatement(query)) {
        stmt.setString(1, username);
        stmt.setString(2, password);

        ResultSet rs = stmt.executeQuery();
        if (rs.next()) {
            Utilisateur user = new Utilisateur();
            user.setId(rs.getInt("id"));
            user.setUsername(rs.getString("username"));
            user.setPassword(rs.getString("password")); // Added this line
            user.setRole(rs.getString("role"));
            return user;
        }
    } catch (SQLException e) {
        e.printStackTrace();
    }
    return null;
}
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
    // ======================================
    // 3. LECTURE D'UN PROFESSEUR PAR USER_ID
    // ======================================
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
        // Exclure les utilisateurs avec le rôle 'admin'
        String sql = "SELECT * FROM utilisateurs WHERE role != 'ADMIN'";
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



    // ======================================
    // 8. RECUPERER LES MODULES ENSEIGNES PAR UN PROFESSEUR
    // ======================================


    /**
     * Méthode pour récupérer les étudiants inscrits à un module.
     */
    public List<Etudiant> getModuleStudents(int moduleId) {
        List<Etudiant> students = new ArrayList<>();
        String query = "SELECT e.* FROM etudiants e " +
                "JOIN inscriptions i ON e.id = i.etudiant_id " +
                "WHERE i.module_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(query)) {

            stmt.setInt(1, moduleId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Etudiant student = new Etudiant();
                    student.setId(rs.getInt("id"));
                    student.setMatricule(rs.getString("matricule"));
                    student.setNom(rs.getString("nom"));
                    student.setPrenom(rs.getString("prenom"));
                    student.setEmail(rs.getString("email"));
                    student.setPromotion(rs.getString("promotion"));
                    students.add(student);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }
    public boolean updatePassword(Utilisateur user, String currentPassword, String newPassword) throws SQLException {
    try {
        // First verify current password
        String verifyQuery = "SELECT * FROM utilisateurs WHERE id = ? AND password = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement verifyStmt = conn.prepareStatement(verifyQuery)) {
            verifyStmt.setInt(1, user.getId());
            verifyStmt.setString(2, currentPassword);

            ResultSet rs = verifyStmt.executeQuery();
            if (!rs.next()) {
                return false; // Current password is incorrect
            }
        }

        // Update with new password
        String updateQuery = "UPDATE utilisateurs SET password = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement updateStmt = conn.prepareStatement(updateQuery)) {
            updateStmt.setString(1, newPassword);
            updateStmt.setInt(2, user.getId());

            int rowsAffected = updateStmt.executeUpdate();
            return rowsAffected > 0;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    }
}

public boolean updateUsername(Utilisateur user, String newUsername) throws SQLException {
    try {
        String query = "UPDATE utilisateurs SET username = ? WHERE id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(query)) {
            pstmt.setString(1, newUsername);
            pstmt.setInt(2, user.getId());

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                user.setUsername(newUsername);
                return true;
            }
            return false;
        }
    } catch (SQLException e) {
        e.printStackTrace();
        throw e;
    }
}



}