package com.crowdcoin.newwork;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;
import com.crowdcoin.mainBoard.table.Column;
import com.crowdcoin.newwork.names.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * A class for creating SQL SELECT queries (SELECT-FROM-WHERE)
 */
public class SelectQuery implements QueryBuilder {
    private FilterManager whereManager;
    private List<Column> columnNames;
    private List<Table> tableNames;

    public SelectQuery() {
        this.whereManager = new FilterManager();
        this.columnNames = new ArrayList<>();
        this.tableNames = new ArrayList<>();
    }

    /**
     * Adds a {@link Table} object for the FROM portion of the SELECT-FROM-WHERE clause. The object can only exist once in the {@link SelectQuery} object.
     * If the table name contains an alias, it will automatically be added to the final query when calling {@link QueryBuilder#getQuery()}.
     * The order in which table names are displayed in the final query is dependent on the insertion order (i.e., which table called this method first)
     * @param tableToAdd the {@link Table} object to add
     * @return true if added, false otherwise
     */
    public boolean addTable(Table tableToAdd) {
        if (!this.tableNames.contains(tableToAdd)) {
            return this.tableNames.add(tableToAdd);
        }
        return false;
    }

    /**
     * Adds a {@link Column} object for the SELECT portion of the SELECT-FROM-WHERE clause. The object can only exist once in the {@link SelectQuery} object.
     * If the column name contains an alias, it will automatically be added to the final query when calling {@link QueryBuilder#getQuery()}.
     * The order in which column names are displayed in the final query is dependent on the insertion order (i.e., which column called this method first)
     * @param columnToAdd the {@link Column} object to add
     * @return true if added, false otherwise
     */
    public boolean addColumn(Column columnToAdd) {
        if (!this.columnNames.contains(columnToAdd)) {
            return this.columnNames.add(columnToAdd);
        }
        return false;
    }

    /**
     * Adds a {@link Filter} object for the WHERE portion of the SELECT-FROM-WHERE clause. The object can only exist once in the {@link SelectQuery} object.
     * {@link Filter#getTargetColumnName()} must match that of a table name or the table name alias already within the {@link SelectQuery} object IF a table name is specified (such as "tableName.columnName").
     * Order of insertion into the WHERE clause in the final query is dependent on the insertion order of {@link FilterManager} (calling this method will call on a {@link FilterManager})
     * @param filterToAdd the given filter to add
     * @return true if added, false otherwise
     */
    public boolean addFilter(Filter filterToAdd) {

        String targetName = filterToAdd.getTargetColumnName();
        // TableName.ColumnName is possible in SQL, thus try to split on the .
        String[] targets = targetName.split("\\.");

        if (targets.length > 1) {
            for (Table table : this.tableNames) {
                if (table.getName().equals(targets[0]) || table.getAlias().equals(targets[0])) {
                    return this.whereManager.add(filterToAdd);
                }
            }
            return false;
        }

        return this.whereManager.add(filterToAdd);
    }

    private String buildSelect() {
        String select = "SELECT ";
        for (int index = 0; index < this.columnNames.size(); index++) {
            if (index == 0) {
                select += this.columnNames.get(index).getColumnAsQuery();
            } else {
                select += ", " + this.columnNames.get(index).getColumnAsQuery();
            }
        }
        return select;
    }

    private String buildFrom() {
        String from = " FROM ";
        for (int index = 0; index < this.tableNames.size(); index++) {
            if (index == 0) {
                from += this.tableNames.get(index).getTableAsQuery();
            } else {
                from += ", " + this.tableNames.get(index).getTableAsQuery();
            }
        }
        return from;
    }
    
    @Override
    public String getQuery() {
        return buildSelect() + buildFrom() + this.whereManager.getCombinedQuery() + ";";
    }
}
