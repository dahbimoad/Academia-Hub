package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.LanguageManager;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;

public class EditProfileDialogController {
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private Label roleLabel;
    @FXML private ProgressBar strengthBar;
    @FXML private Label strengthLabel;

    private UtilisateurDAO utilisateurDAO;
    private Utilisateur currentUser;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        utilisateurDAO = new UtilisateurDAO();
        setupValidation();
        clearError();
    }

    private void setupValidation() {
        usernameField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                validateUsername(newVal);
            }
        });
    }

    private void validateUsername(String username) {
        clearError();
        if (username.length() < 3) {
            showError(LanguageManager.getInstance().getString("profile.error.length"));
        } else if (!username.matches("[a-zA-Z0-9_]+")) {
            showError(LanguageManager.getInstance().getString("profile.error.format"));
        }
    }

    public void loadUserData(Utilisateur user) {
        this.currentUser = user;
        usernameField.setText(user.getUsername());
        // Format the role label with the role from user
        roleLabel.setText(String.format("%s: %s",
            LanguageManager.getInstance().getString("profile.role"),
            user.getRole()));
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();

        if (validateInput()) {
            try {
                Utilisateur existingUser = utilisateurDAO.findByUsername(username);
                if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                    showError(LanguageManager.getInstance().getString("profile.error.exists"));
                    return;
                }

                currentUser.setUsername(username);
                utilisateurDAO.update(currentUser);
                showSuccessAndClose();
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private void showSuccessAndClose() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(LanguageManager.getInstance().getString("alert.success"));
        alert.setHeaderText(null);
        alert.setContentText(LanguageManager.getInstance().getString("profile.success"));
        alert.showAndWait();
        dialogStage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), errorLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void clearError() {
        errorLabel.setVisible(false);
    }

    private boolean validateInput() {
        String username = usernameField.getText().trim();

        if (username.isEmpty()) {
            showError(LanguageManager.getInstance().getString("profile.error.required"));
            return false;
        }

        if (username.length() < 3) {
            showError(LanguageManager.getInstance().getString("profile.error.length"));
            return false;
        }

        if (!username.matches("[a-zA-Z0-9_]+")) {
            showError(LanguageManager.getInstance().getString("profile.error.format"));
            return false;
        }

        return true;
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}