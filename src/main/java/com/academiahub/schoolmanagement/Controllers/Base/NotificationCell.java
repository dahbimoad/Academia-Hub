package com.academiahub.schoolmanagement.Controllers.Base;

import com.academiahub.schoolmanagement.Models.Notification;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.control.Label;
import javafx.geometry.Insets;

public class NotificationCell extends ListCell<Notification> {
    private VBox content;
    private Label titleLabel;
    private Label messageLabel;
    private Label timeLabel;

    public NotificationCell() {
        titleLabel = new Label();
        titleLabel.getStyleClass().add("notification-title");

        messageLabel = new Label();
        messageLabel.getStyleClass().add("notification-message");
        messageLabel.setWrapText(true);

        timeLabel = new Label();
        timeLabel.getStyleClass().add("notification-time");

        content = new VBox(5);
        content.getChildren().addAll(titleLabel, messageLabel, timeLabel);
        content.setPadding(new Insets(10));
    }

    @Override
    protected void updateItem(Notification notification, boolean empty) {
        super.updateItem(notification, empty);

        if (empty || notification == null) {
            setGraphic(null);
        } else {
            titleLabel.setText(notification.getTitle());
            messageLabel.setText(notification.getMessage());
            timeLabel.setText(notification.getCreatedAt().toString());

            // Add style based on read status
            content.getStyleClass().removeAll("unread-notification", "read-notification");
            content.getStyleClass().add(notification.isReadStatus() ?
                "read-notification" : "unread-notification");

            setGraphic(content);
        }
    }
}