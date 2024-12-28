package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.ProfesseurDAO;
import com.academiahub.schoolmanagement.Models.Etudiant;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

public class StudentListController {
    @FXML private TableView<Etudiant> studentsTable;
    @FXML private TableColumn<Etudiant, String> matriculeCol;
    @FXML private TableColumn<Etudiant, String> nomCol;
    @FXML private TableColumn<Etudiant, String> prenomCol;
    @FXML private TableColumn<Etudiant, String> emailCol;
    @FXML private TableColumn<Etudiant, String> promotionCol;

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
        matriculeCol.setCellValueFactory(new PropertyValueFactory<>("matricule"));
        nomCol.setCellValueFactory(new PropertyValueFactory<>("nom"));
        prenomCol.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        emailCol.setCellValueFactory(new PropertyValueFactory<>("email"));
        promotionCol.setCellValueFactory(new PropertyValueFactory<>("promotion"));
    }

    public void loadStudentData(String username) {
        try {
            // First get professor's modules
            var modules = professeurDAO.getProfesseurModules(username);
            // Then get all students from those modules
            var students = modules.stream()
                .flatMap(m -> professeurDAO.getModuleStudents(m.getId()).stream())
                .distinct()
                .toList();
            studentsTable.setItems(FXCollections.observableArrayList(students));
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des étudiants");
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