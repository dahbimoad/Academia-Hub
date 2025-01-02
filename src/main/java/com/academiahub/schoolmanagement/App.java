package com.academiahub.schoolmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javafx.scene.Parent;

public class App extends Application {

    @Override
   public void start(Stage primaryStage) throws Exception {
    try {
        String fxmlPath = "/com/academiahub/schoolmanagement/Views/Base/Login.fxml";
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
        Parent root = loader.load();
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/com/academiahub/schoolmanagement/Images/app_icon.png")));
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