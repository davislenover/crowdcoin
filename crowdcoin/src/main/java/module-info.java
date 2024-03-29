module com.crowdcoin.main {
    requires javafx.controls;
    requires javafx.fxml;
    //requires javafx.web;

    //requires org.controlsfx.controls;
    //requires com.dlsc.formsfx;
    //requires net.synedra.validatorfx;
    //requires org.kordamp.ikonli.javafx;
    //requires org.kordamp.bootstrapfx.core;
    //requires com.almasb.fxgl.all;
    requires java.sql;
    requires org.apache.logging.log4j;
    requires com.ratchet;

    opens com.crowdcoin.loginBoard to javafx.fxml;
    exports com.crowdcoin.loginBoard;
    exports com.crowdcoin.security;
    opens com.crowdcoin.security to javafx.fxml;
    exports com.crowdcoin.mainBoard;
    opens com.crowdcoin.mainBoard to javafx.fxml;
    exports com.crowdcoin.mainBoard.grade;
    opens com.crowdcoin.mainBoard.grade to javafx.fxml;
}