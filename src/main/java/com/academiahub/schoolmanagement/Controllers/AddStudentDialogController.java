package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.Models.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;

public class AddStudentDialogController {
    @FXML private TextField matriculeField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private TextField emailField;
    @FXML private TextField promotionField;

    private Stage dialogStage;
    private Etudiant newEtudiant;
    private boolean confirmed = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public Etudiant getNewEtudiant() {
        return confirmed ? newEtudiant : null;
    }

    @FXML
    private void handleAdd() {
        try {
            // Create a new Etudiant object from the form fields
            newEtudiant = new Etudiant(
                    matriculeField.getText(),
                    nomField.getText(),
                    prenomField.getText(),
                    Date.valueOf(dateNaissancePicker.getValue()),
                    emailField.getText(),
                    promotionField.getText()
            );

            confirmed = true; // Set confirmation flag
            dialogStage.close(); // Close the dialog

            // Show a success alert
            showAlert("Étudiant ajouté", "L'étudiant a été ajouté avec succès.", Alert.AlertType.INFORMATION);

        } catch (Exception e) {
            // Handle invalid input or errors
            showAlert("Erreur", "Erreur lors de l'ajout de l'étudiant: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }



    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
