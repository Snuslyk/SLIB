module com.github.Snuslyk.slib {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.desktop;
    requires jakarta.persistence;


    opens com.github.Snuslyk.slib to javafx.fxml;
    exports com.github.Snuslyk.slib;
}