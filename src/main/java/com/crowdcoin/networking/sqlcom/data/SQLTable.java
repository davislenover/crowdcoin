package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.exceptions.table.UnknownColumnNameException;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.SQLDefaultQueries;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

public class SQLTable {

    private String tableName;
    // 0 corresponds to table name, 1 is data type as specified in SQL table, 2 specifies the ordinal position
    private List<String[]> tableColumns;
    private SQLConnection connection;

    /**
     * An object to get information from an SQL database
     * @param connection the current connection to communicate with the database
     * @param tableName the table within the database to get data from. Note the ordering of the columns when returning rows is via ordinal position (as raw ResultSets do NOT guarantee specific column ordering on queries thus SQLTable is intended deal with it via sorting the ResultSet via ordinal position)
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause. It may indicate the table provided does not exist within the database
     * @throws SQLException if a database access error occurs
     */
    public SQLTable(SQLConnection connection, String tableName) throws FailedQueryException, SQLException {

        // Setup
        this.connection = connection;
        this.tableName = tableName;
        getTableData();

    }

    // Method gets table information and set's up tableColumn list
    private void getTableData() throws FailedQueryException, SQLException {

        this.tableColumns = new ArrayList<>();

        // Use information schema to get all table information for the specified table name
        ResultSet result = this.connection.sendQuery(SQLDefaultQueries.getGetColumnDataQuery(this.tableName));

        // Loop over each row
        while (result.next()) {

            String[] newColumn = new String[3];
            // Get corresponding column name
            newColumn[0] = result.getString(SQLDefaultQueries.informationSchemaColumnName);
            // Get columns data type
            newColumn[1] = result.getString(SQLDefaultQueries.informationSchemaDataType);
            // Get it's ordinal position
            // All results will be returned in ordinal position for query consistency
            newColumn[2] = result.getString(SQLDefaultQueries.informationSchemaOrdinalPosition);

            this.tableColumns.add(newColumn);

        }

        // Sort each column within list by ordinal position (where o1 and o2 are the corresponding String arrays within the list to compare)
        this.tableColumns.sort((o1, o2) -> {

            // Get ordinal position
            int o1Value = Integer.valueOf(o1[2]);
            int o2Value = Integer.valueOf(o2[2]);

            // Return result of comparing both ordinal positions
            return Integer.compare(o1Value,o2Value);
        });

        result.close();

    }

    /**
     * Get a list of data between two columns corresponding to a given row in the SQL table (NOT Table object). Results are returned in ordinal position of columns as specified in database
     * @param rowIndex the corresponding row within the SQL table to get data from
     * @param startColumn the starting column in the row to begin reading data from (inclusive) where the column string is the same as that found within the table. Note the ordering of the columns is via ordinal position
     * @param endColumn the ending column in the row to end reading data from (inclusive) where the column string is the same as that found within the table. Note the ordering of the columns is via ordinal position
     * @return a list of Object type containing the data found via the specified arguments
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws UnknownColumnNameException if either startColumn or endColumn do not exist within the table
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note if rowIndex is not physically present in the database, an empty list will be returned
     */
    public List<Object> getRow(int rowIndex, String startColumn, String endColumn) throws InvalidRangeException, UnknownColumnNameException, SQLException, FailedQueryException {

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

        result.close();

        return returnRow;

    }

    /**
     * Get a list of data between two columns corresponding to a given row in the SQL table (NOT Table object).
     * @param rowIndex the corresponding row within the SQL table to get data from
     * @param startColumnIndex the starting column in the row to begin reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards)
     * @param endColumnIndex the ending column in the row to end reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards)
     * @return a list of Object type containing the data found via the specified arguments
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note this method does not throw UnknownColumnNameException as columns are referenced by index and not by string. If rowIndex is not physically present in the database, an empty list will be returned
     */
    public List<Object> getRow(int rowIndex, int startColumnIndex, int endColumnIndex) throws InvalidRangeException, FailedQueryException, SQLException {

        if (startColumnIndex > endColumnIndex) {
            throw new InvalidRangeException(String.valueOf(startColumnIndex),String.valueOf(endColumnIndex));
        }

        ResultSet result = this.connection.sendQuery(SQLDefaultQueries.getAllWithLimit(this.tableName,rowIndex,1));
        List<Object> returnRow = new ArrayList<>();

        // Loop through start to end and add the corresponding column data to return list
        // The result set is not guaranteed to have returned a query in any particular column position
        // Thus, given the ordinal position is known, we will return the result list in said position
        for (int index = startColumnIndex; index <= endColumnIndex; index++) {
            // getObject gets the corresponding data from a corresponding column name thus, given tableColumns list is sorted in ordinal position, returnRow list will add data according to ordinal position
            returnRow.add(result.getObject(this.tableColumns.get(index)[0]));
        }

        result.close();

        return returnRow;

    }

