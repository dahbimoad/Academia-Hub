//src/main/java/com/academiahub/schoolmanagement/Controllers/LoginController.java
package com.academiahub.schoolmanagement.Controllers;
import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.Models.Utilisateur;
import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class LoginController {
    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorMessage;

    private UtilisateurDAO utilisateurDAO;

    @FXML
    public void initialize() {
        try {
            utilisateurDAO = new UtilisateurDAO(DatabaseConnection.getConnection());
            errorMessage.setText("");
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setText("Erreur de connexion à la base de données");
            errorMessage.setTextFill(Color.RED);
        }
    }

    @FXML
    public void handleLoginButton() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText().trim();

        if (username.isEmpty() || password.isEmpty()) {
            errorMessage.setTextFill(Color.RED);
            errorMessage.setText("Veuillez remplir tous les champs");
            return;
        }

        try {
            Utilisateur user = utilisateurDAO.authenticate(username, password);

            if (user != null) {
                try {
                    // Load the dashboard FXML
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/academiahub/schoolmanagement/Fxml/Dashboard.fxml"));
                    Parent dashboardRoot = loader.load();

                    // Get the controller
                    DashboardController dashboardController = loader.getController();

                    dashboardController.initializeUserData(user);

                    // Create new scene
                    Scene dashboardScene = new Scene(dashboardRoot);
                    dashboardScene.getStylesheets().add(getClass().getResource("/com/academiahub/schoolmanagement/Styles/login.css").toExternalForm());

                    // Get the stage from current scene
                    Stage stage = (Stage) usernameField.getScene().getWindow();

                    // Set the new scene
                    stage.setScene(dashboardScene);

                    // Initialize user data after setting the scene
                    dashboardController.initializeUserData(user);

                    // Make window larger
                    stage.setWidth(1200);
                    stage.setHeight(800);
                    stage.centerOnScreen();

                    stage.show();

                } catch (Exception e) {
                    e.printStackTrace();
                    errorMessage.setTextFill(Color.RED);
                    errorMessage.setText("Erreur lors du chargement du tableau de bord");
                }
            } else {
                errorMessage.setTextFill(Color.RED);
                errorMessage.setText("Nom d'utilisateur ou mot de passe incorrect");
            }
        } catch (Exception e) {
            e.printStackTrace();
            errorMessage.setTextFill(Color.RED);
            errorMessage.setText("Erreur lors de la connexion");
        }
    }
}