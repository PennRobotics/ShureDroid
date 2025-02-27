package com.github.pennrobotics.shuredroid.core.services;

import java.util.LinkedList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

import com.github.pennrobotics.shuredroid.core.Consts;
import com.github.pennrobotics.shuredroid.core.events.PrepareDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.SelectDeviceEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataSendEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public abstract class AbstractUSBHIDService extends Service {

	private static final String TAG = AbstractUSBHIDService.class.getCanonicalName();

	private USBThreadDataReceiver usbThreadDataReceiver;

	private final Handler uiHandler = new Handler();

	private List<UsbInterface> interfacesList = null;

	private UsbManager mUsbManager;
	private UsbDeviceConnection connection;
	private UsbDevice device;

	private IntentFilter filter;
	private PendingIntent mPermissionIntent;

    protected EventBus eventBus = EventBus.getDefault();

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@SuppressLint("UnspecifiedRegisterReceiverFlag")
    @Override
	public void onCreate() {
		super.onCreate();
		mPermissionIntent = PendingIntent.getBroadcast(
				this,
				0,
				new Intent(Consts.ACTION_USB_PERMISSION),
				PendingIntent.FLAG_UPDATE_CURRENT| (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S ? PendingIntent.FLAG_MUTABLE | PendingIntent.FLAG_ALLOW_UNSAFE_IMPLICIT_INTENT : 0)
				);
		filter = new IntentFilter(Consts.ACTION_USB_PERMISSION);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_ATTACHED);
		filter.addAction(UsbManager.ACTION_USB_DEVICE_DETACHED);
		filter.addAction(Consts.ACTION_USB_SHOW_DEVICES_LIST);
		filter.addAction(Consts.ACTION_USB_DATA_TYPE);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
			registerReceiver(mUsbReceiver, filter, Context.RECEIVER_EXPORTED);
		} else {
			registerReceiver(mUsbReceiver, filter);
		}
		eventBus.register(this);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		String action = intent.getAction();
		if (Consts.ACTION_USB_DATA_TYPE.equals(action)) {
            intent.getBooleanExtra(Consts.ACTION_USB_DATA_TYPE, false);
		}
		onCommand(intent, action, flags, startId);
		return START_REDELIVER_INTENT;
	}

	@Override
	public void onDestroy() {
		eventBus.unregister(this);
		super.onDestroy();
		if (usbThreadDataReceiver != null) {
			usbThreadDataReceiver.stopThis();
		}
		unregisterReceiver(mUsbReceiver);
	}

	private class USBThreadDataReceiver extends Thread {

		private volatile boolean isStopped;

		public USBThreadDataReceiver() {
		}

		@Override
		public void run() {
			try {
				if (connection != null) {
					while (!isStopped) {
						for (UsbInterface intf: interfacesList) {
							for (int i = 0; i < intf.getEndpointCount(); i++) {
								//eventBus.post(new LogMessageEvent("(" + Integer.toString(i) + ")"));  // TODO-debug
								UsbEndpoint endPointRead = intf.getEndpoint(i);
								if (UsbConstants.USB_DIR_IN == endPointRead.getDirection()) {
									final byte[] buffer = new byte[endPointRead.getMaxPacketSize()];
									if (0 < connection.bulkTransfer(endPointRead, buffer, buffer.length, 100)) {
										uiHandler.post(() -> onUSBDataReceive(buffer));
									}
								}
							}
						}
					}
				}
			} catch (Exception e) {
				Log.e(TAG, "Error in receive thread", e);
			}
		}

		public void stopThis() {
			isStopped = true;
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(USBDataSendEvent event){
		sendBytes(event.bytes());
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(SelectDeviceEvent event) {
		device = (UsbDevice) mUsbManager.getDeviceList().values().toArray()[event.getDevice()];
		mUsbManager.requestPermission(device, mPermissionIntent);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEventMainThread(PrepareDevicesListEvent event) {
        mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);
        List<CharSequence> list = new LinkedList<>();
        for (UsbDevice usbDevice : mUsbManager.getDeviceList().values()) {
            list.add(onBuildingDevicesList(usbDevice));
        }
        final CharSequence[] devicesName = new CharSequence[mUsbManager.getDeviceList().size()];
		list.toArray(devicesName);
		onShowDevicesList(devicesName);
    }

	private void sendBytes(byte[] bytes) {
		if (device != null && mUsbManager.hasPermission(device) && bytes.length > 0) {
			for (UsbInterface intf: interfacesList) {
				for (int i = 0; i < intf.getEndpointCount(); i++) {
					UsbEndpoint endPointWrite = intf.getEndpoint(i);
					if (i == 1 && UsbConstants.USB_DIR_OUT == endPointWrite.getDirection()) {
						int status = connection.bulkTransfer(endPointWrite, bytes, bytes.length, 250);
						onUSBDataSent(status, bytes);
					}
				}
			}
		}
	}

	/**
	 * receives the permission request to connect usb devices
	 */
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (Consts.ACTION_USB_PERMISSION.equals(action)) {
				setDevice(intent);
			}
			if (UsbManager.ACTION_USB_DEVICE_ATTACHED.equals(action)) {
				setDevice(intent);
				if (device != null) {
					onDeviceConnected(device);
				}
			}
			if (UsbManager.ACTION_USB_DEVICE_DETACHED.equals(action)) {
				if (device != null) {
					device = null;
					if (usbThreadDataReceiver != null) {
						usbThreadDataReceiver.stopThis();
					}
					onDeviceDisconnected(device);
				}
			}
		}

		private void setDevice(Intent intent) {
			synchronized (this) {
				device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
				Log.d(TAG, "Broadcast received. Device: " + device);
				if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false) && device != null) {
					Log.d(TAG, "Permission granted for device: " + device.getDeviceName());
					onDeviceSelected(device);
					connection = mUsbManager.openDevice(device);
					if (connection == null) {
						return;
					}
					interfacesList = new LinkedList<>();
					for (int i = 0; i < device.getInterfaceCount(); i++) {
						// TODO-hi: only claim the necessary interfaces
						UsbInterface intf = device.getInterface(i);
						connection.claimInterface(intf, true);
						interfacesList.add(intf);
					}
					usbThreadDataReceiver = new USBThreadDataReceiver();
					usbThreadDataReceiver.start();
					onDeviceAttached(device);
				} else {
					Log.e(TAG, "Permission denied for the USB HID device");
				}
			}
		}
	};

	public void onCommand(Intent intent, String action, int flags, int startId) {
	}

	public void onUSBDataReceive(byte[] buffer) {
	}

	public void onDeviceConnected(UsbDevice device) {
	}

	public void onDeviceDisconnected(UsbDevice device) {
	}

	public void onDeviceSelected(UsbDevice device) {
	}

	public void onDeviceAttached(UsbDevice device) {
	}

	public void onShowDevicesList(CharSequence[] deviceName) {
	}

	public CharSequence onBuildingDevicesList(UsbDevice usbDevice) {
		return null;
	}

	public void onUSBDataSent(int status, byte[] out) {
	}

}