package com.academiahub.schoolmanagement.DAO;

import com.academiahub.schoolmanagement.Models.Notification;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class NotificationDAO {
    private Connection connection;

    public NotificationDAO(Connection connection) {
        this.connection = connection;
    }

    public List<Notification> getNotificationsForRole(String role) throws SQLException {
    String query = "SELECT * FROM notifications WHERE recipient_role IN ('ALL', ?) " +
                  "ORDER BY created_at DESC LIMIT 50";
    List<Notification> notifications = new ArrayList<>();

    try (PreparedStatement stmt = connection.prepareStatement(query)) {
        stmt.setString(1, role);
        ResultSet rs = stmt.executeQuery();

        while (rs.next()) {
            Notification notification = new Notification();
            notification.setId(rs.getInt("id"));
            notification.setTitle(rs.getString("title"));
            notification.setMessage(rs.getString("message"));
            notification.setType(rs.getString("type"));
            notification.setRecipientRole(rs.getString("recipient_role"));
            notification.setReadStatus(rs.getBoolean("read_status"));
            notification.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());

            notifications.add(notification);
        }
    }
    return notifications;
}


    public void markAsRead(int notificationId) throws SQLException {
        String query = "UPDATE notifications SET read_status = TRUE WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(query)) {
            stmt.setInt(1, notificationId);
            stmt.executeUpdate();
        }
    }
}