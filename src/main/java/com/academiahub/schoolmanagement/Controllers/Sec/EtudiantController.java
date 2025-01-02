package com.academiahub.schoolmanagement.Controllers.Sec;

import com.academiahub.schoolmanagement.DAO.EtudiantDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.itextpdf.text.*;
import com.itextpdf.text.Font;
import com.itextpdf.text.pdf.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.FileOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    @FXML private TextField searchField;
    @FXML private ComboBox<String> searchCriteriaBox;

    private EtudiantDAO etudiantDAO;
    private ObservableList<Etudiant> etudiantList;
    private FilteredList<Etudiant> filteredList;

    public void initialize() {
        // Initialize table columns
        matriculeColumn.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomColumn.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        promotionColumn.setCellValueFactory(new PropertyValueFactory<>("promotion"));

        // Initialize lists
        etudiantList = FXCollections.observableArrayList();
        filteredList = new FilteredList<>(etudiantList, p -> true);
        etudiantTable.setItems(filteredList);

        // Initialize search criteria combo box
        searchCriteriaBox.setItems(FXCollections.observableArrayList(
                "Matricule", "Nom", "Prénom", "Email", "Promotion"
        ));
        searchCriteriaBox.setValue("Nom"); // Default search criteria

        // Add listener for search field
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            updateFilteredList(newValue);
        });
    }

    private void updateFilteredList(String searchText) {
        filteredList.setPredicate(etudiant -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseSearch = searchText.toLowerCase();
            String searchCriteria = searchCriteriaBox.getValue();

            switch (searchCriteria) {
                case "Matricule":
                    return etudiant.getMatricule().toLowerCase().contains(lowerCaseSearch);
                case "Nom":
                    return etudiant.getNom().toLowerCase().contains(lowerCaseSearch);
                case "Prénom":
                    return etudiant.getPrenom().toLowerCase().contains(lowerCaseSearch);
                case "Email":
                    return etudiant.getEmail().toLowerCase().contains(lowerCaseSearch);
                case "Promotion":
                    return etudiant.getPromotion().toLowerCase().contains(lowerCaseSearch);
                default:
                    return false;
            }
        });
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
            loader.setLocation(getClass().getResource("/com/academiahub/schoolmanagement/Views/Sec/AddStudentDialog.fxml"));

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Ajouter un Étudiant");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(etudiantTable.getScene().getWindow());

            Scene scene = new Scene(loader.load());
            dialogStage.setScene(scene);

            AddStudentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);

            dialogStage.showAndWait();

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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Sec/EditStudentDialog.fxml"));
            Parent page = loader.load();

            Stage dialogStage = new Stage();
            dialogStage.setTitle("Modifier Étudiant");
            dialogStage.initModality(Modality.WINDOW_MODAL);
            dialogStage.initOwner(etudiantTable.getScene().getWindow());
            Scene scene = new Scene(page);
            dialogStage.setScene(scene);

            EditStudentDialogController controller = loader.getController();
            controller.setDialogStage(dialogStage);
            controller.setEtudiant(selectedEtudiant);

            dialogStage.showAndWait();

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

    @FXML
    private void handleExportPDF() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(
                    new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );
            String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
            fileChooser.setInitialFileName("liste_etudiants_" + timestamp + ".pdf");

            java.io.File file = fileChooser.showSaveDialog(etudiantTable.getScene().getWindow());

            if (file != null) {
                Document document = new Document(PageSize.A4.rotate());
                PdfWriter.getInstance(document, new FileOutputStream(file));

                document.open();

                // Add title
                Font titleFont = new Font(Font.FontFamily.HELVETICA, 18, Font.BOLD);
                Paragraph title = new Paragraph("Liste des Étudiants", titleFont);
                title.setAlignment(Element.ALIGN_CENTER);
                document.add(title);
                document.add(new Paragraph("\n")); // Add some spacing

                // Create table
                PdfPTable pdfTable = new PdfPTable(5); // 5 columns
                pdfTable.setWidthPercentage(100);

                // Add headers
                String[] headers = {"Matricule", "Nom", "Prénom", "Email", "Promotion"};
                for (String header : headers) {
                    PdfPCell cell = new PdfPCell(new Phrase(header, new Font(Font.FontFamily.HELVETICA, 12, Font.BOLD)));
                    cell.setHorizontalAlignment(Element.ALIGN_CENTER);
                    cell.setBackgroundColor(BaseColor.LIGHT_GRAY);
                    pdfTable.addCell(cell);
                }

                // Add data
                for (Etudiant etudiant : filteredList) {
                    pdfTable.addCell(etudiant.getMatricule());
                    pdfTable.addCell(etudiant.getNom());
                    pdfTable.addCell(etudiant.getPrenom());
                    pdfTable.addCell(etudiant.getEmail());
                    pdfTable.addCell(etudiant.getPromotion());
                }

                document.add(pdfTable);
                document.close();

                showInfo("Le fichier PDF a été créé avec succès!");
            }
        } catch (Exception e) {
            showError("Erreur lors de l'exportation en PDF: " + e.getMessage());
        }
    }


}