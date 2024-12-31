package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.EtudiantDAO;
import com.academiahub.schoolmanagement.DAO.InscriptionDAO;
import com.academiahub.schoolmanagement.DAO.ModuleDOA;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Inscription;
import com.academiahub.schoolmanagement.Models.Module;



import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class InscriptionController {
    @FXML
    private ComboBox<Etudiant> studentComboBox;

    @FXML
    private ComboBox<Module> moduleComboBox;




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
    private ObservableList<Inscription> inscriptionList;

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

        inscriptionList = FXCollections.observableArrayList();
        inscriptionTable.setItems(inscriptionList);
        loadInscriptionData();
        loadStudents();
        loadModules();

    }

    private void loadStudents() throws SQLException {
        EtudiantDAO etudiantDAO = new EtudiantDAO(DatabaseConnection.getConnection());
        List<Etudiant> students = etudiantDAO.getAllStudents(); // Fetch from DAO
        studentComboBox.setItems(FXCollections.observableArrayList(students));
    }

    private void loadModules() throws SQLException {
        ModuleDOA moduleDAO = new ModuleDOA(DatabaseConnection.getConnection());
        List<Module> modules = moduleDAO.getAllModules(); // Fetch from DAO
        moduleComboBox.setItems(FXCollections.observableArrayList(modules));
    }

    private void loadInscriptionData() throws SQLException {
        List<Inscription> inscriptions = inscriptionDAO.getAllInscriptions();
        System.out.println("Loaded inscriptions: " + inscriptions.size());
        inscriptionList.setAll(inscriptions);

    }

    @FXML
    private void addInscription() throws SQLException {
        Etudiant selectedStudent = studentComboBox.getValue();
        Module selectedModule = moduleComboBox.getValue();
        LocalDate selectedDate = datePicker.getValue();

        if (selectedStudent != null && selectedModule != null && selectedDate != null) {
            Inscription newInscription = new Inscription();
            newInscription.setEtudiantId(selectedStudent.getId());
            newInscription.setModuleId(selectedModule.getId());
            newInscription.setDateInscription(Date.valueOf(selectedDate));

            inscriptionDAO.addInscription(newInscription); // Save to database
            loadInscriptionData(); // Refresh table
            showAlert("Inscription ajouté", "L'inscription a été ajouté avec succès.", Alert.AlertType.INFORMATION);
        } else {
            // Show error message if any field is missing
            showAlert("Erreur", "Erreur lors de l'ajout de l'inscription " , Alert.AlertType.ERROR);
            System.out.println("All fields are required.");
        }
    }

    private void showAlert(String title, String message, Alert.AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // No header text
        alert.setContentText(message);
        alert.showAndWait();
    }


    @FXML
    private void handleAddInscription(ActionEvent event) {
        try {
            // Parse user input
            int etudiantId = Integer.parseInt(etudiantIdField.getText()); // Get student ID from input field
            int moduleId = Integer.parseInt(moduleIdField.getText()); // Get module ID from input field
            Date date = Date.valueOf(datePicker.getValue()); // Convert DatePicker value to Date

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
        if (selectedInscription == null) {
            showError("Veuillez sélectionner une inscription à supprimer.");
            return;
        }

        Alert confirmationAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmationAlert.setTitle("Confirmation");
        confirmationAlert.setHeaderText("Supprimer l'inscription");
        confirmationAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette inscription ?");
        Optional<ButtonType> result = confirmationAlert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                inscriptionDAO.deleteInscription(selectedInscription.getId());
                inscriptionList.remove(selectedInscription);
                showInfo("Inscription supprimé avec succès.");
            } catch (Exception e) {
                showError("Erreur lors de la suppression de l'inscription: " + e.getMessage());
            }
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
