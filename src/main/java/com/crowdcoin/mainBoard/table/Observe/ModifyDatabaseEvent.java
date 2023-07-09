package com.crowdcoin.mainBoard.table.Observe;

/**
 * Event is triggered when anything related to an SQLTable object changes (such as when a new filter is added, or a new row was added to the database)
 */
public class ModifyDatabaseEvent implements ObservableEvent<EventType> {

    private EventType eventType;
    private String eventData;

    public ModifyDatabaseEvent(EventType eventType) {
        this.eventType = eventType;
    }

    public ModifyDatabaseEvent(EventType eventType, String eventData) {
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
