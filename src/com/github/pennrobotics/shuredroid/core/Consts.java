package com.github.pennrobotics.shuredroid.core;

public abstract class Consts {
	public static final String ACTION_USB_PERMISSION = "com.google.android.HID.action.USB_PERMISSION";
	public static final String MESSAGE_SELECT_YOUR_USB_HID_DEVICE = "Select your Shure MVX2U adapter";
	public static final String MESSAGE_CONNECT_YOUR_USB_HID_DEVICE = "Connect your Shure MVX2U adapter";
	public static final String NEW_LINE = "\n";
	public static final char SPACE = ' ';

	public static final String ACTION_USB_SHOW_DEVICES_LIST = "ACTION_USB_SHOW_DEVICES_LIST";
	public static final String ACTION_USB_DATA_TYPE = "ACTION_USB_DATA_TYPE";
	public static final String USB_HID_TERMINAL_CLOSE_ACTION = "USB_HID_TERMINAL_EXIT";
	public static final int USB_HID_TERMINAL_NOTIFICATION = 45277991;

	private Consts() {
	}
}