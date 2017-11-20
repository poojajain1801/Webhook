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

package com.mastercard.mcbp.lde.data.mobilecheck;

import com.mastercard.mcbp.utils.BuildInfo;

import java.io.Serializable;

import flexjson.JSON;

public class DigitizeCardProfileTransactionLog implements Serializable {

    private static final long serialVersionUID = -1207846901697379748L;
    @JSON(name = "dcID")
    private String digitizedCardId;

    @JSON(name = "unpredictableNumber")
    private String unpredictableNumber;

    @JSON(name = "atc")
    private String atc;

    @JSON(name = "cryptogramFormat")
    private byte cryptogramFormat;

    @JSON(name = "hostingMEJailbroken")
    private boolean hostingMEJailbroken;

    @JSON(name = "recentAttack")
    private boolean recentAttack;

    @JSON(name = "date")
    private String date;

    @JSON(name = "amount")
    private String amount;

    @JSON(name = "currencyCode")
    private String currencyCode;

    public String getDigitizedCardId() {
        return digitizedCardId;
    }

    public void setDigitizedCardId(String digitizedCardId) {
        this.digitizedCardId = digitizedCardId;
    }

    public String getUnpredictableNumber() {
        return unpredictableNumber;
    }

    public void setUnpredictableNumber(String unpredictableNumber) {
        this.unpredictableNumber = unpredictableNumber;
    }

    public String getAtc() {
        return atc;
    }

    public void setAtc(String atc) {
        this.atc = atc;
    }

    public byte getCryptogramFormat() {
        return cryptogramFormat;
    }

    public void setCryptogramFormat(byte cryptogramFormat) {
        this.cryptogramFormat = cryptogramFormat;
    }

    public boolean isHostingMEJailbroken() {
        return hostingMEJailbroken;
    }

    public void setHostingMEJailbroken(boolean hostingMEJailbroken) {
        this.hostingMEJailbroken = hostingMEJailbroken;
    }

    public boolean isRecentAttack() {
        return recentAttack;
    }

    public void setRecentAttack(boolean recentAttack) {
        this.recentAttack = recentAttack;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public void setCurrencyCode(String currencyCode) {
        this.currencyCode = currencyCode;
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
            return "DigitizeCardProfileTransactionLog [digitizedCardId=" + digitizedCardId + ", unpredictableNumber=" + unpredictableNumber
                   + ", atc=" + atc + ", cryptogramFormat=" + cryptogramFormat
                   + ", hostingMEJailbroken=" + hostingMEJailbroken
                   + ", recentAttack=" + recentAttack + ", date=" + date
                   + ", amount=" + amount + ", currencyCode=" + currencyCode
                   + "]";
        } else {
            return "DigitizeCardProfileTransactionLog";
        }
    }
}
