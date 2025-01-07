package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Etudiant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class EtudiantDAOTest {
    private Connection connection;
    private EtudiantDAO etudiantDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        cleanDatabase();
        etudiantDAO = new EtudiantDAO(connection);
    }

    private void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS etudiants");
            stmt.execute("""
                CREATE TABLE etudiants (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    matricule VARCHAR(50),
                    nom VARCHAR(100),
                    prenom VARCHAR(100),
                    date_naissance DATE,
                    email VARCHAR(100),
                    promotion VARCHAR(50)
                )
            """);
        }
    }

    @Test
    void testAddAndGetEtudiant() throws SQLException {
        Etudiant etudiant = createTestEtudiant();
        etudiantDAO.addEtudiant(etudiant);

        List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
        assertEquals(1, etudiants.size());
        assertEquals("Dupont", etudiants.get(0).getNom());
    }

    @Test
    void testUpdateEtudiant() throws SQLException {
        // 1. Création et ajout d'un étudiant de test
        Etudiant etudiant = createTestEtudiant();
        etudiantDAO.addEtudiant(etudiant);

        // 2. Récupération de l'étudiant depuis la base de données
        List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
        Etudiant savedEtudiant = etudiants.get(0);

        // 3. Modification des informations de l'étudiant
        savedEtudiant.setNom("Martin");  // Changement du nom
        savedEtudiant.setDateNaissance(new Date(System.currentTimeMillis())); // Mise à jour de la date

        // 4. Sauvegarde des modifications
        etudiantDAO.updateEtudiant(savedEtudiant);

        // 5. Vérification que la modification a bien été effectuée
        etudiants = etudiantDAO.getAllEtudiants();
        assertEquals("Martin", etudiants.get(0).getNom());
    }

    @Test
    void testDeleteEtudiant() throws SQLException {
        Etudiant etudiant = createTestEtudiant();
        etudiantDAO.addEtudiant(etudiant);

        List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
        assertFalse(etudiants.isEmpty());

        etudiantDAO.deleteEtudiant(etudiants.get(0).getId());
        etudiants = etudiantDAO.getAllEtudiants();
        assertTrue(etudiants.isEmpty());
    }

    private Etudiant createTestEtudiant() {
        Etudiant etudiant = new Etudiant();
        etudiant.setMatricule("MAT001");
        etudiant.setNom("Dupont");
        etudiant.setPrenom("Jean");
        etudiant.setEmail("jean@test.com");
        etudiant.setPromotion("2024");
        etudiant.setDateNaissance(new Date(System.currentTimeMillis()));
        return etudiant;
    }
}