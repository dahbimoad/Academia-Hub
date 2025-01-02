package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import com.academiahub.schoolmanagement.utils.LanguageManager;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class ProfModulesController {
    @FXML private TableView<Module> modulesTable;
    @FXML private TableColumn<Module, String> codeModuleCol;
    @FXML private TableColumn<Module, String> nomModuleCol;
    @FXML private TableColumn<Module, Integer> nbEtudiantsCol;

    @FXML private TableView<Etudiant> studentsTable;
    @FXML private TableColumn<Etudiant, String> matriculeCol;
    @FXML private TableColumn<Etudiant, String> nomCol;
    @FXML private TableColumn<Etudiant, String> prenomCol;
    @FXML private TableColumn<Etudiant, String> emailCol;
    @FXML private TableColumn<Etudiant, String> promotionCol;

    private ProfesseurDAO professeurDAO;
    private String currentUsername;
    private final LanguageManager langManager = LanguageManager.getInstance();

    @FXML
    public void initialize() {
        try {
            professeurDAO = new ProfesseurDAO(DatabaseConnection.getConnection());

            // Set localized column headers for modules
            codeModuleCol.setText(langManager.getString("mymodules.code"));
            nomModuleCol.setText(langManager.getString("mymodules.name"));
            nbEtudiantsCol.setText(langManager.getString("mymodules.students.count"));

            // Set localized column headers for students
            matriculeCol.setText(langManager.getString("studentlist.matricule"));
            nomCol.setText(langManager.getString("studentlist.name"));
            prenomCol.setText(langManager.getString("studentlist.firstname"));
            emailCol.setText(langManager.getString("studentlist.email"));
            promotionCol.setText(langManager.getString("studentlist.promotion"));

            // Initialize columns
            codeModuleCol.setCellValueFactory(new PropertyValueFactory<>("codeModule"));
            nomModuleCol.setCellValueFactory(new PropertyValueFactory<>("nomModule"));
            nbEtudiantsCol.setCellValueFactory(new PropertyValueFactory<>("nbEtudiants"));

            matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
            nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
            prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
            emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
            promotionCol.setCellValueFactory(new PropertyValueFactory<>("promotion"));

            // Add listener for module selection
            modulesTable.getSelectionModel().selectedItemProperty().addListener(
                    (obs, oldSelection, newSelection) -> {
                        if (newSelection != null) {
                            loadStudentsForModule(newSelection.getId());
                        }
                    }
            );
        } catch (Exception e) {
            e.printStackTrace();
            showError(langManager.getString("mymodules.error.init"));
        }
    }

    public void loadProfesseurData(String username) {
        this.currentUsername = username;
        loadModules();
    }

    private void loadModules() {
        try {
            var modules = professeurDAO.getProfesseurModules(currentUsername);
            modulesTable.setItems(FXCollections.observableArrayList(modules));
        } catch (Exception e) {
            e.printStackTrace();
            showError(langManager.getString("mymodules.error.loading"));
        }
    }

    private void loadStudentsForModule(int moduleId) {
        try {
            var students = professeurDAO.getModuleStudents(moduleId);
            studentsTable.setItems(FXCollections.observableArrayList(students));
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