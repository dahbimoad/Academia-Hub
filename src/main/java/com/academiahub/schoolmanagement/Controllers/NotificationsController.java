package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.NotificationDAO;
import com.academiahub.schoolmanagement.Models.Notification;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.sql.SQLException;
import java.util.List;

public class NotificationsController {
    @FXML private ListView<Notification> notificationsListView;
    @FXML private Button markAllReadButton;

    private NotificationDAO notificationDAO;
    private String userRole;

    public void initialize() {
        setupNotificationsList();
    }


    public void setUserRole(String role) {
    this.userRole = role;
    if (this.notificationDAO != null) { // Ensure DAO is set
        loadNotifications();
    }
}

    private void setupNotificationsList() {
        notificationsListView.setCellFactory(lv -> new NotificationCell());
        loadNotifications();
    }

    private void loadNotifications() {
    if (notificationDAO == null || userRole == null) {
        throw new IllegalStateException("NotificationDAO and userRole must be set before loading notifications!");
    }

    try {
        List<Notification> notifications = notificationDAO.getNotificationsForRole(userRole);
        notificationsListView.setItems(FXCollections.observableArrayList(notifications));
    } catch (SQLException e) {
        e.printStackTrace();
        showError("Erreur lors du chargement des notifications: " + e.getMessage());
    }
}



    @FXML
private void handleMarkAllRead() {
    try {
        for (Notification notification : notificationsListView.getItems()) {
            if (!notification.isReadStatus()) {
                notificationDAO.markAsRead(notification.getId());
                notification.setReadStatus(true); // Update local state
            }
        }
        notificationsListView.refresh(); // Refresh the ListView to update styles
    } catch (SQLException e) {
        e.printStackTrace();
        // Show error message
    }
}

    public void setNotificationDAO(NotificationDAO notificationDAO) {
    this.notificationDAO = notificationDAO;
    if (this.userRole != null) { // Ensure role is set
        loadNotifications();
    }
}


    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}