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
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.ViewSwitcher;

import com.github.pennrobotics.shuredroid.core.Consts;
import com.github.pennrobotics.shuredroid.core.USBUtils;
import com.github.pennrobotics.shuredroid.core.events.DeviceAttachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DeviceDetachedEvent;
import com.github.pennrobotics.shuredroid.core.events.DevicePluggedEvent;
import com.github.pennrobotics.shuredroid.core.events.LogMessageEvent;
import com.github.pennrobotics.shuredroid.core.events.PrepareDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.SelectDeviceEvent;
import com.github.pennrobotics.shuredroid.core.events.ShowDevicesListEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataReceiveEvent;
import com.github.pennrobotics.shuredroid.core.events.USBDataSendEvent;
import com.github.pennrobotics.shuredroid.core.services.USBHIDService;
import com.google.android.material.tabs.TabLayout;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.EventBusException;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class ShureDroid extends Activity implements View.OnClickListener, SeekBar.OnSeekBarChangeListener {

	private Intent usbService;

	private ImageButton btnSelectHIDDevice;

	private ViewSwitcher viewSwitcher;
	private ScrollView viewAuto;
	private ScrollView viewManual;

	private TabLayout tabLayout;

	private CheckBox switchLockAPanel;
	private CheckBox switchLockMPanel;
	private CheckBox switchPhantomAPanel;
	private CheckBox switchPhantomMPanel;
	private CheckBox switchMicMuteAPanel;
	private CheckBox switchMicMuteMPanel;
	private EditText editTextNumberMixAPanel;
	private EditText editTextNumberMixMPanel;
	private SeekBar seekBarMixAPanel;
	private SeekBar seekBarMixMPanel;

	private RadioGroup radioGroupADist;
	private RadioButton radioADistNear;
	private RadioButton radioADistFar;
	private RadioGroup radioGroupATone;
	private RadioButton radioAToneDark;
	private RadioButton radioAToneNeutral;
	private RadioButton radioAToneBright;
	private RadioGroup radioGroupAGain;
	private RadioButton radioAGainLow;
	private RadioButton radioAGainNormal;
	private RadioButton radioAGainHigh;

	private EditText editTextNumberMGain;
	private SeekBar seekBarMGain;
	private CheckBox switchMLimiter;
	private RadioGroup radioGroupMComp;
	private RadioButton radioMCompOff;
	private RadioButton radioMCompLight;
	private RadioButton radioMCompModerate;
	private RadioButton radioMCompHeavy;
	private RadioGroup radioGroupMHpf;
	private RadioButton radioMHpfOff;
	private RadioButton radioMHpf75Hz;
	private RadioButton radioMHpf150Hz;
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

	private EditText editLogText;

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
		setupNotifications();
	}

	private void initUI() {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		/* TODO: getActionBar().hide(); */

		btnSelectHIDDevice = findViewById(R.id.btnSelectHIDDevice);

		viewSwitcher = findViewById(R.id.viewSwitcher);
		viewAuto = findViewById(R.id.viewAuto);
		viewManual = findViewById(R.id.viewManual);

		tabLayout = findViewById(R.id.tabLayout);
		//tabManual = findViewById(R.id.tabManual);
		//tabAuto = findViewById(R.id.tabAuto);

		switchLockAPanel = findViewById(R.id.switchLockAPanel);
		switchLockMPanel = findViewById(R.id.switchLockMPanel);
		switchPhantomAPanel = findViewById(R.id.switchPhantomAPanel);
		switchPhantomMPanel = findViewById(R.id.switchPhantomMPanel);
		switchMicMuteAPanel = findViewById(R.id.switchMicMuteAPanel);
		switchMicMuteMPanel = findViewById(R.id.switchMicMuteMPanel);
		editTextNumberMixAPanel = findViewById(R.id.editTextNumberMixAPanel);
		editTextNumberMixMPanel = findViewById(R.id.editTextNumberMixMPanel);
		seekBarMixAPanel = findViewById(R.id.seekBarMixAPanel);
		seekBarMixMPanel = findViewById(R.id.seekBarMixMPanel);

		radioGroupADist = findViewById(R.id.radioGroupADist);
		radioADistNear = findViewById(R.id.radioADistNear);
		radioADistFar = findViewById(R.id.radioADistFar);
		radioGroupATone = findViewById(R.id.radioGroupATone);
		radioAToneDark = findViewById(R.id.radioAToneDark);
		radioAToneNeutral = findViewById(R.id.radioAToneNeutral);
		radioAToneBright = findViewById(R.id.radioAToneBright);
		radioGroupAGain = findViewById(R.id.radioGroupAGain);
		radioAGainLow = findViewById(R.id.radioAGainLow);
		radioAGainNormal = findViewById(R.id.radioAGainNormal);
		radioAGainHigh = findViewById(R.id.radioAGainHigh);

		editTextNumberMGain = findViewById(R.id.editTextNumberMGain);
		seekBarMGain = findViewById(R.id.seekBarMGain);
		switchMLimiter = findViewById(R.id.switchMLimiter);
		radioGroupMComp = findViewById(R.id.radioGroupMComp);
		radioMCompOff = findViewById(R.id.radioMCompOff);
		radioMCompLight = findViewById(R.id.radioMCompLight);
		radioMCompModerate = findViewById(R.id.radioMCompModerate);
		radioMCompHeavy = findViewById(R.id.radioMCompHeavy);
		radioGroupMHpf = findViewById(R.id.radioGroupMHpf);
		radioMHpfOff = findViewById(R.id.radioMHpfOff);
		radioMHpf75Hz = findViewById(R.id.radioMHpf75Hz);
		radioMHpf150Hz = findViewById(R.id.radioMHpf150Hz);
		switchEqEnable = findViewById(R.id.switchEqEnable);
		switchEq1 = findViewById(R.id.switchEq1);
		seekBarEq1 = findViewById(R.id.seekBarEq1);
		switchEq2 = findViewById(R.id.switchEq2);
		seekBarEq2 = findViewById(R.id.seekBarEq2);
		switchEq3 = findViewById(R.id.switchEq3);
		seekBarEq3 = findViewById(R.id.seekBarEq3);
		switchEq4 = findViewById(R.id.switchEq4);
		seekBarEq4 = findViewById(R.id.seekBarEq4);
		switchEq5 = findViewById(R.id.switchEq5);
		seekBarEq5 = findViewById(R.id.seekBarEq5);

		editLogText = findViewById(R.id.editLogText);

		makeSettingsUIEnabled(false);

		btnSelectHIDDevice.setOnClickListener(this);

		tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
			@Override
			public void onTabSelected(TabLayout.Tab tab) {
				boolean currentlyAuto = viewAuto == viewSwitcher.getCurrentView();

				switch(tab.getPosition()) {
					case 0:  /* tabAuto */
						if (!currentlyAuto) { viewSwitcher.showPrevious(); }
						// TODO: send and verify packet to enter auto mode
						break;
					case 1:  /* tabManual */
						if (currentlyAuto) { viewSwitcher.showNext(); }
						// TODO: send and verify packet to enter manual mode
						break;
				}
			}

			@Override
			public void onTabUnselected(TabLayout.Tab tab)  {}
			@Override
			public void onTabReselected(TabLayout.Tab tab)  {}
		});

		seekBarMixAPanel.setOnSeekBarChangeListener(this);
		seekBarMixMPanel.setOnSeekBarChangeListener(this);
		seekBarMGain.setOnSeekBarChangeListener(this);
		seekBarEq1.setOnSeekBarChangeListener(this);
		seekBarEq2.setOnSeekBarChangeListener(this);
		seekBarEq3.setOnSeekBarChangeListener(this);
		seekBarEq4.setOnSeekBarChangeListener(this);
		seekBarEq5.setOnSeekBarChangeListener(this);

		switchLockAPanel.setOnClickListener(this);
		switchLockMPanel.setOnClickListener(this);
		switchPhantomAPanel.setOnClickListener(this);
		switchPhantomMPanel.setOnClickListener(this);
		switchMicMuteAPanel.setOnClickListener(this);
		switchMicMuteMPanel.setOnClickListener(this);
		editTextNumberMixAPanel.setOnClickListener(this);
		editTextNumberMixMPanel.setOnClickListener(this);

		radioADistNear.setOnClickListener(this);
		radioADistFar.setOnClickListener(this);
		radioAToneDark.setOnClickListener(this);
		radioAToneNeutral.setOnClickListener(this);
		radioAToneBright.setOnClickListener(this);
		radioAGainLow.setOnClickListener(this);
		radioAGainNormal.setOnClickListener(this);
		radioAGainHigh.setOnClickListener(this);

		editTextNumberMGain.setOnClickListener(this);
		switchMLimiter.setOnClickListener(this);
		radioMCompOff.setOnClickListener(this);
		radioMCompLight.setOnClickListener(this);
		radioMCompModerate.setOnClickListener(this);
		radioMCompHeavy.setOnClickListener(this);
		radioMHpfOff.setOnClickListener(this);
		radioMHpf75Hz.setOnClickListener(this);
		radioMHpf150Hz.setOnClickListener(this);
		switchEqEnable.setOnClickListener(this);
		switchEq1.setOnClickListener(this);
		switchEq2.setOnClickListener(this);
		switchEq3.setOnClickListener(this);
		switchEq4.setOnClickListener(this);
		switchEq5.setOnClickListener(this);

		//mLog("Initialized\nPlease select your USB HID device\n", false);
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
	}

	public void onClick(View v) {
		/*
			TODO: Move to correct handler and test:
				eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000185"+p,0)));  // Auto Mode Enable
				eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202010186"+p,0)));  // Mix
		 */
		if (v.getId() == R.id.btnSelectHIDDevice) {
			eventBus.post(new PrepareDevicesListEvent());
		}

		if (v.getId() == R.id.switchLockAPanel ||
			v.getId() == R.id.switchLockMPanel)
		{
			// TODO: eventBus.post(new USBDataSendEvent(USBUtils.padPktData("0202010600A6"+p,0)));  // Parameter lock
			// TODO: set both elements to the same value
			// TODO: lock should disable nearly every element except itself
		}

		if (v.getId() == R.id.switchPhantomAPanel ||
			v.getId() == R.id.switchPhantomMPanel) {
			boolean c = (v.getId() == R.id.switchPhantomAPanel)
					? switchPhantomAPanel.isChecked()
					: switchPhantomMPanel.isChecked();
			if (c) {
				eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200016630", 0)));  // Phantom
				eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010000")));  // Phantom
			} else {
				eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200016600", 0)));  // Phantom
				eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010000")));  // Phantom
			}
			switchPhantomAPanel.setChecked(c);
			switchPhantomMPanel.setChecked(c);
		}

		if (v.getId() == R.id.switchMicMuteAPanel ||
			v.getId() == R.id.switchMicMuteMPanel)
		{
			// TODO: eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000104"+p,0)));  // Mute
			// TODO: set both elements to same value
			// TODO: indicate mute state somehow
		}

		if (v.getId() == R.id.editTextNumberMixAPanel)  { /*TODO*/ }
		if (v.getId() == R.id.editTextNumberMixMPanel)  { /*TODO*/ }
		if (v.getId() == R.id.seekBarMixAPanel)  { /*TODO*/ }
		if (v.getId() == R.id.seekBarMixMPanel)  { /*TODO*/ }

		if (v.getId() == R.id.radioADistNear) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018200", 0/*TODO: which should be default, 0 or 3?*/)));  // Auto Position
		}
		if (v.getId() == R.id.radioADistFar) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018201", 0)));  // Auto Position
		}

		if (v.getId() == R.id.radioAToneDark) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018300", 0)));  // Auto Tone
		}
		if (v.getId() == R.id.radioAToneNeutral) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018301", 0)));  // Auto Tone
		}
		if (v.getId() == R.id.radioAToneBright) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018302", 0)));  // Auto Tone
		}

		if (v.getId() == R.id.radioAGainLow) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018700", 0)));  // Auto Gain
		}
		if (v.getId() == R.id.radioAGainNormal) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018701", 0)));  // Auto Gain
		}
		if (v.getId() == R.id.radioAGainHigh) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200018702", 0)));  // Auto Gain
		}

		if (v.getId() == R.id.editTextNumberMGain) {
			// TODO: link with seekbar and vice-versa
			// TODO: link all text edit boxes to their seekbars
			// TODO: set seekbar, run that (seekbar) code
		}

		if (v.getId() == R.id.switchMLimiter) {
			String p = switchMLimiter.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000151"+p,0)));  // Limiter
		}

		if (v.getId() == R.id.radioMCompOff) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200015C00", 0)));  // Compressor
		}
		if (v.getId() == R.id.radioMCompLight) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200015C01", 0)));  // Compressor
		}
		if (v.getId() == R.id.radioMCompModerate) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200015C02", 0)));  // Compressor
		}
		if (v.getId() == R.id.radioMCompHeavy) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200015C03", 0)));  // Compressor
		}

		if (v.getId() == R.id.radioMHpfOff) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200010600", 0)));  // HPF
		}
		if (v.getId() == R.id.radioMHpf75Hz) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200010601", 0)));  // HPF
		}
		if (v.getId() == R.id.radioMHpf150Hz) {
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("02020200010602", 0)));  // HPF
		}

		if (v.getId() == R.id.switchEqEnable) {
			String p = switchEqEnable.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000200" + p, 0)));  // EQ Enable
		}
		if (v.getId() == R.id.switchEq1) {
			String p = switchEq1.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000210" + p, 0)));  // EQ Band 1 Enable
		}
		if (v.getId() == R.id.switchEq2) {
			String p = switchEq2.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000220" + p, 0)));  // EQ Band 2 Enable
		}
		if (v.getId() == R.id.switchEq3) {
			String p = switchEq3.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000230" + p, 0)));  // EQ Band 3 Enable
		}
		if (v.getId() == R.id.switchEq4) {
			String p = switchEq4.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000240" + p, 0)));  // EQ Band 4 Enable
		}
		if (v.getId() == R.id.switchEq5) {
			String p = switchEq5.isChecked() ? "01" : "00";
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000250" + p, 0)));  // EQ Band 5 Enable
		}
	}

	void getAllMVX2UParameters() {
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010201000000")));  // ID
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("0102010600A6")));  // Parameter lock
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000102")));  // Manual Gain
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000104")));  // Mute
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000106")));  // HPF
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000151")));  // Limiter
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("01020200015C")));  // Compressor
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000166")));  // Phantom
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000182")));  // Auto Position
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000183")));  // Auto Tone
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000185")));  // Auto Mode Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202010186")));  // Mix
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000187")));  // Auto Gain
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000200")));  // EQ Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000210")));  // EQ Band 1 Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000214")));  // EQ Band 1 Gain
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000220")));  // EQ Band 2 Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000224")));  // EQ Band 2 Gain
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000230")));  // EQ Band 3 Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000234")));  // EQ Band 3 Gain
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000240")));  // EQ Band 4 Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000244")));  // EQ Band 4 Gain
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000250")));  // EQ Band 5 Enable
		eventBus.post(new USBDataSendEvent(USBUtils.padPktData("010202000254")));  // EQ Band 5 Gain
	}

	void showListOfDevices(CharSequence[] devicesName) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);

		if (devicesName.length == 0) {
			builder.setTitle(Consts.MESSAGE_CONNECT_YOUR_USB_HID_DEVICE);
		} else {
			builder.setTitle(Consts.MESSAGE_SELECT_YOUR_USB_HID_DEVICE);
		}

		builder.setItems(devicesName, (dialog, which) -> eventBus.post(new SelectDeviceEvent(which)));
		builder.setCancelable(true);
		builder.show();
	}

	void makeSettingsUIEnabled(boolean enable) {
		if (!enable) {
			editTextNumberMixAPanel.setText(Consts.UNKNOWN_VALUE);
			editTextNumberMixMPanel.setText(Consts.UNKNOWN_VALUE);
			editTextNumberMGain.setText(Consts.UNKNOWN_VALUE);
		}

		switchPhantomAPanel.setEnabled(enable);
		switchPhantomMPanel.setEnabled(enable);
		switchMicMuteAPanel.setEnabled(enable);
		switchMicMuteMPanel.setEnabled(enable);
		editTextNumberMixAPanel.setEnabled(enable);
		editTextNumberMixMPanel.setEnabled(enable);
		seekBarMixAPanel.setEnabled(enable);
		seekBarMixMPanel.setEnabled(enable);

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
		switchMLimiter.setEnabled(enable);
		radioMCompOff.setEnabled(enable);
		radioMCompLight.setEnabled(enable);
		radioMCompModerate.setEnabled(enable);
		radioMCompHeavy.setEnabled(enable);
		radioMHpfOff.setEnabled(enable);
		radioMHpf75Hz.setEnabled(enable);
		radioMHpf150Hz.setEnabled(enable);
		switchEqEnable.setEnabled(enable);

		switchEq1.setEnabled(enable);
		switchEq2.setEnabled(enable);
		switchEq3.setEnabled(enable);
		switchEq4.setEnabled(enable);
		switchEq5.setEnabled(enable);
		seekBarEq1.setEnabled(enable);
		seekBarEq2.setEnabled(enable);
		seekBarEq3.setEnabled(enable);
		seekBarEq4.setEnabled(enable);
		seekBarEq5.setEnabled(enable);
	}

	private void mLog(String log) {
		editLogText.append(log + "\n");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(USBDataReceiveEvent event) {
		byte[] ba = event.getData();

		int pType = USBUtils.getParamType(ba);
		if (pType < 0)  { mLog(event.getDataAsHex()); return; }
		int pVal = USBUtils.getParamVal(ba);

		switch (pType) {
			case 0x01000000:  break;
			case 0x010600a6:  // Lock state
				makeSettingsUIEnabled(pVal == 0);
				switchLockAPanel.setChecked(pVal != 0);
				switchLockMPanel.setChecked(pVal != 0);
				break;
			case 0x02000102:
				seekBarMixAPanel.setProgress(pVal);
				editTextNumberMixAPanel.setText(Integer.toString(pVal));
				seekBarMGain.setProgress(pVal / 50);
				editTextNumberMGain.setText(String.valueOf((float)(pVal/50)/2.0));
			case 0x02000104:
				switchMicMuteAPanel.setChecked(pVal != 0);
				switchMicMuteMPanel.setChecked(pVal != 0);
				break;
			case 0x02000106:
				if (pVal == 0) {
					radioGroupMHpf.check(R.id.radioMHpfOff);
				} else if (pVal == 1) {
					radioGroupMHpf.check(R.id.radioMHpf75Hz);
				} else if (pVal == 2) {
					radioGroupMHpf.check(R.id.radioMHpf150Hz);
				} else {
					mLog("HPF param error");
				}
				break;
			case 0x02000151:  switchMLimiter.setChecked(pVal != 0); break;
			case 0x0200015c:
				if (pVal == 0) {
					radioGroupMComp.check(R.id.radioMCompOff);
				} else if (pVal == 1) {
					radioGroupMComp.check(R.id.radioMCompLight);
				} else if (pVal == 2) {
					radioGroupMComp.check(R.id.radioMCompModerate);
				} else if (pVal == 3) {
					radioGroupMComp.check(R.id.radioMCompHeavy);
				} else {
					mLog("Comp param error");
				}
				break;
			case 0x02000166:
				switchPhantomAPanel.setChecked(pVal != 0);
				switchPhantomMPanel.setChecked(pVal != 0);
				break;
			case 0x02000182:
				if (pVal == 0) {
					radioGroupADist.check(R.id.radioADistNear);
				} else if (pVal == 1) {
					radioGroupADist.check(R.id.radioADistFar);
				} else {
					mLog("Dist param error");
				}
				break;
			case 0x02000183:
				if (pVal == 0) {
					radioGroupATone.check(R.id.radioAToneDark);
				} else if (pVal == 1) {
					radioGroupATone.check(R.id.radioAToneNeutral);
				} else if (pVal == 2) {
					radioGroupATone.check(R.id.radioAToneBright);
				} else {
					mLog("Tone param error");
				}
				break;
			case 0x02000185:  // Auto mode enable
				{
					boolean currentlyAuto = viewAuto == viewSwitcher.getCurrentView();

					if (pVal == 0) {
						if (currentlyAuto) { viewSwitcher.showNext(); }
						TabLayout.Tab tabM = tabLayout.getTabAt(1);
						if (tabM != null) { tabM.select(); }
					}
					if (pVal == 1) {
						if (!currentlyAuto)  { viewSwitcher.showPrevious(); }
						TabLayout.Tab tabA = tabLayout.getTabAt(0);
						if (tabA != null)  { tabA.select(); }
					}
					if (pVal < 0 || pVal > 1) {
						mLog("Auto mode param error");
					}
				}
				break;
			case 0x02010186:
				seekBarMixAPanel.setProgress(pVal);
				seekBarMixMPanel.setProgress(pVal);
				editTextNumberMixAPanel.setText(Integer.toString(pVal));
				editTextNumberMixMPanel.setText(Integer.toString(pVal));
				break;
			case 0x02000187:
				if (pVal == 0) {
					radioGroupAGain.check(R.id.radioAGainLow);
				} else if (pVal == 1) {
					radioGroupAGain.check(R.id.radioAGainNormal);
				} else if (pVal == 2) {
					radioGroupAGain.check(R.id.radioAGainHigh);
				} else {
					mLog("Gain (auto) param error");
				}
				break;
			case 0x02000200:  switchEqEnable.setChecked(pVal != 0); break;
			case 0x02000210:  switchEq1.setChecked(pVal != 0); break;
			case 0x02000214:  seekBarEq1.setProgress(pVal/20 + 4); break;
			case 0x02000220:  switchEq2.setChecked(pVal != 0); break;
			case 0x02000224:  seekBarEq2.setProgress(pVal/20 + 4); break;
			case 0x02000230:  switchEq3.setChecked(pVal != 0); break;
			case 0x02000234:  seekBarEq3.setProgress(pVal/20 + 4); break;
			case 0x02000240:  switchEq4.setChecked(pVal != 0); break;
			case 0x02000244:  seekBarEq4.setProgress(pVal/20 + 4); break;
			case 0x02000250:  switchEq5.setChecked(pVal != 0); break;
			case 0x02000254:  seekBarEq5.setProgress(pVal/20 + 4); break;
			default:
				mLog("Param Error:");
				mLog(event.getDataAsHex());
		}
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
	public void onEvent(DevicePluggedEvent event) {
		btnSelectHIDDevice.setBackgroundResource(android.R.drawable.presence_audio_away);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(DeviceAttachedEvent event) {
		btnSelectHIDDevice.setBackgroundResource(android.R.drawable.presence_audio_online);
		makeSettingsUIEnabled(true);
		switchLockAPanel.setEnabled(true);
		switchLockMPanel.setEnabled(true);
		getAllMVX2UParameters();
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(DeviceDetachedEvent event) {
		makeSettingsUIEnabled(false);
		btnSelectHIDDevice.setBackgroundResource(android.R.drawable.presence_audio_busy);
	}

	@Override
	protected void onStart() {
		super.onStart();
		prepareServices();  // USBService
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
		if (action == Consts.USB_HID_TERMINAL_CLOSE_ACTION) {
			stopService(new Intent(this, USBHIDService.class));
			((NotificationManager) getSystemService(NOTIFICATION_SERVICE)).cancel(Consts.USB_HID_TERMINAL_NOTIFICATION);
			finish();
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

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		if (seekBar.getId() == R.id.seekBarMixAPanel)  { /*TODO*/ }
		if (seekBar.getId() == R.id.seekBarMixMPanel)  { /*TODO*/ }

		if (seekBar.getId() == R.id.seekBarMGain) {
			int val = seekBarMGain.getProgress() * 50;
			String valHx = String.format("%04X", val);
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("020202000102" + valHx, 0)));  // Manual Gain
		}

		if (seekBar.getId() == R.id.seekBarEq1 ||
			seekBar.getId() == R.id.seekBarEq2 ||
			seekBar.getId() == R.id.seekBarEq3 ||
			seekBar.getId() == R.id.seekBarEq4 ||
			seekBar.getId() == R.id.seekBarEq5)
		{
			int band = 0x14;  /* default */
			if (seekBar.getId() == R.id.seekBarEq1)  { /* default */ }
			if (seekBar.getId() == R.id.seekBarEq2)  { band = 0x24; }
			if (seekBar.getId() == R.id.seekBarEq3)  { band = 0x34; }
			if (seekBar.getId() == R.id.seekBarEq4)  { band = 0x44; }
			if (seekBar.getId() == R.id.seekBarEq5)  { band = 0x54; }

			int val = 20 * (seekBar.getProgress() - 4);
			String valHx = String.format("%04X", val & 0xFFFF);
			mLog(valHx);
			eventBus.post(new USBDataSendEvent(USBUtils.padPktData("0202020002"
					+ Integer.toHexString(band) + valHx, 0)));  // EQ Band N Gain
		}
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {

	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {

	}

	private void setupNotifications()  {}  // TODO

}