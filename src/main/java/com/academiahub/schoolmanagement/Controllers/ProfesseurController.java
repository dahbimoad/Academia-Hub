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

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;

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

        // Chargement initial des données
        actualiserTable();
    }

    @FXML
    private void ajouterProfesseur() {
        String nom = nomField.getText();
        String prenom = prenomField.getText();
        String specialite = specialiteField.getText();

        if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        Professeur professeur = new Professeur(0, nom, prenom, specialite);
        professeurDAO.create(professeur);

        // Nettoyer les champs et rafraîchir la table
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
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

        if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        // Mettre à jour l'objet sélectionné
        selected.setNom(nom);
        selected.setPrenom(prenom);
        selected.setSpecialite(specialite);

        // Appel DAO pour la mise à jour
        professeurDAO.update(selected);
        actualiserTable();

        // Nettoyer les champs
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();

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
