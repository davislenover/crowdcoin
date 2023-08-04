package com.crowdcoin.networking.sqlcom;

import com.crowdcoin.networking.sqlcom.permissions.SQLPermission;

import java.util.Arrays;
import java.util.List;

public class SQLDatabase {

    private SQLConnection connection;

    public SQLDatabase(SQLConnection connection) {
        this.connection = connection;
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

}
