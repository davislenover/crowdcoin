package com.crowdcoin.maindisplay;

import com.crowdcoin.exceptions.network.NullConnectionException;
import com.crowdcoin.sqlcom.SQLConnection;
import com.crowdcoin.sqlcom.SQLData;

import java.sql.Connection;

// Class for the right hand display in the MainBoard
public class rightDisp {

    // an integer defines the current status of the right display
    // helps to keep track of what is currently on the display
    public static int status = 0;

    // on new entry click
    // method to setup right display to enter in a new coin
    public static void newEntry() throws Exception {

        // Check if we are setting up from status 0 (i.e. nothing was on screen)
        if (status == 0) {
            // First, get table data from sql server
            // i.e. what fields we need in the form
            try {
                // Attempt to get the current connection (if not null)
                SQLConnection currentConnection = SQLData.getSqlConnection();
            } catch (NullConnectionException exception) {

            }
        }
    }

}
