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

import java.math.BigInteger;

/**
 * The Class Utils.
 */
public enum Utils {
    INSTANCE;
    /**
     * Hexadecimal string prefix.
     */
    private static final String HEX_PREFIX = "0x";

    /**
     * Clear byte array.
     *
     * @param buffer the buffer
     */
    public static void clearByteArray(final byte[] buffer) {
        if (buffer == null) {
            return;
        }
        final int length = buffer.length;
        for (int i = 0; i < length; i++) {
            buffer[i] = (byte) 0;
        }
    }

    /**
     * Clear ByteArray.
     *
     * @param buffer instance of ByteArray
     */
    public static void clearByteArray(ByteArray buffer) {
        if (buffer != null) {
            buffer.clear();
        }
    }

    /**
     * BCD Amount array to string.
     *
     * @param data   the data
     * @param offset the offset
     * @param length the length
     * @return the string
     */
    public static String bcdAmountArrayToString(final byte[] data, final int offset,
                                                final int length) {
        String string = "";
        byte high;
        byte low;
        Integer hiInteger;
        Integer loInteger;
        boolean maskZeros = true;

        if ((offset >= data.length) || (offset + length > data.length)) {
            throw new ArrayIndexOutOfBoundsException();
        }

        for (int i = offset; i < (offset + length); i++) {
            high = (byte) ((data[i] >>> 4) & 0xF);
            low = (byte) (data[i] & 0xF);

            if (high > 9 || low > 9) {
                throw new IllegalArgumentException();
            }

            hiInteger = (int) high;
            loInteger = (int) low;

            if (!maskZeros || (high != 0)) {
                maskZeros = false;
                string = string + hiInteger.toString();
            }

            if (!maskZeros || (low != 0)) {
                maskZeros = false;
                string = string + loInteger.toString();
            }

            if (i == (offset + length - 2)) {

                string = string + ".";
                maskZeros = false;
            }
        }

        if ((string.isEmpty()) || (string.charAt(0) == '.')) {
            string = "0" + string;
        }

        return string;
    }

    /**
     * Constructs a byte array from the given hexadecimal string. The string may
     * begin with the prefix '0x'.
     *
     * @param hexString the hexadecimal string.
     * @return a byte array. Never <code>null</code>.
     * @throws NumberFormatException if the string has invalid characters.
     */
    public static byte[] readHexString(String hexString) {

        if (hexString == null || hexString.isEmpty() || hexString.equals(HEX_PREFIX)) {
            return new byte[]{};
        } else {

            if (hexString.startsWith(HEX_PREFIX)) {
                hexString = hexString.substring(2);
            }

            final byte[] data = new byte[hexString.length() / 2];

            for (int i = 0; i < data.length; i++) {
                data[i] =
                        (byte) (Integer.parseInt(hexString.substring(i * 2, i * 2 + 2), 16) & 0xFF);
            }

            return data;
        }
    }

    /**
     * Reads an (signed) integer from the given byte array starting at the
     * specified array index.
     *
     * @param data         byte array containing the 4 integer bytes.
     * @param offset       beginning of the integer value in the array.
     * @param littleEndian true if little endian byte order is used. If false then the
     *                     integer is read using big endian.
     * @return the read integer value.
     */
    private static int readInt(byte[] data, final int offset, final boolean littleEndian) {

        final int tmpLength = (data.length - offset);
        if (tmpLength < 4) {
            final byte[] tmpBuffer = {(byte) 0, (byte) 0, (byte) 0, (byte) 0};
            System.arraycopy(data, 0, tmpBuffer, (4 - tmpLength), tmpLength);
            data = tmpBuffer;
        }
        if (littleEndian) {
            return (((data[offset + 3] & 0xff) << 24) | ((data[offset + 2] & 0xff) << 16)
                    | ((data[offset + 1] & 0xff) << 8) | (data[offset] & 0xff));
        } else {
            return (((data[offset] & 0xff) << 24) | ((data[offset + 1] & 0xff) << 16)
                    | ((data[offset + 2] & 0xff) << 8) | (data[offset + 3] & 0xff));
        }
    }

    /**
     * Reads an integer from the given byte array starting at the specified
     * array index using big endian byte order.
     *
     * @return the read integer value.
     */
    public static int readInt(final byte[] data, final int offset) {
        return readInt(data, offset, false);
    }

    /**
     * Reads a (signed) short integer from the byte array.
     *
     * @param data         byte array containing the 2 integer bytes.
     * @param offset       beginning of the short integer value in the array.
     * @param littleEndian true if little endian byte order is used. If false then the
     *                     integer is read using big endian.
     * @return the read short integer value.
     */
    private static int readShort(final byte[] data, final int offset,
                                 final boolean littleEndian) {

        if (littleEndian) {
            return ((data[offset + 1] << 8) | (data[offset] & 0xFF)) & 0xFFFF;
        } else {
            return ((data[offset] << 8) | (data[offset + 1] & 0xFF)) & 0xFFFF;
        }
    }

