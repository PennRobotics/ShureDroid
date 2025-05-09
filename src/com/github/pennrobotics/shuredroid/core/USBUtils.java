package com.github.pennrobotics.shuredroid.core;

import androidx.annotation.NonNull;
import java.util.Arrays;

public class USBUtils {
	static byte SEQ = 0x00;

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

	public static byte hexB(String hexPair) {
		return (byte) ((Character.digit(hexPair.charAt(0), 16) << 4) + Character.digit(hexPair.charAt(1), 16));
	}

    /* Renderer: HexFormat.ofDelimiter(" ").formatHex(Arrays.copyOfRange(this, 0, 20)) */
    public static byte[] padPktData(String hexStr) {
        return padPktData(hexStr, 0x03);
    }

    /* Renderer: HexFormat.ofDelimiter(" ").formatHex(Arrays.copyOfRange(this, 0, 20)) */
	public static byte[] padPktData(String hexStr, int ch) {
		byte[] packet = new byte[64];
		int len = hexStr.length() / 2;

		packet[0] = 0x01;
		packet[1] = (byte) (10 + len);
		packet[2] = 0x11;
		packet[3] = 0x22;
		packet[4] = SEQ;
		packet[5] = (byte) ch;
		packet[6] = 0x08;
		packet[7] = (byte) (len + 2);
		packet[8] = 0x70;
		packet[9] = (byte) (len + 2);

		for (int i = 0; i < 2*len; i += 2) {
			packet[10 + i/2] = hexB(hexStr.substring(i, i+2));
		}

		int crc = getCRC(Arrays.copyOfRange(packet, 2, 10+len));
		packet[len+10] = (byte) ((crc>>8) & 0xFF);
		packet[len+11] = (byte) ((crc) & 0xFF);

		SEQ = (byte) ((SEQ + 1) % 256);

		return packet;
	}

	private static int getCRC(@NonNull final byte[] ba) {
		int crc = 0x0000;

		for (byte b : ba) {
			for (int sh = 0; sh < 8; ++sh) {
				final int bit = b >> sh & 1;
				final int msb = crc >> 15 & 1;
				crc <<= 1;
				if ((msb ^ bit) != 0)  { crc ^= 0x8005; }
			}
		}

		return Integer.reverse(crc) >>> 16;
	}

	public static int getParamType(@NonNull final byte[] ba) {
		if (ba.length < 10
				|| ba[0] != 0x01
				|| ba[2] != 0x11
				|| ba[3] != 0x22
				|| ba[6] != 0x08
				|| ba[8] != 0x70
				|| ba[7] != ba[9])
		{
			return -1;  // Bad packet header
		}

		if (ba.length < 16
				|| ba[10] != 0x03
				|| ba[11] != 0x02)
		{
			return -2;  // Not a GetParam result packet
		}

		return Byte.toUnsignedInt(ba[12]) << 24
				| Byte.toUnsignedInt(ba[13]) << 16
				| Byte.toUnsignedInt(ba[14]) << 8
				| Byte.toUnsignedInt(ba[15]);
	}

	public static int getParamVal(@NonNull final byte[] ba) {
		int len = ba[7];
		int result = Byte.toUnsignedInt(ba[16]);
		if (len == 9)  { return result; }
		result = (result << 8) | Byte.toUnsignedInt(ba[17]);
		if (len == 10)  { return result; }
		result = (result << 16)
				| (Byte.toUnsignedInt(ba[18]) << 8)
				| (Byte.toUnsignedInt(ba[19]));
		return result;
	}
}