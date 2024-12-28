module com.academiahub.schoolmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;

    opens com.academiahub.schoolmanagement to javafx.fxml;
    opens com.academiahub.schoolmanagement.Controllers to javafx.fxml;

    exports com.academiahub.schoolmanagement;
    exports com.academiahub.schoolmanagement.Controllers;
    exports com.academiahub.schoolmanagement.Models;
}