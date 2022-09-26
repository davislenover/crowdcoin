package com.crowdcoin.sqlcom;

import java.net.URL;
import java.net.URLConnection;

public class InternetConnectionCheck extends Thread {

    // Boolean for other classes to reference
    public Boolean connectedToInternet;

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

    };

}
