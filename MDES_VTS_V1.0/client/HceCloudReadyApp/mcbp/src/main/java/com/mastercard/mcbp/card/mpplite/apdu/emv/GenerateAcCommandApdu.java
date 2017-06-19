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

package com.mastercard.mcbp.card.mpplite.apdu.emv;

import com.mastercard.mcbp.card.mpplite.apdu.CommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidCommandApdu;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidLc;
import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.InvalidP1P2;
import com.mastercard.mobile_api.utils.Utils;

/**
 * The Class Generate AC Command APDU.
 */
public class GenerateAcCommandApdu extends CommandApdu {
    /**
     * Expected P2 value
     */
    public static final byte EXPECTED_P2 = 0x00;

    /**
     * Expected LE value
     */
    public static final byte EXPECTED_LE = 0x00;

    /**
     * Minimum Card risk management Data Object List length
     */
    public static final int MIN_LC_VALUE = 45;

    /**
     * The mCdol.
     */
    private final byte[] mCdol;
    /**
     * The authorized amount.
     */
    private final byte[] mAuthorizedAmount;
    /**
     * The other amount.
     */
    private final byte[] mAmountOther;
    /**
     * The terminal country code.
     */
    private final byte[] mTerminalCountryCode;
    /**
     * The terminal verification results.
     */
    private final byte[] mTerminalVerificationResults;
    /**
     * The transaction currency code.
     */
    private final byte[] mTransactionCurrencyCode;
    /**
     * The transaction date.
     */
    private final byte[] mTransactionDate;
    /**
     * The transaction type.
     */
    private final byte[] mTransactionType;
    /**
     * The unpredictable number.
     */
    private final byte[] mUnpredictableNumber;
    /**
     * The terminal type.
     */
    private byte mTerminalType;
    /**
     * The data authentication code.
     */
    private final byte[] mDataAuthenticationCode;
    /**
     * The icc dynamic number.
     */
    private final byte[] mIccDynamicNumber;
    /**
     * The cvm results.
     */
    private final byte[] mCvmResults;

    /**
     * Merchant Category Code
     */
    private final byte[] mMerchantCategoryCode;

    /**
     * Flag indicating whether AC has been requested in the Command APDU
     */
    private final boolean mAcRequested;

    /**
     * Flag indicating whether TC has been requested in the Command APDU
     */
    private final boolean mTcRequested;

    /**
     * Flag indicating whether ARQC has been requested in the Command APDU
     */
    private final boolean mArqcRequested;

    /**
     * Flag indicating whether Combined DDA/AC generation has been requested in the Command APDU
     */
    private final boolean mCombinedDdaAcGenerationRequested;

    /**
     * Instantiates a new generate ac apdu.
     *
     * @param apdu The Generate AC APDU as received from the NFC field (byte[])
     */
    public GenerateAcCommandApdu(final byte[] apdu) {
        super(apdu);
        validate(apdu);

        byte p1 = this.getP1();
        mAcRequested = (p1 & (byte) 0xC0) == 0x00;
        mTcRequested = (p1 & (byte) 0xC0) == (byte) 0x40;
        mArqcRequested = (p1 & (byte) 0xC0) == (byte) 0x80;
        mCombinedDdaAcGenerationRequested = ((p1 & (byte) 0x10) == (byte) 0x10);

        final short lc = (short) (apdu[Iso7816.LC_OFFSET] & 0xFF);

        mCdol = new byte[lc];
        System.arraycopy(apdu, Iso7816.C_DATA_OFFSET, mCdol, 0, lc);

        mAuthorizedAmount = Utils.copyArrayRange(mCdol, 0, 6);
        mAmountOther = Utils.copyArrayRange(mCdol, 6, 12);
        mTerminalCountryCode = Utils.copyArrayRange(mCdol, 12, 14);
        mTerminalVerificationResults = Utils.copyArrayRange(mCdol, 14, 19);
        mTransactionCurrencyCode = Utils.copyArrayRange(mCdol, 19, 21);
        mTransactionDate = Utils.copyArrayRange(mCdol, 21, 24);
        mTransactionType = Utils.copyArrayRange(mCdol, 24, 25);
        mUnpredictableNumber = Utils.copyArrayRange(mCdol, 25, 29);
        mTerminalType = mCdol[29];
        mDataAuthenticationCode = Utils.copyArrayRange(mCdol, 30, 32);
        mIccDynamicNumber = Utils.copyArrayRange(mCdol, 32, 40);
        mCvmResults = Utils.copyArrayRange(mCdol, 40, 43);
        mMerchantCategoryCode = Utils.copyArrayRange(mCdol, 43, 45);
    }

