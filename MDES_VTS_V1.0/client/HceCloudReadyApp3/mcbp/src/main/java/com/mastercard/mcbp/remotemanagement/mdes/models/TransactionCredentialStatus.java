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

package com.mastercard.mcbp.remotemanagement.mdes.models;

import flexjson.JSON;

/**
 * Encapsulation for required transaction credentials status.
 */
public class TransactionCredentialStatus {

    /**
     * The Application Transaction Counter (ATC) identifying this Transaction Credential.
     */
    @JSON(name = "atc")
    private int atc;
    /**
     * The status of this Transaction Credential.<br>
     * Value                        Meaning<br>
     * UNUSED_ACTIVE                This Transaction Credential has not yet been used and remains active.<br>
     * UNUSED_DISCARDED             This Transaction Credential was not used but has been discarded.<br>
     * USED_FOR_CONTACTLESS         This Transaction Credential has been used for a contactless transaction.<br>
     * USED_FOR_DSRP                This Transaction Credential has been used for a digital secure remote payment transaction.<br>
     */
    @JSON(name = "status")
    private String status;
    /**
     * The date/time stamp for this status.<br>
     * Format:<br>
     * YYYY-MM-DDThh:mm:ss[.sss]Z
     * YYYY-MM-DDThh:mm:ss[.sss]±hh:mm
     * Where [.sss] is optional and can be 1 to 3 digits
     */

    @JSON(name = "timestamp")
    private String timestamp;

    /**
     * Parameterize constructor
     *
     * @param atc       ATC.
     * @param status    Status of TransactionCredentialStatus.
     * @param timestamp Timestamp.
     */
    public TransactionCredentialStatus(int atc, String status, String timestamp) {
        this.atc = atc;
        this.status = status;
        this.timestamp = timestamp;
    }

    public TransactionCredentialStatus() {

    }

    public int getAtc() {
        return atc;
    }

    public void setAtc(int atc) {
        this.atc = atc;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }


    public enum Status {
        UNUSED_DISCARDED(),

        USED_FOR_CONTACTLESS(),

        USED_FOR_DSRP(),

        UNUSED_ACTIVE()

    }

}
