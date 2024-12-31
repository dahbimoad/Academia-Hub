package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.EtudiantDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

public class EtudiantController {
    @FXML private TableView<Etudiant> etudiantTable;
    @FXML private TableColumn<Etudiant, String> matriculeColumn;
    @FXML private TableColumn<Etudiant, String> nomColumn;
    @FXML private TableColumn<Etudiant, String> prenomColumn;
    @FXML private TableColumn<Etudiant, String> emailColumn;
    @FXML private TableColumn<Etudiant, String> promotionColumn;

    private EtudiantDAO etudiantDAO;
    private ObservableList<Etudiant> etudiantList;

    public void initialize() {
        matriculeColumn.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        promotionColumn.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        etudiantList = FXCollections.observableArrayList();
        etudiantTable.setItems(etudiantList);
    }

    @FXML
    public void setDAO() throws SQLException {
        etudiantDAO = new EtudiantDAO(DatabaseConnection.getConnection());
        loadEtudiants();
    }

    private void loadEtudiants() {
        try {
            List<Etudiant> etudiants = etudiantDAO.getAllEtudiants();
            etudiantList.setAll(etudiants);
        } catch (Exception e) {
            showError("Erreur lors du chargement des étudiants: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddStudent() {
        try {
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/AddStudentDialog.fxml"));

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Étudiant");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(etudiantTable.getScene().getWindow());

            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            AddStudentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

            // Retrieve the new student if confirmed
            Etudiant newEtudiant = controller.getNewEtudiant();
            if (newEtudiant != null) {
                etudiantDAO.addEtudiant(newEtudiant);
                etudiantList.add(newEtudiant);
            }
        } catch (IOException | SQLException e) {
            showError("Erreur lors de l'ouverture de la fenêtre d'ajout: " + e.getMessage());
            System.out.println(e);
        }
    }



    @FXML
    private void handleEditStudent() {
        Etudiant selectedEtudiant = etudiantTable.getSelectionModel().getSelectedItem();
        if (selectedEtudiant == null) {
            showError("Veuillez sélectionner un étudiant à modifier.");
            return;
        }

        try {
            // Load the FXML file for the edit dialog
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/EditStudentDialog.fxml"));
            Parent page = loader.load();

            // Create the dialog stage
            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Étudiant");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(etudiantTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            // Set the student details in the dialog controller
            EditStudentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEtudiant(selectedEtudiant);

            // Show the dialog and wait for user input
            dialogStage.showAndWait();

            // Refresh the table if the user confirmed the changes
            if (controller.isConfirmed()) {
                etudiantDAO.updateEtudiant(selectedEtudiant);
                etudiantTable.refresh();
                showInfo("Étudiant modifié avec succès.");
            }
        } catch (Exception e) {
            showError("Erreur lors de l'ouverture de la fenêtre de modification: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteStudent() {
        Etudiant selectedEtudiant = etudiantTable.getSelectionModel().getSelectedItem();
        if (selectedEtudiant == null) {
            showError("Veuillez sélectionner un étudiant à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Supprimer l'étudiant");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cet étudiant ?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                etudiantDAO.deleteEtudiant(selectedEtudiant.getId());
                etudiantList.remove(selectedEtudiant);
                showInfo("Étudiant supprimé avec succès.");
            } catch (Exception e) {
                showError("Erreur lors de la suppression de l'étudiant: " + e.getMessage());
            }
        }
    }


    @FXML
    private void handleRefresh() {
        loadEtudiants();
        showInfo("Liste des étudiants actualisée.");
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
