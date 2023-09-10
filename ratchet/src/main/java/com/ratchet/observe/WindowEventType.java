package com.ratchet.observe;

public enum WindowEventType implements GeneralEventType {
    CLOSE_WINDOW("Close the window");

    private String description;
    WindowEventType(String s) {
        this.description = s;
    }

    @Override
    public String description() {
        return null;
    }
}
