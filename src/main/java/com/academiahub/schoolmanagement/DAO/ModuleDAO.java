package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.Models.Inscription;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

public class ModuleDAO {
    private static final Logger logger = LoggerFactory.getLogger(ModuleDAO.class);
    private Connection connection;

    public ModuleDAO(Connection connection) {
        this.connection = connection;
        logger.debug("ModuleDAO initialized with connection");
    }

    public ModuleDAO() {
        logger.debug("ModuleDAO initialized with default constructor");
    }
    public boolean existsByCode(String codeModule) {
        String query = "SELECT COUNT(*) FROM modules WHERE code_module = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setString(1, codeModule);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            logger.info("Erreur lors de la vérification de l'existence du code du module: ");
        }
        return false;
    }
    public boolean create(Module module) {
        logger.info("Creating new module: {}", module.getNomModule());
        String sql = "INSERT INTO modules(nom_module, code_module, professeur_id) VALUES (?, ?, ?)";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            pstmt.setString(1, module.getNomModule());
            pstmt.setString(2, module.getCodeModule());
            pstmt.setInt(3, module.getProfesseur().getId());

            int affectedRows = pstmt.executeUpdate();
            if (affectedRows > 0) {
                try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        module.setId(generatedKeys.getInt(1));
                        logger.info("Successfully created module with ID: {}", module.getId());
                        return true;
                    }
                }
            }
            logger.warn("Failed to create module: {}", module.getNomModule());
        } catch (SQLException e) {
            logger.error("Error creating module: {}", module.getNomModule(), e);
        }
        return false;
    }
    public List<Module> getAllModules() throws SQLException {
        List<Module> modules = new ArrayList<>();
        String query = "SELECT id, nom_module FROM modules";
        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Module module = new Module();
                module.setId(resultSet.getInt("id"));
                module.setNomModule(resultSet.getString("nom_module"));
                modules.add(module);
            }
        }
        return modules;
    }

    public Module read(int id) {
        logger.info("Fetching module with ID: {}", id);
        String sql = "SELECT m.*, p.id as prof_id, p.nom as prof_nom, p.prenom as prof_prenom " +
                "FROM modules m " +
                "LEFT JOIN professeurs p ON m.professeur_id = p.id " +
                "WHERE m.id = ?";
        Module module = null;

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    Professeur professeur = new Professeur();
                    professeur.setId(rs.getInt("prof_id"));
                    professeur.setNom(rs.getString("prof_nom"));
                    professeur.setPrenom(rs.getString("prof_prenom"));

                    module = new Module();
                    module.setId(rs.getInt("id"));
                    module.setNomModule(rs.getString("nom_module"));
                    module.setCodeModule(rs.getString("code_module"));
                    module.setProfesseur(professeur);

                    // Load inscriptions
                    module.setInscriptions(loadInscriptions(id));
                    // Set number of students
                    module.setNbEtudiants(module.getInscriptions().size());

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

    private Set<Inscription> loadInscriptions(int moduleId) {
        Set<Inscription> inscriptions = new HashSet<>();
        String sql = "SELECT * FROM inscriptions WHERE module_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, moduleId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Inscription inscription = new Inscription();
                    inscription.setId(rs.getInt("id"));
                    // Set other inscription properties as needed
                    inscriptions.add(inscription);
                }
            }
        } catch (SQLException e) {
            logger.error("Error loading inscriptions for module ID: {}", moduleId, e);
        }
        return inscriptions;
    }

    public boolean update(Module module) {
        logger.info("Updating module with ID: {}", module.getId());
        String sql = "UPDATE modules SET nom_module = ?, code_module = ?, professeur_id = ? WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(sql)) {

            stmt.setString(1, module.getNomModule());
            stmt.setString(2, module.getCodeModule());
            stmt.setInt(3, module.getProfesseur().getId());
            stmt.setInt(4, module.getId());

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                logger.info("Successfully updated module with ID: {}", module.getId());
                return true;
            } else {
                logger.warn("No module found to update with ID: {}", module.getId());
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error updating module with ID: {}", module.getId(), e);
            return false;
        }
    }

    public boolean delete(int id) {
        logger.info("Attempting to delete module with ID: {}", id);
        String sql = "DELETE FROM modules WHERE id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, id);
            int rowsDeleted = pstmt.executeUpdate();
            if (rowsDeleted > 0) {
                logger.info("Successfully deleted module with ID: {}", id);
                return true;
            } else {
                logger.warn("No module found to delete with ID: {}", id);
                return false;
            }
        } catch (SQLException e) {
            logger.error("Error deleting module with ID: {}", id, e);
            return false;
        }
    }

    public List<Module> findAll() {
        logger.info("Fetching all modules");
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT m.*, p.id as prof_id, p.nom as prof_nom, p.prenom as prof_prenom " +
                "FROM modules m " +
                "LEFT JOIN professeurs p ON m.professeur_id = p.id";

        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                Professeur professeur = new Professeur();
                professeur.setId(rs.getInt("prof_id"));
                professeur.setNom(rs.getString("prof_nom"));
                professeur.setPrenom(rs.getString("prof_prenom"));

                Module module = new Module();
                module.setId(rs.getInt("id"));
                module.setNomModule(rs.getString("nom_module"));
                module.setCodeModule(rs.getString("code_module"));
                module.setProfesseur(professeur);

                // Load inscriptions for each module
                Set<Inscription> inscriptions = loadInscriptions(module.getId());
                module.setInscriptions(inscriptions);
                module.setNbEtudiants(inscriptions.size());

                modules.add(module);
            }
            logger.info("Successfully retrieved {} modules", modules.size());
        } catch (SQLException e) {
            logger.error("Error fetching all modules", e);
        }
        return modules;
    }

    public List<Module> findByProfesseur(int professeurId) {
        logger.info("Fetching modules for professor ID: {}", professeurId);
        List<Module> modules = new ArrayList<>();
        String sql = "SELECT * FROM modules WHERE professeur_id = ?";

        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, professeurId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    Module module = new Module();
                    module.setId(rs.getInt("id"));
                    module.setNomModule(rs.getString("nom_module"));
                    module.setCodeModule(rs.getString("code_module"));

                    // Load professor details
                    Professeur professeur = new ProfesseurDAO().read(professeurId).getProfesseur();
                    module.setProfesseur(professeur);

                    // Load inscriptions
                    Set<Inscription> inscriptions = loadInscriptions(module.getId());
                    module.setInscriptions(inscriptions);
                    module.setNbEtudiants(inscriptions.size());

                    modules.add(module);
                }
            }
            logger.info("Retrieved {} modules for professor ID: {}", modules.size(), professeurId);
        } catch (SQLException e) {
            logger.error("Error fetching modules for professor ID: {}", professeurId, e);
        }
        return modules;
    }


}