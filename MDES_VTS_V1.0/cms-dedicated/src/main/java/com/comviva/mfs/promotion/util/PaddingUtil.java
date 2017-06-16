package com.comviva.mfs.promotion.util;

import java.util.Arrays;

/** Contains all padding algorithms implementations
 *  Created by tarkeshwar.v on 1/27/2017.
 */
public class PaddingUtil {
    public enum Padding {
        NoPadding,      // JCA standard
        PKCS5Padding,   // JCA standard
        ISO7816_4,
        PKCS7Padding
    }

    /**
     * ISO/IEC 7816-4 padding.
     *
     * @param data Data to be padded
     * @return padded data
     */
    public static byte[] padISO7816(byte[] data, final int blockSize) {
        int paddedDataLen = data.length + (blockSize - (data.length % blockSize));
        byte[] paddedData = Arrays.copyOf(data, paddedDataLen);
        paddedData[data.length] = (byte) 0x80;
        return paddedData;
    }

    /**
     * Removes ISO/IES 7816-4 padding
     *
     * @param data Padded data
     * @return Data after removing padding
     */
    public static byte[] removeISO7816Padding(byte[] data) {
        int i = data.length - 1;
        while (i >= 0) {
            if (data[i] == (byte) 0x80) {
                break;
            }
            i--;
        }
        if (i < 0) {
            return null;
        }
        return Arrays.copyOf(data, i);
    }
}
