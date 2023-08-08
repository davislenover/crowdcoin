package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.InvalidRangeException;
import com.crowdcoin.exceptions.table.UnknownColumnNameException;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.Observe.*;
import com.crowdcoin.mainBoard.table.permissions.IsReadable;
import com.crowdcoin.mainBoard.table.permissions.IsSystemWriteable;
import com.crowdcoin.mainBoard.table.permissions.IsWriteable;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.data.constraints.ConstraintContainer;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.query.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

/**
 * Handles communication between a program and an SQL table within an SQL Database.
 * Note this class takes into account permissions and constraints, where a method in question has "raw" in it, both permissions and constraints are ignored. All methods which check permissions also check constraints
 */
public class SQLTable implements Observable<ModifyEvent,String>, Observer<ModifyEvent,String> {

    private static SQLDatabase database = null;

    private List<Observer<ModifyEvent,String>> subscriptionList;

    private String tableName;
    // Within the String array (inside the list), index 0 corresponds to table name, 1 is data type as specified in SQL table, 2 specifies the ordinal position
    // (List, NOT String array) It is important to NOT directly correspond indices of columns to ordinal positions as column indices within this list are ordered in RELATIVE ordinal position. Ordinal positions start at 1. This means index 0 would correspond to an ordinal position of 1, 1 to 2, 2 to 3, etc
    private List<String[]> tableColumns;
    private SQLConnection connection;
    private FilterManager filterManager;
    private List<Column> columnsPermList;
    private String isReadablePerm = IsReadable.class.getSimpleName();
    private String isWriteablePerm = IsWriteable.class.getSimpleName();
    private String isSystemWriteablePerm = IsSystemWriteable.class.getSimpleName();

    private ConstraintContainer constraints;

    /**
     * An object to get information from an SQL database
     * @param connection the current connection to communicate with the database
     * @param tableName the table within the database to get data from. Note the ordering of the columns when returning rows is via ordinal position (as raw ResultSets do NOT guarantee specific column ordering on queries thus SQLTable is intended deal with it via sorting the ResultSet via ordinal position)
     * @param columnObjectList the list of Column objects representing which contain permission data about each column. Each column name (from {@link Column#getColumnName()}) within this list must match that of a column from the SQL Table. The list is copied and thus, reference of list passed into object is only used for creation and not modified (Column objects stored within may be modified).
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause. It may indicate the table provided does not exist within the database
     * @throws SQLException if a database access error occurs
     * @throws UnknownColumnNameException if one or more Column object names (from {@link Column#getColumnName()}) do not match any SQL column name
     */
    public SQLTable(SQLConnection connection, String tableName, List<Column> columnObjectList) throws FailedQueryException, SQLException, UnknownColumnNameException {

        // Setup
        this.connection = connection;
        this.tableName = tableName;
        getTableData();
        this.filterManager = new FilterManager();
        // Create new list (as modification of order may be needed)
        this.columnsPermList = new ArrayList<>() {{
            addAll(columnObjectList);
        }};
        checkColumnNames();
        sortColumnObjectList();

        this.subscriptionList = new ArrayList<>();
        this.constraints = new ConstraintContainer();
        if (database == null) {
            database = new SQLDatabase(this.connection);
        }
        database.addObserver(this);
    }

    /**
     * Refreshes the SQLTable in the case that there are new columns in a SQL Table that the given SQLTable object needs to be aware of
     * @throws FailedQueryException
     * @throws SQLException
     * @throws UnknownColumnNameException
     */
    public void refresh() throws FailedQueryException, SQLException, UnknownColumnNameException {
        getTableData();
        checkColumnNames();
        sortColumnObjectList();
    }

