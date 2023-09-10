package com.crowdcoin.loginBoard;

import com.crowdcoin.format.Defaults;
import com.ratchet.observe.Observer;
import com.ratchet.observe.TaskEvent;
import com.ratchet.observe.TaskEventType;
import com.crowdcoin.networking.connections.InternetConnection;
import com.crowdcoin.networking.sqlcom.SQLData;
import com.ratchet.threading.TaskException;
import com.ratchet.threading.TaskManager;
import com.ratchet.threading.TaskTools;
import com.ratchet.threading.VoidTask;
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


public class LoginController implements Observer<TaskEvent,String> {

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
    private static TaskManager taskMgr = TaskTools.getTaskManager();
    private static InternetConnection connection;
    private static String connectionTaskId = "connectionCheck";
    private static String loginTaskId = "loginTask";
    private static Credentials loginInfo = null;
    @FXML
    protected void onLoginButtonClick() {

        // Create new credentials object and store the username and password as inputted into the text fields
        loginInfo = new Credentials(loginUsername.getText(), loginPassword.getText());


        // Call subroutine to disable login (to avoid user from inputting while their current fields are processing)
        disableLogin(true);

        // Display message to let user know login is being attempted
        displayMessage("Logging in...", Color.GRAY);

        // The problem here is that SQLConnection completely hangs the main JavaFX UI thread so there is no time to update the scene to say "Logging in..." and disabling the buttons, you just get a frozen screen!
        // To solve this, we handle the sql connection in a Task (i.e., a separate Thread), different from the main JavaFX UI thread
        connection = new InternetConnection();
        connection.setTaskId(connectionTaskId);
        taskMgr.addObserver(this);
        taskMgr.addTask(connection);
        taskMgr.runNextTask();

        // Then await TaskEvent in update()

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

    @Override
    public void removeObserving() {
        taskMgr.removeObserver(this);
    }

    @Override
    public void update(TaskEvent event) {

        TaskEventType eventType = event.getEventType();
        String taskId = event.getEventData().get(0);

        if (eventType.equals(TaskEventType.TASK_END)) {

            if (taskId.equals(connectionTaskId)) {

                if (connection.isOnline()) {
                    
                    // If connected to a network, attempt connection to database
                    setupSQLConnection.setTaskId(loginTaskId);
                    taskMgr.addTask(setupSQLConnection);
                    taskMgr.runNextTask();
                }

            } else if (taskId.equals(loginTaskId)) {

                // If this code executes, then connecting to the database was successful

                // Set login info
                SQLData.credentials = loginInfo;

                // Get current stage
                Stage login = (Stage) loginButton.getScene().getWindow();

                // To close the window outside the current JavaFX thread, we must call a runlater
                Platform.runLater(() -> {

                    loginPassed=true;
                    // Fire window close request event, this will trigger the event code in loginboard
                    login.fireEvent(new WindowEvent(login, WindowEvent.WINDOW_CLOSE_REQUEST));

                });

            }

        } else if (eventType.equals(TaskEventType.TASK_FAILED)) {

            if (taskId.equals(connectionTaskId)) {

                // Display no connection error
                disableLogin(false);
                displayMessage(Defaults.noConnection, Color.RED);

            } else if (taskId.equals(loginTaskId)) {

                // Note since we are running the connection in a separate thread, all logic behind it must also be in the thread

                // Get error
                String errorMessage = taskMgr.getFailedTaskException().getRootException().getMessage();

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
                    System.out.println(taskMgr.getFailedTaskException().getRootException().getCause());
                }
            }
        }
    }

    // VoidTask declaration for setting up the SQLConnection to use when login button is pressed
    private static VoidTask setupSQLConnection = new VoidTask() {
        @Override
        public Void runTask() throws TaskException {
            try {
                // If connected, attempt login
                // "jdbc:mysql://127.0.0.1:3306/sys"
                // "jdbc:mysql://192.168.2.56:3306/coinbase"
                SQLData.sqlConnection = new SQLConnection("jdbc:mysql://192.168.2.56:3306/coinbase",loginInfo.getUsername(), loginInfo.getPassword());

                Platform.runLater(() -> {
                    // If no errors, then we can continue as the user has successfully logged in
                    displayMessage(Defaults.goodLogin, Color.GREEN);
                });

                // Wait a moment before returning
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

            } catch (Exception exception) {
                throw new TaskException(exception);
            }
            return null;
        }
    };
}