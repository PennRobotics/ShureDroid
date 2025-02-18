package com.github.pennrobotics.shuredroid.core;

import java.util.HexFormat;

public class USBUtils {

	public static int toInt(byte b) {
		return (int) b & 0xFF;
	}

	public static byte toByte(int c) {
		return (byte) (c <= 0x7f ? c : ((c % 0x80) - 0x80));
	}

	private static final char[] HEX_DIGITS_UPPER = "0123456789ABCDEF".toCharArray();
	public static String toHex(byte[] bytes) {
		final char[] buf = new char[3 * bytes.length + 1];
		for (int i = 0; i < bytes.length; i++) {
			buf[3*i] = Consts.SPACE;
			buf[3*i + 1] = HEX_DIGITS_UPPER[(bytes[i] >> 4) & 0xF];
			buf[3*i + 2] = HEX_DIGITS_UPPER[(bytes[i] & 0xF)];
		}
		buf[3*bytes.length] = '\n';
		return new String(buf);
	}
}