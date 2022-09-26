module com.crowdcoin.main {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires com.almasb.fxgl.all;
    requires java.sql;

    opens com.crowdcoin.main to javafx.fxml;
    exports com.crowdcoin.main;
    exports com.crowdcoin.security;
    opens com.crowdcoin.security to javafx.fxml;
}