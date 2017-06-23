package com.comviva.mfs.promotion.util;

import java.security.SecureRandom;

public class ArrayUtil {
    private static final String ALPHA_NUMERIC_STRING = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";

	public static String getHexString(byte[] buffer){
		StringBuilder hexStr = new StringBuilder();

		for(int i = 0; i < buffer.length; i++){
			hexStr.append(String.format("%02X", buffer[i]));
		}
		return hexStr.toString();
	}

	public static byte[] getByteArray(String hexStr){
		int length = hexStr.length()/2;
		byte[] buffer = new byte[length];

		for(int i = 0; i < length; i++){
			buffer[i] = (byte)Integer.parseInt(hexStr.substring(2*i, 2*i+2), 16);
		}
		return buffer;
	}

	public static byte[] xor(byte[] arr1, int offset1, byte[] arr2, int offset2, int length) {
	    byte[] out = new byte[length];
        for(int i = 0; i < length; i++) {
            out[i] = (byte)(arr1[offset1+i] ^ arr2[offset2+i]);
        }
        return out;
    }

    public static boolean compare(byte[] arr1, int offset1, byte[] arr2, int offset2, int length) {
		for(int i = 0; i < length; i++) {
			if(arr1[offset1+i] != arr2[offset2+i]) {
				return false;
			}
		}
		return true;
	}

	public static byte[] getRandom(final int length) {
		SecureRandom secureRandom = new SecureRandom();
		byte[] random = new byte[length];
		secureRandom.nextBytes(random);
		return random;
	}

    public static String randomAlphaNumeric(int count) {
        StringBuilder builder = new StringBuilder();
        while (count-- != 0) {
            int character = (int)(Math.random()*ALPHA_NUMERIC_STRING.length());
            builder.append(ALPHA_NUMERIC_STRING.charAt(character));
        }
        return builder.toString();
    }
}
