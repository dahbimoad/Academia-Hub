package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Notification;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class NotificationsDAO {
    private Connection connection; // Votre connexion à la base de données

    public NotificationsDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Notification> getStudentsWithoutModules() {
        List<Notification> notifications = new ArrayList<>();
        String query = """
            SELECT e.id, e.nom, e.prenom 
            FROM etudiants e 
            LEFT JOIN inscriptions i ON e.id = i.etudiant_id 
            WHERE i.id IS NULL
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                String message = String.format("L'étudiant %s %s n'est inscrit à aucun module",
                        rs.getString("nom"), rs.getString("prenom"));
                notifications.add(new Notification(
                        rs.getInt("id"),
                        message,
                        "ALERT",
                        LocalDateTime.now(),
                        false
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }

    public List<Notification> getUpcomingDeadlines() {
        List<Notification> notifications = new ArrayList<>();
        LocalDateTime oneWeekFromNow = LocalDateTime.now().plusDays(7);

        String query = """
            SELECT m.nom_module, i.date_inscription 
            FROM modules m 
            JOIN inscriptions i ON m.id = i.module_id 
            WHERE i.date_inscription <= ?
            GROUP BY m.nom_module, i.date_inscription
            """;

        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setDate(1, Date.valueOf(oneWeekFromNow.toLocalDate()));
            ResultSet rs = stmt.executeQuery();

            while (rs.next()) {
                String message = String.format("Rappel: Date limite d'inscription pour le module %s approche (%s)",
                        rs.getString("nom_module"),
                        rs.getDate("date_inscription").toString());
                notifications.add(new Notification(
                        0, // ID généré automatiquement
                        message,
                        "REMINDER",
                        LocalDateTime.now(),
                        false
                ));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return notifications;
    }
}