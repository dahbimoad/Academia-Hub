package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.scene.layout.VBox;

public class UserProfileController {
    @FXML private TextField usernameField;
    @FXML private PasswordField oldPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;

    private UtilisateurDAO utilisateurDAO;
    private Utilisateur currentUser;

    public void initialize() {
        utilisateurDAO = new UtilisateurDAO();
    }

    public void setCurrentUser(Utilisateur user) {
        this.currentUser = user;
        loadUserData();
    }

    private void loadUserData() {
        if (currentUser != null) {
            usernameField.setText(currentUser.getUsername());
        }
    }

    @FXML
    private void handleSave() {
        if (validateInputs()) {
            try {
                if (!oldPasswordField.getText().equals(currentUser.getPassword())) {
                    showError("L'ancien mot de passe est incorrect");
                    return;
                }

                if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
                    showError("Les nouveaux mots de passe ne correspondent pas");
                    return;
                }

                currentUser.setPassword(newPasswordField.getText());
                utilisateurDAO.update(currentUser);

                showSuccess("Profil mis à jour avec succès");
                closeDialog();
            } catch (Exception e) {
                showError("Erreur lors de la mise à jour du profil: " + e.getMessage());
            }
        }
    }

    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @FXML
    private void handleCancel() {
        closeDialog();
    }

    private boolean validateInputs() {
        if (oldPasswordField.getText().isEmpty()) {
            showError("Veuillez saisir l'ancien mot de passe");
            return false;
        }
        if (newPasswordField.getText().isEmpty()) {
            showError("Veuillez saisir le nouveau mot de passe");
            return false;
        }
        if (confirmPasswordField.getText().isEmpty()) {
            showError("Veuillez confirmer le nouveau mot de passe");
            return false;
        }
        return true;
    }

    private void closeDialog() {
        ((Stage) usernameField.getScene().getWindow()).close();
    }
}