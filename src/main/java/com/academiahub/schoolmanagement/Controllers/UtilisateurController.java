package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
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

    // TableView et ses colonnes
    @FXML private TableView<Utilisateur> utilisateurTable;
    @FXML private TableColumn<Utilisateur, Integer> colId;
    @FXML private TableColumn<Utilisateur, String> colUsername;
    @FXML private TableColumn<Utilisateur, String> colPassword;
    @FXML private TableColumn<Utilisateur, String> colRole;

    // Conteneur du formulaire et champs de saisie
    @FXML private VBox formContainer;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<String> roleComboBox;
    @FXML private TextField searchField;

    // Boutons d'action
    @FXML private Button btnAjouter;
    @FXML private Button btnModifier;
    @FXML private Button btnSupprimer;
    @FXML private Button btnValider;
    @FXML private Button btnAnnuler;

    // DAO et listes de données
    private final UtilisateurDAO utilisateurDAO = new UtilisateurDAO();
    private final ProfesseurDAO professeurDAO = new ProfesseurDAO();
    private final ObservableList<Utilisateur> masterData = FXCollections.observableArrayList();
    private final ObservableList<Utilisateur> utilisateurList = FXCollections.observableArrayList();
    private FilteredList<Utilisateur> filteredData;
    private SortedList<Utilisateur> sortedData;

    // Mode actuel de l'interface
    private Mode currentMode = Mode.NORMAL;

    // Enumération pour les différents modes
    private enum Mode {
        NORMAL, AJOUT, MODIFICATION
    }

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearch();
        setupRoleComboBox();
        setupTableSelection();
        loadData();
        initializeFormVisibility();
    }

    /**
     * Initialise la visibilité du formulaire et des champs de saisie.
     */
    private void initializeFormVisibility() {
        formContainer.setVisible(false);
        formContainer.setManaged(false);
        passwordField.setDisable(true);
        passwordField.setPromptText("Le mot de passe sera généré automatiquement");
        btnValider.setVisible(false);
        btnAnnuler.setVisible(false);
    }

    /**
     * Configure les colonnes de la TableView.
     */
    private void setupTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colUsername.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUsername()));
        colPassword.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPassword()));
        colRole.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRole()));
    }

    /**
     * Configure la fonctionnalité de recherche.
     */
    private void setupSearch() {
        filteredData = new FilteredList<>(masterData, p -> true);
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

        sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(utilisateurTable.comparatorProperty());
        utilisateurTable.setItems(sortedData);
    }

    /**
     * Configure les options disponibles dans le ComboBox des rôles.
     */
    private void setupRoleComboBox() {
        roleComboBox.setItems(FXCollections.observableArrayList("PROFESSEUR", "SECRETAIRE"));
    }

    /**
     * Configure la sélection d'un utilisateur dans la TableView.
     */
    private void setupTableSelection() {
        utilisateurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (currentMode == Mode.NORMAL && newSelection != null) {
                usernameField.setText(newSelection.getUsername());
                roleComboBox.setValue(newSelection.getRole());
                passwordField.clear();
            }
        });
    }

    /**
     * Charge les données des utilisateurs depuis la base de données.
     */
    private void loadData() {
        Utilisateur selectedUser = utilisateurTable.getSelectionModel().getSelectedItem();
        masterData.clear();
        masterData.addAll(utilisateurDAO.findAll());
        if (selectedUser != null) {
            for (Utilisateur user : masterData) {
                if (user.getId() == selectedUser.getId()) {
                    utilisateurTable.getSelectionModel().select(user);
                    break;
                }
            }
        }
    }

    /**
     * Gère l'action du bouton "Ajouter".
     */
    @FXML
    private void handleAjouterButton() {
        currentMode = Mode.AJOUT;
        showForm(true);
        clearFields();
    }

    /**
     * Gère l'action du bouton "Modifier".
     */
    @FXML
    private void handleModifierButton() {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un utilisateur à modifier!");
            return;
        }
        currentMode = Mode.MODIFICATION;
        showForm(true);
        populateFields(selected);
    }

    /**
     * Gère l'action du bouton "Valider".
     */
    @FXML
    private void handleValiderButton() {
        if (currentMode == Mode.AJOUT) {
            ajouterUtilisateur();
        } else if (currentMode == Mode.MODIFICATION) {
            modifierUtilisateur();
        }
        showForm(false);
    }

    /**
     * Gère l'action du bouton "Annuler".
     */
    @FXML
    private void handleAnnulerButton() {
        showForm(false);
        clearFields();
        currentMode = Mode.NORMAL;
    }

    /**
     * Affiche ou cache le formulaire en fonction du paramètre.
     * @param show Vrai pour afficher le formulaire, faux pour le cacher.
     */
    private void showForm(boolean show) {
        formContainer.setVisible(show);
        formContainer.setManaged(show);
        btnAjouter.setDisable(show);
        btnModifier.setDisable(show);
        btnSupprimer.setDisable(show);
        utilisateurTable.setDisable(show);
        btnValider.setVisible(show);
        btnAnnuler.setVisible(show);
    }

    /**
     * Ajoute un nouvel utilisateur.
     */
    private void ajouterUtilisateur() {
        if (!validateFields()) return;

        String generatedPassword = PasswordGenerator.generateStrongPassword();
        Utilisateur user = new Utilisateur(0,
                usernameField.getText().trim(),
                generatedPassword,
                roleComboBox.getValue());

        if (utilisateurDAO.create(user)) {
            showPasswordAlert("Utilisateur Créé", user.getUsername(), generatedPassword);
            masterData.add(user);
            clearFields();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de la création de l'utilisateur.");
        }
    }

    /**
     * Modifie un utilisateur existant.
     */
    @FXML
    public void modifierUtilisateur() {

        try {
        Utilisateur selected = utilisateurTable.getSelectionModel().getSelectedItem();
        if (selected == null || !validateFields()) return;

        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Confirmation");
        confirm.setHeaderText("Générer un nouveau mot de passe ?");
        confirm.setContentText("Cette action va générer un nouveau mot de passe pour l'utilisateur. Continuer ?");

        if (confirm.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK) {
            String newPassword = PasswordGenerator.generateStrongPassword();
            selected.setUsername(usernameField.getText().trim());
            selected.setPassword(newPassword);
            selected.setRole(roleComboBox.getValue());

            System.out.println("Tentative de mise à jour de l'utilisateur : " + selected);

            if (utilisateurDAO.update(selected)) {
                System.out.println("Utilisateur mis à jour avec succès.");
                showPasswordAlert("Modification Utilisateur", selected.getUsername(), newPassword);
                loadData();
                clearFields();
            } else {
                System.out.println("Erreur lors de la mise à jour de l'utilisateur.");
                showAlert(Alert.AlertType.ERROR, "Erreur lors de la modification de l'utilisateur.");
            }
        }
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Une erreur inattendue s'est produite : " + e.getMessage());
        }
    }

    /**
     * Supprime un utilisateur sélectionné.
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
     * Actualise la TableView avec les données actuelles.
     */
    @FXML
    private void actualiserTable() {
        loadData();
    }

    /**
     * Affiche une alerte contenant le mot de passe généré.
     * @param title Titre de l'alerte.
     * @param username Nom d'utilisateur.
     * @param password Mot de passe généré.
     */
    private void showPasswordAlert(String title, String username, String password) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText("L'opération a été effectuée avec succès");

        TextArea textArea = new TextArea(String.format("""
                Informations de connexion :
                Username: %s
                Mot de passe: %s

                Veuillez noter ces informations, le mot de passe ne sera plus visible après.""",
                username, password));

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
    }

    /**
     * Valide les champs de saisie.
     * @return Vrai si les champs sont valides, faux sinon.
     */
    private boolean validateFields() {
        String username = usernameField.getText().trim();
        String role = roleComboBox.getValue();

        if (username.isEmpty() || role == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez remplir le nom d'utilisateur et sélectionner un rôle !");
            return false;
        }

        Utilisateur existingUser = utilisateurDAO.findByUsername(username);
        Utilisateur selectedUser = utilisateurTable.getSelectionModel().getSelectedItem();

        if (existingUser != null && (currentMode == Mode.AJOUT || (selectedUser != null && existingUser.getId() != selectedUser.getId()))) {
            showAlert(Alert.AlertType.WARNING, "Ce nom d'utilisateur existe déjà !");
            return false;
        }

        if (!username.matches("^[a-zA-Z0-9._-]{3,20}$")) {
            showAlert(Alert.AlertType.WARNING,
                    "Le nom d'utilisateur doit :\n" +
                            "- Contenir entre 3 et 20 caractères\n" +
                            "- Utiliser uniquement des lettres, chiffres, points, tirets ou underscores");
            return false;
        }

        return true;
    }

    /**
     * Efface les champs de saisie.
     */
    private void clearFields() {
        usernameField.clear();
        passwordField.clear();
        roleComboBox.getSelectionModel().clearSelection();
        utilisateurTable.getSelectionModel().clearSelection();
    }

    /**
     * Affiche une alerte générique.
     * @param type Type de l'alerte.
     * @param message Message à afficher.
     */
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(type == Alert.AlertType.ERROR ? "Erreur" : (type == Alert.AlertType.WARNING ? "Attention" : "Information"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Remplit les champs de saisie avec les données de l'utilisateur sélectionné.
     * @param user Utilisateur sélectionné.
     */
    private void populateFields(Utilisateur user) {
        usernameField.setText(user.getUsername());
        roleComboBox.setValue(user.getRole());
        passwordField.clear();
    }
}
