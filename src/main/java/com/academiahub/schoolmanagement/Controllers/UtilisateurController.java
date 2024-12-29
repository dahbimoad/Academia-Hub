package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class UtilisateurController {

    @FXML private TableView<Utilisateur> utilisateurTable;
    @FXML private TableColumn<Utilisateur, Integer> colId;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colPassword;
    @FXML private TableColumn<Utilisateur, String> colRole;

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;

    @FXML private VBox professeurBox;
    @FXML private TextField nomField;
    @FXML private TextField prenomField;
    @FXML private TextField specialiteField;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();

    private final ObservableList<Utilisateur> utilisateurList = FXCollections.observableArrayList();

    /**
     * Méthode appelée automatiquement au chargement du FXML.
     */
    @FXML
    private void initialize() {
        // Liaison des colonnes du TableView aux attributs de Utilisateur
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colPassword.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));

        // Charger les données initiales
        actualiserTable();

        // Ajouter les éléments au ComboBox
        roleComboBox.setItems(FXCollections.observableArrayList("ADMIN", "PROFESSEUR", "SECRETAIRE"));

        // Gérer la visibilité des champs supplémentaires selon le rôle
        roleComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal) -> {
            if ("PROFESSEUR".equalsIgnoreCase(newVal)) {
                professeurBox.setVisible(true);
                professeurBox.setManaged(true);
            } else {
                professeurBox.setVisible(false);
                professeurBox.setManaged(false);
                nomField.clear();
                prenomField.clear();
                specialiteField.clear();
            }
        });

        // Optionnel : Remplir les champs lorsque la sélection dans la table change
        utilisateurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                passwordField.setText(newSelection.getPassword());
                roleComboBox.setValue(newSelection.getRole());

                // Si le rôle est PROFESSEUR, remplir les champs nom, prenom, specialite
                if ("PROFESSEUR".equalsIgnoreCase(newSelection.getRole())) {
                    Professeur prof = professeurDAO.readByUserId(newSelection.getId());
                    if (prof != null) {
                        nomField.setText(prof.getNom());
                        prenomField.setText(prof.getPrenom());
                        specialiteField.setText(prof.getSpecialite());
                        professeurBox.setVisible(true);
                        professeurBox.setManaged(true);
                    }
                } else {
                    nomField.clear();
                    prenomField.clear();
                    specialiteField.clear();
                }
            }
        });

        System.out.println("Initialisation du contrôleur UtilisateurController terminée.");
    }

    /**
     * Méthode pour ajouter un utilisateur (et un professeur si nécessaire).
     */
    @FXML
    private void ajouterUtilisateur() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null || role.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        // Créer l'utilisateur
        Utilisateur user = new Utilisateur(0, username, password, role);
        utilisateurDAO.create(user); // Cette méthode doit assigner l'ID généré à 'user'

        // Si le rôle est PROFESSEUR, ajouter également dans la table professeurs
        if ("PROFESSEUR".equalsIgnoreCase(role)) {
            String nom         = nomField.getText().trim();
            String prenom      = prenomField.getText().trim();
            String specialite  = specialiteField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs pour le professeur !");
                // Optionnel : Supprimer l'utilisateur créé précédemment si les champs ne sont pas remplis
                utilisateurDAO.delete(user.getId());
                return;
            }

            Professeur prof = new Professeur(0, nom, prenom, specialite, user.getId());
            professeurDAO.create(prof);
        }

        // Nettoyage des champs
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
        professeurBox.setVisible(false);
        professeurBox.setManaged(false);

        // Rafraîchir la table
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur créé avec succès !");
    }

    /**
     * Méthode pour modifier un utilisateur (et un professeur si nécessaire).
     */
    @FXML
    private void modifierUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à modifier !");
            return;
        }

        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();
        String role     = roleComboBox.getValue();

        if (username.isEmpty() || password.isEmpty() || role == null || role.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs !");
            return;
        }

        // Mettre à jour l'utilisateur
        selected.setUsername(username);
        selected.setPassword(password);
        selected.setRole(role);
        utilisateurDAO.update(selected);

        // Gérer le professeur associé
        if ("PROFESSEUR".equalsIgnoreCase(role)) {
            String nom         = nomField.getText().trim();
            String prenom      = prenomField.getText().trim();
            String specialite  = specialiteField.getText().trim();

            if (nom.isEmpty() || prenom.isEmpty() || specialite.isEmpty()) {
                showAlert(Alert.AlertType.WARNING, "Veuillez remplir tous les champs pour le professeur !");
                return;
            }

            Professeur prof = professeurDAO.readByUserId(selected.getId());
            if (prof != null) {
                // Mettre à jour le professeur existant
                prof.setNom(nom);
                prof.setPrenom(prenom);
                prof.setSpecialite(specialite);
                professeurDAO.update(prof);
            } else {
                // Créer un nouveau professeur si aucun n'existe
                prof = new Professeur(0, nom, prenom, specialite, selected.getId());
                professeurDAO.create(prof);
            }
        } else {
            // Si le rôle n'est plus PROFESSEUR, supprimer l'entrée dans professeurs si elle existe
            Professeur prof = professeurDAO.readByUserId(selected.getId());
            if (prof != null) {
                professeurDAO.delete(prof.getId());
            }
        }

        // Nettoyage des champs
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
        professeurBox.setVisible(false);
        professeurBox.setManaged(false);

        // Rafraîchir la table
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur modifié avec succès !");
    }

    /**
     * Méthode pour supprimer un utilisateur (et un professeur si nécessaire).
     */
    @FXML
    private void supprimerUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à supprimer !");
            return;
        }

        // Si l'utilisateur est PROFESSEUR, supprimer également l'entrée correspondante dans professeurs
        if ("PROFESSEUR".equalsIgnoreCase(selected.getRole())) {
            Professeur prof = professeurDAO.readByUserId(selected.getId());
            if (prof != null) {
                professeurDAO.delete(prof.getId());
            }
        }

        // Supprimer l'utilisateur
        utilisateurDAO.delete(selected.getId());

        // Rafraîchir la table
        actualiserTable();
        showAlert(Alert.AlertType.INFORMATION, "Utilisateur supprimé avec succès !");
    }

    /**
     * Méthode pour actualiser la table des utilisateurs.
     */
    @FXML
    private void actualiserTable() {
        utilisateurList.clear();
        utilisateurList.addAll(utilisateurDAO.findAll());
        utilisateurTable.setItems(utilisateurList);
    }

    /**
     * Méthode utilitaire pour afficher des boîtes de dialogue.
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
