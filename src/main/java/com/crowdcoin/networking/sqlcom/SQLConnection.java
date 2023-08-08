package com.crowdcoin.networking.sqlcom;


import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.format.Defaults;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;

import java.sql.*;
import java.util.EmptyStackException;
import java.util.Stack;

public class SQLConnection {
    // Declare connection and credential info
    private Connection connection;
    private String address;
    private String databaseUsername;
    private String databasePassword;
    private String schemaName;

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

        String schemaBackwards = "";
        char[] addressCharArray = this.address.toCharArray();
        Stack<Character> schemaNameStack = new Stack<>();
        // Go backwards through address and copy all char's up to the last '/'. This gives the schema name
        for (int index = addressCharArray.length-1; index >= 0; index--) {
            char curChar = addressCharArray[index];
            if (curChar != '/') {
                schemaNameStack.push(addressCharArray[index]);
            } else {
                break;
            }
        }
        // The schema comes out backwards so reverse it
        this.schemaName = "";
        while (!schemaNameStack.isEmpty()) {
            this.schemaName+=schemaNameStack.pop();
        }
    }

    /**
     * Gets the schema name currently connected to
     * @return the schema name as a String
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     * Execute query on database
     * @param query a string containing the query to execute
     * @return a ResultSet object containing the result of the query execution (data)
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause
     */
    public ResultSet sendQuery(QueryBuilder query) throws FailedQueryException {

        Statement statement = null;
        ResultSet result = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.executeQuery(query.getQuery());

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
            throw new FailedQueryException(query.getQuery(), exception);
        } finally {

            statement = null;
            result = null;

        }
    }

    /**
     * Execute manipulative on database (i.e., write to table, any command where a result is not required)
     * @param query a string containing the query to execute
     * @return true if the first result is a ResultSet object; false if it is an update count or there are no result
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause
     */
    public boolean executeQuery(QueryBuilder query) throws FailedQueryException {

        Statement statement = null;
        boolean result = false;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.execute(query.getQuery());

            return result;

        } catch (Exception exception) {
            try {
                // Attempt to close connections if failed
                statement.close();
                // Ignore any exceptions
            } catch (Exception ignore) {
            }
            // If an exception occurs, throw custom failed query exception
            throw new FailedQueryException(query.getQuery(), exception);
        } finally {
            statement = null;
        }
    }

    /**
     * Execute query on database
     * @param query a string containing the query to execute
     * @return a ResultSetMetaData object containing the metaData of the query execution (data)
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause
     */
    public ResultSetMetaData sendQueryGetMetaData(QueryBuilder query) throws FailedQueryException {

        Statement statement = null;
        ResultSet result = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.executeQuery(query.getQuery());
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
            throw new FailedQueryException(query.getQuery(), exception);
        } finally {

            statement = null;
            result = null;

        }
    }

}
