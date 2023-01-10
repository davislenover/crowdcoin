package com.crowdcoin.main;

import com.crowdcoin.sqlcom.SQLData;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class MainBoard {

    // Subroutine to start main window after login
    public static void start() throws Exception {

        // Create a new stage and get the mainboard file
        Stage stage = new Stage();
        FXMLLoader fxmlLoader = new FXMLLoader(MainBoard.class.getResource("mainBoard.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
        stage.setTitle("CrowdCoin - " + SQLData.credentials.getUsername());
        stage.setScene(scene);
        stage.setResizable(true);
        stage.show();

        // Initialize coin list
        MainBoardController controller = (MainBoardController) fxmlLoader.getController();
        controller.initializeList();

    }
}
