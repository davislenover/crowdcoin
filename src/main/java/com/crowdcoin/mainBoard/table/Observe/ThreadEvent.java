package com.crowdcoin.mainBoard.table.Observe;

import java.util.ArrayList;
import java.util.List;

public class ThreadEvent implements ObservableEvent<ThreadEventType,String> {

    private ThreadEventType threadEventType;
    private List<String> eventData;

    private boolean broadcast = false;

    public ThreadEvent(ThreadEventType eventType) {
        this.threadEventType = eventType;
        this.eventData = new ArrayList<>();
    }

    public ThreadEvent(ThreadEventType eventType, String ... eventData) {
        this.threadEventType = eventType;

        this.eventData = new ArrayList<>();
        this.eventData.addAll(List.of(eventData));
    }

    @Override
    public ThreadEventType getEventType() {
        return this.threadEventType;
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
