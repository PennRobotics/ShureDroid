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
	}

	private void initUI() {
		getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
		getActionBar().hide();

		btnSelectHIDDevice = (ImageButton) findViewById(R.id.btnSelectHIDDevice);

		viewSwitcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);
		viewAuto = (ScrollView) findViewById(R.id.viewAuto);
		viewManual = (ScrollView) findViewById(R.id.viewManual);

		tabLayout = (TabLayout) findViewById(R.id.tabLayout);
		tabManual = (TabItem) findViewById(R.id.tabManual);
		tabAuto = (TabItem) findViewById(R.id.tabAuto);

		switchLockAPanel = (CheckBox) findViewById(R.id.switchLockAPanel);
		switchLockMPanel = (CheckBox) findViewById(R.id.switchLockMPanel);
		switchPhantomAPanel = (CheckBox) findViewById(R.id.switchPhantomAPanel);
		switchPhantomMPanel = (CheckBox) findViewById(R.id.switchPhantomMPanel);
		switchMicMuteAPanel = (CheckBox) findViewById(R.id.switchMicMuteAPanel);
		switchMicMuteMPanel = (CheckBox) findViewById(R.id.switchMicMuteMPanel);
		editTextNumberMixAPanel = (EditText) findViewById(R.id.editTextNumberMixAPanel);
		editTextNumberMixMPanel = (EditText) findViewById(R.id.editTextNumberMixMPanel);
		seekBarMixAPanel = (SeekBar) findViewById(R.id.seekBarMixAPanel);
		seekBarMixMPanel = (SeekBar) findViewById(R.id.seekBarMixMPanel);

		radioGroupADist = (RadioGroup) findViewById(R.id.radioGroupADist);
		radioADistNear = (RadioButton) findViewById(R.id.radioADistNear);
		radioADistFar = (RadioButton) findViewById(R.id.radioADistFar);
		radioGroupATone = (RadioGroup) findViewById(R.id.radioGroupATone);
		radioAToneDark = (RadioButton) findViewById(R.id.radioAToneDark);
		radioAToneNeutral = (RadioButton) findViewById(R.id.radioAToneNeutral);
		radioAToneBright = (RadioButton) findViewById(R.id.radioAToneBright);
		radioGroupATone = (RadioGroup) findViewById(R.id.radioGroupATone);
		radioAGainLow = (RadioButton) findViewById(R.id.radioAGainLow);
		radioAGainNormal = (RadioButton) findViewById(R.id.radioAGainNormal);
		radioAGainHigh = (RadioButton) findViewById(R.id.radioAGainHigh);

		editTextNumberMGain = (EditText) findViewById(R.id.editTextNumberMGain);
		seekBarMGain = (SeekBar) findViewById(R.id.seekBarMGain);
		switchMLimiter = (CheckBox) findViewById(R.id.switchMLimiter);
		radioGroupMComp = (RadioGroup) findViewById(R.id.radioGroupMComp);
		radioMCompOff = (RadioButton) findViewById(R.id.radioMCompOff);
		radioMCompLight = (RadioButton) findViewById(R.id.radioMCompLight);
		radioMCompModerate = (RadioButton) findViewById(R.id.radioMCompModerate);
		radioMCompHeavy = (RadioButton) findViewById(R.id.radioMCompHeavy);
		radioGroupMHpf = (RadioGroup) findViewById(R.id.radioGroupMHpf);
		radioMHpfOff = (RadioButton) findViewById(R.id.radioMHpfOff);
		radioMHpf75Hz = (RadioButton) findViewById(R.id.radioMHpf75Hz);
		radioMHpf150Hz = (RadioButton) findViewById(R.id.radioMHpf150Hz);
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

		editLogText = (EditText) findViewById(R.id.editLogText);

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

		switchLockAPanel.setOnClickListener(this);
		switchLockMPanel.setOnClickListener(this);
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
		radioMHpfOff.setOnClickListener(this);
		radioMHpf75Hz.setOnClickListener(this);
		radioMHpf150Hz.setOnClickListener(this);
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
		switch (v.getId()/*TODO-hi*/) {
			case R.id.btnSelectHIDDevice:
				eventBus.post(new PrepareDevicesListEvent());
				break;

			case R.id.switchLockAPanel:
			case R.id.switchLockMPanel:
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
			case R.id.radioMHpfOff:
				break;
			case R.id.radioMHpf75Hz:
				break;
			case R.id.radioMHpf150Hz:
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
	}

	void getAllMVX2UParameters() {
		mLog("start");
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
		mLog("finish");
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
		switchPhantomAPanel.setEnabled(enable);
		switchPhantomMPanel.setEnabled(enable);
		switchMicMuteAPanel.setEnabled(enable);
		switchMicMuteMPanel.setEnabled(enable);
		editTextNumberMixAPanel.setEnabled(enable);
		editTextNumberMixAPanel.setText(Consts.UNKNOWN_VALUE);  // TODO: conditional
		editTextNumberMixMPanel.setEnabled(enable);
		editTextNumberMixMPanel.setText(Consts.UNKNOWN_VALUE);  // TODO: conditional
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
		editTextNumberMGain.setText(Consts.UNKNOWN_VALUE);  // TODO: conditional
		seekBarMGain.setEnabled(enable);
		seekBarMGain.setIndeterminate(!enable);
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

	private void mLog(String log) {
		editLogText.append(log + "\n");
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onEvent(USBDataReceiveEvent event) {
		mLog(event.getDataAsHex());

		byte[] ba = event.getData();
		int pType = USBUtils.getParamType(ba);
		if (pType < 0)  { return; }
		int pVal = USBUtils.getParamVal(ba);

		mLog(Integer.toHexString(pType) + " - " + Integer.toString(pVal));
		switch (pType) {
			case 0x01000000:  break;
			case 0x010600a6:  // Lock state
				makeSettingsUIEnabled(pVal == 0);
				switchLockAPanel.setChecked(pVal != 0);
				switchLockMPanel.setChecked(pVal != 0);
				break;
			case 0x02000102:  mLog("TODO Manual gain " + String.valueOf(pVal)); break;
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
				mLog("PPOW " + Integer.toString(pVal));
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
			case 0x02000185:  break;  // TODO-hi: Switch to correct panel!
			case 0x02010186:  mLog("TODO Mic mix " + String.valueOf(pVal)); break;
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
			case 0x02000214:  mLog("TODO EQ 1 @ " + String.valueOf(pVal)); break;
			case 0x02000220:  switchEq2.setChecked(pVal != 0); break;
			case 0x02000224:  mLog("TODO EQ 2 @ " + String.valueOf(pVal)); break;
			case 0x02000230:  switchEq3.setChecked(pVal != 0); break;
			case 0x02000234:  mLog("TODO EQ 3 @ " + String.valueOf(pVal)); break;
			case 0x02000240:  switchEq4.setChecked(pVal != 0); break;
			case 0x02000244:  mLog("TODO EQ 4 @ " + String.valueOf(pVal)); break;
			case 0x02000250:  switchEq5.setChecked(pVal != 0); break;
			case 0x02000254:  mLog("TODO EQ 5 @ " + String.valueOf(pVal)); break;
			default:
				mLog("Param Error!");
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
}