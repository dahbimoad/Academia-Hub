package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Module;
import com.academiahub.schoolmanagement.Models.Professeur;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class MyModulesController {
    @FXML
    private TableView<Module> modulesTable;
    @FXML
    private TableColumn<Module, String> codeModuleCol;
    @FXML
    private TableColumn<Module, String> nomModuleCol;
    @FXML
    private TableColumn<Module, Integer> etudiantsCol;

    private ProfesseurDAO professeurDAO;

    @FXML
    public void initialize() {
        try {
            professeurDAO = new ProfesseurDAO(DatabaseConnection.getConnection());
            setupTable();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors de l'initialisation");
        }
    }

    private void setupTable() {
        codeModuleCol.setCellValueFactory(new PropertyValueFactory<>("codeModule"));
        nomModuleCol.setCellValueFactory(new PropertyValueFactory<>("nomModule"));
        // For number of students, you might need to add this property to your Module class
        etudiantsCol.setCellValueFactory(new PropertyValueFactory<>("nombreEtudiants"));
    }

    public void loadProfesseurData(Utilisateur user) {
        try {
            // First get the professor details
            Professeur prof = professeurDAO.getProfesseurByUserId(user.getId());
            if (prof != null) {
                // Then get their modules
                var modules = professeurDAO.getProfesseurModules(prof.getId());
                modulesTable.setItems(FXCollections.observableArrayList(modules));
            }
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
