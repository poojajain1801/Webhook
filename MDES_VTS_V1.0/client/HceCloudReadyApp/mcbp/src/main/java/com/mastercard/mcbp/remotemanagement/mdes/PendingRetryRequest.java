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

package com.mastercard.mcbp.remotemanagement.mdes;

import flexjson.JSON;

/**
 * Encapsulate attribute required for storing pending request
 */
public class PendingRetryRequest {
    /**
     * Type of request
     */
    @JSON(name = "requestType")
    private CmsDRequestEnum mCmsDRequestEnum;
    /**
     * Card identifier
     */
    @JSON(name = "cardId")
    private String cardId;
    /**
     * Meta data for request
     */
    @JSON(name = "metaData")
    private String metaData;
    /**
     * Number of retry remaining
     */
    @JSON(name = "retryCount")
    private int retryCount;

    public PendingRetryRequest() {

    }

    public PendingRetryRequest(CmsDRequestEnum requestEnum, String cardId, String metaData, int retryCount) {
        setRequestType(requestEnum);
        setCardId(cardId);
        setMetaData(metaData);
        setRetryCount(retryCount);
    }

    private void setRequestType(CmsDRequestEnum requestEnum) {
        this.mCmsDRequestEnum = requestEnum;
    }

    private void setCardId(String id) {
        this.cardId = id;
    }

    public CmsDRequestEnum getRequestType() {
        return mCmsDRequestEnum;
    }

    public String getCardId() {
        return cardId;
    }

    public String getMetaData() {
        return metaData;
    }

    private void setMetaData(String metaData) {
        this.metaData = metaData;
    }

    public int getRetryCount() {
        return retryCount;
    }

    public void setRetryCount(int retryCount) {
        this.retryCount = retryCount;
    }
}
