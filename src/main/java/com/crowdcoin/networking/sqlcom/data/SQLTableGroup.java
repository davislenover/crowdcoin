package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.UnknownColumnNameException;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.ModifyEventType;
import com.crowdcoin.mainBoard.table.permissions.PermissionNames;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.query.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class SQLTableGroup extends SQLTable implements SQLQueryGroup {

    private List<QueryBuilder> queries;
    private List<ModifyEvent> events;

    /**
     * An object to get information from an SQL database
     * @param connection       the current connection to communicate with the database
     * @param tableName        the table within the database to get data from. Note the ordering of the columns when returning rows is via ordinal position (as raw ResultSets do NOT guarantee specific column ordering on queries thus SQLTable is intended deal with it via sorting the ResultSet via ordinal position)
     * @param columnObjectList the list of Column objects representing which contain permission data about each column. Each column name (from {@link Column#getColumnName()}) within this list must match that of a column from the SQL Table. The list is copied and thus, reference of list passed into object is only used for creation and not modified (Column objects stored within may be modified).
     * @throws FailedQueryException       if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause. It may indicate the table provided does not exist within the database
     * @throws SQLException               if a database access error occurs
     * @throws UnknownColumnNameException if one or more Column object names (from {@link Column#getColumnName()}) do not match any SQL column name
     */
    public SQLTableGroup(SQLConnection connection, String tableName, List<Column> columnObjectList) throws FailedQueryException, SQLException, UnknownColumnNameException {
        super(connection, tableName, columnObjectList);
        this.queries = new ArrayList<>();
        this.events = new ArrayList<>();
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
    public void writeToRow(int columnWriteIndex, String dataToWrite, int columnWhereIndex, String dataWhereRead) throws IndexOutOfBoundsException {

        // Error checking
        if (columnWriteIndex > super.getRawColumnNames().size()-1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWriteIndex + " where max is " + (super.getRawColumnNames().size()-1) + ")");
        }

        if (columnWhereIndex > super.getRawColumnNames().size()-1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWhereIndex + " where max is " + (super.getRawColumnNames().size()-1) + ")");
        }

        if (!super.checkPermissions(columnWriteIndex,PermissionNames.ISWRITEABLE.getName())) {
            throw new IllegalAccessError("ColumnWriteIndex, " + columnWriteIndex + ", user is not permitted to write to");
        }

        // Add query and event to list
        queries.add(new InsertSingleValueIntoRowQuery(super.getTableName(),super.getRawColumnNames().get(columnWriteIndex),dataToWrite,super.getRawColumnNames().get(columnWhereIndex),dataWhereRead));
        events.add(new ModifyEvent(ModifyEventType.ROW_MODIFIED,this.getTableName()));

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

        if (columnWhereIndex > super.getRawColumnNames().size()-1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWhereIndex + " where max is " + (super.getRawColumnNames().size()-1) + ")");
        }

        // Check if any column name is not within the table (error-checking)
        for (String columnName : columnsToInsertData) {

            boolean doesExist = false;
            for (String checkName : super.getRawColumnNames()) {

                if (checkName.equals(columnName)) {
                    doesExist = true;
                    break;
                }

            }

            if (!doesExist) {
                throw new UnknownColumnNameException(columnName);
            }

            if (!super.checkPermissions(columnName,PermissionNames.ISWRITEABLE.getName())) {
                throw new IllegalAccessError("One or more columns user is not permitted to write to");
            }

        }

        // Add query and event to list
        this.queries.add(new InsertIntoRowQuery(super.getTableName(),columnsToInsertData,correspondingDataToInsert,super.getRawColumnNames().get(columnWhereIndex),dataWhereRead));
        this.events.add(new ModifyEvent(ModifyEventType.ROW_MODIFIED,this.getTableName()));

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
            for (String checkName : super.getRawColumnNames()) {

                if (checkName.equals(columnName)) {
                    doesExist = true;
                    break;
                }

            }

            if (!doesExist) {
                throw new UnknownColumnNameException(columnName);
            }

            if (!super.checkPermissions(columnName,PermissionNames.ISWRITEABLE.getName())) {
                throw new IllegalAccessError("One or more columns user is not permitted to write to");
            }

        }

        // Add query and event to list
        this.queries.add(new NewRowQuery(super.getTableName(),columnsToInsertData,correspondingDataToInsert));
        this.events.add(new ModifyEvent(ModifyEventType.NEW_ROW,this.getTableName()));

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
            for (String checkName : super.getRawColumnNames()) {

                if (checkName.equals(columnName)) {
                    doesExist = true;
                    break;
                }

            }

            if (!doesExist) {
                throw new UnknownColumnNameException(columnName);
            }

            if (!super.checkPermissions(columnName,PermissionNames.ISSYSTEMWRITEABLE.getName())) {
                throw new IllegalAccessError("One or more columns system is not permitted to write to");
            }

        }

        // Add query and event to list
        this.queries.add(new NewRowQuery(super.getTableName(),columnsToInsertData,correspondingDataToInsert));
        this.events.add(new ModifyEvent(ModifyEventType.NEW_ROW,this.getTableName()));

    }

    /**
     * Deletes a row from the SQL database table
     * @param columnWhereIndex the corresponding column to look for data
     * @param dataWhereRead the data within the corresponding column. This identifies the row which will be removed
     */
    public void deleteRow(int columnWhereIndex, String dataWhereRead) throws FailedQueryException {

        if (columnWhereIndex > super.getRawColumnNames().size() - 1) {
            throw new IndexOutOfBoundsException("The specified column does not exist within the table (" + columnWhereIndex + " where max is " + (super.getRawColumnNames().size()-1) + ")");
        }

        this.queries.add(new DeleteRowQuery(super.getTableName(),super.getRawColumnNames().get(columnWhereIndex),dataWhereRead));
        this.events.add(new ModifyEvent(ModifyEventType.ROW_REMOVED,this.getTableName()));

    }

    /**
     * Execute all queries in group. Afterward, all queries in group are cleared.
     * @throws SQLException if any one of the queries fails. {@link SQLConnection#rollBack()} is automatically called and all successful queries (if any) are rollback. Regardless of exception, all queries in group will be cleared
     */
    @Override
    public void executeQueries() throws SQLException {
        SQLConnection connection = super.getConnection();
        try {
            // Execute queries
            connection.executeGroupQuery(this.queries);
            // Fire all events
            for (ModifyEvent event : this.events) {
                super.notifyObservers(event);
            }
        } catch (SQLException exception) {
            connection.rollBack();
            // Clear both lists regardless of exception or not
            this.queries.clear();
            this.events.clear();
            throw exception;
        }
        this.queries.clear();
        this.events.clear();
    }

    @Override
    public void clearQueries() {
        this.queries.clear();
        this.events.clear();
    }

    @Override
    public List<QueryBuilder> getQueries() {
        return List.copyOf(this.queries);
    }

    @Override
    public List<ModifyEvent> getEvents() {
        return List.copyOf(this.events);
    }


}
