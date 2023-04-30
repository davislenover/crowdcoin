package com.crowdcoin.networking.sqlcom;

import com.crowdcoin.exceptions.network.NullConnectionException;
import com.crowdcoin.security.Credentials;

public class SQLData {

    // This class contains important references to SQL information
    public static SQLConnection sqlConnection;
    public static Credentials credentials;


    // Getters
    // Get current connection
    public static SQLConnection getSqlConnection() throws Exception {
        if (sqlConnection != null) {
            return sqlConnection;
        } else {
            throw new NullConnectionException();
        }
    }

}
