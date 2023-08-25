package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.exceptions.network.FailedQueryException;
import com.crowdcoin.exceptions.table.UnknownColumnNameException;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.mainBoard.table.Observe.Observer;
import com.crowdcoin.mainBoard.table.Observe.TaskEvent;
import com.crowdcoin.mainBoard.table.permissions.PermissionNames;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.data.constraints.ConstraintContainer;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.query.GetColumnDataQuery;
import com.crowdcoin.networking.sqlcom.query.QueryPrefix;
import com.crowdcoin.threading.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.BlockingDeque;
import java.util.concurrent.LinkedBlockingDeque;

public class TempSQLTable implements Observer<TaskEvent,String> {
    private SQLTableGroup tableGroup = null;
    private List<Observer<ModifyEvent,String>> subscriptionList;
    private String tableName;
    // Within the String array (inside the list), index 0 corresponds to table name, 1 is data type as specified in SQL table, 2 specifies the ordinal position
    // (List, NOT String array) It is important to NOT directly correspond indices of columns to ordinal positions as column indices within this list are ordered in RELATIVE ordinal position. Ordinal positions start at 1. This means index 0 would correspond to an ordinal position of 1, 1 to 2, 2 to 3, etc
    private List<String[]> tableColumns;
    private SQLConnection connection;
    private FilterManager filterManager;
    private List<Column> columnsPermList;
    private String isReadablePerm = PermissionNames.ISREADABLE.getName();
    private String isWriteablePerm = PermissionNames.ISWRITEABLE.getName();
    private String isSystemWriteablePerm = PermissionNames.ISSYSTEMWRITEABLE.getName();
    private ConstraintContainer constraints;
    // Used when calling getGroupFilteredRows to store how many rows in total were retrieved (as some are excluded)
    private int lastRowAddedIndex = 0;
    private TaskManager taskMgr = TaskTools.getTaskManager();

    {
        this.taskMgr.addObserver(this);
    }

    private SetupSQLTable setupObj = new SetupSQLTable();

    private BlockingDeque<Task<?>> queryTasks = new LinkedBlockingDeque<>();
    private List<Object> resultFromQuery = null;

    /**
     * An object to get information from an SQL database
     * @param connection the current connection to communicate with the database
     * @param tableName the table within the database to get data from. Note the ordering of the columns when returning rows is via ordinal position (as raw ResultSets do NOT guarantee specific column ordering on queries thus SQLTable is intended deal with it via sorting the ResultSet via ordinal position)
     * @param columnObjectList the list of Column objects representing which contain permission data about each column. Each column name (from {@link Column#getColumnName()}) within this list must match that of a column from the SQL Table. The list is copied and thus, reference of list passed into object is only used for creation and not modified (Column objects stored within may be modified).
     * @throws FailedQueryException if the query fails to execute. This could be for a multitude of reasons and is recommended to get rootException within this exception for exact cause. It may indicate the table provided does not exist within the database
     * @throws SQLException if a database access error occurs
     * @throws UnknownColumnNameException if one or more Column object names (from {@link Column#getColumnName()}) do not match any SQL column name
     */
    public TempSQLTable(SQLConnection connection, String tableName, List<Column> columnObjectList) throws FailedQueryException, SQLException, UnknownColumnNameException {

        // Setup
        this.connection = connection;
        this.tableName = tableName;
        this.filterManager = new FilterManager();

        // Create new list (as modification of order may be needed)
        this.columnsPermList = new ArrayList<>() {{
            addAll(columnObjectList);
        }};

        this.subscriptionList = new ArrayList<>();
        this.constraints = new ConstraintContainer();

        this.setupObj.setupSQLTable();

    }

    private class SetupSQLTable {
        private static String getTableDataTaskId = "getData";
        private static String checkColumnNamesTaskId = "checkNames";
        private static String sortColumnObjectListTaskId = "sortColumns";
        private static String groupTaskId = "setupSQLTable";

        // Task gets table information and set's up tableColumn list
        private VoidTask getTableDataTask = new VoidTask() {
            @Override
            public Void runTask() throws TaskException {
                try {
                    tableColumns = new ArrayList<>();

                    // Use information schema to get all table information for the specified table name
                    ResultSet result = connection.sendQuery(new GetColumnDataQuery(tableName));

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

                        tableColumns.add(newColumn);

                    }

                    // Sort each column within list by ordinal position (where o1 and o2 are the corresponding String arrays within the list to compare)
                    tableColumns.sort((o1, o2) -> {

                        // Get ordinal position
                        int o1Value = Integer.valueOf(o1[2]);
                        int o2Value = Integer.valueOf(o2[2]);

                        // Return result of comparing both ordinal positions
                        return Integer.compare(o1Value,o2Value);
                    });

                    result.close();
                } catch (Exception exception) {
                    throw new TaskException(exception);
                }
                return null;
            }
        };

        {
            getTableDataTask.setPriority(TaskPriority.VITAL);
            getTableDataTask.setTaskId(this.getTableDataTaskId);
        }

        // Task to check that Column object names match that of one column name from the SQL table
        private VoidTask checkColumnNamesTask = new VoidTask() {
            @Override
            public Void runTask() throws TaskException {
                try {
                    if (columnsPermList.size() == tableColumns.size()) {
                        for (Column column : columnsPermList) {

                            boolean hasMatch = false;

                            for (String[] SQLColumnName : tableColumns) {

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

                        for (Column column : columnsPermList) {
                            if (column.isVariable()) {
                                return null;
                            }
                        }
                        throw new IndexOutOfBoundsException("Column object list and SQL table columns list are not the same size");
                    }
                } catch (Exception exception) {
                    throw new TaskException(exception);
                }
                return null;
            }
        };

        {
            checkColumnNamesTask.setPriority(TaskPriority.URGENT);
            checkColumnNamesTask.setTaskId(this.checkColumnNamesTaskId);
        }

        private VoidTask sortColumnObjectListTask = new VoidTask() {
            @Override
            public Void runTask() throws TaskException {
                // First, set ordinal position of each Column
                for (String[] column : tableColumns) {
                    getColumnObject(column[0]).setOrdinalPosition(Integer.valueOf(column[2]));
                }

                // Sort using custom comparator
                columnsPermList.sort((Comparator.comparingInt(Column::getOrdinalPosition)));
                return null;
            }
        };

        {
            sortColumnObjectListTask.setPriority(TaskPriority.CRITICAL);
            sortColumnObjectListTask.setTaskId(this.sortColumnObjectListTaskId);
        }

        private VoidGroupTask groupedTask = new VoidGroupTask() {{
            addTask(getTableDataTask);
            addTask(checkColumnNamesTask);
            addTask(sortColumnObjectListTask);
            setTaskId(groupTaskId);
            setPriority(TaskPriority.VITAL);
        }};

        public void setupSQLTable() {
            taskMgr.addTask(this.groupedTask);
            taskMgr.runNextTask();
        }
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













    @Override
    public void removeObserving() {
        this.taskMgr.removeObserver(this);
    }

    @Override
    public void update(TaskEvent event) {
        System.out.println("EVENT: " + event.getEventType().toString());
        if (event.getEventData().get(0).equals(SetupSQLTable.groupTaskId)) {
            System.out.println("COMPLETE!");
        }
    }

    public List<Object> getResult() {
        return this.resultFromQuery;
    }
}
