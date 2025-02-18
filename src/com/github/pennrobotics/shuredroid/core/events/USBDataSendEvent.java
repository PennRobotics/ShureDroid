package com.github.pennrobotics.shuredroid.core.events;

public class USBDataSendEvent {
    private final String data;
    private final byte[] bytes;

    public USBDataSendEvent(String data) {
        this.data = data;
        bytes = new byte[]{};
    }

    public USBDataSendEvent(byte[] bytes) {
        this.bytes = bytes;
        data = "";
    }

    public String getData() {
        return data;
    }

    public byte[] getBytes() {
        return bytes;
    }

}