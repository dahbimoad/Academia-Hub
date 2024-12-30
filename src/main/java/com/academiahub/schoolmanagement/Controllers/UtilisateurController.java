package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

public class UtilisateurController {

    @FXML private TableView<Utilisateur> utilisateurTable;
    @FXML private TableColumn<Utilisateur, Integer> colId;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colPassword;
    @FXML private TableColumn<Utilisateur, String> colRole;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField searchField;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ObservableList<Utilisateur> utilisateurList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Liaison des colonnes du TableView aux attributs de Utilisateur
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colPassword.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        // Charger les données initiales
        actualiserTable();

        // Ajouter les éléments au ComboBox
        roleComboBox.setItems(FXCollections.observableArrayList("PROFESSEUR", "SECRETAIRE"));

        // Remplir les champs lorsque la sélection dans la table change
        utilisateurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                passwordField.setText(newSelection.getPassword());
                roleComboBox.setValue(newSelection.getRole());
            }
        });
    }

    @FXML
    private void rechercher() {
        String username = searchField.getText().trim();
        if (username.isEmpty()) {
            actualiserTable();
            return;
        }

        Utilisateur user = utilisateurDAO.findByUsername(username);
        utilisateurList.clear();
        if (user != null) {
            utilisateurList.add(user);
        } else {
            showAlert(Alert.AlertType.INFORMATION, "Aucun utilisateur trouvé avec le nom : " + username);
        }
        utilisateurTable.setItems(utilisateurList);
    }

    @FXML
    private void ajouterUtilisateur() {
        if (!validateFields()) return;

        Utilisateur user = new Utilisateur(0,
                usernameField.getText().trim(),
                passwordField.getText().trim(),
                roleComboBox.getValue());
        utilisateurDAO.create(user);

        clearFields();
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur créé avec succès !");
    }

    @FXML
    private void modifierUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null || !validateFields()) return;

        selected.setUsername(usernameField.getText().trim());
        selected.setPassword(passwordField.getText().trim());
        selected.setRole(roleComboBox.getValue());
        utilisateurDAO.update(selected);

        clearFields();
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur modifié avec succès !");
    }

    @FXML
    private void supprimerUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à supprimer !");
            return;
        }

        utilisateurDAO.delete(selected.getId());
        clearFields();
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur supprimé avec succès !");
    }

    @FXML
    private void actualiserTable() {
        utilisateurList.clear();
        utilisateurList.addAll(utilisateurDAO.findAll());
        utilisateurTable.setItems(utilisateurList);
        searchField.clear();
    }

    private boolean validateFields() {
        if (usernameField.getText().trim().isEmpty() ||
                passwordField.getText().trim().isEmpty() ||
                roleComboBox.getValue() == null) {

            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return false;
        }
        return true;
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        searchField.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}