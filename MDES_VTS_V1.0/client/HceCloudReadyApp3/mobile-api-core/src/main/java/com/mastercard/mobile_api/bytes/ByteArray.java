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
package com.mastercard.mobile_api.bytes;

import org.apache.commons.codec.DecoderException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

/**
 * ByteArray
 */
final public class ByteArray {
    /**
     * Get a ByteArray of a specified length
     */
    static public ByteArray get(int size) {
        return new ByteArray(size, (byte) 0x00);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param hexString A Java byte array
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(String hexString) {
        return new ByteArray(hexString);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param value The Value of the ByteArray as Short (e.g. 127 -> 0x007F, 257 -> 0x0101)
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(short value) {
        return new ByteArray(value);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param value The Value of the ByteArray as char (e.g. 127 -> 0x007F, 257 -> 0x0101)
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(char value) {
        return new ByteArray(value);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param value The Value of the ByteArray as Byte (e.g. 127 -> 0x7F, 8 -> 0x08)
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(byte value) {
        return new ByteArray(value);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param bytes A Java byte array
     * @param size  The number of bytes (from the beginning) to be added to the ByteArray
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(final byte[] bytes, int size) {
        return new ByteArray(bytes, size);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param byteArray A ByteArray object
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(ByteArray byteArray) {
        byte[] bytes = byteArray.getBytes();
        return new ByteArray(bytes, bytes.length);
    }

    /**
     * Create a Byte Array object with the equivalent value of the input
     * The input data is copied
     *
     * @param bytes A Java byte array
     * @return a ByteArray object that has an equivalent value of the input data
     */
    static public ByteArray of(byte[] bytes) {
        return new ByteArray(bytes, bytes.length);
    }

    /**
     * Convert the Byte Array into a Java String Base64 formatted
     *
     * @return A Java String with the content of the Byte Array encoded in Base64
     */
    public final String toBase64String() {
        return new String(Base64.encodeBase64(mData));
    }

    /**
     * Convert the Byte Array into a Java String HEX formatted
     *
     * @return A Java String with the content of the Byte Array encoded in HEX
     */
    public final String toHexString() {
        return new String(Hex.encodeHex(mData)).toUpperCase();
    }

    /**
     * Convert the Byte Array into a Java String. By default the String would be HEX formatted
     *
     * @return A Java String with the content of the Byte Array encoded in HEX
     */
    public final String toString() {
        return toHexString();
    }

    /**
     * Convert the Byte Array into a Java String formatted as UTF 8
     *
     * @return A Java String with the content of the Byte Array encoded in UTF 8
     */
    public final String toUtf8String() {
        return new String(mData);
    }

    /**
     * Gets the bytes.
     *
     * @return the bytes
     */
    public final byte[] getBytes() {
        return mData;
    }

    /**
     * Compare ByteArray objects.
     *
     * @return true if each element of the internal representation matches, false otherwise.
     */
    public final boolean isEqual(ByteArray toCompare) {
        return Arrays.equals(this.mData, toCompare.getBytes());
    }

    /**
     * Gets the length.
     *
     * @return the length
     */
    public final int getLength() {
        return mData.length;
    }

    /**
     * Check whether the Byte Array is empty
     *
     * @return True if empty (i.e. zero elements), false otherwise
     */
    public final boolean isEmpty() {
        return this.mData.length == 0;
    }

    /**
     * Calculate the bit wise AND between the Byte Array and the Byte Array provided as parameter
     *
     * @param andMask The Byte Array to be used as AND Mask
     * @return A third ByteArray which is the logic AND between the mask and the ByteArray
     */
    public final ByteArray bitWiseAnd(final ByteArray andMask) {
        if (andMask == null || (andMask.getLength() != this.mData.length)) {
            throw new IllegalArgumentException("Invalid AND Mask");
        }

        byte[] mask = andMask.getBytes();
        byte[] result = new byte[this.mData.length];

        for (int i = 0; i < mData.length; i++) {
            result[i] = (byte) (this.mData[i] & mask[i]);
        }
        return ByteArray.of(result);
    }

    public final ByteArray append(final ByteArray input) {
        if (input == null || input.getBytes() == null) {
            return this;
        }

        ByteArray toAppend;

        // Manage aliasing
        if (input == this) {
            toAppend = ByteArray.of(input);
        } else {
            toAppend = input;
        }

        // Resizing byte array
        final int oldLength = mData.length;

        resize(mData.length + toAppend.getLength());
        // Data to be appended
        System.arraycopy(toAppend.getBytes(), 0, mData, oldLength, toAppend.getLength());
        if (input == this) {
            // Clear the vector if we have cloned part of it (security reasons)
            toAppend.clear();
        }
        return this;
    }

    /**
     * Sets the byte.
     *
     * @param offset    the offset
     * @param byteValue the byteValue
     */
    public final void setByte(final int offset, final byte byteValue) {
        mData[offset] = byteValue;
    }

    /**
     * Gets the byte.
     *
     * @param offset the offset
     * @return the byte
     */
    public final byte getByte(final int offset) {
        return mData[offset];
    }

    /**
     * Resize.
     *
     * @param newLength the newLength
     */
    public final void resize(final int newLength) {
        if (newLength > mData.length) {
            final byte[] newArray = new byte[newLength];
            System.arraycopy(mData, 0, newArray, 0, mData.length);
            mData = newArray;
        }
    }

    /**
     * Append byte.
     *
     * @param value the value
     */
    public final ByteArray appendByte(final byte value) {
        final int oldLength = mData.length;

        resize(mData.length + 1);
        // Data to be appended
        mData[oldLength] = value;
        return this;
    }

    /**
     * Fill all the elements of the Byte Array with the same value provided as input
     *
     * @param val The value to be used to fill the ByteArray
     */
    @Deprecated
    public final void fill(byte val) {
        Arrays.fill(this.mData, val);
    }

    /**
     * Securely clear the internal data by zeroing all the elements
     */
    public final void clear() {
        if (this.mData != null) Arrays.fill(this.mData, (byte) 0x00);
    }

    /**
     * Copies the specified range of the specified Byte Array into a new Byte Array.
     * The initial index of the range (<tt>from</tt>) must lie between zero
     * and <tt>original.length</tt>, inclusive.  The value at
     * <tt>original[from]</tt> is placed into the initial element of the copy
     * (unless <tt>from == original.length</tt> or <tt>from == to</tt>).
     * Values from subsequent elements in the original array are placed into
     * subsequent elements in the copy.  The final index of the range
     * (<tt>to</tt>), which must be greater than or equal to <tt>from</tt>,
     * may be greater than <tt>original.length</tt>, in which case
     * <tt>(byte)0</tt> is placed in all elements of the copy whose index is
     * greater than or equal to <tt>original.length - from</tt>.  The length
     * of the returned array will be <tt>to - from</tt>.
     *
     * @param from the initial index of the range to be copied, inclusive
     * @param to   the final index of the range to be copied, exclusive.
     *             (This index may lie outside the array.)
     * @return a new array containing the specified range from the original array,
     * truncated or padded with zeros to obtain the required length
     * @throws ArrayIndexOutOfBoundsException if {@code from < 0}
     *                                        or {@code from > original.length}
     * @throws IllegalArgumentException       if <tt>from &gt; to</tt>
     * @throws NullPointerException           if <tt>original</tt> is null
     */
    public final ByteArray copyOfRange(final int from, final int to) {
        int length = to - from;
        return new ByteArray(Arrays.copyOfRange(this.mData, from, to), length);
    }

    /**
     * The internal representation of the Byte Array.
     */
    private byte[] mData;

    /**
     * Instantiates a new byte array.
     *
     * @param length the length
     */
    private ByteArray(final int length, byte defaultValue) {
        mData = new byte[length];
        if (defaultValue != 0x00) {
            for (int i = 0; i < mData.length; i++) {
                mData[i] = defaultValue;
            }
        }
    }

    /**
     * Instantiates a new byte array.
     *
     * @param hexString the hexString
     */
    private ByteArray(final String hexString) {
        // We add one zero at the beginning to support odd HEX strings
        final String adjustedHex;
        if ((hexString.length() % 2) == 0) {
            adjustedHex = hexString;
        } else {
            adjustedHex = "0" + hexString;
        }
        try {
            mData = Hex.decodeHex(adjustedHex.toCharArray());
        } catch (DecoderException e) {
            throw new IllegalArgumentException("Invalid HEX String: " + adjustedHex);
        }
    }

    /**
     * Instantiates a new Byte Array object. This constructor is private and cannot be called,
     * please use ByteArray.of(...) instead.
     *
     * @param value the value of the byte array as short
     */
    private ByteArray(char value) {
        mData = new byte[2];
        mData[0] = (byte) ((value & 0xFF00) >> 8);
        mData[1] = (byte) (value & 0x00FF);
    }

    /**
     * Instantiates a new Byte Array object. This constructor is private and cannot be called,
     * please use ByteArray.of(...) instead.
     *
     * @param value the value of the byte array as short
     */
    private ByteArray(short value) {
        this((char) value);
    }

    /**
     * Instantiates a new Byte Array object. This constructor is private and cannot be called,
     * please use ByteArray.of(...) instead.
     *
     * @param value the value of the byte array as byte
     */
    private ByteArray(byte value) {
        mData = new byte[]{value};
    }

    /**
     * Instantiates a new Byte Array object. This constructor is private and cannot be called,
     * please use ByteArray.of(...) instead.
     *
     * @param bytes  the source Java Byte array
     * @param length The number of bytes to be copied from the source Java byte array
     */
    private ByteArray(final byte[] bytes, int length) {
        mData = new byte[length];
        System.arraycopy(bytes, 0, mData, 0, length);
    }
}