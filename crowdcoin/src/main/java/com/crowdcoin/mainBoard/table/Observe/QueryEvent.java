package com.crowdcoin.mainBoard.table.Observe;

import java.util.ArrayList;
import java.util.List;

public class QueryEvent implements ObservableEvent<QueryEventType,String> {

    private QueryEventType queryEventType;
    private List<String> eventData;

    private boolean broadcast = false;

    public QueryEvent(QueryEventType eventType) {
        this.queryEventType = eventType;
        this.eventData = new ArrayList<>();
    }

    public QueryEvent(QueryEventType eventType, String ... eventData) {
        this.queryEventType = eventType;

        this.eventData = new ArrayList<>();
        this.eventData.addAll(List.of(eventData));
    }

    @Override
    public QueryEventType getEventType() {
        return this.queryEventType;
    }

    @Override
    public List<String> getEventData() {
        return this.eventData;
    }

    @Override
    public boolean isBroadcast() {
        return this.broadcast;
    }

    @Override
    public void setBroadcast(boolean isBroadcast) {
        this.broadcast = isBroadcast;
    }

}
