package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

public class StudentListController {
    @FXML private TableView<Etudiant> studentsTable;
    @FXML private TableColumn<Etudiant, String> matriculeCol;
    @FXML private TableColumn<Etudiant, String> nomCol;
    @FXML private TableColumn<Etudiant, String> prenomCol;
    @FXML private TableColumn<Etudiant, String> emailCol;
    @FXML private TableColumn<Etudiant, String> promotionCol;

    // Add missing FXML fields
    @FXML private TextField searchField;
    @FXML private Button exportButton;
    @FXML private ComboBox<String> moduleFilter;
    @FXML private ComboBox<String> promotionFilter;

    private ProfesseurDAO professeurDAO;
    private ObservableList<Etudiant> originalItems = FXCollections.observableArrayList();
    private List<Module> professorModules;
    @FXML
    public void initialize() {
        try {
            professeurDAO = new ProfesseurDAO(DatabaseConnection.getConnection());
            setupTable();

            // Initialize search and export functionality
            searchField.textProperty().addListener((observable, oldValue, newValue) -> filterStudents());
            exportButton.setOnAction(e -> exportToCSV());

            // Setup filters
            setupFilters();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'initialisation");
        }
    }

    private void setupFilters() {
        moduleFilter.getItems().add("Tous les modules");
        promotionFilter.getItems().add("Toutes les promotions");

        moduleFilter.setValue("Tous les modules");
        promotionFilter.setValue("Toutes les promotions");

        moduleFilter.setOnAction(e -> filterStudents());
        promotionFilter.setOnAction(e -> filterStudents());
    }

     private void filterStudents() {
        String searchText = searchField.getText().toLowerCase();
        String selectedModule = moduleFilter.getValue();
        String selectedPromotion = promotionFilter.getValue();

        // Create a new filtered list based on the original items
        FilteredList<Etudiant> filteredList = new FilteredList<>(originalItems);

        filteredList.setPredicate(student -> {
            // Search text filter
            boolean matchesSearch = searchText == null || searchText.isEmpty() ||
                student.getNom().toLowerCase().contains(searchText) ||
                student.getPrenom().toLowerCase().contains(searchText) ||
                student.getEmail().toLowerCase().contains(searchText);

            // Module filter
            boolean matchesModule = selectedModule == null ||
                selectedModule.equals("Tous les modules");

            if (!matchesModule) {
                // Find the selected module
                Optional<Module> selectedModuleObj = professorModules.stream()
                    .filter(m -> m.getNomModule().equals(selectedModule))
                    .findFirst();

                if (selectedModuleObj.isPresent()) {
                    List<Etudiant> moduleStudents = professeurDAO.getModuleStudents(selectedModuleObj.get().getId());
                    matchesModule = moduleStudents.stream()
                        .anyMatch(e -> e.getId() == student.getId());
                }
            }

            // Promotion filter
            boolean matchesPromotion = selectedPromotion == null ||
                selectedPromotion.equals("Toutes les promotions") ||
                student.getPromotion().equals(selectedPromotion);

            return matchesSearch && matchesModule && matchesPromotion;
        });

        studentsTable.setItems(filteredList);
    }

    private void setupTable() {
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        promotionCol.setCellValueFactory(new PropertyValueFactory<>("promotion"));
    }

    public void loadStudentData(String username) {
        try {
            // First get professor's modules
            professorModules = professeurDAO.getProfesseurModules(username);
            if (professorModules.isEmpty()) {
                System.out.println("No modules found for professor: " + username);
                return;
            }

            // Get all students from those modules
            List<Etudiant> students = new ArrayList<>();
            for (Module module : professorModules) {
                List<Etudiant> moduleStudents = professeurDAO.getModuleStudents(module.getId());
                System.out.println("Module " + module.getNomModule() + " has " + moduleStudents.size() + " students");
                students.addAll(moduleStudents);
            }

            // Remove duplicates while preserving order
            List<Etudiant> distinctStudents = new ArrayList<>(new LinkedHashSet<>(students));

            originalItems.setAll(distinctStudents);
            studentsTable.setItems(originalItems);

            // Update filters
            updateFilterOptions();

            // Debug information
            System.out.println("Loaded " + distinctStudents.size() + " total students");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des étudiants");
        }
    }
    private void updateFilterOptions(List<Etudiant> students) {
        // Add unique promotions to the promotion filter
        Set<String> promotions = students.stream()
            .map(Etudiant::getPromotion)
            .collect(Collectors.toSet());

        promotionFilter.getItems().clear();
        promotionFilter.getItems().add("Toutes les promotions");
        promotionFilter.getItems().addAll(promotions);
        promotionFilter.setValue("Toutes les promotions");
    }

    private void exportToCSV() {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Sauvegarder la liste des étudiants");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("CSV Files", "*.csv"));

            File file = fileChooser.showSaveDialog(studentsTable.getScene().getWindow());
            if (file != null) {
                try (PrintWriter writer = new PrintWriter(file)) {
                    writer.println("Matricule,Nom,Prénom,Email,Promotion");
                    for (Etudiant student : studentsTable.getItems()) {
                        writer.println(String.format("%s,%s,%s,%s,%s",
                            student.getMatricule(),
                            student.getNom(),
                            student.getPrenom(),
                            student.getEmail(),
                            student.getPromotion()));
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
            showError("Erreur lors de l'exportation");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    private void updateFilterOptions(List<Etudiant> students, List<Module> professorModules) {
        // Update promotion filter
        Set<String> promotions = students.stream()
            .map(Etudiant::getPromotion)
            .collect(Collectors.toSet());

        promotionFilter.getItems().clear();
        promotionFilter.getItems().add("Toutes les promotions");
        promotionFilter.getItems().addAll(promotions);
        promotionFilter.setValue("Toutes les promotions");

        // Update module filter
        moduleFilter.getItems().clear();
        moduleFilter.getItems().add("Tous les modules");
        // Add each module's name to the filter
        professorModules.forEach(module ->
            moduleFilter.getItems().add(module.getNomModule())
        );
        moduleFilter.setValue("Tous les modules");
    }
    private void updateFilterOptions() {
        // Update module filter
        moduleFilter.getItems().clear();
        moduleFilter.getItems().add("Tous les modules");
        for (Module module : professorModules) {
            moduleFilter.getItems().add(module.getNomModule());
            System.out.println("Added module to filter: " + module.getNomModule());
        }
        moduleFilter.setValue("Tous les modules");

        // Update promotion filter
        Set<String> promotions = originalItems.stream()
            .map(Etudiant::getPromotion)
            .collect(Collectors.toSet());

        promotionFilter.getItems().clear();
        promotionFilter.getItems().add("Toutes les promotions");
        promotionFilter.getItems().addAll(promotions);
        promotionFilter.setValue("Toutes les promotions");
    }




}