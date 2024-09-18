module com.github.Snuslyk.slib {
    requires javafx.controls;
    requires javafx.fxml;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires java.persistence;



    opens com.github.Snuslyk.slib to javafx.fxml;
    exports com.github.Snuslyk.slib;
}