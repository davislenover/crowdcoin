package com.crowdcoin.newwork;

import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;
import com.ratchet.threading.QuantifiableTask;
import com.ratchet.threading.TaskException;
import com.ratchet.threading.VoidTask;
import com.ratchet.threading.workers.Future;
import com.ratchet.threading.workers.PriorityWorker;
import com.ratchet.threading.workers.ThreadingWorker;

import java.sql.ResultSet;

/**
 * Handles communication between a program and an SQL Database. Controls access to a {@link SQLConnection} object
 */
public class SQLDatabase {

    private SQLConnection connection;
    private ThreadingWorker queryExecuter;
    private int taskCapacity = 10;

    public SQLDatabase(SQLConnection connection) {
        this.connection = connection;
        this.queryExecuter = new PriorityWorker(taskCapacity);
    }

    /**
     * Execute a query with a return for a {@link QueryResult}. Query will be processed on another Thread by a {@link PriorityWorker} and as such, this method may block if the capacity for the
     * {@link PriorityWorker} is full
     * @param query the specified {@link QueryBuilder} to execute
     * @return a {@link Future} object holding the {@link QueryResult} when the query finishes. It is recommended to observe said {@link Future} object to be notified when the query completes
     */
    public Future executeQuantifiedQuery(QueryBuilder query) {
        return this.queryExecuter.performTask(new QuantifiableTask<QueryResult>() {
            @Override
            public QueryResult runTask() throws TaskException {
                try {
                    ResultSet resultSet = connection.sendQuery(query);
                    QueryResult returnResult = new QueryResult(resultSet);
                    resultSet.close();
                    return returnResult;
                } catch (Exception e) {
                    throw new TaskException(e);
                }
            }
        });
    }

    /**
     * Execute a query without a return. Query will be processed on another Thread by a {@link PriorityWorker} and as such, this method may block if the capacity for the
     * {@link PriorityWorker} is full
     * @param query the specified {@link QueryBuilder} to execute
     * @return a {@link Future} object holding a {@link Void} object when the query finishes. It is recommended to observe said {@link Future} object to be notified when the query completes
     */
    public Future executeQuery(QueryBuilder query) {
        return this.queryExecuter.performTask(new VoidTask() {
            @Override
            public Void runTask() throws TaskException {
                try {
                    connection.executeQuery(query);
                    return null;
                } catch (Exception e) {
                    throw new TaskException(e);
                }
            }
        });
    }

}
