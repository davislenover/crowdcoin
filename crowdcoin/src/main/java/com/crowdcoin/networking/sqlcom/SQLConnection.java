package com.crowdcoin.networking.sqlcom;


import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.format.Defaults;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;
import com.crowdcoin.newwork.Query;

import java.sql.*;
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
                DriverManager.setLoginTimeout(6);
                this.connection = DriverManager.getConnection(address, databaseUsername, databasePassword);
                this.address = address;
                break;

                // Catch timeout
            } catch (SQLTimeoutException e) {

                // TODO

                // Catch if access to url failed
            } catch (SQLException e) {

                // Check number of attempts already
                if (currentAttemptNumber >= Defaults.maxConnectionAttempts) {

                    // TODO why?
                    // If connection fails after x amount of attempts, set variables appropriately and get error message
                    throw new SQLTimeoutException("SQLTimeoutException -> The driver has not received any packets from the server");

                } else {

                    // If we have not reached max attempts, add 1 to currentAttemptNumber and retry connection
                    currentAttemptNumber++;

                }

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
            statement = connection.createStatement();
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
            // TODO Error handling
            throw new FailedQueryException(query.getQuery(), exception);
        } finally {

            statement = null;
            result = null;

        }

    }

    /**
     * Execute query on database that returns some result (e.g., using SELECT). This method call is not transactional
     * @param query a QueryBuilder containing the query to execute
     * @return a ResultSet object containing the result of the query execution (data)
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause
     */
    public ResultSet sendQuery(Query query) throws FailedQueryException {

        Statement statement = null;
        ResultSet result = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = connection.createStatement();
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
            // TODO Error handling
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
    // TODO FIX RETURN
    public void executeQuery(QueryBuilder query) throws FailedQueryException {

        Statement statement = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = connection.createStatement();
            statement.executeUpdate(query.getQuery());
            // If no exception, commit the transaction
            connection.commit();

        } catch (Exception exception) {
            try {
                // Attempt to close connections if failed
                statement.close();
                // Ignore any exceptions
            } catch (Exception ignore) {
            }
            // If an exception occurs, throw custom failed query exception
            // TODO Error handling
            throw new FailedQueryException(query.getQuery(), exception);
        } finally {
            statement = null;
        }

    }

    /**
     * Execute manipulative (DML statement) on database (i.e., write to table, any command where a result is not required). This method call is transactional meaning a savepoint is set before the query is executed.
     * An attempt will be made to commit the query right after execution. If an execution occurs, one must call {@link SQLConnection#rollBack()} to rollback the failed query. One can also call {@link SQLConnection#rollBack()} if the query was determined to not be of use
     * @param query a QueryBuilder containing the query to execute
     * @return a number greater than 0 if the first result is a row count for the DML statement; 0 for statements that return nothing
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause.
     */
    // TODO FIX RETURN
    public void executeQuery(Query query) throws FailedQueryException {

        Statement statement = null;

        try {
            // Attempt to create the statements and execution of said statement
            statement = connection.createStatement();
            statement.executeUpdate(query.getQuery());
            // If no exception, commit the transaction
            connection.commit();

        } catch (Exception exception) {
            try {
                // Attempt to close connections if failed
                statement.close();
                // Ignore any exceptions
            } catch (Exception ignore) {
            }
            // If an exception occurs, throw custom failed query exception
            // TODO Error handling
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
        try {
            statement = connection.createStatement();

            for (QueryBuilder query : queries) {
                statement.executeUpdate(query.getQuery());
            }

            connection.commit();

        } catch (SQLException exception) {
            try {
                statement.close();
            } catch (SQLException exception2) {
            }
            // TODO handle exception
        } finally {
            statement = null;
        }

    }

    /**
     * Execute manipulative's (DML statements) on database (i.e., write to table, any command where a result is not required). This method call is transactional meaning a savepoint is set before the query group is executed.
     * NO attempt will be made to commit the queries right after all have been executed thus one can call this method multiple times to perform queries under the same transaction. If no exception occurs, one must call {@link SQLConnection#commitGroupQuery()} to commit all changes. If an exception occurs, one must call {@link SQLConnection#rollBack()} to rollback the failed query(s). One can also call {@link SQLConnection#rollBack()} if the queries were determined to not be of use (this MUST be done before committing)
     * @param queries a group of queries to execute
     * @throws SQLException if any query fails to execute
     */
    public void executeGroupQueryNoCommit(List<QueryBuilder> queries) throws SQLException {

        try {

            if (groupStatement == null) {
                groupStatement = connection.createStatement();
            }

            for (QueryBuilder query : queries) {
                groupStatement.executeUpdate(query.getQuery());
            }

        } catch (SQLException exception) {
            // TODO Handle exception
        }

    }

    public void commitGroupQuery() throws SQLException {
        try {
            if (groupStatement != null) {
                connection.commit();
                groupStatement.close();
                groupStatement = null;
            }
        } catch (SQLException exception) {
            // TODO Error handling
        }
    }

    /**
     * Rollback the database to the last set savepoint
     * @throws SQLException if a database access error occurs
     */
    public void rollBack() throws SQLException {
        try {
            connection.rollback();
            if (groupStatement != null) {
                groupStatement.close();
                groupStatement = null;
            }
        } catch (SQLException exception) {
            // TODO Error handling
        }
    }
}
