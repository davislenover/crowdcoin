package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.mainBoard.table.Observe.Observable;
import com.crowdcoin.mainBoard.table.Observe.Observer;
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
        } catch (Exception e) {
            // TODO Error handling
        }

    }

    public void grantUserPermissions(String username, SQLPermission ... permissions) {

        try {
            this.connection.executeQuery(SQLDefaultQueries.grantPermissions(username,Arrays.stream(permissions).map(Enum::name).toArray(String[]::new)));
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
