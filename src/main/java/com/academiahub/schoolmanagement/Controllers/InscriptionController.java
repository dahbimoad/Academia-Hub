package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.InscriptionDAO;
import com.academiahub.schoolmanagement.Models.Inscription;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.util.List;

public class InscriptionController {

    @FXML
    private TableView<Inscription> inscriptionTable;


    @FXML
    private TableColumn<Inscription, Integer> idEtudiant;

    @FXML
    private TableColumn<Inscription, Integer> idModule;

    @FXML
    private TableColumn<Inscription, String> colNom;

    @FXML
    private TableColumn<Inscription, String> colPrenom;

    @FXML
    private TableColumn<Inscription, String> colModule;

    @FXML
    private TableColumn<Inscription, Date> colDate;

    @FXML
    private TextField etudiantIdField;

    @FXML
    private TextField moduleIdField;

    @FXML
    private DatePicker datePicker;

    @FXML
    private Button addButton;

    @FXML
    private Button deleteButton;

    private InscriptionDAO inscriptionDAO;

    public void initialize() throws SQLException {
        this.inscriptionDAO = new InscriptionDAO(DatabaseConnection.getConnection());

        // Bind columns to specific attributes
        idEtudiant.setCellValueFactory(new PropertyValueFactory<>("etudiantId"));
        idModule.setCellValueFactory(new PropertyValueFactory<>("moduleId"));

        colNom.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtudiantNom()));
        colPrenom.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getEtudiantPrenom()));
        colModule.setCellValueFactory(cellData ->
                new SimpleStringProperty(cellData.getValue().getModuleNom()));

        colDate.setCellValueFactory(new PropertyValueFactory<>("dateInscription"));

        loadInscriptionData();
    }

    private void loadInscriptionData() throws SQLException {
        ObservableList<Inscription> data = FXCollections.observableArrayList(inscriptionDAO.getAllInscriptions());
        System.out.println("Loaded inscriptions: " + data.size());
        inscriptionTable.setItems(data);
    }


    @FXML
    private void handleAddInscription(ActionEvent event) {
        try {
            // Parse user input
            int etudiantId = Integer.parseInt(etudiantIdField.getText()); // Get student ID from input field
            int moduleId = Integer.parseInt(moduleIdField.getText()); // Get module ID from input field
            Date date = java.sql.Date.valueOf(datePicker.getValue()); // Convert DatePicker value to Date

            // Create and populate an Inscription object
            Inscription inscription = new Inscription();
            inscription.setEtudiantId(etudiantId); // Set student ID
            inscription.setModuleId(moduleId); // Set module ID
            inscription.setDateInscription(date); // Set enrollment date

            // Add the inscription to the database
            if (inscriptionDAO.addInscription(inscription)) {
                showInfo("Inscription added successfully.");
                loadInscriptionData(); // Reload the data in the table after successful insertion
            } else {
                showError("Failed to add inscription.");
            }
        } catch (NumberFormatException e) {
            showError("Invalid input: Please enter numeric values for IDs.");
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }


    @FXML
    private void handleDeleteInscription(ActionEvent event) {
        Inscription selectedInscription = inscriptionTable.getSelectionModel().getSelectedItem();
        if (selectedInscription != null) {
            try {
                if (inscriptionDAO.deleteInscription(selectedInscription.getId())) {
                    showInfo("Inscription deleted successfully.");
                    loadInscriptionData();
                } else {
                    showError("Failed to delete inscription.");
                }
            } catch (SQLException e) {
                showError("Error deleting inscription: " + e.getMessage());
            }
        } else {
            showError("No inscription selected.");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
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
