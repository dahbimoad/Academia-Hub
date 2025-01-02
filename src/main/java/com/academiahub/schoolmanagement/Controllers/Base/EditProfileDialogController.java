package com.academiahub.schoolmanagement.Controllers.Base;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
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
            showError("Le nom d'utilisateur doit contenir au moins 3 caractères");
        } else if (!username.matches("[a-zA-Z0-9_]+")) {
            showError("Le nom d'utilisateur ne peut contenir que des lettres, des chiffres et des underscores");
        }
    }

    public void loadUserData(Utilisateur user) {
        this.currentUser = user;
        usernameField.setText(user.getUsername());
        roleLabel.setText("Role: " + user.getRole());
    }

    @FXML
    private void handleSave() {
        String username = usernameField.getText().trim();

        if (validateInput()) {
            try {
                Utilisateur existingUser = utilisateurDAO.findByUsername(username);
                if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                    showError("Ce nom d'utilisateur est déjà utilisé");
                    return;
                }

                currentUser.setUsername(username);
                utilisateurDAO.update(currentUser);
                showSuccessAndClose();
            } catch (Exception e) {
                showError("Erreur lors de la mise à jour: " + e.getMessage());
            }
        }
    }

    private void showSuccessAndClose() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText("Profil mis à jour avec succès");
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
            showError("Le nom d'utilisateur est obligatoire");
            return false;
        }

        if (username.length() < 3) {
            showError("Le nom d'utilisateur doit contenir au moins 3 caractères");
            return false;
        }

        if (!username.matches("[a-zA-Z0-9_]+")) {
            showError("Le nom d'utilisateur ne peut contenir que des lettres, des chiffres et des underscores");
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