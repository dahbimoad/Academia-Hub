package com.academiahub.schoolmanagement;

import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import com.academiahub.schoolmanagement.utils.LanguageManager;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void init() {
        // Initialize LanguageManager before loading any FXML
        LanguageManager.getInstance();
    }


    @Override
   public void start(Stage primaryStage) throws Exception {
    try {
        String fxmlPath = "/com/academiahub/schoolmanagement/Fxml/Login.fxml";
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/academiahub/schoolmanagement/Fxml/Login.fxml"),
                LanguageManager.getInstance().getBundle()
            );
        Parent root = loader.load();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/academiahub/schoolmanagement/images/app_icon.png")));
        primaryStage.setTitle("School Management System");

        Scene scene = new Scene(root);
        scene.getStylesheets().add(getClass().getResource("/com/academiahub/schoolmanagement/Styles/login.css").toExternalForm());

        primaryStage.setTitle("School Management System - Login");
        primaryStage.setScene(scene);
        primaryStage.show();
    } catch (Exception e) {
        System.out.println("Error loading FXML:");
        e.printStackTrace();
    }
    }

    public static void main(String[] args) {
        launch(args);
    }
}