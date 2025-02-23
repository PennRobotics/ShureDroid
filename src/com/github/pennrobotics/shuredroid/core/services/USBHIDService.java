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
import com.github.pennrobotics.shuredroid.core.events.LogMessageEvent;
import com.github.pennrobotics.shuredroid.core.events.ShowDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataReceiveEvent;

import java.io.ByteArrayOutputStream;

public class USBHIDService extends AbstractUSBHIDService {

	private String delimiter;
	private String receiveDataFormat;

	@Override
	public void onCreate() {
		super.onCreate();
		setupNotifications();
	}

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

	private void mLog(String log) {
		eventBus.post(new LogMessageEvent(log));
	}

	private void setupNotifications() { //called in onCreate()
		NotificationManager mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		NotificationCompat.Builder mNotificationBuilder = new NotificationCompat.Builder(this);
		PendingIntent pendingIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ShureDroid.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP),
				PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
		PendingIntent pendingCloseIntent = PendingIntent.getActivity(this, 0,
				new Intent(this, ShureDroid.class)
						.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP)
						.setAction(Consts.USB_HID_TERMINAL_CLOSE_ACTION),
				PendingIntent.FLAG_IMMUTABLE | PendingIntent.FLAG_UPDATE_CURRENT);
		mNotificationBuilder
				.setSmallIcon(R.mipmap.ic_launcher)
				.setCategory(NotificationCompat.CATEGORY_SERVICE)
				.setVisibility(NotificationCompat.VISIBILITY_PUBLIC)
				.setContentTitle(getText(R.string.app_name))
				.setWhen(System.currentTimeMillis())
				.setContentIntent(pendingIntent)
				.addAction(android.R.drawable.ic_menu_close_clear_cancel,
						getString(R.string.action_exit), pendingCloseIntent)
				.setOngoing(true);
		mNotificationBuilder
				.setTicker(getText(R.string.app_name))
				.setContentText(getText(R.string.app_name));
		if (mNotificationManager != null) {
			mNotificationManager.notify(Consts.USB_HID_TERMINAL_NOTIFICATION, mNotificationBuilder.build());
		}
	}

}
