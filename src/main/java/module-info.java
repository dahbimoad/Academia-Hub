module com.academiahub.schoolmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires itextpdf;
    requires java.prefs;


    opens com.academiahub.schoolmanagement to javafx.fxml;
    opens com.academiahub.schoolmanagement.Controllers to javafx.fxml;

    exports com.academiahub.schoolmanagement;
    exports com.academiahub.schoolmanagement.Controllers;
    exports com.academiahub.schoolmanagement.Models;
}