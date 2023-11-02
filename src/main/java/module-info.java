module com.example.browser {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires com.dlsc.formsfx;

    opens com.example.browser to javafx.fxml;
    exports com.example.browser;
}