    /**
     * Get a list of data between two columns corresponding to given rows in the SQL table (NOT Table object). Results are returned in ordinal position of columns as specified in database
     * @param rowIndex the corresponding starting row within the SQL table to get data from
     * @param numberOfRows how many rows (starting from row given in rowIndex) to get. e.g., specifying a rowIndex of 0 and numberOfRows as 3 will get the first 3 rows
     * @param startColumn the starting column in the row to begin reading data from (inclusive) where the column string is the same as that found within the table. Note the ordering of the columns is via ordinal position
     * @param endColumn the ending column in the row to end reading data from (inclusive) where the column string is the same as that found within the table. Note the ordering of the columns is via ordinal position
     * @return a list of Object type containing the data found via the specified arguments. First list corresponds to each row whereas second list contains the data for the specified row.
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws UnknownColumnNameException if either startColumn or endColumn do not exist within the table
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note if numberOfRows exceeds that of what is physically available in the database, up to and including the last row possible will be returned
     */
    public List<List<Object>> getRows(int rowIndex, int numberOfRows, String startColumn, String endColumn) throws InvalidRangeException, UnknownColumnNameException, SQLException, FailedQueryException {

        // Get query result
        // Specifies to get a specific row of all data from table
        ResultSet result = this.connection.sendQuery(SQLDefaultQueries.getAllWithLimit(this.tableName,rowIndex,numberOfRows));
        List<List<Object>> returnRows = new ArrayList<>();

        // Get list of all column names in the desired column range to return data from
        List<String> columnRange = getColumnNameList(startColumn,endColumn);

        while (result.next()) {
            List<Object> returnRow = new ArrayList<>();
            // Loop through range and add data of the specified columns to the return list
            for (String columnName : columnRange) {
                returnRow.add(result.getObject(columnName));
            }

            // Add row to return list
            returnRows.add(returnRow);

        }

        result.close();

        return returnRows;

    }

    /**
     * Get a list of data between two columns corresponding to given rows in the SQL table (NOT Table object). Results are returned in ordinal position of columns as specified in database
     * @param rowIndex the corresponding starting row within the SQL table to get data from
     * @param numberOfRows how many rows (starting from row given in rowIndex) to get. e.g., specifying a rowIndex of 0 and numberOfRows as 3 will get the first 3 rows
     * @param startColumnIndex the starting column in the row to begin reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @param endColumnIndex the ending column in the row to end reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @return a list of Object type containing the data found via the specified arguments. First list corresponds to each row whereas second list contains the data for the specified row.
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws UnknownColumnNameException if either startColumn or endColumn do not exist within the table
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note if numberOfRows exceeds that of what is physically available in the database, up to and including the last row possible will be returned
     */
    public List<List<Object>> getRows(int rowIndex, int numberOfRows, int startColumnIndex, int endColumnIndex) throws InvalidRangeException, FailedQueryException, SQLException {

        if (startColumnIndex > endColumnIndex) {
            throw new InvalidRangeException(String.valueOf(startColumnIndex),String.valueOf(endColumnIndex));
        }

        ResultSet result = this.connection.sendQuery(SQLDefaultQueries.getAllWithLimit(this.tableName,rowIndex,numberOfRows));
        List<List<Object>> returnRows = new ArrayList<>();

        while (result.next()) {
            List<Object> returnRow = new ArrayList<>();

            // Loop through start to end and add the corresponding column data to return list
            for (int index = startColumnIndex; index <= endColumnIndex; index++) {
                returnRow.add(result.getObject(this.tableColumns.get(index)[0]));
            }
            // Add row to return list
            returnRows.add(returnRow);

        }


        result.close();

        return returnRows;

    }

    /**
     * Get the number of columns the sql table has
     * @return the number of columns as an Integer
     */
    public int getNumberOfColumns() {
        return this.tableColumns.size();
    }

    /**
     * Get all column names found within the table
     * @return the column names in a list of Strings
     */
    public List<String> getColumnNames() {

        List<String> columnNames = new ArrayList<>();

        for (String[] currentColumn : this.tableColumns) {
            columnNames.add(currentColumn[0]);
        }

        return columnNames;

    }

    /**
     * Get all column types found within the table (in-order)
     * @return the column types as specified within the database in a list of Strings
     */
    public List<String> getColumnTypes() {

        List<String> columnTypes = new ArrayList<>();

        for (String[] currentColumn : this.tableColumns) {
            columnTypes.add(currentColumn[1]);
        }

        return columnTypes;

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
