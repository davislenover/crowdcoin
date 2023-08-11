package com.crowdcoin.networking.sqlcom;


import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.format.Defaults;
import com.crowdcoin.mainBoard.Interactive.input.validation.PaneValidator;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;

import java.sql.*;
import java.util.EmptyStackException;
import java.util.List;
import java.util.Stack;

public class SQLConnection {
    // Declare connection and credential info
    private Connection connection;
    private String address;
    private String schemaName;
    private Statement groupStatement = null;

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
        // Set AutoCommit to false, enable rollbacks
        this.connection.setAutoCommit(false);
    }

    /**
     * Gets the schema name currently connected to
     * @return the schema name as a String
     */
    public String getSchemaName() {
        return this.schemaName;
    }

    /**
     * Execute query on database that returns some result (e.g., using SELECT). This method call is not transactional
     * @param query a QueryBuilder containing the query to execute
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
     * Execute manipulative (DML statement) on database (i.e., write to table, any command where a result is not required). This method call is transactional meaning a savepoint is set before the query is executed.
     * An attempt will be made to commit the query right after execution. If an execution occurs, one must call {@link SQLConnection#rollBack()} to rollback the failed query. One can also call {@link SQLConnection#rollBack()} if the query was determined to not be of use
     * @param query a QueryBuilder containing the query to execute
     * @return a number greater than 0 if the first result is a row count for the DML statement; 0 for statements that return nothing
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause.
     */
    public int executeQuery(QueryBuilder query) throws FailedQueryException {

        Statement statement = null;
        int result = 0;

        try {
            // Attempt to create the statements and execution of said statement
            statement = this.connection.createStatement();
            // Get result of statement
            result = statement.executeUpdate(query.getQuery());
            // If no exception, commit the transaction
            this.connection.commit();

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
     * Execute manipulative's (DML statements) on database (i.e., write to table, any command where a result is not required). This method call is transactional meaning a savepoint is set before the query group is executed.
     * An attempt will be made to commit the queries right after all have been executed. If an exception occurs, one must call {@link SQLConnection#rollBack()} to rollback the failed query(s). One can also call {@link SQLConnection#rollBack()} if the queries were determined to not be of use
     * @param queries a group of queries to execute
     * @throws SQLException if any query fails to execute
     */
    public void executeGroupQuery(List<QueryBuilder> queries) throws SQLException {

        Statement statement = null;
        int result = 0;

        try {
            statement = this.connection.createStatement();

            for (QueryBuilder query : queries) {
                statement.executeUpdate(query.getQuery());
            }

            this.connection.commit();

        } catch (SQLException exception) {
            try {
                statement.close();
            } catch (SQLException exception2) {
            }
            throw exception;
        } finally {
            statement = null;
        }

    }

    /**
     * Execute manipulative's (DML statements) on database (i.e., write to table, any command where a result is not required). This method call is transactional meaning a savepoint is set before the query group is executed.
     * NO attempt will be made to commit the queries right after all have been executed. If no exception occurs, one must call {@link SQLConnection#commitGroupQuery()}. If an exception occurs, one must call {@link SQLConnection#rollBack()} to rollback the failed query(s). One can also call {@link SQLConnection#rollBack()} if the queries were determined to not be of use
     * @param queries a group of queries to execute
     * @throws SQLException if any query fails to execute
     */
    public void executeGroupQueryNoCommit(List<QueryBuilder> queries) throws SQLException {

        int result = 0;

        try {
            this.groupStatement = this.connection.createStatement();

            for (QueryBuilder query : queries) {
                this.groupStatement.executeUpdate(query.getQuery());
            }

        } catch (SQLException exception) {
            throw exception;
        }

    }

    public void commitGroupQuery() throws SQLException {
        if (this.groupStatement != null) {
            this.connection.commit();
            this.groupStatement.close();
            this.groupStatement = null;
        }
    }

    /**
     * Rollback the database to the last set savepoint
     * @throws SQLException if a database access error occurs
     */
    public void rollBack() throws SQLException {
        this.connection.rollback();
        if (this.groupStatement != null) {
            this.groupStatement.close();
            this.groupStatement = null;
        }
    }



}
