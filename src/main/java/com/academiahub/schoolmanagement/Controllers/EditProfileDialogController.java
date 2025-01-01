package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.SQLException;

public class EditProfileDialogController {
    @FXML private TextField usernameField;
    @FXML private Label errorLabel;
    @FXML private Label roleLabel;

    private UtilisateurDAO utilisateurDAO;
    private Utilisateur currentUser;
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

    public void loadUserData(Utilisateur user) {
        this.currentUser = user;
        usernameField.setText(user.getUsername());
        roleLabel.setText("Role: " + user.getRole());
    }

    public boolean validateAndSave() {
        String username = usernameField.getText().trim();

        // Validate inputs
        if (username.isEmpty()) {
            showError("Le nom d'utilisateur est obligatoire");
            return false;
        }

        try {
            // Check if username is already taken by another user
            Utilisateur existingUser = utilisateurDAO.findByUsername(username);
            if (existingUser != null && existingUser.getId() != currentUser.getId()) {
                showError("Ce nom d'utilisateur est déjà utilisé");
                return false;
            }

            // Update user information
            currentUser.setUsername(username);
            utilisateurDAO.update(currentUser);

            if (dialogStage != null) {
                dialogStage.close();
            }
            return true;
        } catch (Exception e) {
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
   String username = usernameField.getText().trim();

   if (validateInput()) {
       currentUser.setUsername(username);
       utilisateurDAO.update(currentUser);
       dialogStage.close();
       showAlert(Alert.AlertType.INFORMATION, "Succès", "Profil mis à jour avec succès");
   }
}

@FXML
private void handleCancel() {
    dialogStage.close();
}

private boolean validateInput() {
    if (usernameField.getText().trim().isEmpty()) {
        showError("Le nom d'utilisateur est obligatoire");
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