    // Method gets table information and set's up tableColumn list
    private void getTableData() throws FailedQueryException, SQLException {

        this.tableColumns = new ArrayList<>();

        // Use information schema to get all table information for the specified table name
        ResultSet result = this.connection.sendQuery(new GetColumnDataQuery(this.tableName));

        // Loop over each row
        while (result.next()) {

            String[] newColumn = new String[3];
            // Get corresponding column name
            newColumn[0] = result.getString(QueryPrefix.informationSchemaColumnName.getPrefix());
            // Get columns data type
            newColumn[1] = result.getString(QueryPrefix.informationSchemaDataType.getPrefix());
            // Get it's ordinal position
            // All results will be returned in ordinal position for query consistency
            newColumn[2] = result.getString(QueryPrefix.informationSchemaOrdinalPosition.getPrefix());

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

    // Method to check that Column object names match that of one column name from the SQL table
    private void checkColumnNames() throws UnknownColumnNameException {

        if (this.columnsPermList.size() == this.tableColumns.size()) {
            for (Column column : this.columnsPermList) {

                boolean hasMatch = false;

                for (String[] SQLColumnName : this.tableColumns) {

                    if (SQLColumnName[0].equals(column.getColumnName())) {
                        hasMatch = true;
                        break;
                    }

                }

                if (!hasMatch) {
                    if (!column.isVariable()) {
                        throw new UnknownColumnNameException("Column name, " + column.getColumnName() + ", from Column objects does not have a corresponding SQL column name");
                    }
                }

            }
        } else {

            for (Column column : this.columnsPermList) {
                if (column.isVariable()) {
                    return;
                }
            }
            throw new IndexOutOfBoundsException("Column object list and SQL table columns list are not the same size");
        }

    }

    // Sort the given Column object list
    // This will allow columnPermsList to index match tableColumns
    private void sortColumnObjectList() {

        // First, set ordinal position of each Column
        for (String[] column : this.tableColumns) {
            this.getColumnObject(column[0]).setOrdinalPosition(Integer.valueOf(column[2]));
        }

        // Sort using custom comparator
        this.columnsPermList.sort((Comparator.comparingInt(Column::getOrdinalPosition)));

    }

    /**
     * Gets the SQLConnection object the SQLTable is using
     * @return the given SQLConnection object
     */
    public SQLConnection getConnection() {
        return this.connection;
    }

    /**
     * Gets the column constraints container for the given SQLTable instance
     * @return a ColumnConstraints object
     */
    public ConstraintContainer getConstraints() {
        return this.constraints;
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
        ResultSet result = this.connection.sendQuery(new GetColumnDataWithLimitQuery(this.tableName,rowIndex,1));

        List<Object> returnRow = new ArrayList<>();
        // Get list of all column names in the desired column range to return data from
        List<String> columnRange = getColumnNameList(startColumn,endColumn);

        // Loop through range and add data of the specified columns to the return list
        for (String columnName : columnRange) {
            // Check if the column name is valid under the current constraints
            if (this.constraints.isValid(columnName)) {
                // Check permissions of column before adding to results
                if (this.getColumnObject(columnName).checkPermissionValue(isReadablePerm)) {
                    // Check if the cell data (for any constraint groups in the container) is valid
                    Object resultObject = result.getObject(columnName);
                    if (this.constraints.isValidGroup(columnName,resultObject.toString())) {
                        returnRow.add(resultObject);
                    }
                }
            }
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

        ResultSet result = this.connection.sendQuery(new GetColumnDataWithLimitQuery(this.tableName,rowIndex,1));
        List<Object> returnRow = new ArrayList<>();

        // Loop through start to end and add the corresponding column data to return list
        // The result set is not guaranteed to have returned a query in any particular column position
        // Thus, given the ordinal position is known, we will return the result list in said position
        for (int index = startColumnIndex; index <= endColumnIndex; index++) {
            String columnName = this.tableColumns.get(index)[0];
            if (this.constraints.isValid(columnName)) {
                // Since columnPermList index matches, check permissions before adding to result
                if (this.columnsPermList.get(index).checkPermissionValue(isReadablePerm)) {
                    Object resultObject = result.getObject(columnName);
                    if (this.constraints.isValidGroup(columnName,resultObject.toString())) {
                        // getObject gets the corresponding data from a corresponding column name thus, given tableColumns list is sorted in ordinal position, returnRow list will add data according to ordinal position
                        returnRow.add(resultObject);
                    }
                }
            }
        }

        result.close();

        return returnRow;

    }

    /**
     * Get a list of data between two columns corresponding to given rows in the SQL table (NOT Table object). Method is affected by FilterManager, any filter is applied. Results are returned in ordinal position of columns as specified in database
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
        // Get combined query string to add filters
        ResultSet result = this.connection.sendQuery(new GetColumnDataWithLimitAndFilterQuery(this.tableName,this.filterManager.getCombinedQuery(),rowIndex,numberOfRows));
        List<List<Object>> returnRows = new ArrayList<>();

        // Get list of all column names in the desired column range to return data from
        List<String> columnRange = getColumnNameList(startColumn,endColumn);

        boolean isValid = true;

        while (result.next()) {
            isValid = true;
            List<Object> returnRow = new ArrayList<>();
            // Loop through range and add data of the specified columns to the return list
            for (String columnName : columnRange) {
                if (this.constraints.isValid(columnName)) {
                    // Check permissions of column before adding to results
                    if (this.getColumnObject(columnName).checkPermissionValue(isReadablePerm)) {
                        Object resultObject = result.getObject(columnName);
                        returnRow.add(resultObject);
                        isValid = this.constraints.isValidGroup(columnName,resultObject.toString());
                    }
                }
            }

            if (isValid) {
                // Add row to return list
                returnRows.add(returnRow);
            }

        }

        result.close();

        return returnRows;

    }

    /**
     * Get a list of data between two columns corresponding to given rows in the SQL table (NOT Table object). Method is affected by FilterManager, any filter is applied. Results are returned in ordinal position of columns as specified in database
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

        ResultSet result = this.connection.sendQuery(new GetColumnDataWithLimitAndFilterQuery(this.tableName,this.filterManager.getCombinedQuery(),rowIndex,numberOfRows));
        List<List<Object>> returnRows = new ArrayList<>();

        boolean isValid = true;

        while (result.next()) {
            isValid = true;
            List<Object> returnRow = new ArrayList<>();
            // Loop through start to end and add the corresponding column data to return list
            for (int index = startColumnIndex; index <= endColumnIndex; index++) {
                String columnName = this.tableColumns.get(index)[0];
                if (this.constraints.isValid(columnName)) {
                    // Since columnPermList index matches, check permissions before adding to result
                    if (this.columnsPermList.get(index).checkPermissionValue(isReadablePerm)) {
                        Object resultObject = result.getObject(columnName);
                        returnRow.add(resultObject);
                        isValid = this.constraints.isValidGroup(columnName,resultObject.toString());
                    }
                }
            }

            if (isValid) {
                // Add row to return list
                returnRows.add(returnRow);
            }

        }


        result.close();

        return returnRows;

    }

    /**
     * Get a list of data between two columns corresponding to given rows in the SQL table (NOT Table object). Unlike {@link SQLTable#getRows(int, int, int, int)}, method ignores ALL permissions and will return all columns specified. Method is affected by FilterManager, any filter is applied. Results are returned in ordinal position of columns as specified in database
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
    public List<List<Object>> getRawRows(int rowIndex, int numberOfRows, int startColumnIndex, int endColumnIndex) throws InvalidRangeException, FailedQueryException, SQLException {

        if (startColumnIndex > endColumnIndex) {
            throw new InvalidRangeException(String.valueOf(startColumnIndex),String.valueOf(endColumnIndex));
        }

        ResultSet result = this.connection.sendQuery(new GetColumnDataWithLimitAndFilterQuery(this.tableName,this.filterManager.getCombinedQuery(),rowIndex,numberOfRows));
        List<List<Object>> returnRows = new ArrayList<>();

        while (result.next()) {
            List<Object> returnRow = new ArrayList<>();

            // Loop through start to end and add the corresponding column data to return list
            for (int index = startColumnIndex; index <= endColumnIndex; index++) {
                // getObject gets the corresponding data from a corresponding column name thus, given tableColumns list is sorted in ordinal position, returnRow list will add data according to ordinal position
                returnRow.add(result.getObject(this.tableColumns.get(index)[0]));
            }
            // Add row to return list
            returnRows.add(returnRow);

        }


        result.close();

        return returnRows;

    }

    /**
     * Get a list of data between two columns corresponding to given rows in the SQL table (NOT Table object). Unlike {@link SQLTable#getRows(int, int, int, int)}, method ignores ALL permissions but does NOT ignore constraint GROUPS and will return all columns specified. Method is affected by FilterManager, any filter is applied. Results are returned in ordinal position of columns as specified in database
     * @param rowIndex the corresponding starting row within the SQL table to get data from
     * @param numberOfRows how many rows (starting from row given in rowIndex) to get. e.g., specifying a rowIndex of 0 and numberOfRows as 3 will get the first 3 rows
     * @param startColumnIndex the starting column in the row to begin reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @param endColumnIndex the ending column in the row to end reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @return a list of Object type containing the data found via the specified arguments. First list corresponds to each row whereas second list contains the data for the specified row.
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws UnknownColumnNameException if either startColumn or endColumn do not exist within the table
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note if numberOfRows exceeds that of what is physically available in the database, up to and including the last row possible will be returned. If a row is to be omitted because of a constraint group, method will try to get numberOfRows specified by getting as many rows as needed until the number of valid rows matches
     */
    public List<List<Object>> getGroupFilteredRows(int rowIndex, int numberOfRows, int startColumnIndex, int endColumnIndex) throws InvalidRangeException, FailedQueryException, SQLException {

        if (startColumnIndex > endColumnIndex) {
            throw new InvalidRangeException(String.valueOf(startColumnIndex),String.valueOf(endColumnIndex));
        }

        boolean isLast = false;
        List<List<Object>> returnRows = new ArrayList<>();

        // If some rows are omitted from constraint groups, then keep adding rows to returnRows until it's size reaches numberOfRows
        // Excluding if the last set of rows as retrieved
        while(returnRows.size() < numberOfRows && !isLast) {

            boolean isRowValid = true;
            ResultSet result = this.connection.sendQuery(new GetColumnDataWithLimitAndFilterQuery(this.tableName,this.filterManager.getCombinedQuery(),rowIndex,numberOfRows));
            int size = 0;

            while (result.next()) {
                // ResultSets don't have traditional iterators so count the number of rows processed
                size++;
                isRowValid = true;
                List<Object> returnRow = new ArrayList<>();

                // Loop through start to end and add the corresponding column data to return list
                for (int index = startColumnIndex; index <= endColumnIndex; index++) {
                    // getObject gets the corresponding data from a corresponding column name thus, given tableColumns list is sorted in ordinal position, returnRow list will add data according to ordinal position
                    String columnName = this.tableColumns.get(index)[0];
                    Object resultObject = result.getObject(columnName);
                    if (this.constraints.isValidGroup(columnName,resultObject.toString())) {
                        returnRow.add(resultObject);
                    } else {
                        isRowValid = false;
                        break;
                    }
                }

                if (isRowValid && returnRows.size() < numberOfRows) {
                    // Add row to return list
                    returnRows.add(returnRow);
                }

            }

            if (size < numberOfRows) {
                isLast = true;
            }

            // Increase rowIndex by the size calculated (in-case another loop needs to happen given the last rows were not retrieved and returnRows size is not that of numberOfRows)
            rowIndex+=size;
            result.close();
        }
        return returnRows;

    }

    /**
     * Get a list of data between two columns where the row contains specific data from a specific column in the SQL table (NOT Table object). Results are returned in ordinal position of columns as specified in database
     * @param columnWithDataIndex the specified index of the column to look for specificData (in ordinal position)
     * @param specificData the specified data as a String to look for in the given columnWithDataIndex (numeric values work as Strings as well in query)
     * @param numberOfRows how many rows to return at max (in case there is more than one row containing the same specified data)
     * @param startColumnIndex the starting column in the row to begin reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @param endColumnIndex the ending column in the row to end reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @return a list of Object type containing the data found via the specified arguments. First list corresponds to each row whereas second list contains the data for the specified row.
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws UnknownColumnNameException if either startColumn or endColumn do not exist within the table
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note if numberOfRows exceeds that of what is physically available in the database, up to and including the last row possible will be returned. Column indices are ordered in RELATIVE ordinal position. Ordinal positions start at 1. This means index 0 would correspond to an ordinal position of 1
     */
    public List<List<Object>> getSpecificRows(int columnWithDataIndex, String specificData, int numberOfRows, int startColumnIndex, int endColumnIndex) throws InvalidRangeException, FailedQueryException, SQLException {

        if (columnWithDataIndex > this.tableColumns.size()-1) {
            throw new IndexOutOfBoundsException("The specified column with data does not exist within the table (" + columnWithDataIndex + " where max is " + (this.tableColumns.size()-1) + ")");
        }

        if (startColumnIndex > endColumnIndex) {
            throw new InvalidRangeException(String.valueOf(startColumnIndex),String.valueOf(endColumnIndex));
        }

        ResultSet result = this.connection.sendQuery(new GetColumnDataSpecificQuery(this.tableName,this.tableColumns.get(columnWithDataIndex)[0],specificData,numberOfRows));

        List<List<Object>> returnRows = new ArrayList<>();
        boolean isValid = true;

        while (result.next()) {
            isValid = true;
            List<Object> returnRow = new ArrayList<>();
            // Loop through start to end and add the corresponding column data to return list
            for (int index = startColumnIndex; index <= endColumnIndex; index++) {
                String columnName = this.tableColumns.get(index)[0];
                if (this.constraints.isValid(columnName)) {
                    // Since columnPermList index matches, check permissions before adding to result
                    if (this.columnsPermList.get(index).checkPermissionValue(isReadablePerm)) {
                        Object resultObject = result.getObject(columnName);
                        returnRow.add(resultObject);
                        isValid = this.constraints.isValidGroup(columnName,resultObject.toString());
                    }
                }
            }


            if (isValid) {
                // Add row to return list
                returnRows.add(returnRow);
            }

        }
        result.close();

        return returnRows;

    }

    /**
     * Get a list of data between two columns where the row contains specific data from a specific column in the SQL table (NOT Table object). Results are returned in ordinal position of columns as specified in database. This method does NOT check for constraints or permissions
     * @param columnWithDataIndex the specified index of the column to look for specificData (in ordinal position)
     * @param specificData the specified data as a String to look for in the given columnWithDataIndex (numeric values work as Strings as well in query)
     * @param numberOfRows how many rows to return at max (in case there is more than one row containing the same specified data)
     * @param startColumnIndex the starting column in the row to begin reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @param endColumnIndex the ending column in the row to end reading data from (inclusive) where the integer corresponds to the position of the column as found within the table (upwards). Note the ordering of the columns is via ordinal position
     * @return a list of Object type containing the data found via the specified arguments. First list corresponds to each row whereas second list contains the data for the specified row.
     * @throws InvalidRangeException if startColumn comes after endColumn (i.e., the range does not make sense)
     * @throws UnknownColumnNameException if either startColumn or endColumn do not exist within the table
     * @throws SQLException if a database access error occurred
     * @throws FailedQueryException if query failed to execute
     * @Note if numberOfRows exceeds that of what is physically available in the database, up to and including the last row possible will be returned. Column indices are ordered in RELATIVE ordinal position. Ordinal positions start at 1. This means index 0 would correspond to an ordinal position of 1
     */
    public List<List<Object>> getRawSpecificRows(int columnWithDataIndex, String specificData, int numberOfRows, int startColumnIndex, int endColumnIndex) throws InvalidRangeException, FailedQueryException, SQLException {

        if (columnWithDataIndex > this.tableColumns.size()-1) {
            throw new IndexOutOfBoundsException("The specified column with data does not exist within the table (" + columnWithDataIndex + " where max is " + (this.tableColumns.size()-1) + ")");
        }

        if (startColumnIndex > endColumnIndex) {
            throw new InvalidRangeException(String.valueOf(startColumnIndex),String.valueOf(endColumnIndex));
        }

        ResultSet result = this.connection.sendQuery(new GetColumnDataSpecificQuery(this.tableName,this.tableColumns.get(columnWithDataIndex)[0],specificData,numberOfRows));

        List<List<Object>> returnRows = new ArrayList<>();

        while (result.next()) {
            List<Object> returnRow = new ArrayList<>();
            // Loop through start to end and add the corresponding column data to return list
            for (int index = startColumnIndex; index <= endColumnIndex; index++) {
                String columnName = this.tableColumns.get(index)[0];
                Object resultObject = result.getObject(columnName);
                returnRow.add(resultObject);
            }

            // Add row to return list
            returnRows.add(returnRow);
        }
        result.close();

        return returnRows;

    }

    /**
     * Write data to an existing row in the SQL table
     * @param columnWriteIndex the column to write the data to
     * @param dataToWrite the data to write to the given columnWriteIndex
     * @param columnWhereIndex specifies the column to look where specific data is contained (this is apart of identifying the given row to write the data to)
     * @param dataWhereRead specifies the data to look for in the given column by columnWhereIndex (this is apart of identifying the given row to write the data to). The rows that contain this data in the given column will have data written to the rows in the specified column
     * @throws FailedQueryException if the query fails
     * @throws IndexOutOfBoundsException if the column indices are not correct
     */
    public void writeToRow(int columnWriteIndex, String dataToWrite, int columnWhereIndex, String dataWhereRead) throws FailedQueryException, IndexOutOfBoundsException {

        // Error checking
        if (columnWriteIndex > this.tableColumns.size()-1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWriteIndex + " where max is " + (this.tableColumns.size()-1) + ")");
        }

        if (columnWhereIndex > this.tableColumns.size()-1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWhereIndex + " where max is " + (this.tableColumns.size()-1) + ")");
        }

        if (!this.columnsPermList.get(columnWriteIndex).checkPermissionValue(isWriteablePerm)) {
            throw new IllegalAccessError("ColumnWriteIndex, " + columnWriteIndex + ", user is not permitted to write to");
        }

        // Invoke query
        this.connection.executeQuery(new InsertSingleValueIntoRowQuery(this.tableName,this.tableColumns.get(columnWriteIndex)[0],dataToWrite,this.tableColumns.get(columnWhereIndex)[0],dataWhereRead));

        ModifyEvent event = new ModifyEvent(ModifyEventType.ROW_MODIFIED,this.getTableName());
        this.notifyObservers(event);

    }

    /**
     * Write data to an existing row in the SQL table
     * @param columnsToInsertData the list of column names as Strings to insert data into
     * @param correspondingDataToInsert the list of data as Strings to insert into columns (this corresponds to each column within the columnsToInsertData list)
     * @param columnWhereIndex specifies the column to look where specific data is contained (this is apart of identifying the given row to write the data to)
     * @param dataWhereRead specifies the data to look for in the given column by columnWhereIndex (this is apart of identifying the given row to write the data to). The rows that contain this data in the given column will have data written to the rows in the specified column
     * @throws FailedQueryException if the query fails
     * @throws IndexOutOfBoundsException if the column indices are not correct
     */
    public void writeToRow(List<String> columnsToInsertData, List<String> correspondingDataToInsert, int columnWhereIndex, String dataWhereRead) throws FailedQueryException, IndexOutOfBoundsException, UnknownColumnNameException {

        // Error checking

        if (columnWhereIndex > this.tableColumns.size()-1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWhereIndex + " where max is " + (this.tableColumns.size()-1) + ")");
        }

        // Check if any column name is not within the table (error-checking)
        for (String columnName : columnsToInsertData) {

            boolean doesExist = false;
            for (String[] checkName : this.tableColumns) {

                if (checkName[0].equals(columnName)) {
                    doesExist = true;
                    break;
                }

            }

            if (!doesExist) {
                throw new UnknownColumnNameException(columnName);
            }

            if (!this.getColumnObject(columnName).checkPermissionValue(isWriteablePerm)) {
                throw new IllegalAccessError("One or more columns user is not permitted to write to");
            }

        }

        // Invoke query
        this.connection.executeQuery(new InsertIntoRowQuery(this.tableName,columnsToInsertData,correspondingDataToInsert,this.tableColumns.get(columnWhereIndex)[0],dataWhereRead));

        ModifyEvent event = new ModifyEvent(ModifyEventType.ROW_MODIFIED,this.getTableName());
        this.notifyObservers(event);

    }

