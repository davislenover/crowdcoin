package com.crowdcoin.mainBoard.export.ExportBehaviour;

import com.crowdcoin.mainBoard.Interactive.InteractivePane;
import com.crowdcoin.mainBoard.window.PopWindow;

import java.util.List;

public interface ExportBehaviour {

    /**
     * Gets the columns for writing to export file
     * @return a List of Strings, each index represents a column
     */
    List<String> getColumns();

    /**
     * Gets all entries to write to the export file
     * @return a List with a List of Strings, each List within is an entry for the Export file
     */
    List<List<String>> getEntries();


     /**
     * Apply the input fields needed for a given InteractivePane object, set any Window preferences.
     * This method assumes the given InteractivePane does not already contain the specified InputFields within the method. This method will NOT call any update to prompt a visual change to the window.
     */
    void applyInputFieldsOnWindow();

}
