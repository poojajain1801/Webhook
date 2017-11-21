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

package com.mastercard.mcbp.card.mpplite.mcbpv1.output;

import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ContactlessTransactionContext;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalTechnology;
import com.mastercard.mcbp.utils.exceptions.datamanagement.UnexpectedData;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

/**
 * Data utility object to store information related to the transaction logging
 */
public final class ContactlessLogImpl implements ContactlessLog {
    private final ByteArray mUnpredictableNumber;
    private final ByteArray mAtc;
    private final ByteArray mCryptogram;
    private final ByteArray mDate;
    private final ByteArray mAmount;
    private final ByteArray mCurrencyCode;
    private final TransactionSummary mResult;
    private final TerminalTechnology mTerminalTechnology;
    private final ByteArray mDynamicTrack1Data;
    private final ByteArray mDynamicTrack2Data;

    /**
     * Log the outcome of a Magstripe transaction
     * @param context The contactless context
     * @param contactlessPaymentData The contactless payment data (to retrieve track 1 and track 2)
     * @param cvc3 The Crypto CVC3
     * @param cryptoAtc The Crypto ATC
     * @return A contactless log object
     */
    public static ContactlessLogImpl forMagstripe(final ContactlessTransactionContext context,
                                                  final ContactlessPaymentData
                                                          contactlessPaymentData,
                                                  final ByteArray cvc3,
                                                  final ByteArray cryptoAtc,
                                                  final boolean isCdCvmSupported) {
        ByteArray dynamicTrack1Data;
        ByteArray dynamicTrack2Data;
        try {
            final DynamicTrackData dynamicTrackData =
                    new DynamicTrackData(contactlessPaymentData,
                                         cryptoAtc,
                                         cvc3,
                                         context.getUnpredictableNumber(),
                                         isCdCvmSupported);
            dynamicTrack1Data = dynamicTrackData.getDynamicTrack1();
            dynamicTrack2Data = dynamicTrackData.getDynamicTrack2();
        } catch (final UnexpectedData e) {
            // We could not generate the Magstripe Dynamic Data
            dynamicTrack1Data = ByteArray.of("");
            dynamicTrack2Data = ByteArray.of("");
        }

        return new ContactlessLogImpl(context,
                                      TerminalTechnology.CONTACTLESS_MAGSTRIPE,
                                      dynamicTrack1Data,
                                      dynamicTrack2Data);
    }

    /**
     * Log the outcome of an MCHIP transaction
     * @param context The contactless context
     */
    public static ContactlessLogImpl forMchip(final ContactlessTransactionContext context) {
        return new ContactlessLogImpl(context,
                                      TerminalTechnology.CONTACTLESS_EMV,
                                      ByteArray.of(""),
                                      ByteArray.of(""));
    }

    /**
     * Generic logger for those transactions that have not successfully completed
     */
    public static ContactlessLogImpl generic(final ContactlessTransactionContext context) {
        return new ContactlessLogImpl(context, null, ByteArray.of(""), ByteArray.of(""));
    }

    /**
     * Constructor is not available. Please use static factory methods instead.
     */
    private ContactlessLogImpl(final ContactlessTransactionContext context,
                               final TerminalTechnology terminalTechnology,
                               final ByteArray dynamicTrack1Data,
                               final ByteArray dynamicTrack2Data) {
        this.mUnpredictableNumber = readValue(context.getUnpredictableNumber());
        this.mAtc = readValue(context.getAtc());
        this.mCryptogram = readValue(context.getCryptogram());
        this.mDate = readValue(context.getTrxDate());
        this.mAmount = readValue(context.getAmount());
        this.mCurrencyCode = readValue(context.getCurrencyCode());
        this.mResult = context.getResult();
        this.mTerminalTechnology = terminalTechnology;

        this.mDynamicTrack1Data = dynamicTrack1Data == null ? ByteArray.of(""): dynamicTrack1Data;
        this.mDynamicTrack2Data = dynamicTrack2Data == null ? ByteArray.of(""): dynamicTrack2Data;
    }

    /**
     * Utility function to convert the input data
     */
    private ByteArray readValue(final ByteArray value) {
        return value != null ? ByteArray.of(value) : null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getAtc() {
        return mAtc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getCryptogram() {
        return mCryptogram;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getDate() {
        return mDate;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getAmount() {
        return mAmount;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getCurrencyCode() {
        return mCurrencyCode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getMagstripeDynamicTrack1Data() {
        return mDynamicTrack1Data;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getMagstripeDynamicTrack2Data() {
        return mDynamicTrack2Data;
    }

    /**
     * Get the Terminal Technology (EVM vs MAGSTRIPE)
     * @return The Terminal Technology, if known. Null otherwise.
     */
    @Override
    public TerminalTechnology getTerminalTechnology() {
        return mTerminalTechnology;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void wipe() {
        Utils.clearByteArray(this.mUnpredictableNumber);
        Utils.clearByteArray(this.mAtc);
        Utils.clearByteArray(this.mCryptogram);
        Utils.clearByteArray(this.mDate);
        Utils.clearByteArray(this.mAmount);
        Utils.clearByteArray(this.mCurrencyCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TransactionSummary getResult() {
        return mResult;
    }
}
