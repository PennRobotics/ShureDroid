package com.github.pennrobotics.shuredroid.core;

import java.util.Arrays;

public class USBUtils {

	public static int toInt(byte b) {
		return (int) b & 0xFF;
	}

	public static byte toByte(int c) {
		return (byte) (c <= 0x7f ? c : ((c % 0x80) - 0x80));
	}

	private static final char[] HEX_DIGITS_UPPER = "0123456789ABCDEF".toCharArray();
	public static String toHex(byte[] bytes) {
		int pktLen = bytes.length > 10 ? bytes[1] : bytes.length;  // packet should report own length
		if (pktLen > bytes.length)  { pktLen = bytes.length; }  // in case the reported length is wrong

		final char[] buf = new char[3 * pktLen];
		for (int i = 0; i < pktLen; i++) {
			buf[3*i] = Consts.SPACE;
			buf[3*i + 1] = HEX_DIGITS_UPPER[(bytes[i] >> 4) & 0xF];
			buf[3*i + 2] = HEX_DIGITS_UPPER[(bytes[i] & 0xF)];
		}

		if (pktLen > 10) {
			return new String(Arrays.copyOfRange(buf,30,buf.length));
		}
		return new String(buf);
	}
}