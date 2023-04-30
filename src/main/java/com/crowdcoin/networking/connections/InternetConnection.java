package com.crowdcoin.networking.connections;

import java.net.URL;
import java.net.URLConnection;

public class InternetConnection extends Thread {

    // Boolean for other classes to reference
    private Boolean connectedToInternet;

    public InternetConnection() {
        // Check connection on creation
        this.start();
    }

    // Call start() instead of run()
    // run() will execute this method on the thread that created this object whereas start() will use a new thread
    public void run() {

        try {

            // Attempt to connect to Google Canada
            URL url = new URL("https://google.ca");
            URLConnection connection = url.openConnection();
            connection.connect();
            connectedToInternet = true;

        } catch (Exception e) {

            // If not connected to the internet, an exception will be thrown
            connectedToInternet = false;

        }

    }

    /**
     * Checks if the computer is connected to the internet
     * @return true if a connection exists, false otherwise
     */
    public boolean isOnline() {
        return connectedToInternet;
    }

}
