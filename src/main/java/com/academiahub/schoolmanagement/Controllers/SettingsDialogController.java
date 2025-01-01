package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.Parent;
import java.io.IOException;

public class SettingsDialogController {
    @FXML private ComboBox<String> themeComboBox;
    @FXML private ComboBox<String> languageComboBox;
    @FXML private CheckBox enableNotificationsCheck;
    @FXML private CheckBox notificationSoundCheck;
    @FXML private CheckBox emailNotificationsCheck;


    @FXML
private void handleEditProfile() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/EditProfileDialog.fxml"));
        Parent root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Modifier le profil");
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        // Get the controller AFTER loading the FXML
        EditProfileDialogController controller = loader.getController();
        if (controller != null) {
            controller.setDialogStage(dialogStage);
            controller.loadUserData(DashboardController.getCurrentUser());
            dialogStage.showAndWait();
        } else {
            throw new RuntimeException("Controller not initialized");
        }
    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du dialogue: " + e.getMessage());
    }
}

    @FXML
private void handleChangePassword() {
    try {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/ChangePasswordDialog.fxml"));
        Parent root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Changer le mot de passe");
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        // Get the controller AFTER loading the FXML
        ChangePasswordDialogController controller = loader.getController();
        if (controller != null) {
            controller.setDialogStage(dialogStage);
            dialogStage.showAndWait();
        } else {
            throw new RuntimeException("Controller not initialized");
        }
    } catch (IOException e) {
        showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du dialogue: " + e.getMessage());
    }
}

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
     @FXML
    public void initialize() {
        // Initialize ComboBox values
        themeComboBox.getItems().addAll("Clair", "Sombre", "Système");
        languageComboBox.getItems().addAll("Français", "English", "العربية");
    }
}