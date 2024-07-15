module com.example.budget {
    requires javafx.controls;
    requires javafx.fxml;

    requires com.dlsc.formsfx;
    requires java.sql;
    requires de.jensd.fx.glyphs.fontawesome;

    opens com.example.budget to javafx.fxml;
    exports com.example.budget;
    exports com.example.budget.Controller;
    opens com.example.budget.Controller to javafx.fxml;
    opens com.example.budget.Entity to javafx.base, javafx.fxml;
}
