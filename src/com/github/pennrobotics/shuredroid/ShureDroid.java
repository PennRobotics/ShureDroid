package com.github.pennrobotics.shuredroid;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.ViewSwitcher;

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
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ShureDroid extends Activity implements View.OnClickListener {

	private Intent usbService;

	private ImageButton btnSelectHIDDevice;

	private ViewSwitcher viewSwitcher;
	private ScrollView viewAuto;
	private ScrollView viewManual;

	private TabLayout tabLayout;
	private TabItem tabManual;
	private TabItem tabAuto;

	private CheckBox switchPhantomAPanel;
	private CheckBox switchPhantomMPanel;
	private CheckBox switchMicMuteAPanel;
	private CheckBox switchMicMuteMPanel;
	private EditText editTextNumberMixAPanel;
	private EditText editTextNumberMixMPanel;
	private SeekBar seekBarMixAPanel;
	private SeekBar seekBarMixMPanel;

	private RadioButton radioADistNear;
	private RadioButton radioADistFar;
	private RadioButton radioAToneDark;
	private RadioButton radioAToneNeutral;
	private RadioButton radioAToneBright;
	private RadioButton radioAGainLow;
	private RadioButton radioAGainNormal;
	private RadioButton radioAGainHigh;

	private EditText editTextNumberMGain;
	private SeekBar seekBarMGain;
	private CheckBox switchMLimiter;
	private RadioButton radioMCompOff;
	private RadioButton radioMCompLight;
	private RadioButton radioMCompModerate;
	private RadioButton radioMCompHeavy;
	private RadioButton radioMLpfOff;
	private RadioButton radioMLpf75Hz;
	private RadioButton radioMLpf150Hz;
	private CheckBox switchEqEnable;
	private CheckBox switchEq1;
	private SeekBar seekBarEq1;
	private CheckBox switchEq2;
	private SeekBar seekBarEq2;
	private CheckBox switchEq3;
	private SeekBar seekBarEq3;
	private CheckBox switchEq4;
	private SeekBar seekBarEq4;
	private CheckBox switchEq5;
	private SeekBar seekBarEq5;
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

		btnSelectHIDDevice = (ImageButton) findViewById(R.id.btnSelectHIDDevice);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewAuto = (ScrollView) findViewById(R.id.viewAuto);
		viewManual = (ScrollView) findViewById(R.id.viewManual);

		tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabManual = (TabItem) findViewById(R.id.tabManual);
		tabAuto = (TabItem) findViewById(R.id.tabAuto);

		switchPhantomAPanel = (CheckBox) findViewById(R.id.switchPhantomAPanel);
		switchPhantomMPanel = (CheckBox) findViewById(R.id.switchPhantomMPanel);
		switchMicMuteAPanel = (CheckBox) findViewById(R.id.switchMicMuteAPanel);
		switchMicMuteMPanel = (CheckBox) findViewById(R.id.switchMicMuteMPanel);
		editTextNumberMixAPanel = (EditText) findViewById(R.id.editTextNumberMixAPanel);
		editTextNumberMixMPanel = (EditText) findViewById(R.id.editTextNumberMixMPanel);
		seekBarMixAPanel = (SeekBar) findViewById(R.id.seekBarMixAPanel);
		seekBarMixMPanel = (SeekBar) findViewById(R.id.seekBarMixMPanel);

		radioADistNear = (RadioButton) findViewById(R.id.radioADistNear);
		radioADistFar = (RadioButton) findViewById(R.id.radioADistFar);
		radioAToneDark = (RadioButton) findViewById(R.id.radioAToneDark);
		radioAToneNeutral = (RadioButton) findViewById(R.id.radioAToneNeutral);
		radioAToneBright = (RadioButton) findViewById(R.id.radioAToneBright);
		radioAGainLow = (RadioButton) findViewById(R.id.radioAGainLow);
		radioAGainNormal = (RadioButton) findViewById(R.id.radioAGainNormal);
		radioAGainHigh = (RadioButton) findViewById(R.id.radioAGainHigh);

		editTextNumberMGain = (EditText) findViewById(R.id.editTextNumberMGain);
		seekBarMGain = (SeekBar) findViewById(R.id.seekBarMGain);
		switchMLimiter = (CheckBox) findViewById(R.id.switchMLimiter);
		radioMCompOff = (RadioButton) findViewById(R.id.radioMCompOff);
		radioMCompLight = (RadioButton) findViewById(R.id.radioMCompLight);
		radioMCompModerate = (RadioButton) findViewById(R.id.radioMCompModerate);
		radioMCompHeavy = (RadioButton) findViewById(R.id.radioMCompHeavy);
		radioMLpfOff = (RadioButton) findViewById(R.id.radioMLpfOff);
		radioMLpf75Hz = (RadioButton) findViewById(R.id.radioMLpf75Hz);
		radioMLpf150Hz = (RadioButton) findViewById(R.id.radioMLpf150Hz);
		switchEqEnable = (CheckBox) findViewById(R.id.switchEqEnable);
		switchEq1 = (CheckBox) findViewById(R.id.switchEq1);
		seekBarEq1 = (SeekBar) findViewById(R.id.seekBarEq1);
		switchEq2 = (CheckBox) findViewById(R.id.switchEq2);
		seekBarEq2 = (SeekBar) findViewById(R.id.seekBarEq2);
		switchEq3 = (CheckBox) findViewById(R.id.switchEq3);
		seekBarEq3 = (SeekBar) findViewById(R.id.seekBarEq3);
		switchEq4 = (CheckBox) findViewById(R.id.switchEq4);
		seekBarEq4 = (SeekBar) findViewById(R.id.seekBarEq4);
		switchEq5 = (CheckBox) findViewById(R.id.switchEq5);
		seekBarEq5 = (SeekBar) findViewById(R.id.seekBarEq5);

		makeSettingsUIEnabled(false);

		btnSelectHIDDevice.setOnClickListener(this);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				boolean currentlyAuto = viewAuto == viewSwitcher.getCurrentView();

				switch(tab.getPosition()) {
					case 0:  /* tabAuto */
						if (!currentlyAuto) { viewSwitcher.showPrevious(); }
						break;
					case 1:  /* tabManual */
						if (currentlyAuto) { viewSwitcher.showNext(); }
						break;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab)  {}
			@Override
			public void onTabReselected(TabLayout.Tab tab)  {}
		});

		switchPhantomAPanel.setOnClickListener(this);
		switchPhantomMPanel.setOnClickListener(this);
		switchMicMuteAPanel.setOnClickListener(this);
		switchMicMuteMPanel.setOnClickListener(this);
		editTextNumberMixAPanel.setOnClickListener(this);
		editTextNumberMixMPanel.setOnClickListener(this);
		seekBarMixAPanel.setOnClickListener(this);
		seekBarMixMPanel.setOnClickListener(this);

		radioADistNear.setOnClickListener(this);
		radioADistFar.setOnClickListener(this);
		radioAToneDark.setOnClickListener(this);
		radioAToneNeutral.setOnClickListener(this);
		radioAToneBright.setOnClickListener(this);
		radioAGainLow.setOnClickListener(this);
		radioAGainNormal.setOnClickListener(this);
		radioAGainHigh.setOnClickListener(this);

		editTextNumberMGain.setOnClickListener(this);
		seekBarMGain.setOnClickListener(this);
		switchMLimiter.setOnClickListener(this);
		radioMCompOff.setOnClickListener(this);
		radioMCompLight.setOnClickListener(this);
		radioMCompModerate.setOnClickListener(this);
		radioMCompHeavy.setOnClickListener(this);
		radioMLpfOff.setOnClickListener(this);
		radioMLpf75Hz.setOnClickListener(this);
		radioMLpf150Hz.setOnClickListener(this);
		switchEqEnable.setOnClickListener(this);
		switchEq1.setOnClickListener(this);
		seekBarEq1.setOnClickListener(this);
		switchEq2.setOnClickListener(this);
		seekBarEq2.setOnClickListener(this);
		switchEq3.setOnClickListener(this);
		seekBarEq3.setOnClickListener(this);
		switchEq4.setOnClickListener(this);
		seekBarEq4.setOnClickListener(this);
		switchEq5.setOnClickListener(this);
		seekBarEq5.setOnClickListener(this);

		//mLog("Initialized\nPlease select your USB HID device\n", false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void onClick(View v) {
		switch (v.getId()) {
			case R.id.btnSelectHIDDevice:
				eventBus.post(new PrepareDevicesListEvent());
				break;

			case R.id.switchPhantomAPanel:
			case R.id.switchPhantomMPanel:
				break;
			case R.id.switchMicMuteAPanel:
			case R.id.switchMicMuteMPanel:
				break;
			case R.id.editTextNumberMixAPanel:
			case R.id.editTextNumberMixMPanel:
				break;
			case R.id.seekBarMixAPanel:
			case R.id.seekBarMixMPanel:
				break;

			case R.id.radioADistNear:
				break;
			case R.id.radioADistFar:
				break;
			case R.id.radioAToneDark:
				break;
			case R.id.radioAToneNeutral:
				break;
			case R.id.radioAToneBright:
				break;
			case R.id.radioAGainLow:
				break;
			case R.id.radioAGainNormal:
				break;
			case R.id.radioAGainHigh:
				break;

			case R.id.editTextNumberMGain:
				break;
			case R.id.seekBarMGain:
				break;
			case R.id.switchMLimiter:
				break;
			case R.id.radioMCompOff:
				break;
			case R.id.radioMCompLight:
				break;
			case R.id.radioMCompModerate:
				break;
			case R.id.radioMCompHeavy:
				break;
			case R.id.radioMLpfOff:
				break;
			case R.id.radioMLpf75Hz:
				break;
			case R.id.radioMLpf150Hz:
				break;
			case R.id.switchEqEnable:
				break;
			case R.id.switchEq1:
				break;
			case R.id.seekBarEq1:
				break;
			case R.id.switchEq2:
				break;
			case R.id.seekBarEq2:
				break;
			case R.id.switchEq3:
				break;
			case R.id.seekBarEq3:
				break;
			case R.id.switchEq4:
				break;
			case R.id.seekBarEq4:
				break;
			case R.id.switchEq5:
				break;
			case R.id.seekBarEq5:
				break;

			default:
				break;
		}
		/*			byte[] packet = {1,16,0x11,0x22,0,3,8,8,0x70,8,1,2,1,0,0,0,
					0x54,0x77,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,
					0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};
			eventBus.post(new USBDataSendEvent(packet)); */
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

	void makeSettingsUIEnabled(boolean enable) {
		tabLayout.setEnabled(enable);

		switchPhantomAPanel.setEnabled(enable);
		switchPhantomMPanel.setEnabled(enable);
		switchMicMuteAPanel.setEnabled(enable);
		switchMicMuteMPanel.setEnabled(enable);
		editTextNumberMixAPanel.setEnabled(enable);
		editTextNumberMixMPanel.setEnabled(enable);
		seekBarMixAPanel.setEnabled(enable);
		seekBarMixMPanel.setEnabled(enable);
		seekBarMixAPanel.setIndeterminate(!enable);
		seekBarMixMPanel.setIndeterminate(!enable);

		radioADistNear.setEnabled(enable);
		radioADistFar.setEnabled(enable);
		radioAToneDark.setEnabled(enable);
		radioAToneNeutral.setEnabled(enable);
		radioAToneBright.setEnabled(enable);
		radioAGainLow.setEnabled(enable);
		radioAGainNormal.setEnabled(enable);
		radioAGainHigh.setEnabled(enable);

		editTextNumberMGain.setEnabled(enable);
		seekBarMGain.setEnabled(enable);
		seekBarMGain.setIndeterminate(!enable);
		switchMLimiter.setEnabled(enable);
		radioMCompOff.setEnabled(enable);
		radioMCompLight.setEnabled(enable);
		radioMCompModerate.setEnabled(enable);
		radioMCompHeavy.setEnabled(enable);
		radioMLpfOff.setEnabled(enable);
		radioMLpf75Hz.setEnabled(enable);
		radioMLpf150Hz.setEnabled(enable);
		switchEqEnable.setEnabled(enable);
		switchEq1.setEnabled(enable);
		seekBarEq1.setEnabled(enable);
		switchEq2.setEnabled(enable);
		seekBarEq2.setEnabled(enable);
		switchEq3.setEnabled(enable);
		seekBarEq3.setEnabled(enable);
		switchEq4.setEnabled(enable);
		seekBarEq4.setEnabled(enable);
		switchEq5.setEnabled(enable);
		seekBarEq5.setEnabled(enable);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(USBDataReceiveEvent event) {
		//mLog(event.getData() + " \nReceived " + event.getBytesCount() + " bytes", true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(LogMessageEvent event) {
		//mLog(event.getData(), true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(ShowDevicesListEvent event) {
		showListOfDevices(event.getCharSequenceArray());
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(DeviceAttachedEvent event) {
		makeSettingsUIEnabled(true);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(DeviceDetachedEvent event) {
		makeSettingsUIEnabled(false);
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