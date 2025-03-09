package com.github.pennrobotics.shuredroid.core.services;

import android.content.Intent;
import android.hardware.usb.UsbDevice;

import com.github.pennrobotics.shuredroid.core.events.DeviceAttachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DeviceDetachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DevicePluggedEvent;
import com.github.pennrobotics.shuredroid.core.events.ShowDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataReceiveEvent;

public class USBHIDService extends AbstractUSBHIDService {
	@Override
	public void onCommand(Intent intent, String action, int flags, int startId) {
		super.onCommand(intent, action, flags, startId);
	}

	@Override
	public void onDeviceConnected(UsbDevice device) {
		eventBus.post(new DevicePluggedEvent());
		//mLog(device.getProductName() + " connected");
	}

	@Override
	public void onDeviceDisconnected(UsbDevice device) {
		//mLog("Disconnected");
		eventBus.post(new DeviceDetachedEvent());
	}

	@Override
	public void onDeviceSelected(UsbDevice device) {
		//mLog("Connected to adapter: " + device.getProductName());
	}

	@Override
	public void onDeviceAttached(UsbDevice device) {
		eventBus.post(new DeviceAttachedEvent());
	}

	@Override
	public void onShowDevicesList(CharSequence[] deviceName) {
		eventBus.post(new ShowDevicesListEvent(deviceName));
	}

	@Override
	public CharSequence onBuildingDevicesList(UsbDevice usbDevice) {
		return usbDevice.getDeviceName() + " devID:" + usbDevice.getDeviceId();
	}

//	@Override
//	public void onUSBDataSent(int status, byte[] out) {
//	}

	@Override
	public void onUSBDataReceive(byte[] buffer) {
		eventBus.post(new USBDataReceiveEvent(buffer));
	}
}
