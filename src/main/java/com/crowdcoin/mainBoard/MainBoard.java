package com.crowdcoin.mainBoard;

import com.crowdcoin.exceptions.handler.ExceptionGuardian;
import com.crowdcoin.exceptions.handler.GeneralExceptionHandler;
import com.crowdcoin.networking.sqlcom.SQLData;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainBoard {

    private static Stage mainStage = new Stage();

    // Subroutine to start main window after login
    public static void start() throws Exception {

        // Create a new stage and get the mainboard file
        ExceptionGuardian.setRootStage(mainStage);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader(MainBoard.class.getResource("mainBoard.fxml"));
            Scene scene = new Scene(fxmlLoader.load(), 1280, 720);
            mainStage.setTitle("CrowdCoin - " + SQLData.credentials.getUsername());
            mainStage.setScene(scene);
            mainStage.setResizable(true);
            mainStage.show();

            // Initialize coin list
            MainBoardController controller = (MainBoardController) fxmlLoader.getController();
            controller.initializeList();
        } catch (Exception e) {
            ExceptionGuardian<Exception> guardian = new ExceptionGuardian<>(new GeneralExceptionHandler());
            guardian.handleException(e);
        }

    }

    public static Stage getMainStage() {
        return mainStage;
    }

}
