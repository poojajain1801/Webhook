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

package com.mastercard.mcbp.remotemanagement.mcbpV1;

import com.mastercard.mobile_api.bytes.ByteArray;
/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class CmsPayload {
    public static final int COUNTER_LENGTH = 3;
    public static final int ENCRYPTED_DATA_LENGTH = 8;
    /**
     * Cms To Mpa Counter
     */
    private ByteArray cmsToMpaCounter;
    /**
     * Encrypted Data
     */
    private ByteArray encryptedData;
    /**
     * MAC
     */
    private ByteArray mac;

    /**
     * Default constructor.
     */
    public CmsPayload(ByteArray cmsPayloadData) {

        if (cmsPayloadData == null) {
            throw new IllegalArgumentException();
        }

        cmsToMpaCounter = cmsPayloadData.copyOfRange(0, COUNTER_LENGTH);
        encryptedData = cmsPayloadData.copyOfRange(3, cmsPayloadData.getLength() -
                ENCRYPTED_DATA_LENGTH);
        mac = cmsPayloadData.copyOfRange(cmsPayloadData.getLength() - ENCRYPTED_DATA_LENGTH,
                cmsPayloadData.getLength());
    }

    public ByteArray getMac() {
        return mac;
    }

    public void setMac(ByteArray mac) {
        this.mac = mac;
    }

    public ByteArray getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(ByteArray encryptedData) {
        this.encryptedData = encryptedData;
    }

    public ByteArray getCmsToMpaCounter() {
        return cmsToMpaCounter;
    }

    public void setCmsToMpaCounter(ByteArray cmsToMpaCounter) {
        this.cmsToMpaCounter = cmsToMpaCounter;
    }

}
