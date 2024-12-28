module com.academiahub.schoolmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;
    requires java.sql;

    opens com.academiahub.schoolmanagement.Controllers to javafx.fxml;
    exports com.academiahub.schoolmanagement;
}