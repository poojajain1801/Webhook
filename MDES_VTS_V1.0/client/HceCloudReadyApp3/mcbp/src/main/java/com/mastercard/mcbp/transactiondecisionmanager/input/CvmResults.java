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

package com.mastercard.mcbp.transactiondecisionmanager.input;

import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;

/**
 * CVM Result is modeled as object to provide easy access to internal data representation
 */
public final class CvmResults {
    /**
     * CVM Result is currently defined as 3 bytes array
     */
    public final int CVM_RESULT_LENGTH = 3;

    /**
     * The position of the byte in the CVM Results that indicates the CVM RESULTS value
     */
    public static final int CVM_RESULTS_BYTE = 0;

    /**
     * The position of the byte in the CVM Results that indicates the results of the last CVM
     */
    public static final int RESULT_OF_LAST_CVM_PERFORMED_BYTE = 2;

    /**
     * The CVM result byte[]
     */
    private final byte[] mCvmResults;

    /**
     * The Mobile Support Indicator used to understand whether we are dealing with a V2 or V3
     * terminal
     */
    private final MobileSupportIndicator mMobileSupportIndicator;

    /**
     * Build a CVM Result object using the
     * @param cvmResults The CVM Result as byte[]
     * @return A CVM Result object that can be used to easily access information
     */
    public static CvmResults of(final byte[] cvmResults,
                                final MobileSupportIndicator mobileSupportIndicator) {
        if (cvmResults == null) {
            throw new MppLiteException("Invalid CVM Results data");
        }
        return new CvmResults(cvmResults, mobileSupportIndicator);
    }

    /**
     * Private constructor, please use the static factory method instead.
     */
    private CvmResults(final byte[] cvmResults,
                       final MobileSupportIndicator mobileSupportIndicator) {
        if (cvmResults.length != CVM_RESULT_LENGTH) {
            throw new MppLiteException("Invalid CVM Result length: " + cvmResults.length);
        }
        mCvmResults = cvmResults;
        mMobileSupportIndicator = mobileSupportIndicator;
    }

    /**
     * Utility function to check whether the POS will perform NO CVM check.
     *
     * Note that this function assumes that all the cards are personalized to support CVM processing
     * in the AIP
     *
     * @return True if NO CVM will be performed at the POS
     */
    public final boolean isNoCvmToBePerformed() {
        // Check V3 terminal first (i.e. mobile support indicator indicates mobile supported)
        if (mMobileSupportIndicator.isSupportedByTheTerminal()) {
            // It is a V3
            return isCvmResultSuccess() && ((mCvmResults[CVM_RESULTS_BYTE] & 0x1F) == 0x1F);
        }
        // Else it is a V2 and we check cvmResults[6 : 1] = 01 1111b
        return (isCvmResultSuccess() || isCvmResultUnknown()) &&
                ((mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x1F);
    }

    /**
     * Utility function to check whether the POS will ask the user to the enter the Online PIN
     *
     * @return True if Online PIN will be performed at the POS
     */
    public final boolean isOnlinePinToBePerformed() {
        // cvmResults[6 : 1] = 00 0010b
        return (isCvmResultUnknown()) && (mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x02;
    }

    /**
     * Utility function to check whether the POS will ask the user to sign
     *
     * @return True if signature will be requested by the POS
     */
    public final boolean isSignatureToBePerformed() {
        // cvmResults[6 : 1] = 01 1110b
        return isSignatureOnlyToBePerformed() || isCdCvmAndSignatureToBePerformed();
    }

    /**
     * Utility function to check whether the POS will ask the user to sign as only CVM
     *
     * @return True if signature will be requested by the POS
     */
    public final boolean isSignatureOnlyToBePerformed() {
        // cvmResults[6 : 1] = 01 1110b
        return (isCvmResultUnknown()) &&
                ((mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x1E);    // Signature only
    }

    /**
     * Utility function to check whether the POS will expect CD CVM to be performed on the mobile
     *
     * @return True if the CD CVM is requested by the POS
     */
    public final boolean isCdCvmAndSignatureToBePerformed() {
        // Please note that we check for both success and unknown, although a good terminal should
        // set this value to unknown.
        return (isCvmResultSuccess() || isCvmResultUnknown()) &&
                ((mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x03 ||   // CD CVM + Signature
                        (mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x05);    // CD CVM + Signature
    }

    /**
     * Utility function to check whether the POS will expect CD CVM only to be performed on the
     * mobile
     *
     * @return True if the CD CVM is requested by the POS
     */
    public final boolean isCdCvmOnlyToBePerformed() {
        return (isCvmResultSuccess()) &&
                ((mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x01 ||    // CD CVM Only
                        (mCvmResults[CVM_RESULTS_BYTE] & 0x3F) == 0x04);     // CD CVM Only
    }

    /**
     * Utility function to check whether the POS will expect CD CVM to be performed on the mobile
     *
     * @return True if the CD CVM is requested by the POS
     */
    public final boolean isCdCvmToBePerformed() {
        return isCdCvmAndSignatureToBePerformed() || isCdCvmOnlyToBePerformed();

    }

    /**
     * Check whether the CVM result success flag is set in the CVM result
     *
     * @return True if the flag CVM Result Success is set
     */
    public final boolean isCvmResultSuccess() {
        // Result of the (last) CVM performed as known by the terminal:
        // '2' = Successful (for example, for offline PIN)
        return mCvmResults[RESULT_OF_LAST_CVM_PERFORMED_BYTE] == 0x02;
    }

    /**
     * Check whether the CVM result flag is set to unknown
     *
     * @return True if the flag CVM Result Unknown is set
     */
    public final boolean isCvmResultUnknown() {
        // Result of the (last) CVM performed as known by the terminal:
        // '0' = Unknown (for example, for signature)
        return mCvmResults[RESULT_OF_LAST_CVM_PERFORMED_BYTE] == 0x00;
    }

    /**
     * Check whether the CVM Results is supported by the Terminal (i.e. is not all zeroes)
     * @return True if the CVM Results is supported by the Terminal, false otherwise
     */
    public boolean isSupportedByTheTerminal() {
        for (final byte mCvmResult : mCvmResults) {
            if (mCvmResult != 0x00) {
                return true;
            }
        }
        return false;
    }
}
