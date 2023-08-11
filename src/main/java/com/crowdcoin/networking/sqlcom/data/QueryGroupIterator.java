package com.crowdcoin.networking.sqlcom.data;

import com.crowdcoin.mainBoard.table.Observe.ModifyEvent;
import com.crowdcoin.networking.sqlcom.SQLConnection;
import com.crowdcoin.networking.sqlcom.query.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class QueryGroupIterator {

    private List<SQLQueryGroup> groups;
    private List<SQLQueryGroup> completedGroups;
    private int groupIndex = 0;
    private boolean queriesPassed = true;

    public QueryGroupIterator() {
        this.groups = new ArrayList<>();
        this.completedGroups = new ArrayList<>();
    }

    public void addGroup(SQLQueryGroup group) {
        this.groups.add(group);
    }

    public void executeNextGroup() throws SQLException {
        SQLQueryGroup group = this.groups.get(groupIndex);
        List<QueryBuilder> queries = group.getQueries();
        List<ModifyEvent> events = group.getEvents();
        group.clearQueries();
        SQLConnection groupConnection =  group.getConnection();
        try {
            groupConnection.executeGroupQueryNoCommit(queries);
            for (ModifyEvent event : events) {
                group.notifyObservers(event);
            }
            this.completedGroups.add(group);
            this.groupIndex++;
        } catch (SQLException exception) {
            for (SQLQueryGroup curGroup : this.groups) {
                curGroup.clearQueries();
            }
            try {
                groupConnection.rollBack();
                for (SQLQueryGroup completeGroup : this.completedGroups) {
                    completeGroup.getConnection().rollBack();
                }
                queriesPassed = false;
                return;
            } catch (Exception exception2) {
            }
        }

        if (this.groupIndex == this.groups.size() && this.queriesPassed) {
            for (SQLQueryGroup completeGroup : this.completedGroups) {
                completeGroup.getConnection().commitGroupQuery();
            }
        }

    }


}
