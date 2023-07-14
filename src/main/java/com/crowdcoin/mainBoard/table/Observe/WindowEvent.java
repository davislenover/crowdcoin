package com.crowdcoin.mainBoard.table.Observe;

public class WindowEvent implements ObservableEvent<WindowEventType> {

    private WindowEventType winodwEventType;
    private String eventData;

    public WindowEvent(WindowEventType windowEventType) {
        this.winodwEventType = windowEventType;
    }

    public WindowEvent(WindowEventType windowEventType, String eventData) {
        this.winodwEventType = windowEventType;
        this.eventData = eventData;
    }

    @Override
    public WindowEventType getEventType() {
        return this.winodwEventType;
    }

    @Override
    public String getEventData() {
        return this.eventData;
    }
}