    /**
     * Validate the basic structure of the Command Apdu
     *
     * @param apdu The Generate AC Command APDU
     */
    private void validate(final byte[] apdu) {
        if (this.getType() != Type.GENERATE_AC) {
            throw new InvalidCommandApdu("Expected a GENERATE AC C-APDU, found: " + this.getType());
        }
        final byte p1 = this.getP1();
        if ((p1 & 0x2F) != 0x00 || (p1 & (byte) 0xC0) == (byte) 0xC0 || this.getP2() !=
                                                                        EXPECTED_P2) {
            throw new InvalidP1P2("Invalid P1 or P2 value: " + this.getP1() + ", " + this.getP2());
        }
        if (apdu[apdu.length - 1] != EXPECTED_LE) {
            throw new InvalidCommandApdu("Invalid LE value for the GENERATE AC C-APDU");
        }
        final short lc = (short) (apdu[Iso7816.LC_OFFSET] & 0xFF);
        if (lc < MIN_LC_VALUE) {
            throw new InvalidLc("Invalid LC for a GENERATE AC C-APDU: " + lc);
        }
        if (lc + 6 != apdu.length) {
            throw new InvalidLc("Invalid GENERATE AC APDU length (does not match LC)");
        }
    }

    /**
     * Gets the cdol.
     *
     * @return the cdol
     */
    public final byte[] getCdol() {
        return mCdol;
    }

    /**
     * Gets the authorized amount.
     *
     * @return the authorized amount
     */
    public final byte[] getAuthorizedAmount() {
        return mAuthorizedAmount;
    }

    /**
     * Gets the other amount.
     *
     * @return the other amount
     */
    public final byte[] getAmountOther() {
        return mAmountOther;
    }

    /**
     * Gets the terminal country code.
     *
     * @return the terminal country code
     */
    public final byte[] getTerminalCountryCode() {
        return mTerminalCountryCode;
    }

    /**
     * Gets the terminal verification results.
     *
     * @return the terminal verification results
     */
    public final byte[] getTerminalVerificationResults() {
        return mTerminalVerificationResults;
    }

    /**
     * Gets the transaction currency code.
     *
     * @return the transaction currency code
     */
    public final byte[] getTransactionCurrencyCode() {
        return mTransactionCurrencyCode;
    }

    /**
     * Gets the transaction date.
     *
     * @return the transaction date
     */
    public final byte[] getTransactionDate() {
        return mTransactionDate;
    }

    /**
     * Gets the transaction type.
     *
     * @return the transaction type
     */
    public final byte[] getTransactionType() {
        return mTransactionType;
    }

    /**
     * Gets the unpredictable number.
     *
     * @return the unpredictable number
     */
    public final byte[] getUnpredictableNumber() {
        return mUnpredictableNumber;
    }

    /**
     * Gets the terminal type.
     *
     * @return the terminal type
     */
    public byte getTerminalType() {
        return mTerminalType;
    }

    /**
     * Gets the data authentication code.
     *
     * @return the data authentication code
     */
    public final byte[] getDataAuthenticationCode() {
        return mDataAuthenticationCode;
    }

    /**
     * Gets the icc dynamic number.
     *
     * @return the icc dynamic number
     */
    public final byte[] getIccDynamicNumber() {
        return mIccDynamicNumber;
    }

    /**
     * Gets the cvm results.
     *
     * @return the cvm results
     */
    public final byte[] getCvmResults() {
        return mCvmResults;
    }

    /**
     * Get the Merchant Category Code
     *
     * @return The Merchant Category Code
     */
    public final byte[] getMerchantCategoryCode() {
        return mMerchantCategoryCode;
    }

    /**
     * Get the Input for the Application Cryptogram
     *
     * @return the cdol subset to be used for the Application Cryptogram generation
     */
    public final byte[] getInputForApplicationCryptogram() {
        // See Table 47 of GAC.7.2
        byte[] result = new byte[29];
        System.arraycopy(mCdol, 0, result, 0, 29);
        return result;
    }

    /**
     * Check whether the AAC has been requested in the Command APDU
     *
     * @return whether the AAC has been requested in the Command APDU
     */
    public final boolean isAacRequested() {
        return mAcRequested;
    }

    /**
     * Check whether the TC has been requested in the Command APDU
     *
     * @return whether the TC has been requested in the Command APDU
     */
    public final boolean isTcRequested() {
        return mTcRequested;
    }

    /**
     * Check whether the ARQC has been requested in the Command APDU
     *
     * @return whether the ARQC has been requested in the Command APDU
     */
    public final boolean isArqcRequested() {
        return mArqcRequested;
    }

    /**
     * Check whether the Combined DDA/AC has been requested in the Command APDU
     *
     * @return whether the Combined DDA/AC has been requested in the Command APDU
     */
    public final boolean isCombinedDdaAcGenerationRequested() {
        return mCombinedDdaAcGenerationRequested;
    }
}
