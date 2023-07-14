package com.crowdcoin.mainBoard.table.Observe;

/**
 * Event is triggered when something is modified (say an SQL database has a new row or a new filter was added)
 */
public class ModifyEvent implements ObservableEvent<ModifyEventType> {

    private ModifyEventType modifyEventType;
    private String eventData;

    public ModifyEvent(ModifyEventType modifyEventType) {
        this.modifyEventType = modifyEventType;
    }

    public ModifyEvent(ModifyEventType modifyEventType, String eventData) {
        this.modifyEventType = modifyEventType;
        this.eventData = eventData;
    }

    @Override
    public ModifyEventType getEventType() {
        return modifyEventType;
    }

    public void setEventData(String data) {
        this.eventData = data;
    }
    @Override
    public String getEventData() {
        return this.eventData;
    }
}
