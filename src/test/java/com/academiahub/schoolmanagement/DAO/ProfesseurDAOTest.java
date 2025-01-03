package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.Models.Etudiant;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class ProfesseurDAOTest {
    private Connection connection;
    private ProfesseurDAO professeurDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        initDatabase();
        professeurDAO = new ProfesseurDAO(connection);
    }

    private void initDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            // Drop tables if they exist
            stmt.execute("DROP TABLE IF EXISTS inscriptions");
            stmt.execute("DROP TABLE IF EXISTS modules");
            stmt.execute("DROP TABLE IF EXISTS professeurs");
            stmt.execute("DROP TABLE IF EXISTS etudiants");
            stmt.execute("DROP TABLE IF EXISTS utilisateurs");

            // Create tables
            stmt.execute("""
                CREATE TABLE utilisateurs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50),
                    role VARCHAR(20)
                )
            """);

            stmt.execute("""
                CREATE TABLE professeurs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom VARCHAR(100),
                    prenom VARCHAR(100),
                    specialite VARCHAR(100),
                    user_id INT,
                    FOREIGN KEY (user_id) REFERENCES utilisateurs(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE modules (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    nom_module VARCHAR(100),
                    code_module VARCHAR(50),
                    professeur_id INT,
                    FOREIGN KEY (professeur_id) REFERENCES professeurs(id)
                )
            """);

            stmt.execute("""
                CREATE TABLE etudiants (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    matricule VARCHAR(50),
                    nom VARCHAR(100),
                    prenom VARCHAR(100),
                    email VARCHAR(100),
                    promotion VARCHAR(50)
                )
            """);

            stmt.execute("""
                CREATE TABLE inscriptions (
                    module_id INT,
                    etudiant_id INT,
                    FOREIGN KEY (module_id) REFERENCES modules(id),
                    FOREIGN KEY (etudiant_id) REFERENCES etudiants(id)
                )
            """);
        }
    }

    private void insertTestData() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO utilisateurs (username, role) VALUES ('prof1', 'PROFESSEUR')");
            stmt.execute("INSERT INTO professeurs (nom, prenom, specialite, user_id) VALUES ('Dupont', 'Jean', 'Math', 1)");
            stmt.execute("INSERT INTO modules (nom_module, code_module, professeur_id) VALUES ('Mathématiques', 'MATH101', 1)");
            stmt.execute("INSERT INTO etudiants (matricule, nom, prenom, email, promotion) VALUES ('E001', 'Martin', 'Paul', 'paul@test.com', '2024')");
            stmt.execute("INSERT INTO inscriptions (module_id, etudiant_id) VALUES (1, 1)");
        }
    }

    @Test
    void testGetProfesseurModules() throws SQLException {
        insertTestData();
        List<Module> modules = professeurDAO.getProfesseurModules("prof1");
        assertFalse(modules.isEmpty());
        assertEquals("Mathématiques", modules.get(0).getNomModule());
        assertEquals(1, modules.get(0).getNbEtudiants());
    }

    @Test
    void testCreate() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("INSERT INTO utilisateurs (username, role) VALUES ('prof2', 'PROFESSEUR')");
        }

        Professeur prof = new Professeur();
        prof.setNom("Smith");
        prof.setPrenom("John");
        prof.setSpecialite("Physics");
        prof.setUserId(1);

        professeurDAO.create(prof);
        assertNotNull(prof.getId());
    }

    @Test
    void testFindByNameOrSpeciality() throws SQLException {
        insertTestData();
        List<Professeur> profs = professeurDAO.findByNameOrSpeciality("math");
        assertFalse(profs.isEmpty());
        assertEquals("Dupont", profs.get(0).getNom());
    }

    @Test
    void testUpdate() throws SQLException {
        insertTestData();
        Professeur prof = professeurDAO.readByUserId(1);
        prof.setNom("Updated");
        professeurDAO.update(prof);

        Professeur updated = professeurDAO.readByUserId(1);
        assertEquals("Updated", updated.getNom());
    }

    @Test
    void testDelete() throws SQLException {
        insertTestData();
        Professeur prof = professeurDAO.readByUserId(1);
        professeurDAO.delete(prof.getId());
        assertNull(professeurDAO.readByUserId(1));
    }
}