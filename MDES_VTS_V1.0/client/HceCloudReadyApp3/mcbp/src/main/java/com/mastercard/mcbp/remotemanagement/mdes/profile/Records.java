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

package com.mastercard.mcbp.remotemanagement.mdes.profile;

import flexjson.JSON;

/**
 * Represents the records in card profile of mdes.
 */
public class Records {

    @JSON(name = "recordNumber")
    private int recordNumber;

    @JSON(name = "sfi")
    private String sfi;

    @JSON(name = "recordValue")
    private String recordValue;

    public int getRecordNumber() {
        return recordNumber;
    }

    public void setRecordNumber(final int recordNumber) {
        this.recordNumber = recordNumber;
    }

    public String getSfi() {
        return sfi;
    }

    public void setSfi(final String sfi) {
        this.sfi = sfi;
    }

    public String getRecordValue() {
        return recordValue;
    }

    public void setRecordValue(final String recordValue) {
        this.recordValue = recordValue;
    }
}