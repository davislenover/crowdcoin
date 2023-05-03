package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.exceptions.table.UnknownColumnNameException;
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

    // Method gets table information and set's up tableColumn list
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

        result.close();

    }

    public List<Object> getRow(int rowIndex, String startColumn, String endColumn) {

        try {

            // Get query result
            // Specifies to get a specific row of all data from table
            ResultSet result = this.connection.sendQuery(SQLDefaultQueries.getAllWithLimit(this.tableName,rowIndex,1));

            List<Object> returnRow = new ArrayList<>();
            // Get list of all column names in the desired column range to return data from
            List<String> columnRange = getColumnNameList(startColumn,endColumn);

            // Loop through range and add data of the specified columns to the return list
            for (String columnName : columnRange) {
                returnRow.add(result.getObject(columnName));
            }

            return returnRow;


        } catch (Exception e) {
            return null;
        }

    }

    // Method to get all column names between startColumn and endColumn (both inclusive)
    private List<String> getColumnNameList(String startColumn, String endColumn) throws InvalidRangeException, UnknownColumnNameException {

        boolean withinRange = false;
        List<String> returnList = new ArrayList<>();

        // Loop through all known columns
        for (String[] columnData : this.tableColumns) {

            // Get current column name
            String columnName = columnData[0];

            // If the start column is found, add to list and set withinRange to true
            if (columnName.equals(startColumn) && !withinRange) {
                returnList.add(columnName);
                withinRange=true;
            } else if (withinRange && !columnName.equals(endColumn)) {
                returnList.add(columnName);
                // Once end column is found, add to list and break
            } else if (withinRange && columnName.equals(endColumn)) {
                returnList.add(columnName);
                break;
                // Throw invalid range if found the end column before the start
            } else if (columnName.equals(endColumn) && !withinRange) {
                throw new InvalidRangeException(startColumn,endColumn);
            }

            // Throw unknown column if either start or end is not found at all
            if (this.tableColumns.get(tableColumns.size()-1)==columnData && withinRange) {
                // Note no check is needed to test if the last columnData is endColumn as this is tested prior in upper if else
                throw new UnknownColumnNameException(endColumn);
            }
            // Indicates startColumn was never found
            if (this.tableColumns.get(tableColumns.size()-1)==columnData && !withinRange) {
                throw new UnknownColumnNameException(startColumn);
            }

        }

        return returnList;

    }

}
