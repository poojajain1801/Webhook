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

package com.mastercard.mcbp.transactiondecisionmanager.transaction;

import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;
import com.mastercard.mcbp.transactiondecisionmanager.input.CvmResults;
import com.mastercard.mcbp.transactiondecisionmanager.input.MobileSupportIndicator;
import com.mastercard.mcbp.transactiondecisionmanager.input.TerminalCapabilities;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalType;
import com.mastercard.mobile_api.currency.Iso4217CurrencyUtils;

import java.util.Currency;

/**
 * Store information related to the current transaction
 */
public class TransactionInformation {
    /**
     * Card Product Type
     */
    private final ProductType mProductType;

    /**
     * Transaction Range
     */
    private final TransactionRange mTransactionRange;

    /**
     * Transaction Type
     */
    private final TransactionType mTransactionType;

    /**
     * Expected User Action at the Point of Interaction
     */
    private final ExpectedUserActionOnPoi mExpectedUserActionOnPoi;

    /**
     * Transaction Currency
     */
    private final Currency mCurrency;

    /**
     * Transaction Amount
     */
    private final double mAuthorizedAmount;

    /**
     * Transaction Other Amount
     */
    private final double mOtherAmount;

    /**
     * Terminal Request
     */
    private final Purpose mPurpose;

    /**
     * Flag indicating whether the Terminal has delegated Cardholder Device Consumer Verification
     * Method (CD-CVM)
     */
    private final boolean mHasTerminalDelegatedCdCvm;

    /**
     * Build the transaction information for contactless mchip
     * @param applicationLabel The Application Label
     * @param commandApdu The Generate AC C-APDU
     * @return The transaction information for MCHIP
     */
    public static TransactionInformation forMchip(final byte[] applicationLabel,
                                                  final GenerateAcCommandApdu commandApdu,
                                                  final DolValues pdolValues) {
        return new TransactionInformation(applicationLabel, commandApdu, pdolValues);
    }

    /**
     * Build the transaction information for contactless magstripe
     * @param applicationLabel The Application Label
     * @param commandApdu The ComputeCC Command APDU
     * @param udolValues The UDOL values as received in the GPO C-APDU
     * @return The transaction information for Magstripe
     */
    public static TransactionInformation forMagstripe(final byte[] applicationLabel,
                                                      final ComputeCcCommandApdu commandApdu,
                                                      final DolValues udolValues) {
        final TerminalCapabilities terminalCapabilities =
                TerminalCapabilities.of(udolValues.getValueByTag("9F33"));
        return new TransactionInformation(applicationLabel, commandApdu, terminalCapabilities);
    }

    /**
     * Build The transaction information for Remote Payment
     * @param cryptogramInput The Cryptogram Input
     * @return The transaction information object for remote payment
     */
    public static TransactionInformation forRemotePayment(final CryptogramInput cryptogramInput) {
        return new TransactionInformation(cryptogramInput);
    }

    /**
     * The constructor is not available, please use the static factory instead
     */
    private TransactionInformation(final byte[] applicationLabel,
                                   final GenerateAcCommandApdu commandApdu,
                                   final DolValues pdolValues) {

        final byte transactionType = commandApdu.getTransactionType()[0];
        final byte[] authorizedAmount = commandApdu.getAuthorizedAmount();
        final byte[] otherAmount = commandApdu.getAmountOther();
        final byte[] merchantCategoryCode = commandApdu.getMerchantCategoryCode();
        final MobileSupportIndicator mobileSupportIndicator = MobileSupportIndicator.of(pdolValues);
        final CvmResults cvmResults =
                CvmResults.of(commandApdu.getCvmResults(), mobileSupportIndicator);
        final byte[] currency = commandApdu.getTransactionCurrencyCode();

        mProductType = ProductType.of(applicationLabel);
        mTransactionType = TransactionType.of(transactionType,
                                              merchantCategoryCode,
                                              authorizedAmount);
        mTransactionRange = TransactionRange.forMchip(cvmResults);
        mExpectedUserActionOnPoi = ExpectedUserActionOnPoi.forMchip(cvmResults);

        mCurrency = Iso4217CurrencyUtils.getCurrencyByCode(currency);

        mAuthorizedAmount = Iso4217CurrencyUtils.convertBcdAmountToDouble(authorizedAmount,
                                                                          mCurrency);
        mOtherAmount = Iso4217CurrencyUtils.convertBinaryAmountToDouble(otherAmount, mCurrency);

        mHasTerminalDelegatedCdCvm = hasTerminalHasDelegatedCvmForMchip(cvmResults);

        if (commandApdu.isArqcRequested() || commandApdu.isTcRequested()) {
            mPurpose = Purpose.AUTHORIZE;
        } else if (commandApdu.isAacRequested()) {
            mPurpose = Purpose.AUTHENTICATE;
        } else {
            mPurpose = Purpose.UNKNOWN;
        }
    }

