package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

import java.sql.SQLException;

/**
 * A class which allows for DML statements to be executed in batches. Allows for much safer query execution as automatic rollback occurs for the entire batch. Implementing classes should hold both queries and events for any query added to the group.
 * Each method to add queries to the list should be overrides to any extending classes that executed queries
 */
public interface SQLQueryGroup extends Observable<ModifyEvent,String> {

    /**
     * Execute all queries in group. Afterward, all queries in group are cleared.
     * @throws SQLException if any one of the queries fails. {@link SQLConnection#rollBack()} is automatically called and all successful queries (if any) are rollback. Regardless of exception, all queries in group will be cleared
     */
    void executeQueries() throws SQLException;

    /**
     * Clears all queries in group
     */
    void clearQueries();

    /**
     * Gets all Queries currently in group
     * @return a List of all QueryBuilder objects currently in the group. List is unmodifiable
     */
    List<QueryBuilder> getQueries();

    /**
     * Gets all ModifyEvents currently in group
     * @return a List of all ModifyEvent objects currently in the group. List is unmodifiable
     */
    List<ModifyEvent> getEvents();

    /**
     * Gets the SQLConnection
     * @return the corresponding SQLConnection
     */
    SQLConnection getConnection();

    /**
     * Combine one or more query groups and execute all queries as one batch. Any events that need to be fired will still be fired from the original class that called for the query to be added.
     * @param groups all SQLQueryGroup objects containing grouped queries
     * @throws SQLException if any queries fail. This will automatically trigger a rollback thus all queries (executed or not) with all groups provided will be rolled back
     */
    default void executeAllQueries(SQLQueryGroup ... groups) throws SQLException {

        List<QueryBuilder> combinedQueries = new ArrayList<>();
        List<List<ModifyEvent>> combinedEvents = new ArrayList<>();

        for (SQLQueryGroup group : groups) {
            // Add all queries
            combinedQueries.addAll(group.getQueries());
            // Add all events
            combinedEvents.add(group.getEvents());

            group.clearQueries();
        }

        SQLConnection connection = groups[0].getConnection();
        try {
            // Execute queries
            connection.executeGroupQuery(combinedQueries);
            // Fire all events
            // Go through each internal list representing a group class
            for (int index = 0; index < combinedEvents.size(); index++) {
                // Have each corresponding group class fire their events
                for (ModifyEvent event : combinedEvents.get(index)) {
                    groups[index].notifyObservers(event);
                }
            }

        } catch (SQLException exception) {
            connection.rollBack();
            throw exception;
        }

    }

}
