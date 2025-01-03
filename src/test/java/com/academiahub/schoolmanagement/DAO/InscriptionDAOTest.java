package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Inscription;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class InscriptionDAOTest {
    private Connection connection;
    private InscriptionDAO inscriptionDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        setupTestDatabase();
        inscriptionDAO = new InscriptionDAO(connection);
    }

    private void setupTestDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS inscriptions");
            stmt.execute("DROP TABLE IF EXISTS etudiants");
            stmt.execute("DROP TABLE IF EXISTS modules");

            stmt.execute("""
                CREATE TABLE etudiants (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(100),
                    prenom VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE modules (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom_module VARCHAR(100)
                )
            """);

            stmt.execute("""
                CREATE TABLE inscriptions (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    etudiant_id INT,
                    module_id INT,
                    date_inscription DATE,
                    FOREIGN KEY (etudiant_id) REFERENCES etudiants(id),
                    FOREIGN KEY (module_id) REFERENCES modules(id)
                )
            """);

            // Insert test data
            stmt.execute("INSERT INTO etudiants (id, nom, prenom) VALUES (1, 'Dupont', 'Jean')");
            stmt.execute("INSERT INTO modules (id, nom_module) VALUES (1, 'Mathematics')");
        }
    }

    @Test
    void testGetAllInscriptions() throws SQLException {
        // Add test inscription
        Inscription testInscription = createTestInscription();
        inscriptionDAO.addInscription(testInscription);

        List<Inscription> inscriptions = inscriptionDAO.getAllInscriptions();
        assertFalse(inscriptions.isEmpty());
        Inscription inscription = inscriptions.get(0);
        assertEquals("Dupont", inscription.getEtudiantNom());
        assertEquals("Jean", inscription.getEtudiantPrenom());
        assertEquals("Mathematics", inscription.getModuleNom());
    }

    @Test
    void testAddInscription() throws SQLException {
        Inscription inscription = createTestInscription();
        assertTrue(inscriptionDAO.addInscription(inscription));
    }

    @Test
    void testDeleteInscription() throws SQLException {
        // First add an inscription
        Inscription inscription = createTestInscription();
        inscriptionDAO.addInscription(inscription);

        // Get the ID of the newly added inscription
        List<Inscription> inscriptions = inscriptionDAO.getAllInscriptions();
        int inscriptionId = inscriptions.get(0).getId();

        // Test deletion
        assertTrue(inscriptionDAO.deleteInscription(inscriptionId));
        inscriptions = inscriptionDAO.getAllInscriptions();
        assertTrue(inscriptions.isEmpty());
    }

    private Inscription createTestInscription() {
        Inscription inscription = new Inscription();
        inscription.setEtudiantId(1);
        inscription.setModuleId(1);
        inscription.setDateInscription(new Date(System.currentTimeMillis()));
        return inscription;
    }
}