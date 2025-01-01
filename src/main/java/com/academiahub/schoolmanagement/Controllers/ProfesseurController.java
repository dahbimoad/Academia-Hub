package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.utils.AlertUtils;
import com.academiahub.schoolmanagement.utils.ExcelExporter;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProfesseurController {
    private static final Logger LOGGER = Logger.getLogger(ProfesseurController.class.getName());

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

    private final ProfesseurDAO professeurDAO;
    private final UtilisateurDAO utilisateurDAO;
    private final ObservableList<Professeur> professeurList;
    private FilteredList<Professeur> filteredData;

    // Constructeur avec injection de dépendances
    public ProfesseurController() {
        this.professeurDAO = new ProfesseurDAO();
        this.utilisateurDAO = new UtilisateurDAO();
        this.professeurList = FXCollections.observableArrayList();
    }

    @FXML
    public void initialize() {
        initializeTableColumns();
        setupSearch();
        loadData();
        setupTableSelection();
    }

    private void initializeTableColumns() {
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colSpecialite.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialite()));
        colUserId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());
    }

    private void setupSearch() {
        filteredData = new FilteredList<>(professeurList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) ->
                filteredData.setPredicate(professeur -> {
                    if (newValue == null || newValue.isEmpty()) {
                        return true;
                    }
                    String lowerCaseFilter = newValue.toLowerCase();
                    return professeur.getNom().toLowerCase().contains(lowerCaseFilter) ||
                            professeur.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                            professeur.getSpecialite().toLowerCase().contains(lowerCaseFilter);
                })
        );

        SortedList<Professeur> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(professeurTable.comparatorProperty());
        professeurTable.setItems(sortedData);
    }

    private void loadData() {
        try {
            professeurList.clear();
            professeurList.addAll(professeurDAO.findAll());
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors du chargement des données", e);
            AlertUtils.showError("Erreur de chargement", "Impossible de charger les données des professeurs.");
        }
    }

    private void setupTableSelection() {
        professeurTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> {
                    if (newSelection != null) {
                        populateFields(newSelection);
                    }
                }
        );
    }

    @FXML
    private void modifierProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner un professeur à modifier.");
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/FXML/EditProfesseur.fxml"));
            Parent root = loader.load();

            EditProfesseurController editController = loader.getController();
            editController.setProfesseur(selected);
            editController.setMainController(this);

            Stage stage = new Stage();
            stage.setTitle("Modifier Professeur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            refreshList();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'ouverture du formulaire d'édition", e);
            AlertUtils.showError("Erreur", "Impossible d'ouvrir le formulaire d'édition.");
        }
    }

    @FXML
    private void supprimerProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            AlertUtils.showWarning("Sélection requise", "Veuillez sélectionner un professeur à supprimer.");
            return;
        }

        Optional<ButtonType> result = AlertUtils.showConfirmation(
                "Confirmation de suppression",
                "Voulez-vous vraiment supprimer ce professeur et son utilisateur associé ?"
        );

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                professeurDAO.delete(selected.getId());
                utilisateurDAO.delete(selected.getUserId());
                clearFields();
                refreshList();
                AlertUtils.showInformation("Succès", "Professeur et utilisateur supprimés avec succès !");
            } catch (Exception e) {
                LOGGER.log(Level.SEVERE, "Erreur lors de la suppression", e);
                AlertUtils.showError("Erreur de suppression", "Impossible de supprimer le professeur.");
            }
        }
    }

    @FXML
    private void exporterExcel() {
        ExcelExporter.exportProfesseurs(filteredData, professeurTable.getScene().getWindow());
    }

    public void refreshList() {
        loadData();
        professeurTable.refresh();
    }

    private void clearFields() {
        nomField.clear();
        prenomField.clear();
        specialiteField.clear();
        userIdField.clear();
    }

    private void populateFields(Professeur professeur) {
        if (professeur != null) {
            nomField.setText(professeur.getNom());
            prenomField.setText(professeur.getPrenom());
            specialiteField.setText(professeur.getSpecialite());
            userIdField.setText(String.valueOf(professeur.getUserId()));
        }
    }
}