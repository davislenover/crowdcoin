package com.crowdcoin.sqlcom;


import com.crowdcoin.format.Defaults;
import java.sql.*;

public class SQLConnection {

    // Declare connection and credential info
    Connection connection;
    String address;
    String databaseUsername;
    String databasePassword;

    Boolean connectionFailed;
    Exception connectionError;

    // Constructor to get MySQL Connection
    public SQLConnection(String address, String databaseUsername, String databasePassword) {

        int currentAttemptNumber = 1;

        // Try to connect multiple times
        for(;;) {

            // Attempt a connection
            try {
                this.connection = DriverManager.getConnection(address, databaseUsername, databasePassword);
                this.address = address;
                this.databaseUsername = databaseUsername;
                this.databasePassword = databasePassword;
                this.connectionFailed = false;
                break;

            } catch (Exception e) {

                // Check if it was a timeout error
                if (e.getClass().getSimpleName() == "java.sql.SQLTimeoutException") {

                    // Check number of attempts already
                    if (currentAttemptNumber >= Defaults.maxConnectionAttempts) {

                        // If connection fails after x amount of attempts, set variables appropriately and get error message
                        this.connectionFailed = true;
                        this.connectionError = e;
                        break;

                    } else {

                        // If we have not reached max attempts, add 1 to currentAttemptNumber and retry connection
                        currentAttemptNumber++;

                    }

                } else {

                    // If it is any other error, then the error may be unexpected
                    this.connectionFailed = true;
                    this.connectionError = e;
                    break;

                }

            }

        }

    }

    // Getters
    public Boolean getConnectionFailed() {
        return connectionFailed;
    }

    public Exception getConnectionError() {
        return connectionError;
    }

}
