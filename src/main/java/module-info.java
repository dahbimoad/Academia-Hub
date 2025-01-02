module com.academiahub.schoolmanagement {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.kordamp.bootstrapfx.core;
    requires org.apache.poi.ooxml;
    requires org.slf4j;
    requires itextpdf;


    opens com.academiahub.schoolmanagement to javafx.fxml;


    exports com.academiahub.schoolmanagement;

    exports com.academiahub.schoolmanagement.Models;
    exports com.academiahub.schoolmanagement.Controllers.Base;
    opens com.academiahub.schoolmanagement.Controllers.Base to javafx.fxml;
    exports com.academiahub.schoolmanagement.Controllers.Admin;
    opens com.academiahub.schoolmanagement.Controllers.Admin to javafx.fxml;
    exports com.academiahub.schoolmanagement.Controllers.Sec;
    opens com.academiahub.schoolmanagement.Controllers.Sec to javafx.fxml;
    exports com.academiahub.schoolmanagement.Controllers.Prof;
    opens com.academiahub.schoolmanagement.Controllers.Prof to javafx.fxml;
}