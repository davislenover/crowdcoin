package com.crowdcoin.mainBoard.Interactive;

import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Region;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class TextFieldCombo {

    private Pane containnerPane;
    private TextField textField;
    private Text fieldHeader;
    private Text fieldDescription;


    public TextFieldCombo(String header, String description) {

        this.textField = new TextField();
        this.fieldHeader = new Text(header);
        this.fieldDescription = new Text(description);

        this.containnerPane = new Pane();
        this.containnerPane.getChildren().addAll(this.textField,this.fieldHeader,this.fieldDescription);

    }

    public void setupForPane(GridPane targetPane) {

        this.containnerPane.setMinWidth(targetPane.getWidth()/targetPane.getColumnCount());
        this.containnerPane.setMinHeight(targetPane.getHeight()/4);

        this.textField.setLayoutX(this.containnerPane.getMinWidth());
        this.textField.setLayoutY(this.containnerPane.getMinHeight()/4);

        this.fieldHeader.setX(this.textField.getLayoutX()-150);
        this.fieldHeader.setY(this.textField.getLayoutY()-6);
        this.fieldHeader.setFont(Font.font("verdana", FontWeight.NORMAL, FontPosture.REGULAR, 14));

        this.fieldDescription.setX(this.textField.getLayoutX()-150);
        this.fieldDescription.setY(this.textField.getLayoutY()+6);
        this.fieldDescription.setWrappingWidth(150);


    }

    public Pane getPane() {
        return this.containnerPane;
    }




}
