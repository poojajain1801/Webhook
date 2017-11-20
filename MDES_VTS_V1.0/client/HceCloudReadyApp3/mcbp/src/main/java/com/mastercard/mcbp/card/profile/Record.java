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

package com.mastercard.mcbp.card.profile;

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import flexjson.JSON;

/**
 * Data class to store the Record data structure
 * A record is formed by a record number, an SFI, and a record value
 */
public class Record {

    /**
     * The Record Number
     */
    @JSON(name = "recordNumber")
    private byte mRecordNumber;

    /**
     * The Short File Identifier (SFI)
     */
    @JSON(name = "sfi")
    private byte mSfi;

    /**
     * The Record Value
     */
    @JSON(name = "recordValue")
    private ByteArray mRecordValue;

    /**
     * Get the Record Number for this record
     *
     * @return the Record Number of this record as byte
     */
    public byte getRecordNumber() {
        return mRecordNumber;
    }

    /**
     * Set the Record Number for this record
     * <p/>
     * This function requires the SFI to be provided as integer value (< 10).
     * Generally, an SFI record is encoded as the 5 msb of a byte, where the least significant
     * ones are 100. Thus, a value "0C" indicates SFI = 1.
     * This function expects you to provide the value 1 (and not 0C)
     *
     * @param recordNumber The SFI value as Byte
     */
    public void setRecordNumber(byte recordNumber) {
        this.mRecordNumber = recordNumber;
    }

    /**
     * Get the SFI value for this record
     *
     * @return the SFI value of this record as byte
     */
    public byte getSfi() {
        return mSfi;
    }

    /**
     * Set the SFI value for this record
     *
     * @param sfi The SFI value as Byte
     */
    public void setSfi(byte sfi) {
        this.mSfi = sfi;
    }

    /**
     * Get the value of the record
     *
     * @return The value of the Record as Byte Array
     */
    public ByteArray getRecordValue() {
        return mRecordValue;
    }

    /**
     * Set the Record Value
     * The method is used by FlexJson
     *
     * @param recordValue The Record Value as Java String
     */
    public void setRecordValue(ByteArray recordValue) {
        this.mRecordValue = recordValue;
    }

    /**
     * Securely erase the content of the data structure
     */
    public void wipe() {
        mRecordNumber = 0x00;
        mSfi = 0x00;
        Utils.clearByteArray(mRecordValue);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return Returns debug information for the class in debug mode.
     * In release mode it returns only the class name, so that sensitive information is never
     * returned by this method.
     */
    @Override
    public String toString() {
        if (BuildInfo.isDebugEnabled()) {
            return "Record{" +
                   "recordNumber=" + mRecordNumber +
                   ", sfi=" + mSfi +
                   ", recordValue=" + mRecordValue.toHexString() +
                   '}';
        } else {
            return "Record";
        }
    }

    public static Record valueOf(byte[] content) {
        return new JsonUtils<Record>(Record.class).valueOf(content);
    }

    public String toJsonString() {
        return new JsonUtils<Record>(Record.class).toJsonString(this);
    }
}