    /**
     * The constructor is not available, please use the static factory instead
     */
    private TransactionInformation(final byte[] fci,
                                   final ComputeCcCommandApdu commandApdu,
                                   final TerminalCapabilities terminalCapabilities) {

        final byte[] authorizedAmount = commandApdu.getAuthorizedAmount();
        final byte transactionType = commandApdu.getTransactionType();
        final MobileSupportIndicator mobileSupportIndicator =
                MobileSupportIndicator.of(commandApdu.getMobileSupportIndicator());
        final byte[] merchantCategoryCode = commandApdu.getMerchantCategoryCode();
        final byte[] currency = commandApdu.getTransactionCurrencyCode();

        mProductType = ProductType.of(fci);
        mTransactionType = TransactionType.of(transactionType,
                                              merchantCategoryCode,
                                              authorizedAmount);
        mTransactionRange = TransactionRange.forMagstripe(mobileSupportIndicator,
                                                          terminalCapabilities);
        mExpectedUserActionOnPoi = ExpectedUserActionOnPoi.forMagstripe(mobileSupportIndicator,
                                                                        terminalCapabilities);

        mCurrency = Iso4217CurrencyUtils.getCurrencyByCode(currency);

        mAuthorizedAmount = Iso4217CurrencyUtils.convertBcdAmountToDouble(authorizedAmount,
                                                                          mCurrency);
        mOtherAmount = 0.0;  // We do not have otherAmount

        mHasTerminalDelegatedCdCvm = hasTerminalHasDelegatedCvmForMagstripe(mobileSupportIndicator);

        final TerminalType type = TerminalType.of(commandApdu.getTerminalType());
        mPurpose = (type == TerminalType.MERCHANT_ATTENDED ||
                    type == TerminalType.MERCHANT_UNATTENDED) ? Purpose.AUTHORIZE: Purpose.UNKNOWN;
    }

    /**
     * The constructor is not available, please use the static factory instead
     */
    private TransactionInformation(final CryptogramInput cryptogramInput) {
        final byte[] merchantCategoryCode = null;
        final byte transactionType = cryptogramInput.getTransactionType().getBytes()[0];
        final byte[] amount = cryptogramInput.getAmountAuthorized().getBytes();
        final byte[] otherAmount = cryptogramInput.getAmountOther().getBytes();
        final byte[] currency = cryptogramInput.getTransactionCurrencyCode().getBytes();

        mProductType = ProductType.UNKNOWN;
        mTransactionType = TransactionType.of(transactionType, merchantCategoryCode, amount);
        mTransactionRange = TransactionRange.forRemotePayment();
        mExpectedUserActionOnPoi = ExpectedUserActionOnPoi.forRemotePayment();

        mCurrency = Iso4217CurrencyUtils.getCurrencyByCode(currency);
        mAuthorizedAmount = Iso4217CurrencyUtils.convertBcdAmountToDouble(amount, mCurrency);
        mOtherAmount = Iso4217CurrencyUtils.convertBinaryAmountToDouble(otherAmount, mCurrency);

        mHasTerminalDelegatedCdCvm = false;

        mPurpose = Purpose.AUTHORIZE;
    }

    /**
     * Utility function to check whether the Terminal has delegated CVM to the device
     *
     * @return True, if the CVM has been delegated
     */
    private boolean hasTerminalHasDelegatedCvmForMchip(final CvmResults cvmResults) {
        return cvmResults.isCdCvmToBePerformed();
    }

    /**
     * Utility function to check whether the Terminal has delegated CVM to the device
     *
     * @return True, if the CVM has been delegated
     */
    private boolean hasTerminalHasDelegatedCvmForMagstripe(
            final MobileSupportIndicator mobileSupportIndicator) {
        return mobileSupportIndicator.isCdCvmRequired();
    }

    /**
     * Get the Product Type
     */
    public ProductType getProductType() {
        return mProductType;
    }

    /**
     * Get the Transaction Range
     */
    public TransactionRange getTransactionRange() {
        return mTransactionRange;
    }

    /**
     * Get the Transaction Type
     */
    public TransactionType getTransactionType() {
        return mTransactionType;
    }

    /**
     * Get the Expected User Action on POI
     */
    public ExpectedUserActionOnPoi getExpectedUserActionOnPoi() {
        return mExpectedUserActionOnPoi;
    }

    /**
     * Get the Currency
     */
    public Currency getCurrency() {
        return mCurrency;
    }

    /**
     * Get the authorized amount
     */
    public double getAuthorizedAmount() {
        return mAuthorizedAmount;
    }

    /**
     * Get the other amount
     */
    public double getOtherAmount() {
        return mOtherAmount;
    }

    /**
     * Check whether the terminal has delegated CD CVM
     */
    public boolean hasTerminalDelegatedCdCvm() {
        return mHasTerminalDelegatedCdCvm;
    }

    /**
     * Get the purpose
     */
    public Purpose getPurpose() {
        return mPurpose;
    }

    /**
     * Convert the object to String. For debug purposes mainly
     * @return The Transaction Information as String
     */
    @Override
    public String toString() {
        final String currency = (mCurrency == null) ? "null": mCurrency.getCurrencyCode();
        return TransactionInformation.class.toString() + "\n" +
               "  Product Type: " + mProductType + "\n" +
               "  Terminal Request: " + mPurpose + "\n" +
               "  Transaction Range: " + mTransactionRange + "\n" +
               "  Transaction Type: " + mTransactionType + "\n" +
               "  ExpectedUserActionOnPoi: " +
               mExpectedUserActionOnPoi + "\n" +
               "  Currency: " + currency + "\n" +
               "  Authorized Amount: " + mAuthorizedAmount + "\n" +
               "  Other Amount: " + mOtherAmount + "\n" +
               "  Has Terminal Delegated CD CVM: " +
               mHasTerminalDelegatedCdCvm + "\n";
    }
}
