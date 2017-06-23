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

package com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement;

import com.mastercard.mcbp.utils.exceptions.mpplite.commandapdu.ConditionsOfUseNotSatisfied;
import com.mastercard.mobile_api.utils.Utils;

/**
 * The Card Verification Results used during the generation of the cryptogram
 */
public final class CardVerificationResults {
    /**
     * Expected Card Verification Results length
     */
    public final int CVR_LENGTH = 6;
    /**
     * Expected Card Issuer Action Code Decline mask length
     */
    public final int CIAC_DECLINE_LENGTH = 3;

    public static final int  TRANSACTION_TYPE_OFFSET = 3;
    public static final byte INTERNATIONAL_TRANSACTION = 0x04;
    public static final byte DOMESTIC_TRANSACTION = 0x02;
    public static final byte ARQC_CVR_DD_AAC_RETURNED = (byte) 0x40;
    public static final byte ARQC_RETURNED_IN_FIRST_GAC = (byte) 0xA0;
    public static final byte OFFLINE_PIN_OK = 0x05;
    public static final byte TERMINAL_ERRONEOUSLY_CONSIDERS_OFFLINE_PIN_OK = 0x01;
    public static final byte OFFLINE_PIN_NOT_PERFORMED = 0x20;
    public static final byte AAC_RETURNED_IN_FIRST_GAC = (byte) 0x80;
    public static final byte CVM_REQUIRED_NOT_SATISFIED = (byte) 0x08;

    /**
     * The Card Verification Results data
     */
    private final byte[] mCardVerificationResults;

    /***
     * Build the Card Verification Results using the Issuer Application Data
     * @param issuerApplicationData The Issuer Application Data as in the card profile
     * @return The Card Verification Results initialized as per MCBP specs
     */
    public static CardVerificationResults withIssuerApplicationData(
            final byte[] issuerApplicationData) {
        if (issuerApplicationData == null) {
            throw new ConditionsOfUseNotSatisfied("Invalid Issuer Application Data");
        }
        return new CardVerificationResults(issuerApplicationData);
    }

    /**
     * Build the Card Verification object
     *
     * @param issuerApplicationData The Issuer Application Data that is used to initialize the CVR
     */
    private CardVerificationResults(final byte[] issuerApplicationData) {
        //CVR := profile.dataRP.issuerApplicationData[3 : 8] & '000300000000'
        mCardVerificationResults = new byte[CVR_LENGTH];
        System.arraycopy(issuerApplicationData, 3, mCardVerificationResults, 1, 1);
        mCardVerificationResults[1] &= 0x03;
    }

    /**
     * Utility function to check a match in the CVR given a ciac decline mask
     *
     * @param ciacDecline The Card Issuer Action Code as specified in the card profile
     * @return True, if a match is found. False otherwise
     */
    public final boolean isCiacDeclineMatchFound(final byte[] ciacDecline) {
        if (ciacDecline == null || ciacDecline.length != CIAC_DECLINE_LENGTH) {
            throw new ConditionsOfUseNotSatisfied("CIAC decline");
        }

        // check whether there is a match in CVR
        for (int i = 0; i < ciacDecline.length; i++) {
            if ((mCardVerificationResults[3 + i] & ciacDecline[i]) != 0x00) {
                // Match found
                return true;
            }
        }
        return false;
    }

    /**
     * Set a bit in the CVR to indicate that the Terminal Erroneously Consider Offline PIN Okay
     */
    public final void indicateTerminalErroneouslyConsiderOfflinePinOk() {
        mCardVerificationResults[TRANSACTION_TYPE_OFFSET]
                |= TERMINAL_ERRONEOUSLY_CONSIDERS_OFFLINE_PIN_OK;
    }

    /**
     * Indicate that CD CVM has been performed
     */
    public final void indicateCdCvmPerformed() {
        mCardVerificationResults[0] |= OFFLINE_PIN_OK;
    }

    /**
     * Indicate that CD CVM has not been performed
     */
    public final void indicateCdCvmNotPerformed() {
        mCardVerificationResults[TRANSACTION_TYPE_OFFSET] |= OFFLINE_PIN_NOT_PERFORMED;
    }

    /**
     * Indicate a domestic transaction
     */
    public final void indicateDomesticTransaction() {
        mCardVerificationResults[TRANSACTION_TYPE_OFFSET] |= DOMESTIC_TRANSACTION;
    }

    /**
     * Indicate an international transaction
     */
    public final void indicatedInternationalTransaction() {
        mCardVerificationResults[TRANSACTION_TYPE_OFFSET] |= INTERNATIONAL_TRANSACTION;
    }

    /**
     * Indicate that AAC was returned in the first Generate AC and that AC was not requested
     * in the Second Generate AC.
     */
    public final void indicateAacReturnedInFirstAndAcNotRequestedInSecondGenerateAc() {
        mCardVerificationResults[0] |= AAC_RETURNED_IN_FIRST_GAC;
    }

    /**
     * Indicate that Combine DDA/AC Generation is returned in the First Generate AC
     */
    public final void indicateCombinedDdaAcGenerationReturnedInFirstGenerateAc() {
        mCardVerificationResults[1] |= ARQC_CVR_DD_AAC_RETURNED;
    }

    /**
     * Indicate that a match has been found in the additional check table
     */
    public final void indicateMatchFoundInAdditionalCheckTable() {
        mCardVerificationResults[5] |= 0x02;
    }

    /**
     * Indicate that a match has not been found in the additional check table
     */
    public final void indicateMatchNotFoundInAdditionalCheckTable() {
        mCardVerificationResults[5] |= 0x01;
    }

    /**
     * Indicate that CVM Required is not satisfied
     */
    public final void indicateCvmRequiredNotSatisfied() {
        mCardVerificationResults[5] |= CVM_REQUIRED_NOT_SATISFIED;
    }

    /**
     * Indicate ARQC returned in the First Generate AC and AC not requested in the Second
     * Generate AC
     */
    public final void indicateArqcReturnedInFirstAndAcNotRequestedInSecondGenerateAc() {
        mCardVerificationResults[0] |= ARQC_RETURNED_IN_FIRST_GAC;
    }

    /**
     * The Card Verification Results AND Mask is a mask applied to the Card
     * Verification Results before the generation of the Application Cryptogram when
     * an M/Chip contactless or remote transaction is performed.
     *
     * @param cvrMaskAnd The Card Verification Results AND Mask
     */
    public final void applyMaskAnd(final byte[] cvrMaskAnd) {
        for (int i = 0; i < mCardVerificationResults.length; i++) {
            mCardVerificationResults[i] &= cvrMaskAnd[i];
        }
    }

    /**
     * Check whether the Combined DDA/AC generation is to be returned in the First Generate AC
     * @return True, if combined DDA/AC is to be returned in the First Generate AC. False otherwise.
     */
    public boolean isCombinedDdaAcGenerationReturnedInFirstGenerateAc() {
        return (mCardVerificationResults[1] & ARQC_CVR_DD_AAC_RETURNED) == ARQC_CVR_DD_AAC_RETURNED;
    }

    /**
     * Get the content of the Cardholder Verification Result as byte[]
     */
    public final byte[] getBytes() {
        return mCardVerificationResults;
    }

    /**
     * Force approval for existing MDES implementation
     */
    public final void setFlagsForApproval() {
        mCardVerificationResults[3] &= 0xFE;
        mCardVerificationResults[5] &= 0xF7;
    }

    /**
     * Securely zeroes the content of the CVR.
     */
    public void wipe() {
        Utils.clearByteArray(mCardVerificationResults);
    }
}
