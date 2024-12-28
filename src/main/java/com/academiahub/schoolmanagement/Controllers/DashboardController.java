package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.Models.Utilisateur;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private VBox adminMenu;
    @FXML private VBox secretaryMenu;
    @FXML private VBox professorMenu;
    @FXML private StackPane contentArea;

    private String currentUsername;
    private String currentRole;

     public void initializeUserData(Utilisateur user) {
        this.currentUsername = user.getUsername();
        this.currentRole = user.getRole();

        welcomeLabel.setText("Bienvenue " + currentUsername + " !");
        roleLabel.setText("Vous êtes un " + currentRole);

        // Show appropriate menu based on role
        switch (currentRole) {
            case "ADMIN":
                adminMenu.setVisible(true);
                adminMenu.setManaged(true);
                break;
            case "SECRETAIRE":
                secretaryMenu.setVisible(true);
                secretaryMenu.setManaged(true);
                break;
            case "PROFESSEUR":
                professorMenu.setVisible(true);
                professorMenu.setManaged(true);
                break;
        }
    }

    @FXML
    private void handleLogout() {
        try {
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/Login.fxml"));
            Scene loginScene = new Scene(loginRoot);
            loginScene.getStylesheets().add(getClass().getResource("/com/academiahub/schoolmanagement/Styles/login.css").toExternalForm());

            Stage stage = (Stage) welcomeLabel.getScene().getWindow();
            stage.setScene(loginScene);
            stage.setWidth(400);
            stage.setHeight(600);
            stage.centerOnScreen();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // Admin functions
    @FXML private void handleStudentManagement() {
        loadContent("StudentManagement");
    }

    @FXML private void handleTeacherManagement() {
        loadContent("TeacherManagement");
    }

    @FXML private void handleModuleManagement() {
        loadContent("ModuleManagement");
    }

    @FXML private void handleUserManagement() {
        loadContent("UserManagement");
    }

    @FXML private void handleDashboardStats() {
        loadContent("DashboardStats");
    }

    // Secretary functions
    @FXML private void handleEnrollmentManagement() {
        loadContent("EnrollmentManagement");
    }

    @FXML private void handleModuleList() {
        loadContent("ModuleList");
    }

    // Professor functions
    @FXML
    private void handleMyModules() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/content/MyModules.fxml"));
        Parent content = loader.load();

        MyModulesController controller = loader.getController();
        // Create a Utilisateur object with the current user's information
        Utilisateur currentUser = new Utilisateur();
        currentUser.setUsername(currentUsername);
        currentUser.setRole(currentRole);
        controller.loadProfesseurData(currentUser);

        contentArea.getChildren().setAll(content);
    } catch (Exception e) {
        e.printStackTrace();
        showError("Erreur lors du chargement des modules");
    }
    }

    @FXML
    private void handleStudentList() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/content/StudentList.fxml"));
        Parent content = loader.load();

        StudentListController controller = loader.getController();
        controller.loadStudentData(currentUsername);

        contentArea.getChildren().setAll(content);
    } catch (Exception e) {
        e.printStackTrace();
        showError("Erreur lors du chargement de la liste des étudiants");
    }
    }
    private void showError(String message) {
    Alert alert = new Alert(Alert.AlertType.ERROR);
    alert.setTitle("Erreur");
    alert.setHeaderText(null);
    alert.setContentText(message);
    alert.showAndWait();
    }

    private void loadContent(String contentName) {
        try {
            Parent content = FXMLLoader.load(getClass().getResource(
                "/com/academiahub/schoolmanagement/Fxml/content/" + contentName + ".fxml"));
            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
            // Show error or "Coming Soon" message
            VBox errorContent = new VBox();
            errorContent.setAlignment(javafx.geometry.Pos.CENTER);
            errorContent.getChildren().add(new Label("Cette fonctionnalité sera disponible prochainement"));
            contentArea.getChildren().setAll(errorContent);
        }
    }
}