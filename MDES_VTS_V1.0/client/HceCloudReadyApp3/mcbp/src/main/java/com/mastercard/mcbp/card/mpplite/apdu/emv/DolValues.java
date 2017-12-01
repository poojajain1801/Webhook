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

import java.util.HashMap;
import java.util.Map;

/**
 * Manage the values of a DOL blob of data that was received from the Reader in a C-APDU
 * The list of requested DOLs must be provided to allow decoding of the received data
 */
public class DolValues {
    /**
     * Each DOL item is mapped as
     */
    private final Map<String, byte[]> mDolValues = new HashMap<>();

    /**
     * Static factory method to build a data structure representing the values of the DOL object
     * @param dolList The list of DOL items that were requested to the reader
     * @param dolValues The DOL values as received from the reader (byte[])
     * @return The object to manage the DOL values
     */
    public static DolValues of(final DolRequestList dolList, final byte[] dolValues) {
        return new DolValues(dolList, dolValues);
    }

    /**
     * Get the value from a given tag. If the value cannot be found, but it is in the PDOL list we
     * return an empty byte array. Otherwise a zero value array
     * @param tag The TAG for which the value has to be retrieved
     * @return The tag value if found, otherwise an empty tag.
     */
    public byte[] getValueByTag(final String tag) {
        return mDolValues.get(tag);
    }

    /**
     * Private constructor used to build the PDOL from the byte[] array value
     */
    private DolValues(final DolRequestList dolList, final byte[] values) {
        if (dolList != null && values != null) {
            // We have also got values, let's see if we can find them...
            int currentPosition = 0;
            for (DolRequestList.DolItem dolItem : dolList.getRequestList()) {
                if (currentPosition + dolItem.getLength() > values.length) {
                    // We have reached the end... something was wrong. Let's just use what we have
                    // read so far
                    break;
                }
                final byte[] value = new byte[dolItem.getLength()];
                System.arraycopy(values, currentPosition, value, 0, dolItem.getLength());
                currentPosition += dolItem.getLength();
                mDolValues.put(dolItem.getTag(), value);
            }
        }
    }

    /**
     * For debug purposes mainly
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        // Print all the items in the set
        for (Map.Entry<String, byte[]> entry: mDolValues.entrySet()) {
            stringBuilder.append("[");
            stringBuilder.append(entry.getKey());
            stringBuilder.append("|");
            stringBuilder.append(ByteArray.of(entry.getValue()).toHexString());
            stringBuilder.append("] ");
        }
        return stringBuilder.toString();
    }
}
