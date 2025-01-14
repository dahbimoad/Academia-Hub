package com.academiahub.schoolmanagement.Controllers.Base;

import com.academiahub.schoolmanagement.Controllers.Admin.ModuleController;
import com.academiahub.schoolmanagement.Controllers.Admin.MyModulesController;
import com.academiahub.schoolmanagement.Controllers.Admin.ProfesseurController;
import com.academiahub.schoolmanagement.Controllers.Admin.UtilisateurController;
import com.academiahub.schoolmanagement.Controllers.Sec.InscriptionController;
import com.academiahub.schoolmanagement.Controllers.Sec.EtudiantController;
import com.academiahub.schoolmanagement.Controllers.Prof.StudentListController;
import com.academiahub.schoolmanagement.Controllers.Sec.ModuleListController;
import com.academiahub.schoolmanagement.DAO.NotificationsDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

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
    private static Utilisateur currentUser;

    private String currentUsername;
    private String currentRole;
    public DashboardController() {
    try {
        dbConnection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/ecole", "postgres", "Imad2002");
    } catch (SQLException e) {
        e.printStackTrace();
        showError("Database connection failed: " + e.getMessage());
    }
    }
    public static Utilisateur getCurrentUser() {
        return currentUser;
    }

     public void initializeUserData(Utilisateur user) {
        currentUser = user;
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
            Parent loginRoot = FXMLLoader.load(getClass().getResource("/com/academiahub/schoolmanagement/Views/Base/Login.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Sec/StudentManagement.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Admin/ProfesseurView.fxml"));
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
        try {
            // Load the EnrollmentManagement.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Admin/Module.fxml"));
            Parent content = loader.load();

            // Get the controller for EnrollmentManagement.fxml
            ModuleController controller = loader.getController();

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



    @FXML private void handleUserManagement() {
        try {
            // Load the EnrollmentManagement.fxml file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Admin/UtilisateurView.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Sec/EnrollmentManagement.fxml"));
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Sec/ModuleList.fxml"));
            Parent content = loader.load();

            ModuleListController controller = loader.getController();
            // Create a Utilisateur object with the current user's information
            Utilisateur currentUser = new Utilisateur();
            currentUser.setUsername(currentUsername);
            currentUser.setRole(currentRole);


            contentArea.getChildren().setAll(content);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Erreur lors du chargement des modules");
        }
    }

    // Professor functions
    @FXML
    private void handleMyModules() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Admin/MyModules.fxml"));
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Prof/StudentList.fxml"));
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
        String[] folders = {"Admin", "Prof", "Base", "Sec"};
        Parent content = null;

        for (String folder : folders) {
            try {
                // Construire le chemin vers le fichier FXML
                String path = "/com/academiahub/schoolmanagement/Views/" + folder + "/" + contentName + ".fxml";
                content = FXMLLoader.load(getClass().getResource(path));
                if (content != null) {
                    // Charger le contenu si trouvé
                    contentArea.getChildren().setAll(content);
                    return;
                }
            } catch (Exception e) {
                // Continuer la recherche dans les autres dossiers
            }
        }

        // Si aucun fichier n'est trouvé, afficher un message "À venir"
        VBox errorContent = new VBox();
        errorContent.setAlignment(javafx.geometry.Pos.CENTER);
        errorContent.getChildren().add(new Label("Cette fonctionnalité sera disponible prochainement"));
        contentArea.getChildren().setAll(errorContent);
    }

    @FXML
    public void showNotifications() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Base/notifications_popup.fxml"));
            Parent root = loader.load();

            // Récupérer le controller et initialiser le DAO
            NotificationsController notificationsController = loader.getController();
            // Supposons que vous avez une classe DatabaseConnection avec une méthode getConnection()
            Connection connection = DatabaseConnection.getConnection();
            notificationsController.setNotificationsDAO(connection);

            Stage notificationStage = new Stage();
            notificationStage.initModality(Modality.APPLICATION_MODAL);
            notificationStage.initStyle(StageStyle.UNDECORATED);
            notificationStage.setTitle("Notifications");

            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/com/academiahub/schoolmanagement/Styles/notifications.css").toExternalForm());

            notificationStage.setScene(scene);
            notificationStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }







    @FXML
private void showProfileDialog() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Base/user_profile_dialog.fxml"));
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
@FXML
private void handleSettings() {
    try {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Views/Base/SettingsDialog.fxml"));
        Parent root = loader.load();

        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.APPLICATION_MODAL);
        dialogStage.setTitle("Paramètres");
        Scene scene = new Scene(root);
        dialogStage.setScene(scene);

        SettingsDialogController controller = loader.getController();
        controller.setDialogStage(dialogStage);

        dialogStage.showAndWait();
    } catch (IOException e) {
        e.printStackTrace();
    }
}
}