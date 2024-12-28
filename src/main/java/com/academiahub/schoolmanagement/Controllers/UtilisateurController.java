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
    @FXML private TextField passwordField;
    @FXML private TextField roleField;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ObservableList<Utilisateur> utilisateurList = FXCollections.observableArrayList();

    /**
     * Méthode appelée automatiquement au chargement du FXML.
     */
    @FXML
    private void initialize() {
        // Lier les colonnes du TableView aux attributs de Utilisateur
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colPassword.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        // Charger les données initiales
        actualiserTable();
    }

    @FXML
    private void ajouterUtilisateur() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String role     = roleField.getText();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        Utilisateur user = new Utilisateur(0, username, password, role);
        utilisateurDAO.create(user);

        // Vider les champs
        usernameField.clear();
        passwordField.clear();
        roleField.clear();

        // Rafraîchir la table
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur créé avec succès !");
    }

    @FXML
    private void modifierUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à modifier !");
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        String role     = roleField.getText();

        if (username.isEmpty() || password.isEmpty() || role.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        // Mise à jour de l'objet
        selected.setUsername(username);
        selected.setPassword(password);
        selected.setRole(role);

        // Appel DAO
        utilisateurDAO.update(selected);

        // Vider les champs
        usernameField.clear();
        passwordField.clear();
        roleField.clear();

        // Rafraîchir la table
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
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur supprimé avec succès !");
    }

    @FXML
    private void actualiserTable() {
        utilisateurList.clear();
        utilisateurList.addAll(utilisateurDAO.findAll());
        utilisateurTable.setItems(utilisateurList);
    }

    /**
     * Méthode utilitaire pour afficher des boîtes de dialogue
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
