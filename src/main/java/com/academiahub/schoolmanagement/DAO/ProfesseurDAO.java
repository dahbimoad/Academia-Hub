package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;


import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ProfesseurDAO {
    private Connection connection;

    public ProfesseurDAO(Connection connection) {
        this.connection = connection;
    }

    public ProfesseurDAO() {

    }


    // Get modules for a professor using professor ID (not user_id)
    public List<Module> getProfesseurModules(String username) {
        List<Module> modules = new ArrayList<>();
        String query = "SELECT m.* FROM modules m " +
                "JOIN professeurs p ON m.professeur_id = p.id " +
                "JOIN utilisateurs u ON p.user_id = u.id " +
                "WHERE u.username = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, username);
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setNomModule(rs.getString("nom_module"));
                module.setCodeModule(rs.getString("code_module"));
                // Fetch and set the number of students
                int moduleId = module.getId();
                List<Etudiant> students = getModuleStudents(moduleId);
                module.setNbEtudiants(students.size());
                modules.add(module);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return modules;
    }
    public List<Etudiant> getModuleStudents(int moduleId) {
        List<Etudiant> students = new ArrayList<>();
        String query = "SELECT e.* FROM etudiants e " +
                "JOIN inscriptions i ON e.id = i.etudiant_id " +
                "WHERE i.module_id = ?";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, moduleId);
            ResultSet rs = stmt.executeQuery();

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
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return students;
    }

    public void create(Professeur professeur) {
        String checkRoleSql = "SELECT role FROM utilisateurs WHERE id = ?";
        String insertSql = "INSERT INTO professeurs(nom, prenom, specialite, user_id) VALUES (?, ?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection()) {
            // Désactiver l'auto-commit pour gérer la transaction manuellement
            conn.setAutoCommit(false);

            // Vérifier le rôle de l'utilisateur
            try (PreparedStatement checkStmt = conn.prepareStatement(checkRoleSql)) {
                checkStmt.setInt(1, professeur.getUserId());
                try (ResultSet rs = checkStmt.executeQuery()) {
                    if (rs.next()) {
                        String role = rs.getString("role");
                        if (!"PROFESSEUR".equalsIgnoreCase(role)) {
                            throw new SQLException("L'utilisateur avec user_id " + professeur.getUserId() + " n'a pas le rôle 'PROFESSEUR'.");
                        }
                    } else {
                        throw new SQLException("Aucun utilisateur trouvé avec user_id " + professeur.getUserId());
                    }
                }
            }

            // Insérer le professeur dans la base de données
            try (PreparedStatement insertStmt = conn.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                insertStmt.setString(1, professeur.getNom());
                insertStmt.setString(2, professeur.getPrenom());
                insertStmt.setString(3, professeur.getSpecialite());
                insertStmt.setInt(4, professeur.getUserId());
                insertStmt.executeUpdate();

                try (ResultSet generatedKeys = insertStmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        professeur.setId(generatedKeys.getInt(1));
                    } else {
                        throw new SQLException("Création du professeur échouée, aucun ID obtenu.");
                    }
                }

                System.out.println("Professeur créé avec succès !");
            }

            // Valider la transaction
            conn.commit();

        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer le rollback en cas d'erreur
            try (Connection conn = DatabaseConnection.getConnection()) {
                if (conn != null && !conn.getAutoCommit()) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public Professeur readByUserId(int userId) {
        String sql = "SELECT * FROM professeurs WHERE user_id = ?";
        Professeur prof = null;
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    prof = new Professeur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getInt("user_id")
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
            pstmt.setInt(4, professeur.getUserId());
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
                        rs.getInt("user_id")
                );
                liste.add(prof);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

    public List<Professeur> findByNameOrSpeciality(String keyword) {
        List<Professeur> liste = new ArrayList<>();
        String sql = "SELECT * FROM professeurs " +
                "WHERE LOWER(nom) LIKE ? OR LOWER(prenom) LIKE ? OR LOWER(specialite) LIKE ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            String likeKeyword = "%" + keyword.toLowerCase() + "%";
            pstmt.setString(1, likeKeyword);
            pstmt.setString(2, likeKeyword);
            pstmt.setString(3, likeKeyword);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Professeur prof = new Professeur(
                            rs.getInt("id"),
                            rs.getString("nom"),
                            rs.getString("prenom"),
                            rs.getString("specialite"),
                            rs.getInt("user_id")
                    );
                    liste.add(prof);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return liste;
    }

}