package com.crowdcoin.networking.connections;

import com.ratchet.threading.TaskException;
import com.ratchet.threading.VoidTask;

import java.net.URI;
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
            URI url = new URI("https://google.ca");
            URLConnection connection = url.toURL().openConnection();
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
