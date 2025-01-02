package com.academiahub.schoolmanagement.utils;

import javafx.scene.control.Alert;
import java.util.Locale;
import java.util.ResourceBundle;

public class LanguageManager {
    private static LanguageManager instance;
    private ResourceBundle bundle;
    private Locale currentLocale;
    private static final String BUNDLE_PATH = "com.academiahub.schoolmanagement.i18n.messages";

    private LanguageManager() {
        currentLocale = new Locale("fr"); // Default to French
        loadBundle();
    }

    public static LanguageManager getInstance() {
        if (instance == null) {
            instance = new LanguageManager();
        }
        return instance;
    }

    private void loadBundle() {
        try {
            bundle = ResourceBundle.getBundle(BUNDLE_PATH, currentLocale);
        } catch (Exception e) {
            showError("Error loading language bundle: " + e.getMessage());
        }
    }

    public void setLanguage(String language) {
        currentLocale = new Locale(language);
        loadBundle();
    }

    public ResourceBundle getBundle() {
        return bundle;
    }

    public String getString(String key) {
        try {
            return bundle.getString(key);
        } catch (Exception e) {
            return "!" + key + "!";
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
