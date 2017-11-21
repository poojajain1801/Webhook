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
 * Represents the magstripe cvm issuer options in card profile of mdes.
 */
public class MagstripeCvmIssuerOptions {

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

    public boolean isAckAlwaysRequiredIfCurrencyNotProvided() {
        return ackAlwaysRequiredIfCurrencyNotProvided;
    }

    public void setAckAlwaysRequiredIfCurrencyNotProvided(
            final boolean ackAlwaysRequiredIfCurrencyNotProvided) {
        this.ackAlwaysRequiredIfCurrencyNotProvided = ackAlwaysRequiredIfCurrencyNotProvided;
    }

    public boolean isAckAlwaysRequiredIfCurrencyProvided() {
        return ackAlwaysRequiredIfCurrencyProvided;
    }

    public void setAckAlwaysRequiredIfCurrencyProvided(
            final boolean ackAlwaysRequiredIfCurrencyProvided) {
        this.ackAlwaysRequiredIfCurrencyProvided = ackAlwaysRequiredIfCurrencyProvided;
    }

    public boolean isAckAutomaticallyResetByApplication() {
        return ackAutomaticallyResetByApplication;
    }

    public void setAckAutomaticallyResetByApplication(
            final boolean ackAutomaticallyResetByApplication) {
        this.ackAutomaticallyResetByApplication = ackAutomaticallyResetByApplication;
    }

    public boolean isAckPreEntryAllowed() {
        return ackPreEntryAllowed;
    }

    public void setAckPreEntryAllowed(final boolean ackPreEntryAllowed) {
        this.ackPreEntryAllowed = ackPreEntryAllowed;
    }

    public boolean isPinAlwaysRequiredIfCurrencyNotProvided() {
        return pinAlwaysRequiredIfCurrencyNotProvided;
    }

    public void setPinAlwaysRequiredIfCurrencyNotProvided(
            final boolean pinAlwaysRequiredIfCurrencyNotProvided) {
        this.pinAlwaysRequiredIfCurrencyNotProvided = pinAlwaysRequiredIfCurrencyNotProvided;
    }

    public boolean isPinAlwaysRequiredIfCurrencyProvided() {
        return pinAlwaysRequiredIfCurrencyProvided;
    }

    public void setPinAlwaysRequiredIfCurrencyProvided(
            final boolean pinAlwaysRequiredIfCurrencyProvided) {
        this.pinAlwaysRequiredIfCurrencyProvided = pinAlwaysRequiredIfCurrencyProvided;
    }

    public boolean isPinAutomaticallyResetByApplication() {
        return pinAutomaticallyResetByApplication;
    }

    public void setPinAutomaticallyResetByApplication(
            final boolean pinAutomaticallyResetByApplication) {
        this.pinAutomaticallyResetByApplication = pinAutomaticallyResetByApplication;
    }

    public boolean isPinPreEntryAllowed() {
        return pinPreEntryAllowed;
    }

    public void setPinPreEntryAllowed(final boolean pinPreEntryAllowed) {
        this.pinPreEntryAllowed = pinPreEntryAllowed;
    }
}