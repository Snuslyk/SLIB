module com.github.Snuslyk.slib {
    requires javafx.fxml;
    requires java.sql;
    requires java.persistence;
    requires org.hibernate.orm.core;
    requires java.naming;
    requires com.sun.istack.runtime;
    requires com.dlsc.gemsfx;
    requires java.transaction;
    requires org.apache.commons.lang3;
    requires org.dhatim.fastexcel.reader;


    opens com.github.Snuslyk.slib to javafx.fxml;
    exports com.github.Snuslyk.slib;
    exports com.github.Snuslyk.slib.electives;
    opens com.github.Snuslyk.slib.electives to javafx.fxml;
}