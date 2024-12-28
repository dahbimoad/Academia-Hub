package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class ProfesseurController {

    @FXML private TableView<Professeur> professeurTable;
    @FXML private TableColumn<Professeur, Integer> colId;
    @FXML private TableColumn<Professeur, String> colNom;
    @FXML private TableColumn<Professeur, String> colPrenom;
    @FXML private TableColumn<Professeur, String> colSpecialite;
    @FXML private TableColumn<Professeur, Integer> colUserId; // Nouvelle colonne pour userId

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;
    @FXML private TextField userIdField; // Nouveau champ pour saisir userId

    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Professeur> professeurList = FXCollections.observableArrayList();

    /**
     * Méthode appelée automatiquement après le chargement du FXML.
     */
    @FXML
    private void initialize() {
        // Associer les colonnes du TableView aux attributs de la classe Professeur
        colId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getPrenom()));
        colSpecialite.setCellValueFactory(cellData -> new javafx.beans.property.SimpleStringProperty(cellData.getValue().getSpecialite()));
        colUserId.setCellValueFactory(cellData -> new javafx.beans.property.SimpleIntegerProperty(cellData.getValue().getUserId()).asObject()); // Liaison pour userId

        // Chargement initial des données
        actualiserTable();
    }

    @FXML
    private void ajouterProfesseur() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String specialite = specialiteField.getText();
        String userIdStr = userIdField.getText(); // Récupération de userId

        if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty() || userIdStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr); // Conversion de userId en entier
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "L'ID utilisateur doit être un nombre !");
            return;
        }

        Professeur professeur = new Professeur(0, nom, prenom, specialite, userId); // Inclure userId
        professeurDAO.create(professeur);

        // Nettoyer les champs et rafraîchir la table
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
        userIdField.clear();
        actualiserTable();

        showAlert(Alert.AlertType.INFORMATION, "Professeur ajouté avec succès !");
    }

    @FXML
    private void modifierProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un professeur à modifier !");
            return;
        }

        // Vérifier que les champs ne sont pas vides
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String specialite = specialiteField.getText();
        String userIdStr = userIdField.getText(); // Récupération de userId

        if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty() || userIdStr.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        int userId;
        try {
            userId = Integer.parseInt(userIdStr); // Conversion de userId en entier
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "L'ID utilisateur doit être un nombre !");
            return;
        }

        // Mettre à jour l'objet sélectionné
        selected.setNom(nom);
        selected.setPrenom(prenom);
        selected.setSpecialite(specialite);
        selected.setUserId(userId); // Mise à jour de userId

        // Appel DAO pour la mise à jour
        professeurDAO.update(selected);
        actualiserTable();

        // Nettoyer les champs
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
        userIdField.clear();

        showAlert(Alert.AlertType.INFORMATION, "Professeur modifié avec succès !");
    }

    @FXML
    private void supprimerProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un professeur à supprimer !");
            return;
        }

        professeurDAO.delete(selected.getId());
        actualiserTable();

        showAlert(Alert.AlertType.INFORMATION, "Professeur supprimé avec succès !");
    }

    @FXML
    private void actualiserTable() {
        professeurList.clear();
        professeurList.addAll(professeurDAO.findAll());
        professeurTable.setItems(professeurList);
    }

    /**
     * Méthode utilitaire pour afficher des alertes
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
