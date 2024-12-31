package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
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
    @FXML private TableColumn<Professeur, Integer> colUserId;

    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;
    @FXML private TextField userIdField;
    @FXML private TextField searchField;

    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Professeur> professeurList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colSpecialite.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialite()));
        colUserId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());

        // Chargement initial des données
        actualiserTable();

        // Listener pour la sélection
        professeurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateFields(newSel);
            }
        });
    }

    @FXML
    private void ajouterProfesseur() {
        if (!validateFields()) return;

        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            Professeur professeur = new Professeur(0,
                    nomField.getText().trim(),
                    prenomField.getText().trim(),
                    specialiteField.getText().trim(),
                    userId);

            professeurDAO.create(professeur);
            clearFields();
            actualiserTable();
            showAlert(Alert.AlertType.INFORMATION, "Professeur ajouté avec succès !");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "L'ID utilisateur doit être un nombre valide !");
        }
    }

    @FXML
    private void modifierProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null || !validateFields()) return;

        try {
            int userId = Integer.parseInt(userIdField.getText().trim());
            selected.setNom(nomField.getText().trim());
            selected.setPrenom(prenomField.getText().trim());
            selected.setSpecialite(specialiteField.getText().trim());
            selected.setUserId(userId);

            professeurDAO.update(selected);
            clearFields();
            actualiserTable();
            showAlert(Alert.AlertType.INFORMATION, "Professeur modifié avec succès !");
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "L'ID utilisateur doit être un nombre valide !");
        }
    }

    @FXML
    private void supprimerProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un professeur à supprimer !");
            return;
        }

        professeurDAO.delete(selected.getId());
        clearFields();
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Professeur supprimé avec succès !");
    }

    @FXML
    private void rechercher() {
        String keyword = searchField.getText().trim();
        if (keyword.isEmpty()) {
            actualiserTable();
            return;
        }

        professeurList.setAll(professeurDAO.findByNameOrSpeciality(keyword));
        if (professeurList.isEmpty()) {
            showAlert(Alert.AlertType.INFORMATION, "Aucun résultat trouvé pour : " + keyword);
        }
    }

    @FXML
    private void actualiserTable() {
        professeurList.clear();
        professeurList.addAll(professeurDAO.findAll());
        professeurTable.setItems(professeurList);
    }

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

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
        userIdField.clear();
        searchField.clear();
    }

    private void populateFields(Professeur professeur) {
        nomField.setText(professeur.getNom());
        prenomField.setText(professeur.getPrenom());
        specialiteField.setText(professeur.getSpecialite());
        userIdField.setText(String.valueOf(professeur.getUserId()));
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}