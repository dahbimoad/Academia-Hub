package com.academiahub.schoolmanagement.Controllers;

import javafx.animation.FadeTransition;
import javafx.util.Duration;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
import javafx.animation.FadeTransition;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.Duration;


public class EditProfesseurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;

    private Professeur professeur;
    private ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private ProfesseurController mainController;

    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;
        populateFields();
    }

    public void setMainController(ProfesseurController controller) {
        this.mainController = controller;
    }

    private void populateFields() {
        if (professeur != null) {
            nomField.setText(professeur.getNom());
            prenomField.setText(professeur.getPrenom());
            specialiteField.setText(professeur.getSpecialite());
        }
    }

    @FXML
    private void saveModification() {
        if (validateFields()) {
            try {
                professeur.setNom(nomField.getText().trim());
                professeur.setPrenom(prenomField.getText().trim());
                professeur.setSpecialite(specialiteField.getText().trim());

                professeurDAO.update(professeur);

                // Animation de transition






                showAlert(Alert.AlertType.INFORMATION, "Professeur modifié avec succès !");
                closeWindow();
            } catch (Exception e) {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification !");
            }
        }
    }

    @FXML
    private void cancelModification() {
        closeWindow();
    }

    private boolean validateFields() {
        if (nomField.getText().trim().isEmpty() ||
                prenomField.getText().trim().isEmpty() ||
                specialiteField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}