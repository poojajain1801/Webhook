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

package com.mastercard.mcbp.card;

public final class BusinessLogicTransactionInformation {

    public static final long MAX_AMOUNT = 999999999999L;
    /**
     * Transaction amount
     */
    private long mAmount;
    /**
     * Currency code
     */
    private int mCurrencyCode;
    /**
     * Exact amount
     */
    private boolean mExactAmount;

    /**
     * Create an empty transaction information object where Amount and Currency Code are set to
     * negative values
     * */
    public BusinessLogicTransactionInformation() {
        super();
    }

    /***
     *
     * @param amount The Transaction Amount
     * @param currencyCode The Transaction Currency Code
     * @param exactAmount A flag specifying whether the exact amount should be approved
     */
    public BusinessLogicTransactionInformation(long amount, int currencyCode, boolean exactAmount) {
        this.mAmount = amount;
        this.mCurrencyCode = currencyCode;
        this.mExactAmount = exactAmount;
    }

    public final long getAmount() {
        return mAmount;
    }

    public final void setAmount(long amount) {
        this.mAmount = amount;
    }

    public final int getCurrencyCode() {
        return mCurrencyCode;
    }

    public final void setCurrencyCode(int currencyCode) {
        this.mCurrencyCode = currencyCode;
    }

    public final boolean isExactAmount() {
        return mExactAmount;
    }

    public final void setExactAmount(boolean exactAmount) {
        this.mExactAmount = exactAmount;
    }

}
