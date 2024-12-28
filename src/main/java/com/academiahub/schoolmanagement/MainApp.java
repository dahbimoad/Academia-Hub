package com.academiahub.schoolmanagement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/academiahub/schoolmanagement/Views/ProfesseurManagement.fxml")
        );
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Gestion des Professeurs");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
