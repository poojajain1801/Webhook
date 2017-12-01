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

package com.mastercard.mcbp.card.mpplite.apdu;

import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * The parent class of all Response APDUs.
 */
public class RespApdu {

    /**
     * The byte array value.
     */
    private ByteArray val;

    /**
     * Default Basic Constructor.
     */
    public RespApdu() {
    }

    /**
     * Instantiates a new R-APDU.
     *
     * @param resp the data of the response
     * @param sw   the Status Word
     */
    public RespApdu(ByteArray resp, ByteArray sw) {
        setValue(resp, sw);
    }


    /**
     * Build an object which is only formed by the status word
     * @param sw The status word
     */
    public RespApdu(ByteArray sw) {
        val = sw;
    }

    /**
     * Sets the value.
     *
     * @param resp the response
     * @param sw   the Status Word
     */
    public void setValue(ByteArray resp, ByteArray sw) {
        val = resp;
        val.append(sw);
    }

    /**
     * Set both value and '9000' response for the apdu
     * @param resp The value of the APDU
     */
    public void setValueAndSuccess(final ByteArray resp) {
        val = resp;
        ByteArray swSuccess = ByteArray.get(2);
        swSuccess.setByte(0, (byte) 0x90);
        swSuccess.setByte(1, (byte) 0x00);
        val.append(swSuccess);
    }

    /**
     * Gets all the bytes of the R-APDU.
     *
     * @return the bytes
     */
    public byte[] getBytes() {
        return val.getBytes();
    }

    /**
     * Get the content of the APDU as ByteArray
     */
    public ByteArray getByteArray() {
        return val;
    }

}
