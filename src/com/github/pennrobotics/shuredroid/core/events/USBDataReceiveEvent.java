package com.github.pennrobotics.shuredroid.core.events;

import com.github.pennrobotics.shuredroid.core.USBUtils;

public class USBDataReceiveEvent {
    private final byte[] bytes;

    public USBDataReceiveEvent(byte[] bytes) {
        this.bytes = bytes;
    }

    public byte[] getData() {
        return bytes;
    }

    public String getDataAsHex() {
        return USBUtils.toHex(bytes);
    }

}