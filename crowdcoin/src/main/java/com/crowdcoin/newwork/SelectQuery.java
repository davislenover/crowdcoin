package com.crowdcoin.newwork;

import com.crowdcoin.networking.sqlcom.data.filter.Filter;
import com.crowdcoin.networking.sqlcom.data.filter.FilterManager;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;
import com.crowdcoin.newwork.names.Column;
import com.crowdcoin.newwork.names.Table;

import java.util.ArrayList;
import java.util.List;

public class SelectQuery implements QueryBuilder {
    private FilterManager whereManager;
    private List<Column> columnNames;
    private List<Table> tableNames;

    public SelectQuery() {
        this.whereManager = new FilterManager();
        this.columnNames = new ArrayList<>();
        this.tableNames = new ArrayList<>();
    }

    public boolean addTable(Table tableToAdd) {
        if (!this.tableNames.contains(tableToAdd)) {
            return this.tableNames.add(tableToAdd);
        }
        return false;
    }

    public boolean addColumn(Column columnToAdd) {
        if (!this.columnNames.contains(columnToAdd)) {
            return this.columnNames.add(columnToAdd);
        }
        return false;
    }

    public boolean addFilter(Filter filterToAdd) {
        String targetName = filterToAdd.getTargetColumnName();
        for (Column columnName : this.columnNames) {
            if (columnName.getName().equals(targetName) || columnName.getAlias().equals(targetName)) {
                return this.whereManager.add(filterToAdd);
            }
        }
        return false;
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
