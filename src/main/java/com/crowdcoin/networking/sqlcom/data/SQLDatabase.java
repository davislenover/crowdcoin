package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.networking.sqlcom.SQLColumnType;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;
import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SQLDatabase implements Observable<ModifyEvent,String> {

    private SQLConnection connection;
    private List<Observer<ModifyEvent,String>> subscriptionList;

    public SQLDatabase(SQLConnection connection) {
        this.connection = connection;
        this.subscriptionList = new ArrayList<>();
    }

    public void addNewUser(String username, String password) {
        try {
            this.connection.executeQuery(SQLDefaultQueries.addUser(username,password));
        } catch (FailedQueryException e) {
            e.rootException.printStackTrace();
        }

    }

    public void grantUserPermissions(String username, String schemaName, SQLPermission ... permissions) {

        try {
            this.connection.executeQuery(SQLDefaultQueries.grantPermissions(username,schemaName,Arrays.stream(permissions).map(Enum::name).toArray(String[]::new)));
        } catch (FailedQueryException e) {
            e.rootException.printStackTrace();
        }

    }

    public void addColumn(String tableName, String columnName, SQLColumnType type, String defaultValue) {
        try {
            this.connection.executeQuery(SQLDefaultQueries.addColumn(tableName,columnName,type.getQueryString(),defaultValue));
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
}
