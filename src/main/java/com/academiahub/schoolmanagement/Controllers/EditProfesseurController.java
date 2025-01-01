package com.academiahub.schoolmanagement.Controllers;



import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class EditProfesseurController {

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;
    @FXML private TextField userIdField;

    private Professeur professeur;
    private ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private ProfesseurController mainController;

    /**
     * Méthode pour passer le professeur sélectionné au contrôleur d'édition.
     */
    public void setProfesseur(Professeur professeur) {
        this.professeur = professeur;
        populateFields();
    }

    /**
     * Méthode pour référencer le contrôleur principal afin de pouvoir rafraîchir la liste après modification.
     */
    public void setMainController(ProfesseurController controller) {
        this.mainController = controller;
    }

    /**
     * Populate les champs avec les données du professeur.
     */
    private void populateFields() {
        if (professeur != null) {
            nomField.setText(professeur.getNom());
            prenomField.setText(professeur.getPrenom());
            specialiteField.setText(professeur.getSpecialite());
            userIdField.setText(String.valueOf(professeur.getUserId()));
        }
    }

    /**
     * Méthode appelée lors du clic sur "Enregistrer".
     */
    @FXML
    private void saveModification() {
        if (validateFields()) {
            try {
                int userId = Integer.parseInt(userIdField.getText().trim());
                professeur.setNom(nomField.getText().trim());
                professeur.setPrenom(prenomField.getText().trim());
                professeur.setSpecialite(specialiteField.getText().trim());
                professeur.setUserId(userId);

                professeurDAO.update(professeur);
                mainController.refreshList();
                showAlert(Alert.AlertType.INFORMATION, "Professeur modifié avec succès !");
                closeWindow();
            } catch (NumberFormatException e) {
                showAlert(Alert.AlertType.ERROR, "L'ID utilisateur doit être un nombre valide !");
            }
        }
    }

    /**
     * Méthode appelée lors du clic sur "Annuler".
     */
    @FXML
    private void cancelModification() {
        closeWindow();
    }

    /**
     * Valide les champs du formulaire.
     */
    private boolean validateFields() {
        if (nomField.getText().trim().isEmpty() ||
                prenomField.getText().trim().isEmpty() ||
                specialiteField.getText().trim().isEmpty() ||
                userIdField.getText().trim().isEmpty()) {

            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return false;
        }
        return true;
    }

    /**
     * Affiche une alerte avec le type et le message spécifiés.
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Ferme la fenêtre actuelle.
     */
    private void closeWindow() {
        Stage stage = (Stage) nomField.getScene().getWindow();
        stage.close();
    }
}
