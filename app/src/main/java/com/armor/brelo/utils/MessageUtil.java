package com.armor.brelo.utils;

import java.math.BigInteger;

public class MessageUtil {

	/**
	 * OP_type Lock transition Transition type
	 * <p/>
	 * 0x00 No operation
	 * <p/>
	 * 0x11 Stay in Position 1 stay Unlocked
	 * <p/>
	 * 0x12 From position 1 to position 2 Locking
	 * <p/>
	 * 0x13 From position 1 to position 3 Locking
	 * <p/>
	 * 0x14 From position 1 to position 4 Locking
	 * <p/>
	 * 0x21 From position 2 to position 1 Unlocking
	 * <p/>
	 * 0x23 From position 2 to position 3 Locking
	 * <p/>
	 * 0x24 From position 2 to position 4 Locking
	 * <p/>
	 * 0x31 From position 3 to position 1 Unlocking
	 * <p/>
	 * 0x32 From position 3 to position 2 Unlocking
	 * <p/>
	 * 0x34 From position 3 to position 4 Locking
	 * <p/>
	 * 0x41 From position 4 to position 1 Unlocking
	 * <p/>
	 * 0x42 From position 4 to position 2 Unlocking
	 * <p/>
	 * 0x43 From position 4 to position 3 Unlocking
	 * <p/>
	 * 0xA0 Electronic access off Security
	 * <p/>
	 * 0xA1 Electronic access on Security
	 * <p/>
	 * 0xB0 Mechanical access off Security
	 * <p/>
	 * 0xB1 Mechanical access on Security
	 * <p/>
	 * 0xC0 SOS on Security
	 * <p/>
	 * 0xC1 SOS off Security
	 */

	public static byte[] getOpData(int previousIdx, int currentIdx) {
		System.out.println("previousIdx" + previousIdx);
		System.out.println("currentIdx" + currentIdx);
		byte[] dummVal = new byte[1];
		byte[] zero = new byte[1];
		zero[0] = 0x00;
		//syso.log("previousIdx"+previousIdx);
		//console.log("currentIdx"+currentIdx);
		if (previousIdx == currentIdx) {
			/*BigInteger integer = BigInteger.valueOf(0xA1);
			dummVal = integer.toByteArray();
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;*/
			return zero;
		} else if (currentIdx == -1) {
			BigInteger integer = BigInteger.valueOf(0xA1);
			dummVal[0] = (byte) 0xA1;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 3 && currentIdx == 1) {
			BigInteger integer = BigInteger.valueOf(0xAA);
			dummVal[0] = (byte) 0xAA;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 3 && currentIdx == 2) {
			BigInteger integer = BigInteger.valueOf(0xA1);
			dummVal[0] = (byte) 0xA1;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 2 && currentIdx == 3) {
			BigInteger integer = BigInteger.valueOf(0xCC);
			dummVal[0] = (byte) 0xCC;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 2 && currentIdx == 1) {
			BigInteger integer = BigInteger.valueOf(0xC1);
			dummVal[0] = (byte) 0xC1;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 2 && currentIdx == 3) {
			BigInteger integer = BigInteger.valueOf(0xA1);
			dummVal[0] = (byte) 0xA1;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 2 && currentIdx == 4) {
			dummVal[0] = 0x24;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;

		} else if (previousIdx == 3 && currentIdx == 1) {
			BigInteger integer = BigInteger.valueOf(0xC1);
			dummVal[0] = (byte) 0xC1;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else if (previousIdx == 3 && currentIdx == 2) {
			BigInteger integer = BigInteger.valueOf(0xA1);
			dummVal[0] = (byte) 0xA1;
			System.out.println("dummVal String: " + dummVal[0] + "byte dummVal " + dummVal);
			return dummVal;
		} else
			return zero;
	}
}
