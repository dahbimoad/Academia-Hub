package com.academiahub.schoolmanagement.Controllers;

import com.academiahub.schoolmanagement.DAO.UtilisateurDAO;
import com.academiahub.schoolmanagement.utils.LanguageManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.animation.FadeTransition;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import javafx.util.Duration;

public class ChangePasswordDialogController {
    @FXML private PasswordField currentPasswordField;
    @FXML private PasswordField newPasswordField;
    @FXML private PasswordField confirmPasswordField;
    @FXML private Label errorLabel;
    @FXML private ProgressBar strengthBar;
    @FXML private Label strengthLabel;

    private UtilisateurDAO utilisateurDAO;
    private Stage dialogStage;

    @FXML
    public void initialize() {
        utilisateurDAO = new UtilisateurDAO();
        setupPasswordValidation();
        clearError();
    }

    private void setupPasswordValidation() {
        newPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                updatePasswordStrength(newVal);
            }
        });

        confirmPasswordField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null && !newPasswordField.getText().isEmpty()) {
                validatePasswordMatch(newPasswordField.getText(), newVal);
            }
        });
    }

    private void updatePasswordStrength(String password) {
        double strength = calculatePasswordStrength(password);
        strengthBar.setProgress(strength);

        String strengthKey = switch ((int) (strength * 4)) {
            case 0 -> "password.strength.veryweak";
            case 1 -> "password.strength.weak";
            case 2 -> "password.strength.medium";
            case 3 -> "password.strength.strong";
            default -> "password.strength.verystrong";
        };

        strengthLabel.setText(LanguageManager.getInstance().getString(strengthKey));
    }

    private double calculatePasswordStrength(String password) {
        int score = 0;
        if (password.length() >= 8) score++;
        if (password.matches(".*[A-Z].*")) score++;
        if (password.matches(".*[a-z].*")) score++;
        if (password.matches(".*[0-9].*")) score++;
        if (password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};':\"\\\\|,.<>/?].*")) score++;
        return score / 5.0;
    }

    private void validatePasswordMatch(String password, String confirmation) {
        if (!password.equals(confirmation)) {
            showError(LanguageManager.getInstance().getString("password.error.match"));
        } else {
            clearError();
        }
    }

    @FXML
    private void handleSave() {
        if (validateInput()) {
            try {
                if (utilisateurDAO.updatePassword(
                        DashboardController.getCurrentUser(),
                        currentPasswordField.getText(),
                        newPasswordField.getText())) {
                    showSuccessAndClose();
                } else {
                    showError(LanguageManager.getInstance().getString("password.error.incorrect"));
                }
            } catch (Exception e) {
                showError(e.getMessage());
            }
        }
    }

    private boolean validateInput() {
        String currentPassword = currentPasswordField.getText();
        String newPassword = newPasswordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (currentPassword.isEmpty()) {
            showError(LanguageManager.getInstance().getString("password.error.current"));
            return false;
        }

        if (newPassword.isEmpty()) {
            showError(LanguageManager.getInstance().getString("password.error.new"));
            return false;
        }

        if (confirmPassword.isEmpty()) {
            showError(LanguageManager.getInstance().getString("password.error.confirm"));
            return false;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError(LanguageManager.getInstance().getString("password.error.match"));
            return false;
        }

        if (calculatePasswordStrength(newPassword) < 0.6) {
            showError(LanguageManager.getInstance().getString("password.error.weak"));
            return false;
        }

        return true;
    }

    private void showSuccessAndClose() {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(LanguageManager.getInstance().getString("alert.success"));
        alert.setHeaderText(null);
        alert.setContentText(LanguageManager.getInstance().getString("password.success"));
        alert.showAndWait();
        dialogStage.close();
    }

    private void showError(String message) {
        errorLabel.setText(message);
        errorLabel.setVisible(true);
        FadeTransition fadeIn = new FadeTransition(Duration.millis(200), errorLabel);
        fadeIn.setFromValue(0.0);
        fadeIn.setToValue(1.0);
        fadeIn.play();
    }

    private void clearError() {
        errorLabel.setVisible(false);
    }

    @FXML
    private void handleCancel() {
        dialogStage.close();
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }
}