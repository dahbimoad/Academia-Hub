package com.academiahub.schoolmanagement.Controllers.Sec;

import com.academiahub.schoolmanagement.Models.Etudiant;
import javafx.fxml.FXML;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.sql.Date;

public class EditStudentDialogController {
    @FXML private TextField matriculeField;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private DatePicker dateNaissancePicker;
    @FXML private TextField emailField;
    @FXML private TextField promotionField;

    private Stage dialogStage;
    private Etudiant etudiant;
    private boolean confirmed = false;

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setEtudiant(Etudiant etudiant) {
        this.etudiant = etudiant;

        // Populate the fields with the student's current details
        matriculeField.setText(etudiant.getMatricule());
        nomField.setText(etudiant.getNom());
        prenomField.setText(etudiant.getPrenom());
        dateNaissancePicker.setValue(etudiant.getDateNaissance().toLocalDate());
        emailField.setText(etudiant.getEmail());
        promotionField.setText(etudiant.getPromotion());
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    @FXML
    private void handleSave() {
        try {
            // Update the student object with new values
            etudiant.setMatricule(matriculeField.getText());
            etudiant.setNom(nomField.getText());
            etudiant.setPrenom(prenomField.getText());
            etudiant.setDateNaissance(Date.valueOf(dateNaissancePicker.getValue()));
            etudiant.setEmail(emailField.getText());
            etudiant.setPromotion(promotionField.getText());

            confirmed = true; // Mark as confirmed
            dialogStage.close(); // Close the dialog
        } catch (Exception e) {
            e.printStackTrace(); // Handle input errors (e.g., missing fields)
        }
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }
}
