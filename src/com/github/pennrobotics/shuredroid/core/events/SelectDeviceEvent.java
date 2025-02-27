package com.github.pennrobotics.shuredroid.core.events;

public class SelectDeviceEvent {
    private final int device;

    public SelectDeviceEvent(int device) {
        this.device = device;
    }

    public int getDevice() {
        return device;
    }
}