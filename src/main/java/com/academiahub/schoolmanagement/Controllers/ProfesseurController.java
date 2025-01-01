package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Professeur;
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
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

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
    private final UtilisateurDAO userDAO = new UtilisateurDAO();
    private final ObservableList<Professeur> professeurList = FXCollections.observableArrayList();
    private FilteredList<Professeur> filteredData;

    @FXML
    public void initialize() {
        // Configuration des colonnes
        colId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getId()).asObject());
        colNom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        colPrenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        colSpecialite.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSpecialite()));
        colUserId.setCellValueFactory(cellData -> new SimpleIntegerProperty(cellData.getValue().getUserId()).asObject());

        // Chargement initial des données
        professeurList.addAll(professeurDAO.findAll());

        // Configuration de la recherche en temps réel
        filteredData = new FilteredList<>(professeurList, p -> true);
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(professeur -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();
                return professeur.getNom().toLowerCase().contains(lowerCaseFilter) ||
                        professeur.getPrenom().toLowerCase().contains(lowerCaseFilter) ||
                        professeur.getSpecialite().toLowerCase().contains(lowerCaseFilter);
            });
        });

        SortedList<Professeur> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(professeurTable.comparatorProperty());
        professeurTable.setItems(sortedData);

        // Listener pour la sélection
        professeurTable.getSelectionModel().selectedItemProperty().addListener((obs, oldSel, newSel) -> {
            if (newSel != null) {
                populateFields(newSel);
            }
        });
    }

    /**
     * Ouvre le formulaire d'édition dans un nouveau dialog.
     */
    @FXML
    private void modifierProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un professeur à modifier !");
            return;
        }

        try {
            // Charger le fichier FXML du formulaire d'édition
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/FXML/EditProfesseur.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur du formulaire d'édition
            EditProfesseurController editController = loader.getController();
            editController.setProfesseur(selected);
            editController.setMainController(this);

            // Créer une nouvelle scène et stage pour le dialog
            Stage stage = new Stage();
            stage.setTitle("Modifier Professeur");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(root));
            stage.showAndWait();

            // Après fermeture du dialog, rafraîchir la liste
            refreshList();

        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'ouverture du formulaire d'édition : " + e.getMessage());
        }
    }

    @FXML
    private void supprimerProfesseur() {
        Professeur selected = professeurTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "Veuillez sélectionner un professeur à supprimer !");
            return;
        }

        Alert confirmDialog = new Alert(Alert.AlertType.CONFIRMATION);
        confirmDialog.setTitle("Confirmation de suppression");
        confirmDialog.setHeaderText(null);
        confirmDialog.setContentText("Voulez-vous vraiment supprimer ce professeur et son utilisateur associé ?");

        Optional<ButtonType> result = confirmDialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Suppression du professeur
            professeurDAO.delete(selected.getId());
            // Suppression de l'utilisateur associé
            userDAO.delete(selected.getUserId());

            clearFields();
            refreshList();
            showAlert(Alert.AlertType.INFORMATION, "Professeur et utilisateur supprimés avec succès !");
        }
    }

    @FXML
    private void exporterExcel() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Exporter vers Excel");
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        fileChooser.setInitialFileName("professeurs_" + timestamp + ".xlsx");
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("Fichiers Excel", "*.xlsx")
        );

        File file = fileChooser.showSaveDialog(professeurTable.getScene().getWindow());
        if (file != null) {
            exportToExcel(file);
        }
    }

    private void exportToExcel(File file) {
        try (Workbook workbook = new XSSFWorkbook()) {
            Sheet sheet = workbook.createSheet("Professeurs");

            // Style pour l'en-tête
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            // Création de l'en-tête
            Row headerRow = sheet.createRow(0);
            headerRow.createCell(0).setCellValue("ID");
            headerRow.createCell(1).setCellValue("Nom");
            headerRow.createCell(2).setCellValue("Prénom");
            headerRow.createCell(3).setCellValue("Spécialité");
            headerRow.createCell(4).setCellValue("User ID");

            for (int i = 0; i < 5; i++) {
                headerRow.getCell(i).setCellStyle(headerStyle);
            }

            // Remplissage des données
            int rowNum = 1;
            for (Professeur prof : filteredData) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(prof.getId());
                row.createCell(1).setCellValue(prof.getNom());
                row.createCell(2).setCellValue(prof.getPrenom());
                row.createCell(3).setCellValue(prof.getSpecialite());
                row.createCell(4).setCellValue(prof.getUserId());
            }

            // Ajustement automatique des colonnes
            for (int i = 0; i < 5; i++) {
                sheet.autoSizeColumn(i);
            }

            // Sauvegarde du fichier
            try (FileOutputStream fileOut = new FileOutputStream(file)) {
                workbook.write(fileOut);
                showAlert(Alert.AlertType.INFORMATION, "Export Excel réussi !\nFichier : " + file.getName());
            }

        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur lors de l'export Excel : " + e.getMessage());
        }
    }

    /**
     * Rafraîchit la liste des professeurs.
     */
    public void refreshList() {
        professeurList.clear();
        professeurList.addAll(professeurDAO.findAll());
        professeurTable.refresh(); // Rafraîchit le TableView pour afficher les mises à jour
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
    }

    private void populateFields(Professeur professeur) {
        nomField.setText(professeur.getNom());
        prenomField.setText(professeur.getPrenom());
        specialiteField.setText(professeur.getSpecialite());
        userIdField.setText(String.valueOf(professeur.getUserId()));
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
}
