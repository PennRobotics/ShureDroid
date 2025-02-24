package com.github.pennrobotics.shuredroid.core.services;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;

import androidx.core.app.NotificationCompat;

import com.github.pennrobotics.shuredroid.R;
import com.github.pennrobotics.shuredroid.ShureDroid;
import com.github.pennrobotics.shuredroid.core.Consts;
import com.github.pennrobotics.shuredroid.core.USBUtils;
import com.github.pennrobotics.shuredroid.core.events.DeviceAttachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DeviceDetachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DevicePluggedEvent;
import com.github.pennrobotics.shuredroid.core.events.LogMessageEvent;
import com.github.pennrobotics.shuredroid.core.events.ShowDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataReceiveEvent;

import java.io.ByteArrayOutputStream;

public class USBHIDService extends AbstractUSBHIDService {

	private String delimiter;
	private String receiveDataFormat;

	@Override
	public void onCreate()  { super.onCreate(); }

	@Override
	public void onCommand(Intent intent, String action, int flags, int startId) {
		super.onCommand(intent, action, flags, startId);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
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

	private String showDecHex(int data) {
		return  data + " 0x" + Integer.toHexString(data);
	}

	@Override
	public CharSequence onBuildingDevicesList(UsbDevice usbDevice) {
		return usbDevice.getDeviceName() + " devID:" + usbDevice.getDeviceId();
	}

	@Override
	public void onUSBDataSent(int status, byte[] out) {
		if (status <= 0) {
			//mLog("Unable to send (" + status + ")");
		} else {
			//mLog("Sent " + status + " bytes");
			//mLog(USBUtils.toHex(out));
		}
	}

	@Override
	public void onUSBDataReceive(byte[] buffer) {
		eventBus.post(new USBDataReceiveEvent(buffer));
	}
}
