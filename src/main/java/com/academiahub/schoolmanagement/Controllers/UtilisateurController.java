package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.PasswordGenerator;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
    @FXML private TextField searchField;

    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ObservableList<Utilisateur> masterData = FXCollections.observableArrayList();
    private FilteredList<Utilisateur> filteredData;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        setupTableColumns();

        // Configuration de la recherche en temps réel
        setupSearch();

        // Configuration du ComboBox des rôles
        setupRoleComboBox();

        // Configuration de la sélection dans la table
        setupTableSelection();

        // Chargement initial des données
        loadData();

        // Désactiver le champ de mot de passe et définir le texte de l'invite
        passwordField.setDisable(true);
        passwordField.setPromptText("Le mot de passe sera généré automatiquement");
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colPassword.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
    }

    private void setupSearch() {
        // Création d'une FilteredList wrappant la masterData
        filteredData = new FilteredList<>(masterData, p -> true);

        // Binding du filtre avec le texte de recherche
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(utilisateur -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                return utilisateur.getUsername().toLowerCase().contains(lowerCaseFilter) ||
                        utilisateur.getRole().toLowerCase().contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Utilisateur> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(utilisateurTable.comparatorProperty());

        // Bind the sortedData to the TableView
        utilisateurTable.setItems(sortedData);
    }

    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList("PROFESSEUR", "SECRETAIRE"));
    }

    private void setupTableSelection() {
        utilisateurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                roleComboBox.setValue(newSelection.getRole());
                passwordField.clear(); // Vider le champ mot de passe
            }
        });
    }

    private void loadData() {
        masterData.clear();
        masterData.addAll(utilisateurDAO.findAll());
    }

    @FXML
    private void ajouterUtilisateur() {
        if (!validateFields()) return;

        // Générer un mot de passe sécurisé
        String generatedPassword = PasswordGenerator.generateStrongPassword();

        Utilisateur user = new Utilisateur(0,
                usernameField.getText().trim(),
                generatedPassword,
                roleComboBox.getValue());

        if (utilisateurDAO.create(user)) {
            // Créer une alerte avec le mot de passe généré
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Utilisateur Créé");
            alert.setHeaderText("L'utilisateur a été créé avec succès");

            // Créer une zone de texte copiable pour les informations
            TextArea textArea = new TextArea(String.format("""
                    Informations de connexion :
                    Username: %s
                    Mot de passe: %s

                    Veuillez noter ces informations, le mot de passe ne sera plus visible après.""",
                    user.getUsername(), generatedPassword));

            textArea.setEditable(false);
            textArea.setWrapText(true);
            textArea.setPrefRowCount(4);
            textArea.setPrefColumnCount(40);

            VBox dialogContent = new VBox(10);
            dialogContent.getChildren().addAll(
                    new Label("⚠️ Veuillez copier ces informations maintenant :"),
                    textArea
            );

            alert.getDialogPane().setContent(dialogContent);
            alert.showAndWait();

            loadData();
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la création de l'utilisateur.");
        }
    }

    private boolean validateFields() {
        String username = usernameField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir le nom d'utilisateur et sélectionner un rôle !");
            return false;
        }

        // Vérifier si le nom d'utilisateur existe déjà
        if (utilisateurDAO.findByUsername(username) != null) {
            showAlert(Alert.AlertType.WARNING, "Ce nom d'utilisateur existe déjà !");
            return false;
        }

        // Validation du format du nom d'utilisateur
        if (!username.matches("^[a-zA-Z0-9._-]{3,20}$")) {
            showAlert(Alert.AlertType.WARNING,
                    "Le nom d'utilisateur doit :\n" +
                            "- Contenir entre 3 et 20 caractères\n" +
                            "- Utiliser uniquement des lettres, chiffres, points, tirets ou underscores");
            return false;
        }

        return true;
    }

    @FXML
    private void modifierUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null || !validateFields()) return;

        // Confirmer la génération d'un nouveau mot de passe
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Générer un nouveau mot de passe ?");
        confirm.setContentText("Cette action va générer un nouveau mot de passe pour l'utilisateur. Continuer ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String newPassword = PasswordGenerator.generateStrongPassword();
            selected.setUsername(usernameField.getText().trim());
            selected.setPassword(newPassword);
            selected.setRole(roleComboBox.getValue());

            if (utilisateurDAO.update(selected)) {
                // Afficher le nouveau mot de passe
                Alert passwordAlert = new Alert(Alert.AlertType.INFORMATION);
                passwordAlert.setTitle("Nouveau mot de passe");
                passwordAlert.setHeaderText("Nouveau mot de passe généré");

                TextArea textArea = new TextArea(String.format("""
                        Nouvelles informations de connexion :
                        Username: %s
                        Nouveau mot de passe: %s""",
                        selected.getUsername(), newPassword));

                textArea.setEditable(false);
                textArea.setWrapText(true);

                passwordAlert.getDialogPane().setContent(textArea);
                passwordAlert.showAndWait();

                loadData();
                clearFields();
            } else {
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification de l'utilisateur.");
            }
        }
    }

    @FXML
    private void supprimerUtilisateur() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à supprimer !");
            return;
        }

        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Confirmation de suppression");
        confirmation.setHeaderText(null);
        confirmation.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        if (confirmation.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            if (utilisateurDAO.delete(selected.getId())) {
                loadData();
                clearFields();
                showAlert(Alert.AlertType.INFORMATION, "Utilisateur supprimé avec succès !");
            }
        }
    }

    @FXML
    private void actualiserTable() {
        loadData();
        clearFields();
    }

    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        searchField.clear();
    }

    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : "Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
