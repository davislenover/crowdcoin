package com.crowdcoin.mainBoard.table.Observe;

public class ModifyDatabaseEvent implements ObservableEvent<ModifyDatabaseEventTypes> {

    private ModifyDatabaseEventTypes eventType;
    private String extraData;

    public ModifyDatabaseEvent(ModifyDatabaseEventTypes eventType) {
        this.eventType = eventType;
    }

    public ModifyDatabaseEvent(ModifyDatabaseEventTypes eventType, String extraData) {
        this.eventType = eventType;
        this.extraData = extraData;
    }

    @Override
    public ModifyDatabaseEventTypes getEventData() {
        return eventType;
    }

    public void setExtraData(String data) {
        this.extraData = data;
    }

    public String getExtraData() {
        return this.extraData;
    }
}
