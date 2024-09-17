module com.lib.slib {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.persistence;



    opens com.lib.slib to javafx.fxml;
    exports com.lib.slib;
}