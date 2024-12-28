package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class UtilisateurDAO {

    public void create(Utilisateur utilisateur) {
        String sql = "INSERT INTO utilisateurs(username, password, role) VALUES (?, ?, ?)";
        Connection conn = null;
        PreparedStatement pstmt = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("ERREUR: La connexion à la base de données est null");
                return;
            }

            // Désactiver l'auto-commit pour gérer la transaction manuellement
            conn.setAutoCommit(false);

            System.out.println("Tentative d'insertion d'utilisateur:");
            System.out.println("Username: " + utilisateur.getUsername());
            System.out.println("Role: " + utilisateur.getRole());

            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            pstmt.setString(1, utilisateur.getUsername());
            pstmt.setString(2, utilisateur.getPassword());
            pstmt.setString(3, utilisateur.getRole());

            int rowsAffected = pstmt.executeUpdate();

            if (rowsAffected > 0) {
                ResultSet generatedKeys = pstmt.getGeneratedKeys();
                if (generatedKeys.next()) {
                    utilisateur.setId(generatedKeys.getInt(1));
                    System.out.println("Utilisateur créé avec succès! ID généré: " + utilisateur.getId());
                }
                conn.commit();
            } else {
                System.err.println("ERREUR: Aucune ligne n'a été insérée");
                conn.rollback();
            }

        } catch (SQLException e) {
            try {
                if (conn != null) {
                    conn.rollback();
                }
            } catch (SQLException ex) {
                System.err.println("ERREUR lors du rollback: " + ex.getMessage());
            }
            System.err.println("ERREUR SQL lors de la création: " + e.getMessage());
            System.err.println("État SQL: " + e.getSQLState());
            e.printStackTrace();
        } finally {
            try {
                if (pstmt != null) pstmt.close();
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("ERREUR lors de la fermeture des ressources: " + e.getMessage());
            }
        }
    }

    public Utilisateur read(int id) {
        String sql = "SELECT * FROM utilisateurs WHERE id = ?";
        Utilisateur user = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERREUR: La connexion à la base de données est null");
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);
                System.out.println("Recherche de l'utilisateur avec ID: " + id);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        user = new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("role")
                        );
                        System.out.println("Utilisateur trouvé: " + user.getUsername());
                    } else {
                        System.out.println("Aucun utilisateur trouvé avec l'ID: " + id);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors de la lecture: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }

    public void update(Utilisateur utilisateur) {
        String sql = "UPDATE utilisateurs SET username=?, password=?, role=? WHERE id=?";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("ERREUR: La connexion à la base de données est null");
                return;
            }

            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, utilisateur.getUsername());
                pstmt.setString(2, utilisateur.getPassword());
                pstmt.setString(3, utilisateur.getRole());
                pstmt.setInt(4, utilisateur.getId());

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Utilisateur mis à jour avec succès! ID: " + utilisateur.getId());
                    conn.commit();
                } else {
                    System.err.println("ERREUR: Aucune ligne n'a été mise à jour");
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("ERREUR lors du rollback: " + ex.getMessage());
            }
            System.err.println("ERREUR SQL lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("ERREUR lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }

    public void delete(int id) {
        String sql = "DELETE FROM utilisateurs WHERE id = ?";
        Connection conn = null;

        try {
            conn = DatabaseConnection.getConnection();
            if (conn == null) {
                System.err.println("ERREUR: La connexion à la base de données est null");
                return;
            }

            conn.setAutoCommit(false);

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setInt(1, id);

                int rowsAffected = pstmt.executeUpdate();
                if (rowsAffected > 0) {
                    System.out.println("Utilisateur supprimé avec succès! ID: " + id);
                    conn.commit();
                } else {
                    System.err.println("ERREUR: Aucun utilisateur trouvé avec l'ID: " + id);
                    conn.rollback();
                }
            }
        } catch (SQLException e) {
            try {
                if (conn != null) conn.rollback();
            } catch (SQLException ex) {
                System.err.println("ERREUR lors du rollback: " + ex.getMessage());
            }
            System.err.println("ERREUR SQL lors de la suppression: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (conn != null) {
                    conn.setAutoCommit(true);
                    conn.close();
                }
            } catch (SQLException e) {
                System.err.println("ERREUR lors de la fermeture de la connexion: " + e.getMessage());
            }
        }
    }

    public List<Utilisateur> findAll() {
        List<Utilisateur> liste = new ArrayList<>();
        String sql = "SELECT * FROM utilisateurs";

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERREUR: La connexion à la base de données est null");
                return liste;
            }

            try (Statement stmt = conn.createStatement();
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
                System.out.println("Nombre d'utilisateurs trouvés: " + liste.size());
            }
        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors de la recherche de tous les utilisateurs: " + e.getMessage());
            e.printStackTrace();
        }
        return liste;
    }

    public Utilisateur findByUsername(String username) {
        String sql = "SELECT * FROM utilisateurs WHERE username = ?";
        Utilisateur user = null;

        try (Connection conn = DatabaseConnection.getConnection()) {
            if (conn == null) {
                System.err.println("ERREUR: La connexion à la base de données est null");
                return null;
            }

            try (PreparedStatement pstmt = conn.prepareStatement(sql)) {
                pstmt.setString(1, username);
                System.out.println("Recherche de l'utilisateur avec username: " + username);

                try (ResultSet rs = pstmt.executeQuery()) {
                    if (rs.next()) {
                        user = new Utilisateur(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("password"),
                                rs.getString("role")
                        );
                        System.out.println("Utilisateur trouvé: " + user.getUsername());
                    } else {
                        System.out.println("Aucun utilisateur trouvé avec le username: " + username);
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("ERREUR SQL lors de la recherche par username: " + e.getMessage());
            e.printStackTrace();
        }
        return user;
    }
}