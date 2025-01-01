package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.NotificationDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.DAO.EtudiantDAO;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DashboardController {
    @FXML private Label welcomeLabel;
    @FXML private Label roleLabel;
    @FXML private VBox adminMenu;
    @FXML private VBox secretaryMenu;
    @FXML private VBox professorMenu;
    @FXML private StackPane contentArea;
    private Connection dbConnection;
    @FXML private Button notificationButton;
    private Utilisateur currentUser;

    private String currentUsername;
    private String currentRole;
    public DashboardController() {
    try {
        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/school_management", "postgres", "mouad1233");
    } catch (SQLException e) {
        e.printStackTrace();
        showError("Database connection failed: " + e.getMessage());
    }
    }

     public void initializeUserData(Utilisateur user) {
        this.currentUser = user;
        this.currentUsername = user.getUsername();
        this.currentRole = user.getRole();

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
    @FXML
    private void handleStudentManagement() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/content/StudentManagement.fxml"));
            Parent content = loader.load();

            // Inject DAO into the EtudiantController
            EtudiantController controller = loader.getController();
            controller.setDAO();

            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement de la gestion des étudiants.");
        }
    }

    @FXML private void handleTeacherManagement() {
        try {
            // Load the EnrollmentManagement.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/ProfesseurView.fxml"));
            Parent content = loader.load();

            // Get the controller for EnrollmentManagement.fxml
            ProfesseurController controller = loader.getController();

            // Initialize the controller
            controller.initialize();

            // Clear existing content and display the new content in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);


        } catch (Exception e) {
            e.printStackTrace();
            showError("Une erreur est survenue lors du chargement de la gestion des inscriptions : " + e.getMessage());
        }
    }

    @FXML private void handleModuleManagement() {
        loadContent("ModuleManagement");
    }


    @FXML private void handleUserManagement() {
        try {
            // Load the EnrollmentManagement.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/UtilisateurView.fxml"));
            Parent content = loader.load();

            // Get the controller for EnrollmentManagement.fxml
            UtilisateurController controller = loader.getController();

            // Initialize the controller
            controller.initialize();

            // Clear existing content and display the new content in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);


        } catch (Exception e) {
            e.printStackTrace();
            showError("Une erreur est survenue lors du chargement de la gestion des inscriptions : " + e.getMessage());
        }
    }

    @FXML private void handleDashboardStats() {
        loadContent("DashboardStats");
    }

    // Secretary functions
    @FXML
    private void handleEnrollmentManagement() {
        try {
            // Load the EnrollmentManagement.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/content/EnrollmentManagement.fxml"));
            Parent content = loader.load();

            // Get the controller for EnrollmentManagement.fxml
            InscriptionController controller = loader.getController();

            // Initialize the controller
            controller.initialize();

            // Clear existing content and display the new content in the content area
            contentArea.getChildren().clear();
            contentArea.getChildren().add(content);


        } catch (Exception e) {
            e.printStackTrace();
            showError("Une erreur est survenue lors du chargement de la gestion des inscriptions : " + e.getMessage());
        }
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
@FXML
private void showNotifications() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/com/academiahub/schoolmanagement/Fxml/notifications_popup.fxml"));
        Parent notificationsPopup = loader.load();

        NotificationsController controller = loader.getController();
        controller.setNotificationDAO(new NotificationDAO(dbConnection));
        controller.setUserRole(currentRole);

        // Create a new stage for the popup
        Stage stage = new Stage();
        stage.setScene(new Scene(notificationsPopup));
        stage.initOwner(notificationButton.getScene().getWindow());
        stage.setTitle("Notifications");
        stage.show();
    } catch (IOException e) {
        e.printStackTrace();
        showError("Erreur lors de l'affichage des notifications.");
    }
}





@FXML
private void showProfileDialog() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/user_profile_dialog.fxml"));
        VBox profileDialog = loader.load();

        UserProfileController controller = loader.getController();
        controller.setCurrentUser(currentUser);

        Dialog<Void> dialog = new Dialog<>();
        dialog.getDialogPane().setContent(profileDialog);
        dialog.show();
    } catch (IOException e) {
        e.printStackTrace();
        showError("Erreur lors de l'affichage du profil");
    }
}

}