    /**
     * Create a new row in the SQL table and write data to given columns
     * @param columnsToInsertData the list of column names as Strings to insert data into
     * @param correspondingDataToInsert the list of data as Strings to insert into columns (this corresponds to each column within the columnsToInsertData list)
     * @throws UnknownColumnNameException if a column name is not within the SQL table
     * @throws FailedQueryException if the query fails
     */
    public void writeNewRow(List<String> columnsToInsertData, List<String> correspondingDataToInsert) throws UnknownColumnNameException, FailedQueryException {

        // Check if any column name is not within the table (error-checking)
        for (String columnName : columnsToInsertData) {

            boolean doesExist = false;
            for (String[] checkName : this.tableColumns) {

                if (checkName[0].equals(columnName)) {
                    doesExist = true;
                    break;
                }

            }

            if (!doesExist) {
                throw new UnknownColumnNameException(columnName);
            }

            if (!this.getColumnObject(columnName).checkPermissionValue(isWriteablePerm)) {
                throw new IllegalAccessError("One or more columns user is not permitted to write to");
            }

        }

        // Invoke query
        this.connection.executeQuery(new NewRowQuery(this.tableName,columnsToInsertData,correspondingDataToInsert));

        // Because this is a new row being added, tabs will need to refresh to see changes, thus notify all tabs watching the table
        ModifyEvent event = new ModifyEvent(ModifyEventType.NEW_ROW,this.getTableName());
        this.notifyObservers(event);

    }

