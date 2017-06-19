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

package com.mastercard.mcbp.transactiondecisionmanager.terminal;

import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;
import com.mastercard.mcbp.transactiondecisionmanager.input.CvmResults;
import com.mastercard.mcbp.transactiondecisionmanager.input.MobileSupportIndicator;
import com.mastercard.mcbp.transactiondecisionmanager.input.TerminalRiskManagementData;

/**
 * Container for Terminal Information data
 */
public class TerminalInformation {
    /**
     * Terminal Type
     */
    private final TerminalType mTerminalType;

    /**
     * Terminal Technology related information
     */
    private final TerminalTechnology mTerminalTechnology;

    /**
     * Specify whether information about two taps support could be retrieved
     */
    private final PersistentTransactionContext mPersistentTransactionContext;

    /**
     * Specify whether information about CD CVM support could be retrieved
     */
    private final CdCvmSupport mCdCvmSupport;

    /**
     * PDOL Data as received during the GPO command
     */
    private final DolValues mPdolValues;

    /**
     * UDOL Data as received during the Compute CC or the Generate AC Command APDU
     */
    private final DolValues mCommandDolValues;

    /**
     * Build the Terminal Information for a MCHIP Transaction
     *
     * @param commandApdu The Generate AC Command APDU
     * @param pdolValues  The PDOL values as received in the GPO C-APDU
     * @param cdolValues  The CDOL values as received in the GenerateAc C-APDU
     * @return The Terminal Information for the current transaction
     */
    public static TerminalInformation forMchip(final GenerateAcCommandApdu commandApdu,
                                               final DolValues pdolValues,
                                               final DolValues cdolValues) {
        final TerminalRiskManagementData terminalRiskManagementData =
                TerminalRiskManagementData.of(pdolValues);

        final MobileSupportIndicator mobileSupportIndicator = MobileSupportIndicator.of(pdolValues);

        final CvmResults cvmResults = CvmResults.of(commandApdu.getCvmResults(),
                mobileSupportIndicator);
        return new TerminalInformation(
                TerminalType.of(commandApdu.getTerminalType()),
                TerminalTechnology.CONTACTLESS_EMV,
                PersistentTransactionContext.forMchip(mobileSupportIndicator),
                CdCvmSupport.forMchip(cvmResults, terminalRiskManagementData),
                pdolValues,
                cdolValues);
    }

    /**
     * Build the Terminal Information for a Magstripe Transaction
     *
     * @param commandApdu The Compute CC Command APDU
     * @param pdolValues  The PDOL values as received in the GPO C-APDU
     * @param udolValues  The UDOL values as received in the Compute CC C-APDU
     * @return The Terminal Information for the current transaction
     */
    public static TerminalInformation forMagstripe(final ComputeCcCommandApdu commandApdu,
                                                   final DolValues pdolValues,
                                                   final DolValues udolValues) {
        // In case of Magstripe we need to build the Mobile Support Indicator from the Command APDU
        // as we are sure the MSI is there.
        final MobileSupportIndicator mobileSupportIndicator =
                MobileSupportIndicator.of(commandApdu.getMobileSupportIndicator());
        final TerminalRiskManagementData terminalRiskManagementData =
                TerminalRiskManagementData.of(commandApdu.getTerminalRiskManagementData());
        return new TerminalInformation(
                TerminalType.of(commandApdu.getTerminalType()),
                TerminalTechnology.CONTACTLESS_MAGSTRIPE,
                PersistentTransactionContext.forMagstripe(mobileSupportIndicator),
                CdCvmSupport.forMagstripe(mobileSupportIndicator, terminalRiskManagementData),
                pdolValues,
                udolValues);
    }

