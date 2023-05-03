package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;

import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class SQLTable {

    private String tableName;
    // 0 corresponds to table name, 1 is data type as specified in SQL table
    private List<String[]> tableColumns;
    private SQLConnection connection;

    public SQLTable(SQLConnection connection, String tableName) {

        try {

            // Setup
            this.connection = connection;
            this.tableName = tableName;
            getTableData();

        } catch (Exception e) {

        }

    }

    private void getTableData() throws Exception {

        this.tableColumns = new ArrayList<>();

        // Use information schema to get all table information for the specified table name
        ResultSet result = this.connection.sendQuery(SQLDefaultQueries.getGetColumnDataQuery(this.tableName));

        // Loop over each row
        while (result.next()) {

            String[] newColumn = new String[2];
            // Get corresponding column name
            newColumn[0] = result.getString(SQLDefaultQueries.informationSchemaColumnName);
            // Get columns data type
            newColumn[1] = result.getString(SQLDefaultQueries.informationSchemaDataType);
            this.tableColumns.add(newColumn);


        }


    }
}
