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

import com.mastercard.mobile_api.bytes.ByteArray;

import flexjson.JSON;

public final class CvmIssuerOptions {

    @JSON(name = "ackAlwaysRequiredIfCurrencyNotProvided")
    private boolean ackAlwaysRequiredIfCurrencyNotProvided;

    @JSON(name = "ackAlwaysRequiredIfCurrencyProvided")
    private boolean ackAlwaysRequiredIfCurrencyProvided;

    @JSON(name = "ackAutomaticallyResetByApplication")
    private boolean ackAutomaticallyResetByApplication;

    @JSON(name = "ackPreEntryAllowed")
    private boolean ackPreEntryAllowed;

    @JSON(name = "pinAlwaysRequiredIfCurrencyNotProvided")
    private boolean pinAlwaysRequiredIfCurrencyNotProvided;

    @JSON(name = "pinAlwaysRequiredIfCurrencyProvided")
    private boolean pinAlwaysRequiredIfCurrencyProvided;

    @JSON(name = "pinAutomaticallyResetByApplication")
    private boolean pinAutomaticallyResetByApplication;

    @JSON(name = "pinPreEntryAllowed")
    private boolean pinPreEntryAllowed;

    public CvmIssuerOptions() {
    }

    /**
     * Set the given bit for the byte value.
     *
     * @param byteValue byte value whose bit is to set
     * @param bitToSet  bit position to set
     * @return byte with the given bit set
     */
    public static byte setBit(byte byteValue, int bitToSet) {

        byteValue = (byte) (byteValue | (1 << bitToSet));

        return byteValue;
    }

    public boolean getAckAlwaysRequiredIfCurrencyProvided() {
        return ackAlwaysRequiredIfCurrencyProvided;
    }

    public void setAckAlwaysRequiredIfCurrencyProvided(boolean ackAlwaysRequiredIfCurrencyProvided) {
        this.ackAlwaysRequiredIfCurrencyProvided = ackAlwaysRequiredIfCurrencyProvided;
    }

    public boolean getPinPreEntryAllowed() {
        return pinPreEntryAllowed;
    }

    public void setPinPreEntryAllowed(boolean pinPreEntryAllowed) {
        this.pinPreEntryAllowed = pinPreEntryAllowed;
    }

    public boolean getPinAlwaysRequiredIfCurrencyNotProvided() {
        return pinAlwaysRequiredIfCurrencyNotProvided;
    }

    public void setPinAlwaysRequiredIfCurrencyNotProvided(
            boolean pinAlwaysRequiredIfCurrencyNotProvided) {
        this.pinAlwaysRequiredIfCurrencyNotProvided = pinAlwaysRequiredIfCurrencyNotProvided;
    }

    public boolean getAckAlwaysRequiredIfCurrencyNotProvided() {
        return ackAlwaysRequiredIfCurrencyNotProvided;
    }

    public void setAckAlwaysRequiredIfCurrencyNotProvided(
            boolean ackAlwaysRequiredIfCurrencyNotProvided) {
        this.ackAlwaysRequiredIfCurrencyNotProvided = ackAlwaysRequiredIfCurrencyNotProvided;
    }

    public boolean getAckPreEntryAllowed() {
        return ackPreEntryAllowed;
    }

    public void setAckPreEntryAllowed(boolean ackPreEntryAllowed) {
        this.ackPreEntryAllowed = ackPreEntryAllowed;
    }

    public boolean getAckAutomaticallyResetByApplication() {
        return ackAutomaticallyResetByApplication;
    }

    public void setAckAutomaticallyResetByApplication(boolean ackAutomaticallyResetByApplication) {
        this.ackAutomaticallyResetByApplication = ackAutomaticallyResetByApplication;
    }

    public boolean getPinAutomaticallyResetByApplication() {
        return pinAutomaticallyResetByApplication;
    }

    public void setPinAutomaticallyResetByApplication(boolean pinAutomaticallyResetByApplication) {
        this.pinAutomaticallyResetByApplication = pinAutomaticallyResetByApplication;
    }

    public boolean getPinAlwaysRequiredIfCurrencyProvided() {
        return pinAlwaysRequiredIfCurrencyProvided;
    }

    public void setPinAlwaysRequiredIfCurrencyProvided(boolean pinAlwaysRequiredIfCurrencyProvided) {
        this.pinAlwaysRequiredIfCurrencyProvided = pinAlwaysRequiredIfCurrencyProvided;
    }

    @JSON(include = false)
    public ByteArray getMpaObject() {
        byte value = 0;
        if (getAckAlwaysRequiredIfCurrencyProvided()) {
            value = setBit(value, 7);
        }
        if (getAckAlwaysRequiredIfCurrencyNotProvided()) {
            value = setBit(value, 6);
        }
        if (getPinAlwaysRequiredIfCurrencyProvided()) {
            value = setBit(value, 4);
        }
        if (getPinAlwaysRequiredIfCurrencyNotProvided()) {
            value = setBit(value, 3);
        }

        return ByteArray.of(new byte[]{value});
    }

}