    /**
     * Reads a (signed) short integer from the byte array using big endian byte
     * order.
     *
     * @param data   the data
     * @param offset the offset
     * @return the short
     */
    public static short readShort(final byte[] data, final int offset) {
        return (short) (readShort(data, offset, false));
    }

    /**
     * Writes a integer (4B) to the byte array.
     *
     * @param buffer a byte array.
     * @param offset defines the offset of the integer in the array.
     * @param value  the value to be written.
     */
    public static void writeInt(ByteArray buffer, int offset, long value) {

        buffer.setByte(offset, (byte) ((value >> 24) & 0xFF));
        buffer.setByte(offset + 1, (byte) ((value >> 16) & 0xFF));
        buffer.setByte(offset + 2, (byte) ((value >> 8) & 0xFF));
        buffer.setByte(offset + 3, (byte) (value & 0xFF));

    }

    /**
     * Returns true if the two given byte arrays have matching content or if
     * they both are <code>null</code>.
     *
     * @param a       the a
     * @param b       the b
     * @param aOffset defines where to start comparing bytes in the <code>a</code>
     *                array.
     * @param bOffset defines where to start comparing bytes in the <code>b</code>
     *                array.
     * @param len     defines how many bytes should be compared.
     * @return true, if successful
     */
    public static boolean equals(final byte[] a, final byte[] b, final int aOffset,
                                 final int bOffset, final int len) {

        if (a == null && b == null) {
            return true;
        } else if (a != null && b != null && (aOffset + len) <= a.length
                && (bOffset + len) <= b.length) {

            for (int i = 0; i < len; i++) {
                if (a[aOffset + i] != b[bOffset + i]) {
                    return false;
                }
            }

            return true;
        } else {
            return false;
        }
    }

    /**
     * Returns true if the two given byte arrays are equal in length and in
     * their contents match, or if they both are <code>null</code>.
     *
     * @param a the a
     * @param b the b
     * @return true, if successful
     */
    public static boolean equals(final byte[] a, final byte[] b) {

        if (a == null && b == null) return true;
        if (a == null || b == null || a.length != b.length) return false;

        for (int i = 0; i < a.length; i++) {
            if (a[i] != b[i]) return false;
        }
        return true;
    }

    /**
     * Perform XOR operation in given data.
     *
     * @param firstArray   First array
     * @param firstOffset  First offset
     * @param secondArray  Second array
     * @param secondOffset Second offset
     * @param length       Length
     * @return byte array
     */
    static public byte[] doXor(byte[] firstArray, int firstOffset, byte[] secondArray,
                               int secondOffset, int length) {
        byte[] temp = new byte[length];
        for (int i = 0; i < length; i++) {
            temp[i] = (byte) (firstArray[i + firstOffset] ^ secondArray[i + secondOffset]);
        }
        return temp;
    }

    /**
     * Perform XOR operation in given data.
     *
     * @param firstArray  First array
     * @param secondArray Second array
     * @param length      Length
     * @return byte array
     */
    static public byte[] doXor(ByteArray firstArray, ByteArray secondArray, int length) {
        return doXor(firstArray.getBytes(), 0, secondArray.getBytes(), 0, length);
    }


    /**
     * Convert a long input to a binary byte array
     *
     * @param value   The long value to be converted to BCD Byte Array
     * @param noBytes The number of bytes into which the result should be stored
     * @return The Binary Byte Array containing the representation of the long
     */
    public static ByteArray longToBinaryByteArray(final long value, final int noBytes) {
        // Check that the number of requested bytes make sense. If not, we will use 8
        final int length = (noBytes < 0 || noBytes > 8) ? 8 : noBytes;
        final ByteArray result = ByteArray.get(length);
        final byte[] data = result.getBytes();

        // Fill each item of the array by masking the relevant bits and then shifting all to the
        // right to make sure they fit into a byte.
        for (int i = 0; i < length; i++) {
            data[length - 1 - i] = (byte) ((value & (0x00FF << (i * 8))) >> (i * 8));
        }
        return result;
    }

