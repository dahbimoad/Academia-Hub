package com.academiahub.schoolmanagement;

import com.academiahub.schoolmanagement.utils.DatabaseConnection;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.SQLException;

public class App extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(App.class.getResource("hello-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 320, 240);
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        try {
            // Attempt to get a connection
            Connection connection = DatabaseConnection.getConnection();

            // Test if the connection is valid
            if (connection != null && !connection.isClosed()) {
                System.out.println("Connection successful!");
            } else {
                System.out.println("Connection failed!");
            }
        } catch (SQLException e) {
            // Print any SQL exception details
            System.err.println("Error while connecting to the database:");
            e.printStackTrace();
        }


//        launch();
    }
}