package com.crowdcoin.networking.connections;

import com.crowdcoin.threading.TaskException;
import com.crowdcoin.threading.VoidTask;

import java.net.URL;
import java.net.URLConnection;

public class InternetConnection extends VoidTask {

    // Boolean for other classes to reference
    private Boolean connectedToInternet;

    /**
     * Checks if the computer is connected to the internet
     * @return true if a connection exists, false otherwise
     */
    public boolean isOnline() {
        return connectedToInternet;
    }

    private void checkConnection() throws Exception {
        try {

            // Attempt to connect to Google Canada
            URL url = new URL("https://google.ca");
            URLConnection connection = url.openConnection();
            connection.connect();
            connectedToInternet = true;
            connection.getInputStream().close();

        } catch (Exception e) {

            // If not connected to the internet, an exception will be thrown
            connectedToInternet = false;
            throw e;

        }
    }

    @Override
    public Void runTask() throws TaskException {
        try {
            checkConnection();
        } catch (Exception exception) {
            throw new TaskException(exception);
        }
        return null;
    }
}
