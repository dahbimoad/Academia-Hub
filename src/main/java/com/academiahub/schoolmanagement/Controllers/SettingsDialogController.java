package com.academiahub.schoolmanagement.Controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.animation.FadeTransition;
import javafx.util.Duration;
import java.io.IOException;
import java.net.URL;

public class SettingsDialogController {
    @FXML private ComboBox<String> themeComboBox;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private CheckBox enableNotificationsCheck;
    @FXML private CheckBox notificationSoundCheck;
    @FXML private CheckBox emailNotificationsCheck;
    @FXML private Label settingsSavedLabel;

    private Stage dialogStage;

   @FXML
public void initialize() {
    setupComboBoxes();
    if (enableNotificationsCheck != null) {
        setupNotificationControls();
    }
    if (settingsSavedLabel != null) {
        settingsSavedLabel.setVisible(false);
    }
}

    private void setupComboBoxes() {
        themeComboBox.getItems().addAll("Clair", "Sombre", "Système");
        themeComboBox.setValue("Clair");
        languageComboBox.getItems().addAll("Français", "English", "العربية");
        languageComboBox.setValue("Français");

        // Add listeners for changes
        themeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) applyTheme(newVal);
        });
    }

    private void setupNotificationControls() {
        enableNotificationsCheck.setSelected(true);
        notificationSoundCheck.disableProperty().bind(enableNotificationsCheck.selectedProperty().not());
        emailNotificationsCheck.disableProperty().bind(enableNotificationsCheck.selectedProperty().not());
    }

    private void applyTheme(String theme) {
        String cssPath = switch (theme) {
            case "Sombre" -> "/styles/dark-theme.css";
            case "Système" -> "/styles/system-theme.css";
            default -> "/styles/light-theme.css";
        };
        dialogStage.getScene().getStylesheets().clear();
        dialogStage.getScene().getStylesheets().add(getClass().getResource(cssPath).toExternalForm());
    }

    @FXML
private void handleEditProfile() {
    try {
        FXMLLoader loader = new FXMLLoader();
        URL fxmlUrl = getClass().getResource("/com/academiahub/schoolmanagement/Fxml/EditProfileDialog.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Cannot find EditProfileDialog.fxml");
        }
        loader.setLocation(fxmlUrl);
        Parent root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Modifier le profil");
        Scene scene = new Scene(root);
        String cssUrl = getClass().getResource("/com/academiahub/schoolmanagement/Styles/settings.css").toExternalForm();
        scene.getStylesheets().add(cssUrl);
        dialogStage.setScene(scene);

        EditProfileDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        controller.loadUserData(DashboardController.getCurrentUser());

        dialogStage.showAndWait();
    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du dialogue: " + e.getMessage());
    }
}


    @FXML
private void handleChangePassword() {
    try {
        URL fxmlUrl = getClass().getResource("/com/academiahub/schoolmanagement/Fxml/ChangePasswordDialog.fxml");
        if (fxmlUrl == null) {
            throw new IOException("Cannot find ChangePasswordDialog.fxml");
        }

        FXMLLoader loader = new FXMLLoader(fxmlUrl);
        Parent root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Changer le mot de passe");
        Scene scene = new Scene(root);

        URL cssUrl = getClass().getResource("/com/academiahub/schoolmanagement/Styles/settings.css");
        if (cssUrl != null) {
            scene.getStylesheets().add(cssUrl.toExternalForm());
        }

        dialogStage.setScene(scene);
        ChangePasswordDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);
        dialogStage.showAndWait();

    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur",
            "Erreur lors de l'ouverture du dialogue: " + e.getMessage());
        e.printStackTrace();
    }
}

    @FXML
    private void handleSaveSettings() {
        // Save settings logic here
        showSavedAnimation();
    }

    private void showSavedAnimation() {
        settingsSavedLabel.setVisible(true);
        FadeTransition fadeOut = new FadeTransition(Duration.seconds(2), settingsSavedLabel);
        fadeOut.setFromValue(1.0);
        fadeOut.setToValue(0.0);
        fadeOut.setOnFinished(e -> settingsSavedLabel.setVisible(false));
        fadeOut.play();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void setDialogStage(Stage dialogStage) {
    this.dialogStage = dialogStage;
    String cssUrl = getClass().getResource("/com/academiahub/schoolmanagement/Styles/settings.css").toExternalForm();
    dialogStage.getScene().getStylesheets().add(cssUrl);
}
}