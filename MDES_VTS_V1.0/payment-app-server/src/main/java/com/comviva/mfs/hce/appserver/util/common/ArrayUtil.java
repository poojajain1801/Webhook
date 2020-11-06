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
 * <p/>
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.comviva.mfs.hce.appserver.util.common;

import lombok.NoArgsConstructor;

import java.security.SecureRandom;

@NoArgsConstructor
public class ArrayUtil {

    public static String getHexString(byte[] buffer){
		StringBuilder hexStr = new StringBuilder();

		for(int i = 0; i < buffer.length; i++){
			hexStr.append(String.format("%02X", buffer[i]));
		}
		return hexStr.toString();
	}

	public static byte[] getByteArray(String hexStr){
        int two = 2;
        int length = hexStr.length()/ two;
		byte[] buffer = new byte[length];

		for(int i = 0; i < length; i++){
            int sixteen = 16;
            buffer[i] = (byte)Integer.parseInt(hexStr.substring(two *i, two *i+ two), sixteen);
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
	public static String getRequestId () {
        final int twentyTwo = 22;
        return "COMREQID"+getHexString(getRandom(twentyTwo));
	}
}
