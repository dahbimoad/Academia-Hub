package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import java.sql.SQLException;

public class ChangePasswordDialogController {
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;

    private UtilisateurDAO utilisateurDAO;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        utilisateurDAO = new UtilisateurDAO();
        errorLabel.setText("");
        errorLabel.setVisible(false);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public boolean validateAndSave() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        // Validation
        if (currentPassword.isEmpty() || newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Veuillez remplir tous les champs");
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Les nouveaux mots de passe ne correspondent pas");
            return false;
        }

        try {
            Utilisateur currentUser = DashboardController.getCurrentUser();
            if (utilisateurDAO.updatePassword(currentUser, currentPassword, newPassword)) {
                dialogStage.close();
                return true;
            } else {
                showError("Mot de passe actuel incorrect");
                return false;
            }
        } catch (SQLException e) {
            showError("Erreur lors de la mise à jour: " + e.getMessage());
            return false;
        }
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
    }
    @FXML
private void handleSave() {
    String currentPassword = currentPasswordField.getText();
    String newPassword = newPasswordField.getText();
    String confirmPassword = confirmPasswordField.getText();

    if (validateInput()) {
        try {
            if (utilisateurDAO.updatePassword(DashboardController.getCurrentUser(), currentPassword, newPassword)) {
                dialogStage.close();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Mot de passe modifié avec succès");
            } else {
                showError("Ancien mot de passe incorrect");
            }
        } catch (SQLException e) {
            showError("Erreur lors de la mise à jour: " + e.getMessage());
        }
    }
}

@FXML
private void handleCancel() {
    dialogStage.close();
}

private boolean validateInput() {
    if (currentPasswordField.getText().isEmpty()) {
        showError("Veuillez saisir votre mot de passe actuel");
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
    if (!newPasswordField.getText().equals(confirmPasswordField.getText())) {
        showError("Les mots de passe ne correspondent pas");
        return false;
    }
    return true;
}

private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


}