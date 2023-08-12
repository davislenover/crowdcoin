package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.networking.sqlcom.SQLColumnType;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;
import com.crowdcoin.networking.sqlcom.query.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SQLDatabaseGroup extends SQLDatabase implements SQLQueryGroup {

    private List<QueryBuilder> queries;
    private List<ModifyEvent> events;

    public SQLDatabaseGroup(SQLConnection connection) {
        super(connection);
        this.queries = new ArrayList<>();
        this.events = new ArrayList<>();
    }

    public void addNewUser(String username, String password) {
        this.queries.add(new AddUserQuery(username,password));
    }

    public void removeUser(String username) {
        this.queries.add(new RemoveUserQuery(username));
    }

    public void grantUserPermissions(String username, String schemaName, SQLPermission... permissions) {
        // map will take each element from permissions and apply getQueryString() to it (i.e. invoke it) to which the result will be added to a String array
        this.queries.add(new GrantPermissionsQuery(username,schemaName, Arrays.stream(permissions).map(SQLPermission::getQueryString).toArray(String[]::new)));
    }

    public void grantGlobalPermissions(String username, SQLPermission ... permissions) {
        this.queries.add(new GrantGlobalPermissionsQuery(username,Arrays.stream(permissions).map(SQLPermission::getQueryString).toArray(String[]::new)));
    }

    public void addColumn(String tableName, String columnName, SQLColumnType type, String defaultValue) {
        this.queries.add(new AddColumnQuery(tableName,columnName,type.getQueryString(),defaultValue));
        this.events.add(new ModifyEvent(ModifyEventType.NEW_COLUMN));
    }

    /**
     * Execute all queries in group. Afterward, all queries in group are cleared.
     * @throws SQLException if any one of the queries fails. {@link SQLConnection#rollBack()} is automatically called and all successful queries (if any) are rollback. Regardless of exception, all queries in group will be cleared
     */
    @Override
    public void executeQueries() throws SQLException {
        SQLConnection connection = super.getConnection();
        try {
            // Execute queries
            connection.executeGroupQuery(this.queries);
            // Fire all events
            for (ModifyEvent event : this.events) {
                super.notifyObservers(event);
            }
        } catch (SQLException exception) {
            connection.rollBack();
            // Clear both lists regardless of exception or not
            this.queries.clear();
            this.events.clear();
            throw exception;
        }
        this.queries.clear();
        this.events.clear();
    }

    @Override
    public void clearQueries() {
        this.queries.clear();
        this.events.clear();
    }

    @Override
    public List<QueryBuilder> getQueries() {
        return List.copyOf(this.queries);
    }

    @Override
    public List<ModifyEvent> getEvents() {
        return List.copyOf(this.events);
    }


}
