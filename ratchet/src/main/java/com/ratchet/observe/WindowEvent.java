package com.ratchet.observe;

import java.util.ArrayList;
import java.util.List;

public class WindowEvent implements ObservableEvent<WindowEventType,String> {

    private WindowEventType winodwEventType;
    private List<String> eventData;

    private boolean broadcast = false;

    public WindowEvent(WindowEventType windowEventType) {
        this.winodwEventType = windowEventType;
        this.eventData = new ArrayList<>();
    }

    public WindowEvent(WindowEventType windowEventType, String eventData) {
        this.winodwEventType = windowEventType;

        this.eventData = new ArrayList<>();
        this.eventData.addAll(List.of(eventData));
    }

    @Override
    public WindowEventType getEventType() {
        return this.winodwEventType;
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