    /**
     * Create a new row in the SQL table and write data to given columns. This method is to not be invoked via user input directly
     * @param columnsToInsertData the list of column names as Strings to insert data into
     * @param correspondingDataToInsert the list of data as Strings to insert into columns (this corresponds to each column within the columnsToInsertData list)
     * @throws UnknownColumnNameException if a column name is not within the SQL table
     * @throws FailedQueryException if the query fails
     */
    public void systemWriteNewRow(List<String> columnsToInsertData, List<String> correspondingDataToInsert) throws UnknownColumnNameException, FailedQueryException {

        // Check if any column name is not within the table (error-checking)
        for (String columnName : columnsToInsertData) {

            boolean doesExist = false;
            for (String[] checkName : this.tableColumns) {

                if (checkName[0].equals(columnName)) {
                    doesExist = true;
                    break;
                }

            }

            if (!doesExist) {
                throw new UnknownColumnNameException(columnName);
            }

            if (!this.getColumnObject(columnName).checkPermissionValue(isSystemWriteablePerm)) {
                throw new IllegalAccessError("One or more columns system is not permitted to write to");
            }

        }

        // Invoke query
        this.connection.executeQuery(new NewRowQuery(this.tableName,columnsToInsertData,correspondingDataToInsert));

        // Because this is a new row being added, tabs will need to refresh to see changes, thus notify all tabs watching the table
        ModifyEvent event = new ModifyEvent(ModifyEventType.NEW_ROW,this.getTableName());
        this.notifyObservers(event);

    }

