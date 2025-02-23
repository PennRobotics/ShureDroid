package com.github.pennrobotics.shuredroid.core.events;

public class USBDataSendEvent {
    private final byte[] bytes;

    public USBDataSendEvent(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getBytes() {
        return bytes;
    }

}