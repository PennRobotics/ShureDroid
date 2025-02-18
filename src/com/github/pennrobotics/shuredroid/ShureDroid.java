package com.github.pennrobotics.shuredroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.github.pennrobotics.shuredroid.core.Consts;
import com.github.pennrobotics.shuredroid.core.events.DeviceAttachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DeviceDetachedEvent;
import com.github.pennrobotics.shuredroid.core.events.LogMessageEvent;
import com.github.pennrobotics.shuredroid.core.events.PrepareDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.SelectDeviceEvent;
import com.github.pennrobotics.shuredroid.core.events.ShowDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataReceiveEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataSendEvent;
import com.github.pennrobotics.shuredroid.core.services.USBHIDService;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ShureDroid extends Activity implements View.OnClickListener {

	private Intent usbService;

	private EditText edtlogText;
	private Button btnSelectHIDDevice;
	private Button btnTestPacket;
	private Button btnClear;

	protected EventBus eventBus;

	private void prepareServices() {
		usbService = new Intent(this, USBHIDService.class);
		startService(usbService);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		try {
			eventBus = EventBus.builder().logNoSubscriberMessages(false).sendNoSubscriberEvent(false).installDefaultEventBus();
		} catch (EventBusException e) {
			eventBus = EventBus.getDefault();
		}
		initUI();
	}

	private void initUI() {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		setVersionToTitle();
		getActionBar().hide();

		btnSelectHIDDevice = (Button) findViewById(R.id.btnSelectHIDDevice);
		btnSelectHIDDevice.setOnClickListener(this);

		btnTestPacket = (Button) findViewById(R.id.btnTestPacket);
		btnTestPacket.setOnClickListener(this);

		btnClear = (Button) findViewById(R.id.btnClear);
		btnClear.setOnClickListener(this);

		edtlogText = (EditText) findViewById(R.id.edtlogText);

		mLog("Initialized\nPlease select your USB HID device\n", false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void onClick(View v) {
		if (v == btnClear) {
			edtlogText.setText("");
		} else if (v == btnSelectHIDDevice) {
			eventBus.post(new PrepareDevicesListEvent());
		} else if (v == btnTestPacket) {
			byte[] packet = {1,16,0x11,0x22,0,3,8,8,0x70,8,1,2,1,0,0,0,
					0x54,0x77,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			eventBus.post(new USBDataSendEvent(packet));
		}
	}

	void showListOfDevices(CharSequence devicesName[]) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		if (devicesName.length == 0) {
			builder.setTitle(Consts.MESSAGE_CONNECT_YOUR_USB_HID_DEVICE);
		} else {
			builder.setTitle(Consts.MESSAGE_SELECT_YOUR_USB_HID_DEVICE);
		}

		builder.setItems(devicesName, new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				eventBus.post(new SelectDeviceEvent(which));
			}
		});
		builder.setCancelable(true);
		builder.show();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(USBDataReceiveEvent event) {
		mLog(event.getData() + " \nReceived " + event.getBytesCount() + " bytes", true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(LogMessageEvent event) {
		mLog(event.getData(), true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(ShowDevicesListEvent event) {
		showListOfDevices(event.getCharSequenceArray());
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(DeviceAttachedEvent event) {
		btnTestPacket.setEnabled(true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(DeviceDetachedEvent event) {
		btnTestPacket.setEnabled(false);
	}

	@Override
	protected void onStart() {
		super.onStart();
		prepareServices();
		eventBus.register(this);
	}

	@Override
	protected void onStop() {
		eventBus.unregister(this);
		super.onStop();
	}

	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		String action = intent.getAction();
		if (action == null) {
			return;
		}
		switch (action) {
			case Consts.USB_HID_TERMINAL_CLOSE_ACTION:
				stopService(new Intent(this, USBHIDService.class));
				((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Consts.USB_HID_TERMINAL_NOTIFICATION);
				finish();
				break;
		}
	}

	void sendToUSBService(String action) {
		usbService.setAction(action);
		startService(usbService);
	}

	void sendToUSBService(String action, boolean data) {
		usbService.putExtra(action, data);
		sendToUSBService(action);
	}

	void sendToUSBService(String action, int data) {
		usbService.putExtra(action, data);
		sendToUSBService(action);
	}

	private void mLog(String log, boolean newLine) {
		if (newLine) {
			edtlogText.append(Consts.NEW_LINE);
		}
		edtlogText.append(log);
		if(edtlogText.getLineCount() > 1000) {
			edtlogText.setText("cleared");
		}
	}

	private void setVersionToTitle() {
		/*
		try {
			this.setTitle(Consts.SPACE + this.getTitle() + Consts.SPACE + getPackageManager().getPackageInfo(getPackageName(), 0).versionName);
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		 */
	}
}