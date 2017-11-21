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

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import flexjson.JSON;

public final class CardRiskManagementData {

    /**
     * The Additional Check Table
     * */
    @JSON(name = "additionalCheckTable")
    private ByteArray mAdditionalCheckTable;

    /**
     * Card Risk Management Data Country Code
     * */
    @JSON(name = "crmCountryCode")
    private ByteArray mCrmCountryCode;

    /**
     * Get the Additional Check Table
     *
     * @return The Additional Check Table as Byte Array
     *
     * */
    public ByteArray getAdditionalCheckTable() {
        return mAdditionalCheckTable;
    }

    /**
     * Set the Additional Check Table
     *
     * @param additionalCheckTable The Additional Check Table as Byte Array
     *
     * */
    public void setAdditionalCheckTable(ByteArray additionalCheckTable) {
        this.mAdditionalCheckTable = additionalCheckTable;
    }

    /**
     * Get the Card Risk Management Data Country Code
     *
     * @return The Card Risk Management Data Country Code as Byte Array
     *
     * */
    public ByteArray getCrmCountryCode() {
        return mCrmCountryCode;
    }

    /**
     * Set the Card Risk Management Data Country Code
     *
     * @param crmCountryCode Card Risk Management Data Country Code as Byte Array
     *
     * */
    public void setCrmCountryCode(ByteArray crmCountryCode) {
        this.mCrmCountryCode = crmCountryCode;
    }

    /**
     * Securely wipe the content of the business logic module profile information
     * */
    public void wipe() {
        Utils.clearByteArray(mAdditionalCheckTable);
        Utils.clearByteArray(mCrmCountryCode);
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
            return "CardRiskManagementData [additionalCheckTable=" + mAdditionalCheckTable
                   + ", crmCountryCode=" + mCrmCountryCode + "]";
        } else {
            return "CardRiskManagementData";
        }
    }
}
