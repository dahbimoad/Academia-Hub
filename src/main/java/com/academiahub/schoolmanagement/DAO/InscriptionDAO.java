package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Inscription;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class InscriptionDAO {
    private Connection connection;
    private static final Logger logger = LoggerFactory.getLogger(InscriptionDAO.class);
    public InscriptionDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Inscription> getAllInscriptions() throws SQLException {
        List<Inscription> inscriptions = new ArrayList<>();
        String query = "SELECT " +
                "i.id AS inscription_id, " +
                "e.id AS etudiant_id, " +
                "m.id AS module_id, " +
                "e.nom AS etudiant_nom, " +
                "e.prenom AS etudiant_prenom, " +
                "m.nom_module AS module_nom, " +
                "i.date_inscription " +
                "FROM inscriptions i " +
                "INNER JOIN etudiants e ON i.etudiant_id = e.id " +
                "INNER JOIN modules m ON i.module_id = m.id";

        try (PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Inscription inscription = new Inscription();
                inscription.setInscriptionId(resultSet.getInt("inscription_id"));
                inscription.setEtudiantId(resultSet.getInt("etudiant_id"));
                inscription.setModuleId(resultSet.getInt("module_id"));
                inscription.setEtudiantNom(resultSet.getString("etudiant_nom"));
                inscription.setEtudiantPrenom(resultSet.getString("etudiant_prenom"));
                inscription.setModuleNom(resultSet.getString("module_nom"));
                inscription.setDateInscription(resultSet.getDate("date_inscription"));
                inscriptions.add(inscription);
            }
        }
        return inscriptions;
    }


    public boolean addInscription(Inscription inscription) throws SQLException {
        String query = "INSERT INTO inscriptions (etudiant_id, module_id, date_inscription) VALUES (?, ?, ?)";

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, inscription.getEtudiantId()); // Use etudiantId directly
            stmt.setInt(2, inscription.getModuleId()); // Use moduleId directly
            stmt.setDate(3, new java.sql.Date(inscription.getDateInscription().getTime())); // Convert Date to java.sql.Date

            return stmt.executeUpdate() > 0; // Execute and check if rows were affected
        }
    }


    public boolean deleteInscription(int inscriptionId) throws SQLException {
        String query = "DELETE FROM inscriptions WHERE id = ?";
        connection.setAutoCommit(false); // Start transaction

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, inscriptionId);
            int result = stmt.executeUpdate();
            connection.commit(); // Commit the transaction
            return result > 0;
        } catch (SQLException e) {
            connection.rollback(); // Rollback on error
            System.err.println("Error deleting inscription: " + e.getMessage());
            throw e; // Re-throw the exception to be handled by the controller
        } finally {
            connection.setAutoCommit(true); // Reset auto-commit
        }
    }



   /* private Inscription mapRowToInscription(ResultSet rs) throws SQLException {
        Inscription inscription = new Inscription();
        Etudiant etudiant = new Etudiant(); // Assume you load Etudiant details elsewhere
        Module module = new Module();     // Assume you load Module details elsewhere

        inscription.setId(rs.getInt("id"));
        etudiant.setId(rs.getInt("etudiant_id"));
        module.setId(rs.getInt("module_id"));
        inscription.setDateInscription(rs.getDate("date_inscription"));

        inscription.setEtudiant(etudiant);
        inscription.setModule(module);

        return inscription;
    }*/
}
