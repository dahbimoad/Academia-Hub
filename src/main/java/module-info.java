module com.academiahub.schoolmanagement {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.kordamp.bootstrapfx.core;

    opens com.academiahub.schoolmanagement to javafx.fxml;
    exports com.academiahub.schoolmanagement;
}