    /**
     * Deletes a row from the SQL database table
     * @param columnWhereIndex the corresponding column to look for data
     * @param dataWhereRead the data within the corresponding column. This identifies the row which will be removed
     */
    public void deleteRow(int columnWhereIndex, String dataWhereRead) throws FailedQueryException {

        if (columnWhereIndex > this.tableColumns.size() - 1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWhereIndex + " where max is " + (this.tableColumns.size()-1) + ")");
        }

        this.connection.executeQuery(new DeleteRowQuery(this.tableName,this.tableColumns.get(columnWhereIndex)[0],dataWhereRead));
        ModifyEvent event = new ModifyEvent(ModifyEventType.ROW_REMOVED,this.getTableName());
        this.notifyObservers(event);

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
        for(String[] column : this.tableColumns) {
            for (Column columnObject : this.columnsPermList) {
                // Take into account columns can house variable methods (thus only need to partially match the column name, for prefix)
                if (columnObject.isVariable() && column[0].contains(columnObject.getColumnName())) {
                    if (this.constraints.isValid(column[0])) {
                        if (columnObject.checkPermissionValue(isReadablePerm)) {
                            columnNames.add(column[0]);
                        }
                    }
                } else if (column[0].equals(columnObject.getColumnName())) {
                    if (this.constraints.isValid(column[0])) {
                        if (columnObject.checkPermissionValue(isReadablePerm)) {
                            columnNames.add(column[0]);
                        }
                    }
                }
            }
        }

