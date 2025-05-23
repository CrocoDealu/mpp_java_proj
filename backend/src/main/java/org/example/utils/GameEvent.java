package org.example.utils;


public class GameEvent {
    private String eventType;
    private Object payload;

    public GameEvent() {}

    public GameEvent(String eventType, Object payload) {
        this.eventType = eventType;
        this.payload = payload;
    }

    public String getEventType() {
        return eventType;
    }

    public void setEventType(String eventType) {
        this.eventType = eventType;
    }

    public Object getPayload() {
        return payload;
    }

    public void setPayload(Object payload) {
        this.payload = payload;
    }
}
