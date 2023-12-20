module com.ratchet {
    requires javafx.graphics;
    requires javafx.controls;
    exports com.ratchet;
    exports com.ratchet.observe;
    exports com.ratchet.threading;
    exports com.ratchet.interactive.input.validation;
    exports com.ratchet.interactive.input;
    exports com.ratchet.interactive;
    exports com.ratchet.exceptions.validation;
    exports com.ratchet.interactive.output;
    exports com.ratchet.interactive.submit;
    exports com.ratchet.window;
    exports com.ratchet.exceptions.handler;
    exports com.ratchet.exceptions.handler.strategies;
    exports com.ratchet.menu;
    exports com.ratchet.system;
    exports com.ratchet.threading.workers;
}