        return columnNames;

    }

    /**
     * Get all column names found within the table that return true for a specified permission plus isReadable.
     * @return the column names in a list of Strings
     */
    public List<String> getColumnNames(String permission) {
        List<String> columnNames = new ArrayList<>();

        for(String[] column : this.tableColumns) {
            for (Column columnObject : this.columnsPermList) {
                if (columnObject.isVariable() && column[0].contains(columnObject.getColumnName())) {
                    if (this.constraints.isValid(column[0])) {
                        if (columnObject.checkPermissionValue(isReadablePerm)) {
                            if (columnObject.checkPermissionValue(permission)) {
                                columnNames.add(column[0]);
                            }
                        }
                    }
                } else if (column[0].equals(columnObject.getColumnName())) {
                    if (this.constraints.isValid(column[0])) {
                        if (columnObject.checkPermissionValue(isReadablePerm)) {
                            if (columnObject.checkPermissionValue(permission)) {
                                columnNames.add(column[0]);
                            }
                        }
                    }
                }
            }
        }

        return columnNames;

    }

    /**
     * Get all column names found within the table Unlike {@link SQLTable#getColumnTypes()}, this method does NOT check for {@link com.crowdcoin.mainBoard.table.permissions.IsReadable} permissions nor {@link com.crowdcoin.networking.sqlcom.data.constraints.SQLConstraint}.
     * Only use this method with classes that are not user visible
     * @return the column names in a list of Strings
     */
    public List<String> getRawColumnNames() {

        List<String> columnNames = new ArrayList<>();

        for (int index = 0; index < this.tableColumns.size(); index++) {
                columnNames.add(this.tableColumns.get(index)[0]);
        }

        return columnNames;

    }

    /**
     * Get all column types found within the table (in-order)
     * @return the column types as specified within the database in a list of Strings
     */
    public List<String> getColumnTypes() {

        List<String> columnTypes = new ArrayList<>();

        for (int index = 0; index < this.tableColumns.size(); index++) {
            if (this.constraints.isValid(this.tableColumns.get(index)[0])) {
                // Check perms before adding
                if (this.columnsPermList.get(index).checkPermissionValue(isReadablePerm)) {
                    columnTypes.add(this.tableColumns.get(index)[1]);
                }
            }
        }

        return columnTypes;

    }

    /**
     * Get all column types found within the table (in-order). Unlike {@link SQLTable#getColumnTypes()}, this method does NOT check for {@link com.crowdcoin.mainBoard.table.permissions.IsReadable} permissions.
     * Only use this method with classes that are not user visible
     * @return the column types as specified within the database in a list of Strings
     */
    public List<String> getRawColumnTypes() {

        List<String> columnTypes = new ArrayList<>();

        for (int index = 0; index < this.tableColumns.size(); index++) {
            columnTypes.add(this.tableColumns.get(index)[1]);
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

    /**
     * Gets the filter manager for the given SQLTable object. Used to add filters to SQL queries. The FilterManager returned is live, thus modifying its contents will automatically apply within the SQLTable object
     * @return the FilterManager object for the SQLTable
     */
    public FilterManager getFilterManager() {
        return this.filterManager;
    }

    // Method to get a Column object (mainly to check permissions) from a column name
    private Column getColumnObject(String columnName) {

        for (Column column : this.columnsPermList) {
            if (columnName.contains(column.getColumnName())) {
                return column;
            }
        }

        return null;

    }

    public String getTableName() {
        return this.tableName;
    }

    /**
     * Gets the SQLDatabase object for higher level queries
     * @return an SQLDatabase object. All SQLTable classes share the same SQLDatabase object (as they all connect to the same database)
     */
    public SQLDatabase getDatabase() {
        return database;
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
    public void removeObserving() {
        database.removeObserver(this);
    }

    @Override
    public void update(ModifyEvent event) {
        this.notifyObservers(event);
    }
}
