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

import flexjson.JSON;

public final class MppLiteModule {

    @JSON(name = "contactlessPaymentData")
    private ContactlessPaymentData contactlessPaymentData;

    @JSON (name = "remotePaymentData")
    private RemotePaymentData remotePaymentData;

    @JSON (name = "cardRiskManagementData")
    private CardRiskManagementData cardRiskManagementData;

    public MppLiteModule() {
    }

    public ContactlessPaymentData getContactlessPaymentData() {
        return contactlessPaymentData;
    }

    public void setContactlessPaymentData(ContactlessPaymentData contactlessPaymentData) {
        this.contactlessPaymentData = contactlessPaymentData;
    }

    public RemotePaymentData getRemotePaymentData() {
        return remotePaymentData;
    }

    public void setRemotePaymentData(RemotePaymentData remotePaymentData) {
        this.remotePaymentData = remotePaymentData;
    }

    public CardRiskManagementData getCardRiskManagementData() {
        return cardRiskManagementData;
    }

    public void setCardRiskManagementData(CardRiskManagementData cardRiskManagementData) {
        this.cardRiskManagementData = cardRiskManagementData;
    }

    public void wipe() {
        if (contactlessPaymentData != null) {
            contactlessPaymentData.wipe();
        }
        if (remotePaymentData != null) {
            remotePaymentData.wipe();
        }
        if (cardRiskManagementData != null) {
            cardRiskManagementData.wipe();
        }
    }

}