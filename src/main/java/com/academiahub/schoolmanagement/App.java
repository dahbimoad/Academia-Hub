package com.academiahub.schoolmanagement;

import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
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
   public void start(Stage primaryStage) throws Exception {
    try {
        // Debug: Print working directory and resource URL
        System.out.println("Working Directory = " + System.getProperty("user.dir"));
        String fxmlPath = "/com/academiahub/schoolmanagement/Fxml/Login.fxml";
        System.out.println("Looking for FXML at: " + fxmlPath);
        System.out.println("Resource URL: " + getClass().getResource(fxmlPath));

        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();

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