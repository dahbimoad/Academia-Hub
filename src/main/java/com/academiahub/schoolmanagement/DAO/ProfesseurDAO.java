package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Inscription;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ProfesseurDAO {
    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(ProfesseurDAO.class);
    public ProfesseurDAO(Connection connection) {
        this.connection = connection;
    }

    public ProfesseurDAO() {

    }


    private Set<Inscription> loadInscriptions(int moduleId) {
        logger.info("Loading inscriptions for module ID: {}", moduleId);
        Set<Inscription> inscriptions = new HashSet<>();

        String sql = "SELECT i.*, e.nom as etudiant_nom, e.prenom as etudiant_prenom, " +
                "m.nom_module as module_nom " +
                "FROM inscriptions i " +
                "JOIN etudiants e ON i.etudiant_id = e.id " +
                "JOIN modules m ON i.module_id = m.id " +
                "WHERE i.module_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            pstmt.setInt(1, moduleId);

            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inscription inscription = new Inscription();
                    inscription.setId(rs.getInt("id"));
                    inscription.setEtudiantId(rs.getInt("etudiant_id"));
                    inscription.setEtudiantNom(rs.getString("etudiant_nom"));
                    inscription.setEtudiantPrenom(rs.getString("etudiant_prenom"));
                    inscription.setModuleId(rs.getInt("module_id"));
                    inscription.setModuleNom(rs.getString("module_nom"));
                    inscription.setDateInscription(rs.getDate("date_inscription"));

                    inscriptions.add(inscription);
                }
                logger.info("Loaded {} inscriptions for module ID: {}", inscriptions.size(), moduleId);
            }
        } catch (SQLException e) {
            logger.error("Error loading inscriptions for module ID: {}", moduleId, e);
        }

        return inscriptions;
    }


    public Module read(int id) {
        logger.info("Fetching module with ID: {}", id);
        String sql = "SELECT m.*, p.id as prof_id, p.nom as prof_nom, p.prenom as prof_prenom, " +
                "p.specialite as prof_specialite, p.user_id as prof_user_id " +
                "FROM modules m " +
                "LEFT JOIN professeurs p ON m.professeur_id = p.id " +
                "WHERE m.id = ?";
        Module module = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    // Créer et remplir l'objet Professeur
                    Professeur professeur = new Professeur();
                    professeur.setId(rs.getInt("prof_id"));
                    professeur.setNom(rs.getString("prof_nom"));
                    professeur.setPrenom(rs.getString("prof_prenom"));
                    professeur.setSpecialite(rs.getString("prof_specialite"));
                    professeur.setUserId(rs.getInt("prof_user_id"));

                    // Créer et remplir l'objet Module
                    module = new Module();
                    module.setId(rs.getInt("id"));
                    module.setNomModule(rs.getString("nom_module"));
                    module.setCodeModule(rs.getString("code_module"));
                    module.setProfesseur(professeur);

                    // Charger les inscriptions
                    Set<Inscription> inscriptions = loadInscriptions(id);
                    module.setInscriptions(inscriptions);
                    module.setNbEtudiants(inscriptions.size());

                    logger.info("Successfully retrieved module with ID: {}", id);
                } else {
                    logger.warn("No module found with ID: {}", id);
                }
            }
        } catch (SQLException e) {
            logger.error("Error fetching module with ID: {}", id, e);
        }
        return module;
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