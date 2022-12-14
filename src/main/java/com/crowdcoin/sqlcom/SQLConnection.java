package com.crowdcoin.sqlcom;


import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.network.NullConnectionException;
import com.crowdcoin.format.Defaults;
import java.sql.*;

public class SQLConnection {
    // Declare connection and credential info
    Connection connection;
    String address;
    String databaseUsername;
    String databasePassword;

    // Constructor to get MySQL Connection
    // This class can throw exceptions
    public SQLConnection(String address, String databaseUsername, String databasePassword) throws Exception {

        int currentAttemptNumber = 1;

        // Try to connect multiple times
        for(;;) {

            // Attempt a connection
            try {
                this.connection = DriverManager.getConnection(address, databaseUsername, databasePassword);
                this.address = address;
                this.databaseUsername = databaseUsername;
                this.databasePassword = databasePassword;
                break;

            } catch (Exception e) {

                // Check if it was a timeout error
                if (e.getClass().getSimpleName().equals("java.sql.SQLTimeoutException")) {

                    // Check number of attempts already
                    if (currentAttemptNumber >= Defaults.maxConnectionAttempts) {

                        // If connection fails after x amount of attempts, set variables appropriately and get error message
                        throw new SQLTimeoutException("SQLTimeoutException -> The driver has not received any packets from the server");

                    } else {

                        // If we have not reached max attempts, add 1 to currentAttemptNumber and retry connection
                        currentAttemptNumber++;

                    }

                } else {

                    // If it is any other error, then the error may be unexpected
                    throw new Exception(e);

                }

            }

        }

    }

    // Send commands to server
    // Throws FailedQueryException if the Query fails
    // This class is generic and return a generic type that extends the Object class
    public <T extends Object> T sendQuery(String query) throws Exception {

        Statement statement = null;
        ResultSet result = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.executeQuery(query);

        } catch (Exception exception) {
            // If an exception occurs, throw custom failed query exception
            throw new FailedQueryException(query, exception);

            // Attempt to close connections regardless of outcome to free resources
        } finally {
            try {
                statement.close();
                result.close();
                // Ignore any exceptions
            } catch (Exception ignore) {
            }

            statement = null;
            result = null;

        }

        // Testing stuff
        Integer test = 1;
        return (T) test;
    }

    // Getters

}
