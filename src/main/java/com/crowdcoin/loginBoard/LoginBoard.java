package com.crowdcoin.loginBoard;

import com.crowdcoin.mainBoard.MainBoard;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.io.IOException;



public class LoginBoard extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(LoginBoard.class.getResource("loginBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 600, 400);
        stage.setTitle("CrowdCoin - Login");
        stage.setScene(scene);
        stage.setResizable(false);
        stage.show();

        // Set close request
        // Without this, the user could close the window while attempting to login but the connection would still be in the background
        // This also triggers when a login was successful
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {

                // Check if login passed is true, this means the program is transitioning to the main scene
                if (LoginController.loginPassed) {

                    // Open main scene
                    try {
                        MainBoard.start();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }

                    // Close current login window
                    stage.close();

                } else {

                    // Exit
                    Platform.exit();
                    System.exit(0);

                }

            }
        });

    }

    public static void main(String[] args) {
        launch();
    }

}