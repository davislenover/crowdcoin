package com.ratchet.observe;

import java.util.ArrayList;
import java.util.List;

public class TaskEvent implements ObservableEvent<TaskEventType,String> {

    private TaskEventType taskEventType;
    private List<String> eventData;

    private boolean broadcast = false;

    public TaskEvent(TaskEventType eventType) {
        this.taskEventType = eventType;
        this.eventData = new ArrayList<>();
    }

    public TaskEvent(TaskEventType eventType, String ... eventData) {
        this.taskEventType = eventType;

        this.eventData = new ArrayList<>();
        this.eventData.addAll(List.of(eventData));
    }

    @Override
    public TaskEventType getEventType() {
        return this.taskEventType;
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