    /***
     * Convert a long into a Byte Array encoded with BCD
     * For example, longToBcdByteArray(1023, 6) is coded as 0x000000001023
     *
     * @param number  The long value to be converted to BCD Byte Array
     * @param noBytes The number of bytes into which the result should be stored
     * @return A Byte Array BCD encoded
     */
    public static ByteArray longToBcdByteArray(final long number, final int noBytes) {
        BigInteger bigInteger = BigInteger.valueOf(number);
        String value = bigInteger.toString(16);
        final int noDigits = noBytes * 2;
        if (noDigits == value.length()) return ByteArray.of(value);
        if (noDigits < value.length()) return ByteArray.of(value.substring(0, noDigits));

        // We need to add leading zeroes
        final StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < noDigits - value.length(); i++) {
            stringBuilder.append("0");
        }
        stringBuilder.append(value);
        return ByteArray.of(stringBuilder.toString());
    }

    public static byte[] longToBcd(long num, int size) {

        int digits = 0;

        long temp = num;
        while (temp != 0) {
            digits++;
            temp /= 10;
        }

        int byteLen = digits % 2 == 0 ? digits / 2 : (digits + 1) / 2;
        boolean isOdd = digits % 2 != 0;

        byte bcd[] = new byte[byteLen];

        for (int i = 0; i < digits; i++) {
            byte tmp = (byte) (num % 10);

            if (i == digits - 1 && isOdd) {
                bcd[i / 2] = tmp;
            } else if (i % 2 == 0) {
                bcd[i / 2] = tmp;
            } else {
                byte foo = (byte) (tmp << 4);
                bcd[i / 2] |= foo;
            }

            num /= 10;
        }

        for (int i = 0; i < byteLen / 2; i++) {
            byte tmp = bcd[i];
            bcd[i] = bcd[byteLen - i - 1];
            bcd[byteLen - i - 1] = tmp;
        }

        if (size == byteLen) {
            return bcd;
        } else {
            byte[] ret = new byte[size];
            System.arraycopy(bcd, 0, ret, size - byteLen, byteLen);
            return ret;
        }
    }

    /**
     * Check whether an array is composed of all zeroes elements.
     *
     * @param data The input data as ByteArray
     * @return true if all the elements are 0x00, false otherwise
     */
    public static boolean isZero(ByteArray data) {
        return isZero(data.getBytes());
    }

    /**
     * Check whether an array is composed of all zeroes elements.
     *
     * @param data The input data as byte[]
     * @return true if all the elements are 0x00, false otherwise
     */
    public static boolean isZero(final byte[] data) {
        if (data == null) throw new NullPointerException("Input data is null in isZero(...)");
        for (byte elem : data) {
            if (elem != 0x00) return false;
        }
        return true;
    }

    /**
     * Check whether the terminal type indicates offline only terminal
     *
     * @param terminalType The terminal type as byte
     * @return true if the terminal is offline only, false otherwise.
     */
    public static boolean isTerminalOffline(byte terminalType) {
        final byte TERMINAL_TYPE_MASK = 0x0F;
        final byte TERMINAL_OFFLINE_VALUE_1 = 0x03;
        final byte TERMINAL_OFFLINE_VALUE_2 = 0x06;

        final byte terminal = (byte) (terminalType & TERMINAL_TYPE_MASK);
        return terminal == TERMINAL_OFFLINE_VALUE_1 ||
                terminal == TERMINAL_OFFLINE_VALUE_2;
    }

    /**
     * Copy a sub range of the given array start is included, end is excluded
     *
     * @param value The byte[] to be parsed
     * @param start The index of the first item to be copied
     * @param end   The index after the last item to be copied
     * @return The copied range
     */
    static public byte[] copyArrayRange(final byte[] value, int start, int end) {
        int noBytes = end - start;
        byte[] result = new byte[noBytes];
        System.arraycopy(value, start, result, 0, noBytes);
        return result;
    }

    /**
     * Utility function to convert two bytes (e.g. a word) into a char (or integer). We could
     * have used Java libraries (e.g. Integer.valueOf(String...)), however, that would have been
     * limiting from a  security perspective (need to rely on strings
     */
    public static char wordToChar(final byte first, final byte second) {
        return (char) ((((first & 0xF0) >>> 4) << 12) + ((first & 0x0F) << 8) +
                (((second & 0xF0) >>> 4) << 4) + (second & 0x0F));
    }

    /**
     * Pad the PAN to be of even length in case of odd PANs. It is used to pad the remote
     * management PAN information
     */
    public static String padPan(String inputPan) {
        String pan = inputPan;
        if (pan.length() % 2 != 0) {
            pan += "F";
        }
        return pan;
    }

    /***
     * Convert a byte BCD encoded value into an integer
     *
     * @param input The BCD packed byte
     * @return the integer value representing the BCD packed byte
     */
    public static int bcdByteToInt(final byte input) {
        return (((input & 0x00F0) >> 4) * 10) + (input & 0x0F);
    }

    /***
     * Convert a 2 byte value into a character. If the input byte array is greater than 2 bytes,
     * only first two bytes will be considered for character conversion.
     *
     * @param input The byte array to convert
     * @return the character value.
     */
    public static Character byteToChar(final byte[] input) {
        if (input == null || input.length < 2) {
            return null;
        }
        return (char) (input[0] << 8 | (input[1] & 0xFF));
    }
}
