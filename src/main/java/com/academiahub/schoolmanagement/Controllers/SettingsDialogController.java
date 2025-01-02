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
import com.academiahub.schoolmanagement.utils.LanguageManager;

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

        languageComboBox.getItems().setAll("Français", "English", "العربية");
        languageComboBox.setValue("Français");

        // Add listeners for changes
        themeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) applyTheme(newVal);
        });

        languageComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                String langCode = switch (newVal) {
                    case "English" -> "en";
                    case "العربية" -> "ar";
                    default -> "fr";
                };
                applyLanguage(langCode);
            }
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

    private void applyLanguage(String langCode) {
    LanguageManager.getInstance().setLanguage(langCode);

    // Apply RTL for Arabic
    Scene scene = dialogStage.getScene();
    if (langCode.equals("ar")) {
        scene.getRoot().getStyleClass().add("rtl");
        scene.getRoot().getStyleClass().add("arabic-text");
    } else {
        scene.getRoot().getStyleClass().removeAll("rtl", "arabic-text");
    }

    refreshUI();
}

    private void refreshUI() {
        // Refresh the current scene
        Scene scene = dialogStage.getScene();
        reloadFXML(scene);
    }

    private void reloadFXML(Scene scene) {
        try {
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/academiahub/schoolmanagement/Fxml/SettingsDialog.fxml"),
                LanguageManager.getInstance().getBundle()
            );
            scene.setRoot(loader.load());

            // Reinitialize the controller
            SettingsDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

        } catch (IOException e) {
            showError("Error reloading UI: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
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

    @FXML
    private void handleEditProfile() {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL fxmlUrl = getClass().getResource("/com/academiahub/schoolmanagement/Fxml/EditProfileDialog.fxml");
            if (fxmlUrl == null) {
                throw new IOException("Cannot find EditProfileDialog.fxml");
            }
            loader.setLocation(fxmlUrl);
            loader.setResources(LanguageManager.getInstance().getBundle());  // Set the resource bundle
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle(LanguageManager.getInstance().getString("settings.profile.edit"));
            Scene scene = new Scene(root);
            String cssUrl = getClass().getResource("/com/academiahub/schoolmanagement/Styles/settings.css").toExternalForm();
            scene.getStylesheets().add(cssUrl);
            dialogStage.setScene(scene);

            EditProfileDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.loadUserData(DashboardController.getCurrentUser());

            dialogStage.showAndWait();
        } catch (IOException e) {
            showError("Error opening dialog: " + e.getMessage());
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
            loader.setResources(LanguageManager.getInstance().getBundle());  // Set the resource bundle
            Parent root = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.setTitle(LanguageManager.getInstance().getString("settings.password.change"));
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
            showError("Error opening dialog: " + e.getMessage());
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
        String cssUrl = getClass().getResource("/com/academiahub/schoolmanagement/Styles/settings.css").toExternalForm();
        dialogStage.getScene().getStylesheets().add(cssUrl);
    }
}