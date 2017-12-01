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

package com.mastercard.mcbp.card.mpplite.apdu.emv;

import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * Manage a DOL request list
 *
 * Each item of the DOL request list is a pair TAG|LENGTH.
 * The MPP Lite requests a specific list of DOLs to the Terminal by adding a specific TLV with
 * the DOL list as value. Examples are the PDOL list that is requested in the SELECT R-APDU,
 * the UDOL list that is included in the Read Record R-APDU for Record 1 SFI 1, and the
 * CDOL list that is included in the Read Record R-APDU for Record 1 SFI 2.
 *
 */
public class DolRequestList {
    /**
     * Structure to represent a single DOL item.
     * A DOL item is formed by its tag and its length
     */
    public static class DolItem {
        private final String mTag;
        private final int mLength;

        /**
         * Build a DOL Item with a given tag and length
         * @param tag The DOL Tag
         * @param length The DOL length
         */
        public DolItem(final String tag, final int length) {
            this.mTag = tag;
            this.mLength = length;
        }

        /**
         * @return The tag value
         */
        public String getTag() {
            return mTag;
        }

        /**
         * @return The tag length as integer
         */
        public int getLength() {
            return mLength;
        }
    }

    /**
     * The List of PDOL values.
     * A container that guarantee ordering is needed to ensure we do not change the order of UDOL
     *
     * items while adding / removing items.
     */
    private final List<DolItem> mDolItems = new ArrayList<>();

    /**
     * Build a DOL Management object that contains only a list of DOLs for a request to the terminal
     * @param dolList The DOL list in the form of Tag|Length values
     * @return A DOL management object that can be used to manipulate the list
     */
    public static DolRequestList of(final byte[] dolList) {
        return new DolRequestList(dolList);
    }

    /**
     * Get the list of DOLs that have been requested as Java List
     * @return The list of DOLs
     */
    public final List<DolItem> getRequestList() {
        return mDolItems;
    }

    public boolean addTag(final String tag, final int length) {
        if (isPdolValueAlreadyPresent(tag)) {
            return false;
        }
        mDolItems.add(new DolItem(tag, length));
        return true;
    }


    /**
     * @return The expected length of the DOL data blob (in bytes)
     */
    public int getExpectedDolLength() {
        int result = 0;
        for (DolItem listItem: mDolItems) {
            result += listItem.mLength;
        }
        return result;
    }

    /**
     * Constructor is not available, please use the static factory method instead.
     * @param dolList The DOL list as byte[]
     */
    private DolRequestList(final byte[] dolList) {
        if (dolList == null) {
            // It is an empty, we cannot add anything
            return;
        }
        int nextTagOffset = 0;
        while (nextTagOffset < dolList.length) {
            final byte[] nextTag = readNextTag(dolList, nextTagOffset);
            final String tag = ByteArray.of(nextTag).toHexString();
            final int lengthOffset = nextTagOffset + nextTag.length;
            final int noBytesInLength = getNumberOfBytesInLength(dolList, lengthOffset);
            final int nextDataLength = getDataLength(dolList, lengthOffset);

            nextTagOffset += nextTag.length + noBytesInLength;

            mDolItems.add(new DolItem(tag, nextDataLength));
        }
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

    /**
     * Get the PDOL List as byte array [tag, length]
     * @return The PDOL List as byte[]
     */
    public byte[] getBytes() {
        // Get the PDOL as a byte[]
        int totalLength = 0;
        for (DolItem item: mDolItems) {
            // In this case the maximum PDOL value length is never greater than 255
            totalLength += (item.mTag.length() / 2) + (item.mLength <= 0x7F ? 1: 2);
        }
        final byte[] pdol = new byte[totalLength];
        int currentPosition = 0;
        for (DolItem item: mDolItems) {
            final byte[] nextValue = ByteArray.of(item.mTag).getBytes();
            System.arraycopy(nextValue, 0, pdol, currentPosition, nextValue.length);
            currentPosition += nextValue.length;

            final int lengthLength = item.mLength <= 0x7F ? 1: 2;
            if (lengthLength == 1) {
                pdol[currentPosition] = (byte)item.mLength;
            } else {
                pdol[currentPosition] = (byte)0x81;
                currentPosition++;
                pdol[currentPosition] = (byte)item.mLength;
            }
            currentPosition++;
        }
        return pdol;
    }

    /**
     * Check whether the PDOL entry (TAG) is already present in the list.
     *
     * @param tag The TAG to search for
     * @return True if the value is already in the list, false otherwise
     */
    private boolean isPdolValueAlreadyPresent(final String tag) {
        for (DolItem dolItem : mDolItems) {
            if (dolItem.mTag.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
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
     * For debug purposes mainly
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        for (DolItem dolItem : mDolItems) {
            stringBuilder.append(dolItem.mTag);
            stringBuilder.append(", ");
            stringBuilder.append(dolItem.mLength);
            stringBuilder.append("\n");
        }
        return stringBuilder.toString();
    }
}
