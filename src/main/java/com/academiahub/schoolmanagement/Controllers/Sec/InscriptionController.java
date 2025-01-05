package com.academiahub.schoolmanagement.Controllers.Sec;

import com.academiahub.schoolmanagement.DAO.EtudiantDAO;
import com.academiahub.schoolmanagement.DAO.InscriptionDAO;
import com.academiahub.schoolmanagement.DAO.ModuleDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Inscription;
import com.academiahub.schoolmanagement.Models.Module;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.*;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import javafx.scene.control.Button;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;

import java.sql.SQLException;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class InscriptionController {
    @FXML
    private Button exportPdfButton;
    @FXML
    private TextField searchField;
    @FXML
    private ComboBox<Etudiant> studentComboBox;

    @FXML
    private ComboBox<Module> moduleComboBox;
    private FilteredList<Inscription> filteredData;
    @FXML
    private TableView<Inscription> inscriptionTable;


    @FXML
    private TableColumn<Inscription, Integer> idEtudiant;
    @FXML
    private TableColumn<Inscription, Integer> id;

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

    @FXML
    public void initialize() throws SQLException {
        this.inscriptionDAO = new InscriptionDAO(DatabaseConnection.getConnection());

        // Bind columns to specific attributes (keep your existing bindings)
        id.setCellValueFactory(new PropertyValueFactory<>("id"));
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

        // Initialize FilteredList
        filteredData = new FilteredList<>(inscriptionList, p -> true);

        // Add listener to searchField for real-time filtering
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(inscription -> {
                // If search field is empty, display all data
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Match against multiple fields
                return inscription.getEtudiantNom().toLowerCase().contains(lowerCaseFilter)
                        || inscription.getEtudiantPrenom().toLowerCase().contains(lowerCaseFilter)
                        || inscription.getModuleNom().toLowerCase().contains(lowerCaseFilter)
                        || String.valueOf(inscription.getEtudiantId()).contains(lowerCaseFilter)
                        || String.valueOf(inscription.getModuleId()).contains(lowerCaseFilter);
            });
        });

        // Wrap the FilteredList in a SortedList
        SortedList<Inscription> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(inscriptionTable.comparatorProperty());

        // Add sorted (and filtered) data to the table
        inscriptionTable.setItems(sortedData);

        loadInscriptionData();
        loadStudents();
        loadModules();
        if (exportPdfButton != null) {
            exportPdfButton.setOnAction(event -> exportToPdf());
        }
    }

    @FXML
    private void exportToPdf() {
        try {
            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save PDF File");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            // Set default file name with current date
            String defaultFileName = "Inscriptions_" +
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm")) +
                    ".pdf";
            fileChooser.setInitialFileName(defaultFileName);

            // Show save dialog
            java.io.File file = fileChooser.showSaveDialog(inscriptionTable.getScene().getWindow());

            if (file != null) {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter writer = PdfWriter.getInstance(document, new FileOutputStream(file));
                document.open();

                // Add title
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("Liste des Inscriptions", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                title.setSpacingAfter(20);
                document.add(title);

                // Add date
                Font dateFont = new Font(Font.FontFamily.HELVETICA, 12, Font.ITALIC);
                Paragraph date = new Paragraph("Généré le: " +
                        LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")),
                        dateFont);
                date.setAlignment(Element.ALIGN_RIGHT);
                date.setSpacingAfter(20);
                document.add(date);

                // Create table
                PdfPTable pdfTable = new PdfPTable(6); // 6 columns
                pdfTable.setWidthPercentage(100);

                // Set column widths
                float[] columnWidths = {1f, 1f, 1.5f, 1.5f, 2f, 1.5f};
                pdfTable.setWidths(columnWidths);

                // Add headers
                Font headerFont = new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD);
                String[] headers = {"ID Étudiant", "ID Module", "Nom", "Prénom", "Module", "Date d'inscription"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, headerFont));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    cell.setPadding(5);
                    pdfTable.addCell(cell);
                }

                // Add data
                Font dataFont = new Font(Font.FontFamily.HELVETICA, 10);
                for (Inscription inscription : filteredData) {
                    pdfTable.addCell(new Phrase(String.valueOf(inscription.getEtudiantId()), dataFont));
                    pdfTable.addCell(new Phrase(String.valueOf(inscription.getModuleId()), dataFont));
                    pdfTable.addCell(new Phrase(inscription.getEtudiantNom(), dataFont));
                    pdfTable.addCell(new Phrase(inscription.getEtudiantPrenom(), dataFont));
                    pdfTable.addCell(new Phrase(inscription.getModuleNom(), dataFont));
                    pdfTable.addCell(new Phrase(inscription.getDateInscription().toString(), dataFont));
                }

                document.add(pdfTable);
                document.close();

                // Show success message
                showInfo("Le PDF a été généré avec succès!\nEmplacement: " + file.getAbsolutePath());
            }
        } catch (Exception e) {
            showError("Erreur lors de la génération du PDF: " + e.getMessage());
            e.printStackTrace();
        }
    }


    private void loadStudents() throws SQLException {
        EtudiantDAO etudiantDAO = new EtudiantDAO(DatabaseConnection.getConnection());
        List<Etudiant> students = etudiantDAO.getAllStudents(); // Fetch from DAO
        studentComboBox.setItems(FXCollections.observableArrayList(students));
    }

    private void loadModules() throws SQLException {
        ModuleDAO moduleDAO = new ModuleDAO(DatabaseConnection.getConnection());
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
                System.out.println(selectedInscription.getId());
                System.out.println(selectedInscription.getModuleNom());
                boolean deleted = inscriptionDAO.deleteInscription(selectedInscription.getId());
                if (deleted) {
                    inscriptionList.remove(selectedInscription);
                    // Reload data from database to ensure sync
                    loadInscriptionData();
                    showInfo("Inscription supprimée avec succès.");
                } else {
                    showError("L'inscription n'a pas pu être supprimée. Veuillez réessayer.");
                }
            } catch (SQLException e) {
                showError("Erreur lors de la suppression de l'inscription: " + e.getMessage());
                e.printStackTrace();
                // Reload data to ensure UI is in sync with database
                try {
                    loadInscriptionData();
                } catch (SQLException ex) {
                    showError("Erreur lors de la actualisation des données: " + ex.getMessage());
                }
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
