package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import com.academiahub.schoolmanagement.utils.LanguageManager;
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
    @FXML private TextField searchField;
    @FXML private ComboBox<String> sortComboBox;
    @FXML private Label totalStudentsLabel;

    private ProfesseurDAO professeurDAO;
    private ObservableList<Module> originalItems = FXCollections.observableArrayList();
    private final LanguageManager langManager = LanguageManager.getInstance();

    @FXML
    public void initialize() {
        try {
            professeurDAO = new ProfesseurDAO(DatabaseConnection.getConnection());

            // Initialize columns with localized headers
            codeModuleCol.setText(langManager.getString("mymodules.code"));
            nomModuleCol.setText(langManager.getString("mymodules.name"));
            etudiantsCol.setText(langManager.getString("mymodules.students.count"));

            // Set up column value factories
            codeModuleCol.setCellValueFactory(new PropertyValueFactory<>("codeModule"));
            nomModuleCol.setCellValueFactory(new PropertyValueFactory<>("nomModule"));
            etudiantsCol.setCellValueFactory(new PropertyValueFactory<>("nbEtudiants"));

            // Setup search with localized prompt
            searchField.setPromptText(langManager.getString("mymodules.search"));
            searchField.textProperty().addListener((observable, oldValue, newValue) ->
                    filterModules(newValue)
            );

            // Setup sorting with localized options
            sortComboBox.getItems().addAll(
                langManager.getString("mymodules.sort.code"),
                langManager.getString("mymodules.sort.name"),
                langManager.getString("mymodules.sort.students")
            );
            sortComboBox.setValue(langManager.getString("mymodules.sort.code"));
            sortComboBox.setOnAction(e -> sortModules(sortComboBox.getValue()));

        } catch (Exception e) {
            e.printStackTrace();
            showError(langManager.getString("mymodules.error.init"));
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
        updateStatistics();
    }

    private void sortModules(String criterion) {
        ObservableList<Module> items = FXCollections.observableArrayList(modulesTable.getItems());

        if (criterion.equals(langManager.getString("mymodules.sort.code"))) {
            items.sort(Comparator.comparing(Module::getCodeModule));
        } else if (criterion.equals(langManager.getString("mymodules.sort.name"))) {
            items.sort(Comparator.comparing(Module::getNomModule));
        } else if (criterion.equals(langManager.getString("mymodules.sort.students"))) {
            items.sort(Comparator.comparing(Module::getNbEtudiants).reversed());
        }

        modulesTable.setItems(items);
    }

    private void updateStatistics() {
        int totalStudents = modulesTable.getItems().stream()
                .mapToInt(Module::getNbEtudiants)
                .sum();
        totalStudentsLabel.setText(String.format(
            langManager.getString("mymodules.total.students"),
            totalStudents
        ));
    }

    public void loadProfesseurData(Utilisateur user) {
        try {
            var modules = professeurDAO.getProfesseurModules(user.getUsername());
            modules.forEach(module -> {
                var students = professeurDAO.getModuleStudents(module.getId());
                module.setNbEtudiants(students.size());
            });

            originalItems.setAll(modules);
            modulesTable.setItems(originalItems);
            updateStatistics();
            sortModules(langManager.getString("mymodules.sort.code"));

        } catch (Exception e) {
            e.printStackTrace();
            showError(langManager.getString("mymodules.error.loading"));
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(langManager.getString("alert.error"));
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}