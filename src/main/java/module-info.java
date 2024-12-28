module com.academiahub.schoolmanagement {
    // Dépendances requises
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;

    // Ouvrir le package des modèles à javafx.base pour permettre la réflexion
    opens com.academiahub.schoolmanagement.Models to javafx.base;

    // Ouvrir le package des contrôleurs à javafx.fxml
    opens com.academiahub.schoolmanagement.Controllers to javafx.fxml;

    // Exporter les packages nécessaires
    exports com.academiahub.schoolmanagement;
    exports com.academiahub.schoolmanagement.Models;
    exports com.academiahub.schoolmanagement.Controllers;
}
