package com.academiahub.schoolmanagement.Controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {
    @FXML
    private Label welcomeLabel;

    @FXML
    private Label roleLabel;

    public void initializeUserData(String username, String role) {
        welcomeLabel.setText("Bienvenue " + username + " !");
        roleLabel.setText("Vous êtes un " + role);
    }
}
