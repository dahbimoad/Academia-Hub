package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
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
    public boolean create(Utilisateur utilisateur) {
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
                        return true;
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
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

    public boolean update(Utilisateur utilisateur) {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE utilisateurs SET username = ?, password = ?, role = ? WHERE id = ?")) {
            stmt.setString(1, utilisateur.getUsername());
            stmt.setString(2, utilisateur.getPassword());
            stmt.setString(3, utilisateur.getRole());
            stmt.setInt(4, utilisateur.getId());
            int rowsUpdated = stmt.executeUpdate();
            return rowsUpdated > 0;
        } catch (SQLException e) {
            e.printStackTrace(); // Ajouter un journal pour déboguer
            return false;
        }
    }


    public static void delete(int id) {
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



    private Connection getConnection() {
        try {
            String url = "jdbc:postgresql://localhost:5432/ecole";
            String user = "postgres";
            String password = "Imad2002";
            return DriverManager.getConnection(url, user, password);
        } catch (SQLException e) {
            e.printStackTrace();
            return null; // Cela pourrait provoquer le NullPointerException
        }
    }

    // ======================================
    // 9. Récupérer les Modules Enseignés par le Nom d'Utilisateur
    // ======================================

}