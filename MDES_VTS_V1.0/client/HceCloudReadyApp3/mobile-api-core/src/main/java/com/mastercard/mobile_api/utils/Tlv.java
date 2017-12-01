/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */
package com.mastercard.mobile_api.utils;

import com.mastercard.mobile_api.bytes.ByteArray;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Hex;

/**
 * Static helper functions to handle Tlv data.
 */
public enum Tlv {
    INSTANCE;

    /**
     * Creates a Tlv byte array from a tag and a value. Length is calculated.
     *
     * @param tag   the Tlv tag
     * @param value the Tlv value
     * @return the Tlv byte array
     */
    public static ByteArray create(ByteArray tag, ByteArray value) {
        byte[] result = create(tag.getBytes(), value.getBytes());
        return ByteArray.of(result, result.length);
    }

    /**
     * Creates a Tlv byte array from a tag and a value. Length is calculated.
     *
     * @param tag   the Tlv tag
     * @param value the Tlv value
     * @return the Tlv as byte[]
     */
    public static byte[] create(byte[] tag, byte[] value) {
        byte[] length = lengthBytes(value);
        byte[] result = new byte[tag.length + length.length + value.length];
        System.arraycopy(tag, 0, result, 0, tag.length);
        System.arraycopy(length, 0, result, tag.length, length.length);
        System.arraycopy(value, 0, result, tag.length + length.length, value.length);
        return result;
    }

    /**
     * Creates a Tlv byte array from a tag and a value. Length is calculated.
     *
     * @param tag   the Tlv tag
     * @param value the Tlv value
     * @return the Tlv byte array
     */
    public static ByteArray create(byte tag, ByteArray value) {
        byte[] result = create(new byte[]{tag}, value.getBytes());
        return ByteArray.of(result, result.length);
    }

    /**
     * Creates a Tlv byte array from a tag and a value. Length is calculated.
     * Data Input is in Hex String
     *
     * @param tag   the Tlv tag (HEX String)
     * @param value the Tlv value (HEX String)
     * @return the Tlv byte array (HEX String)
     */
    public static String create(String tag, String value) {
        byte[] bValue = new byte[0];
        try {
            bValue = Hex.decodeHex(value.toCharArray());
        } catch (DecoderException e) {
            e.printStackTrace();
        }
        byte[] bLength = lengthBytes(bValue);
        String length = new String(Hex.encodeHex(bLength));
        return (tag + length + value).toUpperCase();
    }

    /**
     * Calculate the Tlv length of a value.
     *
     * @param value the value for which the length is returned
     * @return the length of the value.
     */
    public static ByteArray lengthBytes(ByteArray value) {
        byte[] result = lengthBytes(value.getBytes());
        return ByteArray.of(result, result.length);
    }

    /**
     * Calculate the Tlv length of a value.
     *
     * @param value the value for which the length is returned
     * @return the length of the value.
     */
    private static byte[] lengthBytes(byte[] value) {
        int length = value.length;
        if (value.length <= 0x7F) {
            return new byte[]{(byte) length};
        }
        if (value.length <= 0xFF) {
            return new byte[]{(byte) 0x81, (byte)(length & 0xFF)};
        }
        if (value.length <= 0xFFFF) {
            byte[] result = new byte[3];
            result[0] = (byte) 0x82;
            result[1] = (byte) ((length & 0x0000FF00) >> 8);
            result[2] = (byte) (length & 0x000000FF);
            return result;
        }
        if (value.length <= 0xFFFFFF) {
            byte[] result = new byte[4];
            result[0] = (byte) 0x83;
            result[1] = (byte) ((length & 0x00FF0000) >> 16);
            result[2] = (byte) ((length & 0x0000FF00) >> 8);
            result[3] = (byte) (length & 0x000000FF);
            return result;
        }
        byte[] result = new byte[5];
        result[0] = (byte) 0x84;
        result[1] = (byte) ((length & 0xFF000000) >> 24);
        result[2] = (byte) ((length & 0x00FF0000) >> 16);
        result[3] = (byte) ((length & 0x0000FF00) >> 8);
        result[4] = (byte) (length & 0x000000FF);
        return result;
    }
}
