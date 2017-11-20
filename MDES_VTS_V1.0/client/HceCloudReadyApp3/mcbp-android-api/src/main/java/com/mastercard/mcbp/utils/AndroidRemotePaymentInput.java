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

package com.mastercard.mcbp.utils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Provides parsing from JSON into a Remote Payment Input object
 */
public class AndroidRemotePaymentInput extends RemotePaymentInput {

    /**
     * Initialise a Remote Payment Object with a JSON string
     *
     * @param json the JSON to use
     * @throws JSONException
     */
    public AndroidRemotePaymentInput(String json) throws JSONException {
        JSONObject obj = new JSONObject(json);

        if (obj.has(KEY_AMOUNT)) {
            this.setAmount(obj.getInt(KEY_AMOUNT));
        }
        if (obj.has(KEY_MERCHANT_LOGO)) {
            this.setMerchantLogo(obj.getString(KEY_MERCHANT_LOGO));
        }
        if (obj.has(KEY_CURRENCY)) {
            this.setCurrency(obj.getInt(KEY_CURRENCY));
        }
        if (obj.has(KEY_MERCHANT)) {
            this.setMerchant(obj.getString(KEY_MERCHANT));
        }
        if (obj.has(KEY_CALLBACK)) {
            this.setCallback(obj.getString(KEY_CALLBACK));
        }
        if (obj.has(KEY_IN_APP)) {
            this.setInApp(obj.getBoolean(KEY_IN_APP));
        }
        if (obj.has(KEY_COUNTRY_CODE)) {
            this.setCountryCode(obj.getInt(KEY_COUNTRY_CODE));
        }
        if (obj.has(KEY_CRYPTOGRAM_TYPE)) {
            this.setCryptogramType(obj.getString(KEY_CRYPTOGRAM_TYPE));
        }
        if (obj.has(KEY_OTHER_AMOUNT)) {
            this.setOtherAmount(obj.getInt(KEY_OTHER_AMOUNT));
        }
        if (obj.has(KEY_TRANSACTION_TYPE)) {
            this.setTransactionType(obj.getInt(KEY_TRANSACTION_TYPE));
        }
        if (obj.has(KEY_UNPREDICTABLE_NUMBER)) {
            this.setUnpredictableNumber(obj.getInt(KEY_UNPREDICTABLE_NUMBER));
        }
        if (obj.has(KEY_TRANSACTION_DAY)) {
            this.setTransactionDay(obj.getInt(KEY_TRANSACTION_DAY));
        }
        if (obj.has(KEY_TRANSACTION_MONTH)) {
            this.setTransactionMonth(obj.getInt(KEY_TRANSACTION_MONTH));
        }
        if (obj.has(KEY_TRANSACTION_YEAR)) {
            this.setTransactionYear(obj.getInt(KEY_TRANSACTION_YEAR));
        }
    }
}
