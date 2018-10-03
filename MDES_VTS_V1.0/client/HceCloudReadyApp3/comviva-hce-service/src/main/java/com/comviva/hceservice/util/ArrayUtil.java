package com.comviva.hceservice.util;

import com.mastercard.mpsdk.utils.Utils;
import com.mastercard.upgrade.utils.bytes.ByteArray;

import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class ArrayUtil {
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

	/**
	 * Length of the random number in bytes.
	 * @param length Length of random number.
	 * @return Random Number
	 */
	public static byte[] getRandomNumber(int length) {
		SecureRandom random = new SecureRandom();
		byte[] randomNumber = new byte[length];
		random.nextBytes(randomNumber);
		return randomNumber;
	}



	public static final ByteArray encryptRandomGeneratedKey(final byte[] data, final byte[] key)
			throws Exception {
		final byte[] result;
		try {
			final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
			final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key);

			cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").
					generatePublic(x509EncodedKeySpec));

			result = cipher.doFinal(data);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
				InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
			throw new Exception(e.getMessage());
		}
		final ByteArray encryptedData = ByteArray.of(result);
		Utils.clearByteArray(result);  // We need to clean up temporary variables
		return encryptedData;
	}


}
