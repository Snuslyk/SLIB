module com.lib.slib {
    requires javafx.controls;
    requires javafx.fxml;


    opens com.lib.slib to javafx.fxml;
    exports com.lib.slib;
}