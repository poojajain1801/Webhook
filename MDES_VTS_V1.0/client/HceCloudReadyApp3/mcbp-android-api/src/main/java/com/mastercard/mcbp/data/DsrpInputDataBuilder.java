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

package com.mastercard.mcbp.data;

import android.annotation.SuppressLint;

import com.mastercard.mcbp.card.mobilekernel.CryptogramType;
import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mobile_api.utils.Date;

import java.security.SecureRandom;

/**
 * Helper class for building the data required for processing a DSRP payment.
 */
public class DsrpInputDataBuilder {
    /**
     * The input data that is build using this class.
     */
    private final DsrpInputData mInputData;

    /**
     * Flag indicating whether the cryptogram type has been set manually.
     */
    private boolean mIsCryptogramTypeSet = false;

    /**
     * Constructor.
     */
    public DsrpInputDataBuilder() {
        mInputData = new DsrpInputData();
    }

    /**
     * Set the amount to be paid.
     *
     * @param amount The amount to be paid, normally retrieved from {@link android.content.Intent}
     *               data.
     * @return Instance of {@link DsrpInputDataBuilder} that can be chained
     * to create the necessary data for a DSRP payment.
     */
    public DsrpInputDataBuilder forAmount(long amount) {
        mInputData.setTransactionAmount(amount);
        return this;
    }

    /**
     * Set the currency code to use for the payment.
     *
     * @param currencyCode The currency code to use for the payment, normally retrieved from
     *                     {@link android.content.Intent} data.
     * @return Instance of {@link DsrpInputDataBuilder} that can be chained
     * to create the necessary data for a DSRP payment.
     */
    public DsrpInputDataBuilder usingCurrencyCode(int currencyCode) {
        return usingCurrencyCode((char) currencyCode);
    }

    /**
     * Set the currency code to use for the payment.
     *
     * @param currencyCode The currency code to use for the payment, normally retrieved from
     *                     {@link android.content.Intent} data.
     * @return Instance of {@link DsrpInputDataBuilder} that can be chained
     * to create the necessary data for a DSRP payment.
     */
    @SuppressWarnings("WeakerAccess")
    public DsrpInputDataBuilder usingCurrencyCode(char currencyCode) {
        mInputData.setCurrencyCode(currencyCode);
        return this;
    }

    /**
     * Set the type of cryptogram to be generated.
     *
     * @param type One of CryptogramType.UCAF or CryptogramType.DE55.
     * @return Instance of {@link DsrpInputDataBuilder} that can be chained
     * to create the necessary data for a DSRP payment.
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess", "UnusedReturnValue"})
    public DsrpInputDataBuilder usingCryptogramType(CryptogramType type) {
        mInputData.setCryptogramType(type);
        mIsCryptogramTypeSet = true;
        return this;
    }

    /**
     * Set the unpredictable number to use.
     *
     * @param unpredictableNumber The unpredictable number.
     * @return Instance of {@link DsrpInputDataBuilder} that can be chained
     * to create the necessary data for a DSRP payment.
     */
    public DsrpInputDataBuilder withUnpredictableNumber(long unpredictableNumber) {
        if (unpredictableNumber > 0) {
            mInputData.setUnpredictableNumber(unpredictableNumber);
        }
        return this;
    }

    /**
     * Build the mandatory input for a DSRP payment.
     *
     * @return Instance of {@link DsrpInputData} that can be
     * used for a DSRP payment.
     */
    @SuppressLint("")
    public DsrpInputData build() {
        // Set an unpredictable number if we don't have one
        if (mInputData.getUnpredictableNumber() == 0) {
            mInputData.setUnpredictableNumber(new SecureRandom().nextInt());
        }

        // Set the default cryptogram type is it's not being set
        if (!mIsCryptogramTypeSet) {
            usingCryptogramType(CryptogramType.UCAF);
        }
        return mInputData;
    }

    /**
     * Set the transaction Date to use
     *
     * @param date Transaction {@link Date}
     */
    public DsrpInputDataBuilder usingTransactionDate(Date date) {
        mInputData.setTransactionDate(date);
        return this;
    }

    /**
     * Set the transaction type to use
     *
     * @param transactionType the transaction type
     */
    public DsrpInputDataBuilder usingTransactionType(byte transactionType) {
        mInputData.setTransactionType(transactionType);
        return this;
    }

    /**
     * The other amount to use
     *
     * @param otherAmount the other amount
     */
    public DsrpInputDataBuilder withOtherAmount(long otherAmount) {
        if (otherAmount > 0) {
            mInputData.setOtherAmount(otherAmount);
        }
        return this;
    }

    /**
     * The country code to use
     *
     * @param countryCode the country code
     */
    public DsrpInputDataBuilder usingCountryCode(char countryCode) {
        if (((int) countryCode) > 0 && ((int) countryCode) < 1000) {
            mInputData.setCountryCode(countryCode);
        }
        return this;
    }
}
