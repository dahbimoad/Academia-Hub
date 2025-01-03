package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Utilisateur;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.sql.*;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class UtilisateurDAOTest {
    private Connection connection;
    private UtilisateurDAO utilisateurDAO;

    @BeforeEach
    void setUp() throws SQLException {
        connection = DriverManager.getConnection("jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1");
        cleanDatabase();
        utilisateurDAO = new UtilisateurDAO(connection);
    }

    private void cleanDatabase() throws SQLException {
        try (Statement stmt = connection.createStatement()) {
            stmt.execute("DROP TABLE IF EXISTS utilisateurs");
            stmt.execute("""
                CREATE TABLE utilisateurs (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    username VARCHAR(50),
                    password VARCHAR(100),
                    role VARCHAR(20)
                )
            """);
        }
    }

    @Test
    void testAuthenticate() throws SQLException {
        Utilisateur user = createTestUser();
        utilisateurDAO.create(user);

        Utilisateur authenticated = utilisateurDAO.authenticate("testuser", "password123");
        assertNotNull(authenticated);
        assertEquals("testuser", authenticated.getUsername());
        assertEquals("PROFESSEUR", authenticated.getRole());

        authenticated = utilisateurDAO.authenticate("testuser", "wrongpassword");
        assertNull(authenticated);
    }

    @Test
    void testCreateAndRead() throws SQLException {
        Utilisateur user = createTestUser();
        assertTrue(utilisateurDAO.create(user));

        Utilisateur retrieved = utilisateurDAO.read(user.getId());
        assertNotNull(retrieved);
        assertEquals(user.getUsername(), retrieved.getUsername());
        assertEquals(user.getRole(), retrieved.getRole());
    }

    @Test
    void testUpdate() throws SQLException {
        Utilisateur user = createTestUser();
        utilisateurDAO.create(user);

        user.setUsername("updateduser");
        user.setRole("SECRETAIRE");
        assertTrue(utilisateurDAO.update(user));

        Utilisateur updated = utilisateurDAO.read(user.getId());
        assertEquals("updateduser", updated.getUsername());
        assertEquals("SECRETAIRE", updated.getRole());
    }

    @Test
    void testDelete() throws SQLException {
        Utilisateur user = createTestUser();
        utilisateurDAO.create(user);

        UtilisateurDAO.delete(user.getId());
        assertNull(utilisateurDAO.read(user.getId()));
    }

    @Test
    void testFindAll() throws SQLException {
        utilisateurDAO.create(createTestUser());
        Utilisateur admin = createTestUser();
        admin.setRole("ADMIN");
        utilisateurDAO.create(admin);

        List<Utilisateur> users = utilisateurDAO.findAll();
        assertEquals(1, users.size());  // Should only return non-admin users
        assertEquals("PROFESSEUR", users.get(0).getRole());
    }

    @Test
    void testFindByUsername() throws SQLException {
        Utilisateur user = createTestUser();
        utilisateurDAO.create(user);

        Utilisateur found = utilisateurDAO.findByUsername("testuser");
        assertNotNull(found);
        assertEquals(user.getUsername(), found.getUsername());

        assertNull(utilisateurDAO.findByUsername("nonexistent"));
    }

    @Test
    void testUpdatePassword() throws SQLException {
        Utilisateur user = createTestUser();
        utilisateurDAO.create(user);

        assertTrue(utilisateurDAO.updatePassword(user, "password123", "newpassword"));
        assertFalse(utilisateurDAO.updatePassword(user, "wrongpassword", "newpassword"));
    }

    private Utilisateur createTestUser() {
        Utilisateur user = new Utilisateur();
        user.setUsername("testuser");
        user.setPassword("password123");
        user.setRole("PROFESSEUR");
        return user;
    }
}