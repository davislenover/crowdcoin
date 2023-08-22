package com.crowdcoin.loginBoard;

import com.crowdcoin.format.Defaults;
import com.crowdcoin.networking.connections.InternetConnection;
import com.crowdcoin.networking.sqlcom.SQLData;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

// Custom Imports
import com.crowdcoin.security.Credentials;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


public class LoginController {

    // Declare Objects for use in Scenebuilder by their type and ID (as defined in Scenebuilder)
    @FXML
    private TextField loginUsername;
    @FXML
    private PasswordField loginPassword;
    @FXML
    private Button loginButton;
    @FXML
    private Text messageField;
    @FXML
    private ProgressIndicator loadingAnimation;

    public static Boolean loginPassed = false;
    @FXML
    protected void onLoginButtonClick() {

        // Create new credentials object and store the username and password as inputted into the text fields
        Credentials loginInfo = new Credentials(loginUsername.getText(), loginPassword.getText());


        // Call subroutine to disable login (to avoid user from inputting while their current fields are processing)
        disableLogin(true);

        // Display message to let user know login is being attempted
        displayMessage("Logging in...", Color.GRAY);

        // Test Connections - TO BE REVAMPED! ------

        // The problem here is that SQLConnection completely hangs the main JavaFX UI thread so there is no time to update the scene to say "Logging in..." and disabling the buttons, you just get a frozen screen!
        // To solve this, we handle the sql connection in a separate thread, different from the main JavaFX UI thread
        // Create new thread
        Thread sqlConnection = new Thread(() -> {

            // First, check if there is a connection to the internet
            // Create another thread and run it
            InternetConnection internetCheck = new InternetConnection();

            // Wait for internet check to finish
            try {

                internetCheck.join();

            } catch (Exception e) {

                // TODO Error

            }

            // Check result
            if (internetCheck.isOnline()) {

                try {

                    // If connected, attempt login
                    // "jdbc:mysql://192.168.2.56:3306/coinbase"
                    SQLData.sqlConnection = new SQLConnection("jdbc:mysql://127.0.0.1:3306/sys",loginInfo.getUsername(), loginInfo.getPassword());

                    // If no errors, then we can continue as the user has successfully logged in
                    displayMessage(Defaults.goodLogin, Color.GREEN);

                    // Set login info
                    SQLData.credentials = loginInfo;

                    // Get current stage
                    Stage login = (Stage) loginButton.getScene().getWindow();

                    // Wait a moment before opening main window
                    try {
                        Thread.sleep(1500);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }

                    // To close the window outside the current JavaFX thread, we must call a runlater
                    Platform.runLater(() -> {

                        loginPassed=true;
                        // Fire window close request event, this will trigger the event code in loginboard
                        login.fireEvent(new WindowEvent(login, WindowEvent.WINDOW_CLOSE_REQUEST));

                    });

                } catch (Exception exception) {

                    // Note since we are running the connection in a separate thread, all logic behind it must also be in the thread

                    // Get error
                    String errorMessage = exception.getMessage();

                    // Check possible errors (we only expect invalid credentials)

                    // Invalid credentials
                    if (errorMessage.contains("Access denied")) {
                        disableLogin(false);
                        displayMessage(Defaults.invalidCredentials, Color.RED);

                        // Internet but the server is not responding
                    } else if (errorMessage.contains("The driver has not received any packets from the server")) {
                        disableLogin(false);
                        displayMessage(Defaults.invalidServer, Color.RED);

                        // Any other error
                    } else {
                        // We need to throw this error to our ErrorHandler class as it's unexpected
                        disableLogin(false);
                        displayMessage(Defaults.abstractLoginError, Color.RED);
                        System.out.println(errorMessage);
                        System.out.println(exception.getCause());
                    }

                }

            } else {

                // Display no connection error
                disableLogin(false);
                displayMessage(Defaults.noConnection, Color.RED);

            }


        });

        // After defining the thread, execute it
        sqlConnection.start();

        // -----


    }

    // Subroutine for displaying messages to user on login screen
    public void displayMessage(String message, Color color) {

        // Set styles and align text to center
        messageField.setFill(color);
        messageField.setFont(new Font(Defaults.fontSize));
        messageField.setTextAlignment(TextAlignment.CENTER);
        // Display message with styles applied
        messageField.setText(message);

    }

    // Subroutine to disable login input
    public void disableLogin(Boolean disable) {

        if (disable) {
            // Clear TextFields and disable button/input
            loginUsername.clear();
            loginUsername.setDisable(true);
            loginPassword.clear();
            loginPassword.setDisable(true);
            loginButton.setDisable(true);
            // Set loading animation to visible
            loadingAnimation.setVisible(true);


        } else {
            // Clear TextFields and enable button/input
            loginUsername.clear();
            loginUsername.setDisable(false);
            loginPassword.clear();
            loginPassword.setDisable(false);
            loginButton.setDisable(false);
            loadingAnimation.setVisible(false);

        }

    }

}