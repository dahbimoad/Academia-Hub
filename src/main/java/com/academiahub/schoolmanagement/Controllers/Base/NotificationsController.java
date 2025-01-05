package com.academiahub.schoolmanagement.Controllers.Base;

import com.academiahub.schoolmanagement.DAO.NotificationsDAO;
import com.academiahub.schoolmanagement.Models.Notification;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.ListView;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.control.Button;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.stage.Stage;

import java.sql.Connection;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class NotificationsController {
    @FXML
    private Button closeButton;
    @FXML
    private ListView<Notification> notificationListView;
    @FXML
    private Label noNotificationsLabel;

    private NotificationsDAO notificationsDAO;
    private ObservableList<Notification> notifications;

    @FXML
    public void initialize() {
        notifications = FXCollections.observableArrayList();
        notificationListView.setItems(notifications);

        // Configuration de la cellule personnalisée
        notificationListView.setCellFactory(param -> new NotificationListCell());

        // Charger les notifications

    }

    public void setNotificationsDAO(Connection connection) {
        this.notificationsDAO = new NotificationsDAO(connection);
        refreshNotifications();
    }

    public void refreshNotifications() {
        notifications.clear();
        notifications.addAll(notificationsDAO.getStudentsWithoutModules());
        notifications.addAll(notificationsDAO.getUpcomingDeadlines());

        if (notifications.isEmpty()) {
            noNotificationsLabel.setVisible(true);
            notificationListView.setVisible(false);
        } else {
            noNotificationsLabel.setVisible(false);
            notificationListView.setVisible(true);
        }
    }

    // Classe interne pour personnaliser l'affichage des notifications
    private class NotificationListCell extends javafx.scene.control.ListCell<Notification> {
        @Override
        protected void updateItem(Notification notification, boolean empty) {
            super.updateItem(notification, empty);

            if (empty || notification == null) {
                setText(null);
                setGraphic(null);
            } else {
                VBox container = new VBox(8);
                container.getStyleClass().add("notification-cell");

                HBox headerBox = new HBox(10);
                headerBox.setAlignment(Pos.CENTER_LEFT);

                Label typeLabel = new Label(notification.getType());
                typeLabel.getStyleClass().add(
                        notification.getType().equals("ALERT") ? "alert-label" : "reminder-label"
                );

                Label dateLabel = new Label(notification.getDateCreation()
                        .format(DateTimeFormatter.ofPattern("dd MMM, HH:mm")));
                dateLabel.getStyleClass().add("date-label");

                headerBox.getChildren().addAll(typeLabel, dateLabel);

                Label messageLabel = new Label(notification.getMessage());
                messageLabel.getStyleClass().add("message-label");
                messageLabel.setWrapText(true);

                container.getChildren().addAll(headerBox, messageLabel);

                if (!notification.isRead()) {
                    container.getStyleClass().add("unread-notification");
                }

                setGraphic(container);
            }
        }
    }

    @FXML
    public void handleClose() {
        Stage stage = (Stage) closeButton.getScene().getWindow();
        stage.close();
    }
}