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

/**
 * Compute Cryptographic Checksum Command APDU
 */
public class ComputeCcCommandApdu extends CommandApdu {
    public static final String AUTHORIZED_AMOUNT_TAG = "9F02";
    public static final String MOBILE_SUPPORT_INDICATOR_TAG = "9F7E";
    public static final String TERMINAL_COUNTRY_CODE_TAG = "9F1A";
    public static final String TRANSACTION_CURRENCY_CODE_TAG = "5F2A";
    public static final String UNPREDICTABLE_NUMBER_TAG = "9F6A";
    public static final String TERMINAL_TYPE_TAG = "9F35";
    public static final String TRANSACTION_TYPE_TAG = "9C";
    public static final String TRANSACTION_DATE_TAG = "9A";
    public static final String MERCHANT_CATEGORY_CODE_TAG = "9F15";
    // Added in MCBP 1.0+ as dynamic item
    public static final String TERMINAL_RISK_MANAGEMENT_DATA = "9F1D";

    /**
     * Expected P1 value
     */
    public static final byte EXPECTED_P1 = (byte) 0x8E;

    /**
     * Expected P2 value
     */
    public static final byte EXPECTED_P2 = (byte) 0x80;


    /**
     * Expected LE value
     */
    public static final byte EXPECTED_LE = (byte) 0x00;

    /**
     * The authorized amount.
     */
    private final byte[] mAuthorizedAmount;

    /**
     * The mobile support indicator.
     */
    private final byte mMobileSupportIndicator;

    /**
     * The terminal country code.
     */
    private final byte[] mTerminalCountryCode;

    /**
     * The transaction currency code.
     */
    private final byte[] mTransactionCurrencyCode;

    /**
     * The unpredictable number.
     */
    private final byte[] mUnpredictableNumber;

    /**
     * The terminal type
     */
    private final byte mTerminalType;

    /**
     * The Transaction Date
     */
    private final byte[] mTransactionDate;

    /***
     * The Merchant Category Code
     */
    private byte[] mMerchantCategoryCode;

    /**
     * The Transaction Type
     */
    private final byte mTransactionType;

    /**
     * The Terminal Risk Management Data (optional field, it may be null)
     */
    private final byte[] mTerminalRiskManagementData;

    /**
     * The PDOL
     */
    private final byte[] mUdol;

    private final DolValues mUdolData;

    /**
     * Instantiates a new generate ac APDU.
     *
     * @param apdu The Compute CC Command APDU
     */
    public ComputeCcCommandApdu(final byte[] apdu, final DolRequestList udolList) {
        super(apdu);
        validate(apdu);

        final short lc = (short) (apdu[Iso7816.LC_OFFSET] & 0xFF);
        mUdol = new byte[lc];
        System.arraycopy(apdu, Iso7816.C_DATA_OFFSET, mUdol, 0, lc);

        mUdolData = DolValues.of(udolList, mUdol);
        if (udolList == null) {
            throw new InvalidLc("ComputeCC C-APDU: Invalid UDOL list length");
        }
        final int expectedPdolLength = udolList.getExpectedDolLength();

        // Check that the PDOL List we have received in the C-APDU matches the expected length
        // of what was requested. If not, we throw an exception.
        if (expectedPdolLength != lc) {
            throw new InvalidLc("ComputeCC C-APDU: Invalid UDOL list length");
        }

        /*
        final byte[] udol = Utils.copyArrayRange(apdu, C_DATA_OFFSET, C_DATA_OFFSET + 22);
        mUnpredictableNumber = Utils.copyArrayRange(udol, 0, 4);
        mMobileSupportIndicator = udol[4];
        mAuthorizedAmount = Utils.copyArrayRange(udol, 5, 11);
        mTransactionCurrencyCode = Utils.copyArrayRange(udol, 11, 13);
        mTerminalCountryCode = Utils.copyArrayRange(udol, 13, 15);
        mTransactionType = udol[15];
        mTransactionDate = Utils.copyArrayRange(udol, 16, 19);
        mMerchantCategoryCode = Utils.copyArrayRange(udol, 19, 21);
        mTerminalType = udol[21];
        */

        mUnpredictableNumber = mUdolData.getValueByTag(UNPREDICTABLE_NUMBER_TAG);
        mMobileSupportIndicator = mUdolData.getValueByTag(MOBILE_SUPPORT_INDICATOR_TAG)[0];
        mAuthorizedAmount = mUdolData.getValueByTag(AUTHORIZED_AMOUNT_TAG);
        mTransactionCurrencyCode = mUdolData.getValueByTag(TRANSACTION_CURRENCY_CODE_TAG);
        mTerminalCountryCode = mUdolData.getValueByTag(TERMINAL_COUNTRY_CODE_TAG);
        mTransactionType = mUdolData.getValueByTag(TRANSACTION_TYPE_TAG)[0];
        mTransactionDate = mUdolData.getValueByTag(TRANSACTION_DATE_TAG);
        mMerchantCategoryCode = mUdolData.getValueByTag(MERCHANT_CATEGORY_CODE_TAG);
        mTerminalType = mUdolData.getValueByTag(TERMINAL_TYPE_TAG)[0];
        mTerminalRiskManagementData = mUdolData.getValueByTag(TERMINAL_RISK_MANAGEMENT_DATA);
    }

    /**
     * Validate the Compute CC Command APDU
     *
     * @param apdu The Command APDU
     * @throws InvalidLc          In case of wrong APDU length
     * @throws InvalidCommandApdu In case of wrong parameters
     * @throws InvalidP1P2        In case of wrong P1 and/or P2
     */
    private void validate(final byte[] apdu) {
        if (this.getType() != Type.COMPUTE_CRYPTOGRAPHIC_CHECKSUM) {
            throw new InvalidCommandApdu("Expected a GENERATE AC C-APDU, found: " + this.getType());
        }
        if (this.getP1() != EXPECTED_P1 || this.getP2() != EXPECTED_P2) {
            throw new InvalidP1P2("Invalid P1/P2 parameter: " + this.getP1() + ", " + this.getP2());
        }
        if (apdu[apdu.length - 1] != EXPECTED_LE) {
            throw new InvalidCommandApdu("Invalid LE value for the Compute CC C-APDU");
        }
        final short lc = (short) (apdu[Iso7816.LC_OFFSET] & 0xFF);
        if (apdu.length != lc + 6) {
            throw new InvalidLc("Invalid length (does not match LC)");
        }
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
     * Gets the terminal country code.
     *
     * @return the terminal country code
     */
    public final byte[] getTerminalCountryCode() {
        return mTerminalCountryCode;
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
     * Gets the transaction currency code.
     *
     * @return the transaction currency code
     */
    public final byte[] getTransactionCurrencyCode() {
        return mTransactionCurrencyCode;
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
     * Gets the mobile support indicator.
     *
     * @return the mobile support indicator
     */
    public final byte getMobileSupportIndicator() {
        return mMobileSupportIndicator;
    }

    /**
     * Get the transaction data
     *
     * @return the transaction date
     */
    public final byte[] getTransactionDate() {
        return mTransactionDate;
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
     * Get the Transaction Type
     *
     * @return The Transaction Type
     */
    public final byte getTransactionType() {
        return mTransactionType;
    }

    /**
     * Get the Terminal Risk Management Data
     *
     * @return The Terminal Risk Management Data
     */
    public final byte[] getTerminalRiskManagementData() {
        return mTerminalRiskManagementData;
    }

    /**
     * Get the UDOL as byte[]
     *
     * @return The UDOL as byte array
     */
    public final byte[] getUdol() {
        return mUdol;
    }
}
