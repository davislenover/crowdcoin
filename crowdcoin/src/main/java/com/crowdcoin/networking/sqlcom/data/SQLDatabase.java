package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.ratchet.observe.ModifyEvent;
import com.ratchet.observe.ModifyEventType;
import com.ratchet.observe.Observable;
import com.ratchet.observe.Observer;
import com.crowdcoin.networking.sqlcom.SQLColumnType;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;
import com.crowdcoin.networking.sqlcom.query.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLDatabase implements QueryGroupable<SQLDatabaseGroup>,Observable<ModifyEvent,String>, Observer<ModifyEvent,String> {

    private SQLConnection connection;
    private SQLDatabaseGroup databaseGroup = null;
    private List<Observer<ModifyEvent,String>> subscriptionList;

    public SQLDatabase(SQLConnection connection) {
        this.connection = connection;
        this.subscriptionList = new ArrayList<>();
    }

    public SQLConnection getConnection() {
        return this.connection;
    }

    public void addNewUser(String username, String password) {
        try {
            this.connection.executeQuery(new AddUserQuery(username,password));
            this.notifyObservers(new ModifyEvent(ModifyEventType.ADDED_USER));
        } catch (FailedQueryException e) {
            e.rootException.printStackTrace();
        }

    }

    public void removeUser(String username) {
        try {
            this.connection.executeQuery(new RemoveUserQuery(username));
            this.notifyObservers(new ModifyEvent(ModifyEventType.REMOVED_USER));
        } catch (FailedQueryException exception) {
            exception.rootException.printStackTrace();
        }
    }

    public void grantUserPermissions(String username, String schemaName, SQLPermission ... permissions) {

        try {
            this.connection.executeQuery(new GrantPermissionsQuery(username,schemaName,Arrays.stream(permissions).map(Enum::name).toArray(String[]::new)));
        } catch (FailedQueryException e) {
            e.rootException.printStackTrace();
        }

    }

    public void grantGlobalPermissions(String username, SQLPermission ... permissions) {
        try {
            this.connection.executeQuery(new GrantGlobalPermissionsQuery(username,Arrays.stream(permissions).map(SQLPermission::getQueryString).toArray(String[]::new)));
        } catch (FailedQueryException exception) {
            exception.rootException.printStackTrace();
        }
    }

    public void addColumn(String tableName, String columnName, SQLColumnType type, String defaultValue) {
        try {
            this.connection.executeQuery(new AddColumnQuery(tableName,columnName,type.getQueryString(),defaultValue));
            this.notifyObservers(new ModifyEvent(ModifyEventType.NEW_COLUMN));
        } catch (Exception e) {
            // TODO Error handling
        }
    }

    public void removeColumn(String tableName, String columnName) {
        try {
            this.connection.executeQuery(new RemoveColumnQuery(tableName,columnName));
            this.notifyObservers(new ModifyEvent(ModifyEventType.REMOVED_COLUMN));
        } catch (Exception e) {
            // TODO Error handling
        }
    }

    @Override
    public boolean addObserver(Observer<ModifyEvent,String> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<ModifyEvent,String> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(ModifyEvent event) {
        for (Observer<ModifyEvent,String> observer : List.copyOf(this.subscriptionList)) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }

    @Override
    public SQLDatabaseGroup getQueryGroup() {
        if (this.databaseGroup == null) {
            this.databaseGroup = new SQLDatabaseGroup(this.connection);
            databaseGroup.addObserver(this);
        }
        return this.databaseGroup;
    }

    @Override
    public void removeObserving() {
        if (this.databaseGroup != null) {
            this.databaseGroup.removeObserver(this);
        }
    }

    @Override
    public void update(ModifyEvent event) {
        this.notifyObservers(event);
    }
}
