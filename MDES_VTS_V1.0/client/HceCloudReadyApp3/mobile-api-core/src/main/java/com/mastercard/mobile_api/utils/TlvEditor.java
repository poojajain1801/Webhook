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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility class to perform simple modifications on TLV data. The library has been mainly design
 * to support editing of mono dimensional TLV data.
 * Nested TLVs can be handled by manually creating other instances of this class.
 */
public class TlvEditor {
    /**
     * Container class for a TLV Field (Tag and Value only)
     */
    public class Field {
        public final byte[] mTag;
        public final byte[] mValue;

        public Field(final byte[] tag, final byte[] value) {
            mTag = tag;
            mValue = value;
        }
    }
    /**
     * The TLV is internally mapped as Tag, Value.
     *
     * The length field is not included as it will be rebuilt, when needed
     */
    private List<Field>mTlv = new ArrayList<>();

    /**
     * Factory method to build a TLV Editor object based on the input data as byte[]
     * @param data The input data which is expected to be a valid TLV BER
     * @return The TlvEditor object representing the input data
     */
    public static TlvEditor of(final byte[] data) {
        try {
            return new TlvEditor(data);
        } catch (final IllegalArgumentException | ArrayIndexOutOfBoundsException e) {
            e.printStackTrace();
            // If something goes wrong we return a null object
            return null;
        }
    }

    /**
     * Get the value for a given tag, if present. Null if not tag can be found
     * @param tag The Tag for which the value should be returned as byte[]
     * @return The value of that tag, if found. Null otherwise.
     */
    public byte[] getValue(final byte[] tag) {
        final Field field = find(tag);
        if (field != null) {
            return field.mValue;
        }
        return null;
    }

    /**
     * Get the value for a given tag, if present. Null if not tag can be found
     * @param tag The Tag for which the value should be returned as String
     * @return The value of that tag, if found. Null otherwise.
     */
    public byte[] getValue(final String tag) {
        return tag == null ? null: getValue(ByteArray.of(tag).getBytes());
    }

    /**
     * Function to add a TLV. If the same tag is already present, the tag is updated
     * @param tag The Tag to be added
     * @param value The value to be added
     */
    public void addTlv(final byte[] tag, final byte[] value) {
        final int index = indexOf(tag);
        if (index == -1) {
            mTlv.add(new Field(tag, value));
        } else {
            mTlv.set(index, new Field(tag, value));
        }
    }

    /**
     * Rebuild each TLV entry and rebuild the entire TLV
     */
    public byte[] rebuildByteArray() {
        final List<byte[]> values = new ArrayList<>();

        for (Field item: mTlv) {
            values.add(Tlv.create(item.mTag, item.mValue));
        }

        int length = 0;
        for (byte[] item: values) {
            length += item.length;
        }

        final byte[] result = new byte[length];

        int current = 0;
        for (byte[] item: values) {
            System.arraycopy(item, 0, result, current, item.length);
            current += item.length;
        }
        return result;
    }

    /**
     * Private constructor used by the factory method
     */
    private TlvEditor(final byte[] data) {
        int nextTagOffset = 0;
        while (nextTagOffset < data.length) {
            final byte[] nextTag = readNextTag(data, nextTagOffset);
            final int lengthOffset = nextTagOffset + nextTag.length;
            final int noBytesInLength = getNumberOfBytesInLength(data, lengthOffset);
            final int nextDataLength = getDataLength(data, lengthOffset);

            final byte[] nextData = new byte[nextDataLength];
            System.arraycopy(data, lengthOffset + noBytesInLength, nextData, 0, nextDataLength);

            mTlv.add(new Field(nextTag, nextData));

            nextTagOffset += nextTag.length + noBytesInLength + nextDataLength;
        }
    }

    /**
     * Utility function to find if a given tag is present. If present the entire Field is returned
     */
    private Field find(final byte[] tag) {
        for (Field item: mTlv) {
            if (Arrays.equals(tag, item.mTag)) {
                return item;
            }
        }
        return null;
    }

    /**
     * Utility function to determine the actual index of a Field with a given tag in the array
     */
    private int indexOf(final byte[] tag) {
        for (int i = 0; i < mTlv.size(); i++) {
            final Field item = mTlv.get(i);
            if (Arrays.equals(item.mTag, tag)) {
                return i;
            }
        }
        return -1;  // Not found
    }

    /**
     * Read The tag of a given byte array formatted as BER TLV.
     * It assumes the TAG is a the beginning of the data
     * @param data The TLV in EMV BER Encoded format
     * @param offset the offset of the next tag
     * @return The Tag of the TLV
     * @throws IllegalArgumentException if the TLV is malformed
     */
    private static byte[] readNextTag(final byte[] data, final int offset)
            throws IllegalArgumentException {
        if (data == null || offset < 0 || offset >= data.length) {
            throw new IllegalArgumentException("Invalid TLV: " + ByteArray.of(data).toHexString());
        }
        final int tagLength = getTagLength(data, offset);
        final byte[] tag = new byte[tagLength];
        System.arraycopy(data, offset, tag, 0, tagLength);
        return tag;
    }

    /**
     * Calculate the length of the tag. The tag is assumed to start at 'offset' position within the
     * data
     * @param data The byte array to be parsed
     * @param offset The offset at which the tag is located
     * @return The length of the tag
     * @throws IllegalArgumentException If the data is malformed
     */
    private static int getTagLength(final byte[] data, final int offset) {
        if (data == null || offset < 0 || offset >= data.length) {
            throw new IllegalArgumentException("Invalid offset or data");
        }
        int tagLength = 1;
        if ((data[offset] & 0x1F) == 0x1F) {  // we need to look at the next byte
            for (int i = 1; i < data.length; i++) {
                tagLength++;
                if ((data[offset + i] & 0x80) != 0x80) {
                    break;
                }
            }
        }
        return tagLength;
    }

    /**
     * Utility function to get the number of bytes in a given length
     * The offset of the length field must be specified
     */
    private static int getNumberOfBytesInLength(final byte[] data, final int lengthOffset) {
        if (data == null || lengthOffset < 0 || lengthOffset >= data.length) {
            throw new IllegalArgumentException("Invalid offset or data");
        }
        final byte firstLengthByte = data[lengthOffset];
        return 1 + ( (firstLengthByte & 0x80) == 0x80 ? (firstLengthByte & 0x7F): 0);
    }

    /**
     * Get the length of the value by reading the length bytes.
     * The offset of the length field must be specified
     */
    private static int getDataLength(final byte[] data, final int lengthOffset) {
        if (data == null || lengthOffset < 0 || lengthOffset >= data.length) {
            throw new IllegalArgumentException("Invalid offset or data");
        }
        final int noBytesInLength = getNumberOfBytesInLength(data, lengthOffset);
        if (data.length < lengthOffset + noBytesInLength) {
            throw new IllegalArgumentException("Invalid length");
        }
        if (noBytesInLength == 1) {
            return data[lengthOffset];
        }
        int length = 0;
        for (int i = 1; i < noBytesInLength; i++) {  // We start from the second byte of length
            final int shift = 8 * (noBytesInLength - i - 1);
            length += ((data[i + lengthOffset] & 0x00FF) << shift);
        }
        return length;
    }
}
