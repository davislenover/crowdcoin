package com.crowdcoin.networking.sqlcom;


import com.crowdcoin.exceptions.network.FailedQueryException;
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

                // Catch timeout
            } catch (SQLTimeoutException e) {

                // Check number of attempts already
                if (currentAttemptNumber >= Defaults.maxConnectionAttempts) {

                    // TODO why?
                    // If connection fails after x amount of attempts, set variables appropriately and get error message
                    throw new SQLTimeoutException("SQLTimeoutException -> The driver has not received any packets from the server");

                } else {

                    // If we have not reached max attempts, add 1 to currentAttemptNumber and retry connection
                    currentAttemptNumber++;

                }

                // Catch if access to url failed
            } catch (SQLException e) {

                // TODO

            }

        }

    }

    /**
     * Execute query on database
     * @param query a string containing the query to execute
     * @return a ResultSet object containing the result of the query execution (data)
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause
     */
    public ResultSet sendQuery(String query) throws FailedQueryException {

        Statement statement = null;
        ResultSet result = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.executeQuery(query);

            return result;

        } catch (Exception exception) {
            try {
                // Attempt to close connections if failed
                statement.close();
                result.close();
                // Ignore any exceptions
            } catch (Exception ignore) {
            }
            // If an exception occurs, throw custom failed query exception
            throw new FailedQueryException(query, exception);
        } finally {

            statement = null;
            result = null;

        }
    }

    /**
     * Execute query on database
     * @param query a string containing the query to execute
     * @return a ResultSetMetaData object containing the metaData of the query execution (data)
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause
     */
    public ResultSetMetaData sendQueryGetMetaData(String query) throws FailedQueryException {

        Statement statement = null;
        ResultSet result = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.executeQuery(query);
            // Get metadata
            ResultSetMetaData metaData = result.getMetaData();
            // Close streams
            statement.close();
            result.close();
            return metaData;

        } catch (Exception exception) {
            try {
                // Attempt to close connections if failed
                statement.close();
                result.close();
                // Ignore any exceptions
            } catch (Exception ignore) {
            }
            // If an exception occurs, throw custom failed query exception
            throw new FailedQueryException(query, exception);
        } finally {

            statement = null;
            result = null;

        }
    }

}
