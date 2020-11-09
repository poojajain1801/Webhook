/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package com.comviva.mfs.hce.appserver.util.common.messagedigest;


import java.security.SecureRandom;

public class ArrayUtil {

    private ArrayUtil(){}

    /**
     * getHexString
     * @param buffer byte array
     * @return string
     * */
	public static String getHexString(byte[] buffer){
		StringBuilder hexStr = new StringBuilder();

		for(int i = 0; i < buffer.length; i++){
			hexStr.append(String.format("%02X", buffer[i]));
		}
		return hexStr.toString();
	}


    /**
     * getRandom
     * @param length length of rand
     * @return byte array
     * */
    public static byte[] getRandom(final int length) {
        SecureRandom secureRandom = new SecureRandom();
        byte[] random = new byte[length];
        secureRandom.nextBytes(random);
        return random;
    }

    /**
     * compare
     * @param arr1 array1
     * @param arr2 array2
     * @param offset1 offset1
     * @param offset2 offset2
     * @param length length
     * @return boolean
     * */
    public static boolean compare(byte[] arr1, int offset1, byte[] arr2, int offset2, int length) {
		for(int i = 0; i < length; i++) {
			if(arr1[offset1+i] != arr2[offset2+i]) {
				return false;
			}
		}
		return true;
	}



	/**
     * asciiToHex
     * @param asciiValue ascii value
     * @return string
     * */
	public static String asciiToHex(String asciiValue) {

		char[] chars = asciiValue.toCharArray();

		StringBuilder hex = new StringBuilder();

		for (int i = 0; i < chars.length; i++)

		{

			hex.append(Integer.toHexString((int) chars[i]));

		}

		return hex.toString();
	}

	/**
     * xor
     * @param arr1 array1
     * @param arr2 array2
     * @param offset1 offset1
     * @param offset2 offset2
     * @param length length
     * @return byte array
     * */
    public static byte[] xor(byte[] arr1, int offset1, byte[] arr2, int offset2, int length) {
        byte[] out = new byte[length];
        for(int i = 0; i < length; i++) {
            out[i] = (byte)(arr1[offset1+i] ^ arr2[offset2+i]);
        }
        return out;
    }


    /**
     * getByteArray
     * @param hexStr hex String
     * @return byte array
     * */
    public static byte[] getByteArray(String hexStr){
        final int two = 2;
        final int sixteen =16;
        int length = hexStr.length()/two;
        byte[] buffer = new byte[length];

        for(int i = 0; i < length; i++){
            buffer[i] = (byte)Integer.parseInt(hexStr.substring(two*i, two*i+two), sixteen);
        }
        return buffer;
    }
}
