package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.networking.sqlcom.SQLColumnType;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;
import com.crowdcoin.networking.sqlcom.query.AddColumnQuery;
import com.crowdcoin.networking.sqlcom.query.AddUserQuery;
import com.crowdcoin.networking.sqlcom.query.GrantGlobalPermissionsQuery;
import com.crowdcoin.networking.sqlcom.query.GrantPermissionsQuery;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLDatabase implements QueryGroupable<SQLDatabaseGroup>,Observable<ModifyEvent,String> {

    private SQLConnection connection;
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
        } catch (FailedQueryException e) {
            e.rootException.printStackTrace();
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
        return new SQLDatabaseGroup(this.connection);
    }
}
