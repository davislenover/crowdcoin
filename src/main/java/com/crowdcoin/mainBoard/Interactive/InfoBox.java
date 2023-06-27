package com.crowdcoin.mainBoard.Interactive;

import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Text;

/**
 * An info box for showing messages under InputFields. To be used within InputField classes for lower-level means
 */
public class InfoBox {

    private Text infoText;

    private int infoTranslateX = 40;
    private int infoTranslateY = 25;
    private int infoWrappingWidth = 180;

    public InfoBox(String text) {
        this.infoText = new Text(text);
        // Set id to check for duplicate when re-applying info box
        this.infoText.setId(text);
    }

    /**
     * Apply an InfoBox to a target InputField object. InputFields already handle application via show and hide methods
     * @param targetField the target InputField object
     */
    public void applyInfoBox(InputField targetField) {

        // Get the given pane from the InputField (NOT to be confused with InteractivePane)
        Pane targetPane = targetField.getPane();

        // Remove previous info box (if it exists)
        this.removeInfoBox(targetField);

        // Add new text to Pane and apply transformations to text
        targetPane.getChildren().add(this.infoText);
        this.infoText.setTranslateX(this.infoTranslateX);
        this.infoText.setTranslateY(this.infoTranslateY);
        this.infoText.setWrappingWidth(this.infoWrappingWidth);

    }

    /**
     * Removes the InfoBox from a target InputField object. InputFields already handle application via show and hide methods
     * @param targetField the target InputField object
     */
    public void removeInfoBox(InputField targetField) {
        // Get the given pane from the InputField (NOT to be confused with InteractivePane)
        Pane targetPane = targetField.getPane();

        // Remove previous info box (if it exists)
        int targetIndex = 0;
        for (Node curNode : targetPane.getChildren()) {
            if (curNode.getId() != null && curNode.getId().equals(this.infoText.getId())) {
                targetPane.getChildren().remove(targetIndex);
                break;
            }
            targetIndex++;
        }

    }

    /**
     * Set new message in InfoBox. This change will not take effect on-screen until re-application of InfoBox is invoked
     * @param text the text as a String object
     */
    public void setInfoText(String text) {
        this.infoText.setText(text);
        // Set id to check for duplicate when re-applying info box
        this.infoText.setId(text);
    }

    /**
     * Sets the X translation relative to the center of the InputField. This change will not take effect on-screen until re-application of InfoBox is invoked
     * @param X the translation as an integer
     */
    public void setInfoTranslateX(int X) {
        this.infoTranslateX = X;
    }

    /**
     * Sets the Y translation relative to the center of the InputField. This change will not take effect on-screen until re-application of InfoBox is invoked
     * @param Y the translation as an integer
     */
    public void setInfoTranslateY(int Y) {
        this.infoTranslateY = Y;
    }

    /**
     * Sets the wrapping width of the info text. This change will not take effect on-screen until re-application of InfoBox is invoked
     * @param width the wrapping width as an integer
     */
    public void setInfoWrappingWidth(int width) {
        this.infoWrappingWidth = width;
    }


}
