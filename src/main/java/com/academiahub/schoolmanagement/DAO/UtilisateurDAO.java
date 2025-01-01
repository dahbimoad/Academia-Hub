package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {
    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(UtilisateurDAO.class);

    public UtilisateurDAO(Connection connection) {
        this.connection = connection;
        logger.debug("UtilisateurDAO initialized with connection");
    }

    public UtilisateurDAO() {
        logger.debug("UtilisateurDAO initialized with default constructor");
    }

    public Utilisateur authenticate(String username, String password) {
        logger.info("Attempting authentication for user: {}", username);
        String query = "SELECT * FROM utilisateurs WHERE username = ? AND password = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            stmt.setString(2, password);

            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                Utilisateur user = new Utilisateur();
                user.setId(rs.getInt("id"));
                user.setUsername(rs.getString("username"));
                user.setRole(rs.getString("role"));
                logger.info("Authentication successful for user: {} with role: {}", username, user.getRole());
                return user;
            }
            logger.warn("Authentication failed for user: {}", username);
        } catch (SQLException e) {
            logger.error("Database error during authentication for user: {}", username, e);
        }
        return null;
    }

    public boolean create(Utilisateur utilisateur) {
        logger.info("Creating new user with username: {}", utilisateur.getUsername());
        String sql = "INSERT INTO utilisateurs(username, password, role) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, utilisateur.getPassword());
            pstmt.setString(3, utilisateur.getRole());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        utilisateur.setId(generatedKeys.getInt(1));
                        logger.info("Successfully created user with ID: {}", utilisateur.getId());
                        return true;
                    }
                }
            }
            logger.warn("Failed to create user: {}", utilisateur.getUsername());
        } catch (SQLException e) {
            logger.error("Error creating user: {}", utilisateur.getUsername(), e);
        }
        return false;
    }

    public Utilisateur read(int id) {
        logger.info("Fetching user with ID: {}", id);
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
                    logger.info("Successfully retrieved user with ID: {}", id);
                } else {
                    logger.warn("No user found with ID: {}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching user with ID: {}", id, e);
        }
        return user;
    }

    public boolean update(Utilisateur utilisateur) {
        if (utilisateur == null) {
            logger.warn("Attempted to update null user");
            return false;
        }

        logger.info("Updating user with ID: {}", utilisateur.getId());
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE utilisateurs SET username = ?, password = ?, role = ? WHERE id = ?")) {

            stmt.setString(1, utilisateur.getUsername());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, utilisateur.getRole());
            stmt.setInt(4, utilisateur.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Successfully updated user with ID: {}", utilisateur.getId());
                return true;
            } else {
                logger.warn("No user found to update with ID: {}", utilisateur.getId());
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error updating user with ID: {}", utilisateur.getId(), e);
            return false;
        }
    }

    public static void delete(int id) {
        Logger logger = LoggerFactory.getLogger(UtilisateurDAO.class);
        logger.info("Attempting to delete user with ID: {}", id);
        String sql = "DELETE FROM utilisateurs WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Successfully deleted user with ID: {}", id);
            } else {
                logger.warn("No user found to delete with ID: {}", id);
            }
        } catch (SQLException e) {
            logger.error("Error deleting user with ID: {}", id, e);
        }
    }

    public List<Utilisateur> findAll() {
        logger.info("Fetching all users except admins");
        List<Utilisateur> liste = new ArrayList<>();
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
            logger.info("Successfully retrieved {} users", liste.size());
        } catch (SQLException e) {
            logger.error("Error fetching all users", e);
        }
        return liste;
    }

    public Utilisateur findByUsername(String username) {
        logger.info("Searching for user with username: {}", username);
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
                    logger.info("Found user with username: {}", username);
                } else {
                    logger.warn("No user found with username: {}", username);
                }
            }
        } catch (SQLException e) {
            logger.error("Error searching for user with username: {}", username, e);
        }
        return user;
    }

    public List<Etudiant> getModuleStudents(int moduleId) {
        logger.info("Fetching students for module ID: {}", moduleId);
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
            logger.info("Retrieved {} students for module ID: {}", students.size(), moduleId);
        } catch (SQLException e) {
            logger.error("Error fetching students for module ID: {}", moduleId, e);
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

}