    /**
     * Build the Terminal Information for a Remote Payment Transaction
     *
     * @param cryptogramInput The Remote Payment Cryptogram Input data
     * @return The Terminal Information for the current transaction
     */
    public static TerminalInformation forRemotePayment(final CryptogramInput cryptogramInput) {
        return new TerminalInformation(TerminalType.CARDHOLDER_OPERATED,
                TerminalTechnology.forRemotePayment(
                        cryptogramInput.getCryptogramType()),
                PersistentTransactionContext.UNKNOWN,
                // We do not know whether the Merchant will recognize CD CVM
                CdCvmSupport.UNKNOWN,
                null, null);
    }

    /**
     * Get the Terminal Type
     *
     * @return The Terminal Type information
     */
    public TerminalType getTerminalType() {
        return mTerminalType;
    }

    /**
     * Get the Terminal Type
     *
     * @return The Terminal Type information
     */
    public TerminalTechnology getTerminalTechnology() {
        return mTerminalTechnology;
    }

    /**
     * The Persistent Transaction Context indicates whether or not the Terminal is able to keep
     * context between different taps. For example, if the persistent transaction context is
     * supported it indicates that the Terminal can support two taps transactions.
     *
     * @return The Information about the Persistent Transaction Context
     */
    public PersistentTransactionContext getPersistentTransactionContext() {
        return mPersistentTransactionContext;
    }

    /**
     * Get the Cardholder Device Consumer Verification Method information
     *
     * @return The CD-CVM support information
     */
    public CdCvmSupport getCdCvmSupport() {
        return mCdCvmSupport;
    }

    /**
     * Search in the PDOL related data for a given tag.
     *
     * @param tag The tag to search for in the PDOL
     * @return the value if both the tag and the value are found. An empty array of zeroes if only
     * the tag is found and the length is known. An empty byte array in the other cases
     */
    public final byte[] getDiscretionaryDataByTag(final String tag) {
        // We first check if the tag is in the CDOL or UDOL
        if (mCommandDolValues != null) {
            final byte[] value = mCommandDolValues.getValueByTag(tag);
            if (value != null) {
                return value;
            }
        }
        // If we could not find the tag, we look into the PDOL
        return mPdolValues == null ? null : mPdolValues.getValueByTag(tag);
    }

    /**
     * The Constructor is accessible only to static methods. Use the static factories to build the
     * object
     *
     * @param terminalType                 The Terminal Type
     * @param terminalTechnology           The Terminal Technology
     * @param persistentTransactionContext The Persistent Transaction Context
     * @param cdCvmSupport                 The CD CVM Support
     * @param pdolValues                   The values in the PDOL
     * @param commandDolValues             The values in the CDOL or UDOL
     */
    private TerminalInformation(final TerminalType terminalType,
                                final TerminalTechnology terminalTechnology,
                                final PersistentTransactionContext persistentTransactionContext,
                                final CdCvmSupport cdCvmSupport,
                                final DolValues pdolValues,
                                final DolValues commandDolValues) {

        mTerminalType = terminalType;
        mTerminalTechnology = terminalTechnology;
        mPersistentTransactionContext = persistentTransactionContext;
        mCdCvmSupport = cdCvmSupport;
        mPdolValues = pdolValues;
        mCommandDolValues = commandDolValues;
    }

    @Override
    public String toString() {
        final StringBuilder stringBuilder =
                new StringBuilder(PersistentTransactionContext.class.toString());
        stringBuilder.append("\n");
        stringBuilder.append("  Terminal Type: ").append(mTerminalType).append("\n");
        stringBuilder.append("  Terminal Technology: ").append(mTerminalTechnology).append("\n");
        stringBuilder.append("  Persistent Transaction Context: ")
                .append(mPersistentTransactionContext).append("\n");
        stringBuilder.append("  CD-CVM Support: ").append(mCdCvmSupport).append("\n");
        stringBuilder.append("  Discretionary Data [TAG|VALUE] HEX: ").append("\n");

        if (mPdolValues != null) {
            stringBuilder.append(mPdolValues.toString());
        }
        if (mCommandDolValues != null) {
            stringBuilder.append(mCommandDolValues.toString());
        }
        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
