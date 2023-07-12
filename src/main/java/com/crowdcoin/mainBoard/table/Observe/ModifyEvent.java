package com.crowdcoin.mainBoard.table.Observe;

/**
 * Event is triggered when something is modified (say an SQL database has a new row or a new filter was added)
 */
public class ModifyEvent implements ObservableEvent<EventType> {

    private EventType eventType;
    private String eventData;

    public ModifyEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public ModifyEvent(EventType eventType, String eventData) {
        this.eventType = eventType;
        this.eventData = eventData;
    }

    @Override
    public EventType getEventType() {
        return eventType;
    }

    public void setEventData(String data) {
        this.eventData = data;
    }
    @Override
    public String getEventData() {
        return this.eventData;
    }
}
