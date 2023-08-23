package com.crowdcoin.mainBoard.table.Observe;

import java.util.ArrayList;
import java.util.List;

public class EventAdapter<A extends ObservableEvent<? extends GeneralEventType, B>,B,C extends ObservableEvent<? extends GeneralEventType, D>,D> implements Observer<A,B>,Observable<C,D> {
    private Observable<A,B> instanceToObserve;
    private List<Observer<C,D>> subscriptionList;
    private GeneralEventType eventTypeA;
    private C convertedEvent;

    public EventAdapter(Observable<A,B> instanceToObserveForEventToConvert, Observer<C,D> instanceToFireConvertedEvent, A eventToConvert, C eventToConvertTo) {
        this.instanceToObserve = instanceToObserveForEventToConvert;
        this.instanceToObserve.addObserver(this);
        this.subscriptionList = new ArrayList<>() {{
            add(instanceToFireConvertedEvent);
        }};
        this.eventTypeA = eventToConvert.getEventType();
        this.convertedEvent = eventToConvertTo;
    }

    @Override
    public boolean addObserver(Observer<C,D> observer) {
        if (!this.subscriptionList.contains(observer)) {
            return this.subscriptionList.add(observer);
        }
        return false;
    }

    @Override
    public boolean removeObserver(Observer<C,D> observer) {
        return this.subscriptionList.remove(observer);
    }

    @Override
    public void notifyObservers(C event) {
        for (Observer<C,D> observer : this.subscriptionList) {
            observer.update(event);
        }
    }

    @Override
    public void clearObservers() {
        this.subscriptionList.clear();
    }

    @Override
    public void removeObserving() {
        this.instanceToObserve.removeObserver(this);
    }

    @Override
    public void update(A event) {
        if (event.getEventType().equals(this.eventTypeA)) {
            this.notifyObservers(this.convertedEvent);
        }
    }
}
