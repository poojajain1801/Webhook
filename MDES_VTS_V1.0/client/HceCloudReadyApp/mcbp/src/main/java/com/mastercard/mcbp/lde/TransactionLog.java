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

package com.mastercard.mcbp.lde;

import com.mastercard.mcbp.card.mobilekernel.DsrpInputData;
import com.mastercard.mcbp.card.mobilekernel.DsrpOutputData;
import com.mastercard.mcbp.card.mobilekernel.MobileKernel;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.ContactlessLog;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.TransactionSummary;
import com.mastercard.mcbp.card.transactionlogging.TransactionIdentifier;
import com.mastercard.mcbp.transactiondecisionmanager.terminal.TerminalTechnology;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.nio.charset.Charset;

/**
 * Template for transaction log<br>
 * <br>
 */

public class TransactionLog {

    public static final byte FORMAT_MCHIP = 1;
    public static final byte FORMAT_MAGSTRIPE = 2;
    public static final byte FORMAT_PPMC_DSRP = 3;
    public static final byte FORMAT_FAILED = 4;

    /**
     * Dc Id
     */
    private final String mDigitizedCardId;
    /**
     * Unpredictable Number
     */
    private final ByteArray mUnpredictableNumber;
    /**
     * Application transaction counter.
     */
    private final ByteArray mAtc;
    /**
     * Cryptogram Format.
     */
    private final byte mCryptogramFormat;

    /**
     * Transaction Identifier (Used only for MCBP with MDES)
     */
    private final ByteArray mTransactionId;
    /**
     * Jail Broken  flag.
     */
    private final boolean mHostingMeJailBroken;
    /**
     * Recent Attack flag.
     */
    private final boolean mRecentAttack;
    /**
     * Data
     */
    private final ByteArray mDate;
    /**
     * Amount value.
     */
    private final ByteArray mAmount;
    /**
     * Currency Code
     */
    private final ByteArray mCurrencyCode;

    public static TransactionLog forRemotePayment (final String digitizedCardId,
                                                   final DsrpInputData dsrpInputData,
                                                   final DsrpOutputData dsrpOutputData,
                                                   final ByteArray transactionId,
                                                   final boolean recentAttack,
                                                   final boolean hostingMeJailBroken) {
        // add transaction record to log
        final ByteArray un = ByteArray.get (4);
        Utils.writeInt (un, 0, dsrpInputData.getUnpredictableNumber ());
        final ByteArray amount = ByteArray.of (
                Utils.longToBcd (dsrpInputData.getTransactionAmount (), 6), 6);

        final ByteArray currency =
                ByteArray.of (Utils.longToBcd (dsrpInputData.getCurrencyCode (), 2), 2);
        final ByteArray atc = ByteArray.of ((char) dsrpOutputData.getAtc ());
        final ByteArray date = MobileKernel.getDateAsByteArray (dsrpInputData.getTransactionDate ());

        return new TransactionLog (digitizedCardId,
                un,
                atc,
                date,
                amount,
                currency,
                TransactionLog.FORMAT_PPMC_DSRP,
                transactionId,
                hostingMeJailBroken,
                recentAttack);
    }


    public static TransactionLog forContactless (final String digitizedCardId,
                                                 final ContactlessLog contactlessLog,
                                                 final ByteArray transactionId,
                                                 final boolean hostingMeJailBroken,
                                                 final boolean recentAttack) {
        return new TransactionLog (digitizedCardId,
                contactlessLog.getUnpredictableNumber (),
                contactlessLog.getAtc (),
                contactlessLog.getDate (),
                contactlessLog.getAmount (),
                contactlessLog.getCurrencyCode (),
                getTransactionTechnology (contactlessLog.getTerminalTechnology (),
                        contactlessLog.getResult ()),
                transactionId,
                hostingMeJailBroken,
                recentAttack);
    }

    public static TransactionLog fromLdeData (final String digitizedCardId,
                                              final ByteArray unpredictableNumber,
                                              final ByteArray atc,
                                              final ByteArray date,
                                              final ByteArray amount,
                                              final ByteArray currencyCode,
                                              final byte cryptogramFormat,
                                              final ByteArray transactionId,
                                              final boolean hostingMeJailBroken,
                                              final boolean recentAttack) {
        return new TransactionLog (digitizedCardId,
                unpredictableNumber,
                atc,
                date,
                amount,
                currencyCode,
                cryptogramFormat,
                transactionId,
                hostingMeJailBroken,
                recentAttack);
    }

    private TransactionLog (final String digitizedCardId,
                            final ByteArray unpredictableNumber,
                            final ByteArray atc,
                            final ByteArray date,
                            final ByteArray amount,
                            final ByteArray currencyCode,
                            final byte cryptogramFormat,
                            final ByteArray transactionId,
                            final boolean hostingMeJailBroken,
                            final boolean recentAttack) {
        this.mDigitizedCardId = digitizedCardId;
        this.mUnpredictableNumber = unpredictableNumber;
        this.mAtc = atc;
        this.mDate = date;
        this.mAmount = amount;
        this.mCurrencyCode = currencyCode;
        this.mCryptogramFormat = cryptogramFormat;
        this.mTransactionId = transactionId;
        this.mHostingMeJailBroken = hostingMeJailBroken;
        this.mRecentAttack = recentAttack;

    }


    private static byte getTransactionTechnology (final TerminalTechnology terminalTechnology,
                                                  final TransactionSummary transactionSummary) {
        if (terminalTechnology != null && transactionSummary != null) {
            switch (transactionSummary) {
                case AUTHORIZE_ONLINE:
                    switch (terminalTechnology) {
                        case CONTACTLESS_EMV:
                            return FORMAT_MCHIP;
                        case CONTACTLESS_MAGSTRIPE:
                            return FORMAT_MAGSTRIPE;
                        default:
                            return 0;
                    }
                case ERROR_CONTEXT_CONFLICT:
                case DECLINE:
                case ERROR:
                    return FORMAT_FAILED;
                default:
                    return 0;
            }
        }
        // If any of the input is null, return 0x00
        return 0;
    }

    public byte getCryptogramFormat () {
        return mCryptogramFormat;
    }

    public ByteArray getDate () {
        return mDate;
    }

    public ByteArray getAmount () {
        return mAmount;
    }

    public ByteArray getCurrencyCode () {
        return mCurrencyCode;
    }

    public String getDigitizedCardId () {
        return mDigitizedCardId;
    }

    public boolean isValid () {
        return true;
    }

    public ByteArray getAtc () {
        return mAtc;
    }

    public boolean isHostingMeJailBroken () {
        return mHostingMeJailBroken;
    }

    public boolean isRecentAttack () {
        return mRecentAttack;
    }

    public ByteArray getUnpredictableNumber () {
        return mUnpredictableNumber;
    }

    public ByteArray getTransactionId () {
        return mTransactionId;
    }

    public void wipe () {
        Utils.clearByteArray (mAmount);
        Utils.clearByteArray (mTransactionId);
        Utils.clearByteArray (mAtc);
        Utils.clearByteArray (mCurrencyCode);
        Utils.clearByteArray (mDate);
        Utils.clearByteArray (mUnpredictableNumber);
    }
}
