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

package com.mastercard.mcbp.remotemanagement.file.profile;

import flexjson.JSON;

class MppLiteModuleMdesCmsC {

    public CardRiskManagementDataMdesCmsC getCardRiskManagementData() {
        return cardRiskManagementData;
    }

    public void setCardRiskManagementData(CardRiskManagementDataMdesCmsC cardRiskManagementData) {
        this.cardRiskManagementData = cardRiskManagementData;
    }

    public ContactlessPaymentDataMdesCmsC getContactlessPaymentData() {
        return contactlessPaymentData;
    }

    public void setContactlessPaymentData(ContactlessPaymentDataMdesCmsC contactlessPaymentData) {
        this.contactlessPaymentData = contactlessPaymentData;
    }

    public RemotePaymentDataMdesCmsC getRemotePaymentData() {
        return remotePaymentData;
    }

    public void setRemotePaymentData(RemotePaymentDataMdesCmsC remotePaymentData) {
        this.remotePaymentData = remotePaymentData;
    }

    @JSON(name = "cardRiskManagementData")
    private CardRiskManagementDataMdesCmsC cardRiskManagementData;

    @JSON(name = "contactlessPaymentData")
    private ContactlessPaymentDataMdesCmsC contactlessPaymentData;

    @JSON(name = "remotePaymentData")
    private RemotePaymentDataMdesCmsC remotePaymentData;
}
