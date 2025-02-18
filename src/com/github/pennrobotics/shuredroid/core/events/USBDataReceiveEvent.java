package com.github.pennrobotics.shuredroid.core.events;

import com.github.pennrobotics.shuredroid.core.USBUtils;

public class USBDataReceiveEvent {
    private final byte[] bytes;
    private final int bytesCount;

    public USBDataReceiveEvent(byte[] bytes) {
        this.bytes = bytes;
        this.bytesCount = bytes.length;
    }

    public String getData() {
        return USBUtils.toHex(bytes);
    }

    public int getBytesCount() {
        return bytesCount;
    }

}