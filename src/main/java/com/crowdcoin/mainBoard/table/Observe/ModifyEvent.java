package com.crowdcoin.mainBoard.table.Observe;

import java.util.ArrayList;
import java.util.List;

/**
 * Event is triggered when something is modified (say an SQL database has a new row or a new filter was added)
 */
public class ModifyEvent implements ObservableEvent<ModifyEventType,String> {

    private ModifyEventType modifyEventType;
    private List<String> eventData;
    private boolean broadcast = false;

    public ModifyEvent(ModifyEventType modifyEventType) {
        this.modifyEventType = modifyEventType;
        this.eventData = new ArrayList<>();
    }

    public ModifyEvent(ModifyEventType modifyEventType, String ... eventData) {
        this.modifyEventType = modifyEventType;

        this.eventData = new ArrayList<>();
        this.eventData.addAll(List.of(eventData));
    }

    @Override
    public ModifyEventType getEventType() {
        return modifyEventType;
    }

    public void addEventData(String data) {
        this.eventData.add(data);
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
