package com.academiahub.schoolmanagement.Controllers.Admin;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.Comparator;

public class MyModulesController {
    @FXML private TableView<Module> modulesTable;
    @FXML private TableColumn<Module, String> codeModuleCol;
    @FXML private TableColumn<Module, String> nomModuleCol;
    @FXML private TableColumn<Module, Integer> etudiantsCol;

    // Add missing FXML fields
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label totalStudentsLabel;

    private ProfesseurDAO professeurDAO;
    private ObservableList<Module> originalItems = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        try {
            professeurDAO = new ProfesseurDAO(DatabaseConnection.getConnection());

            // Initialize columns
            codeModuleCol.setCellValueFactory(new PropertyValueFactory<>("codeModule"));
            nomModuleCol.setCellValueFactory(new PropertyValueFactory<>("nomModule"));
            etudiantsCol.setCellValueFactory(new PropertyValueFactory<>("nbEtudiants"));

            // Initialize search functionality
            searchField.textProperty().addListener((observable, oldValue, newValue) ->
                    filterModules(newValue)
            );

            // Initialize sorting
            sortComboBox.getItems().addAll("Code Module", "Nom Module", "Nombre d'étudiants");
            sortComboBox.setValue("Code Module"); // Set default value
            sortComboBox.setOnAction(e -> sortModules(sortComboBox.getValue()));

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'initialisation");
        }
    }

    private void filterModules(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            modulesTable.setItems(originalItems);
            return;
        }

        FilteredList<Module> filteredList = new FilteredList<>(originalItems);
        filteredList.setPredicate(module ->
                module.getCodeModule().toLowerCase().contains(searchText.toLowerCase()) ||
                        module.getNomModule().toLowerCase().contains(searchText.toLowerCase())
        );

        modulesTable.setItems(filteredList);
        updateStatistics(); // Update statistics after filtering
    }

    private void sortModules(String criterion) {
        ObservableList<Module> items = FXCollections.observableArrayList(modulesTable.getItems());

        switch (criterion) {
            case "Code Module":
                items.sort(Comparator.comparing(Module::getCodeModule));
                break;
            case "Nom Module":
                items.sort(Comparator.comparing(Module::getNomModule));
                break;
            case "Nombre d'étudiants":
                items.sort(Comparator.comparing(Module::getNbEtudiants).reversed());
                break;
        }
        modulesTable.setItems(items);
    }

    private void updateStatistics() {
        int totalStudents = modulesTable.getItems().stream()
                .mapToInt(Module::getNbEtudiants)
                .sum();
        totalStudentsLabel.setText("Total d'étudiants: " + totalStudents);
    }

    public void loadProfesseurData(Utilisateur user) {
        try {
            // Get professor's modules using username
            var modules = professeurDAO.getProfesseurModules(user.getUsername());

            // For each module, get the number of students
            modules.forEach(module -> {
                var students = professeurDAO.getModuleStudents(module.getId());
                module.setNbEtudiants(students.size());
            });

            originalItems.setAll(modules);
            modulesTable.setItems(originalItems);
            updateStatistics();

            // Sort initially by code module
            sortModules("Code Module");

        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des modules");
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Erreur");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}