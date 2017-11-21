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

import com.mastercard.mcbp.transactiondecisionmanager.advice.Reason;
import com.mastercard.mobile_api.utils.Tlv;

import java.util.List;

/**
 * POS Cardholder Interaction Information is modeled as an object to allow high level utility
 * functions
 */
public class PosCardholderInteractionInformation {
    /**
     * The length of the POSCII data
     */
    public final static int POSCII_LENGTH = 3;

    /**
     * The POSCII tag when used in TLV format
     */
    public final static byte[] POSCII_TAG = new byte[]{(byte) 0xDF, 0x4B};

    /**
     * The POSCII data
     */
    private final byte[] mPoscii;

    /**
     * API to update POSCII information based on the Reasons provided by wallet
     *
     * @param walletReasons Reasons provided by wallet for assessment
     */
    static PosCardholderInteractionInformation using(final List<Reason> walletReasons) {
        PosCardholderInteractionInformation poscii = new PosCardholderInteractionInformation();
        for (Reason walletReason : walletReasons) {
            switch (walletReason) {
                case CONTEXT_NOT_MATCHING:
                    poscii.indicateContextIsConflicting();
                    break;
                case MISSING_CD_CVM:
                    poscii.indicateCdCvmRequired();
                    break;
                case MISSING_CONSENT:
                    poscii.indicateConsentRequired();
                    break;
                case CREDENTIALS_NOT_ACCESSIBLE_WITHOUT_CVM:
                    poscii.indicateCdCvmRequired();
                    break;
                default:
                    break;
            }
        }
        return poscii;
    }

    static PosCardholderInteractionInformation using(final boolean isCvmEntered,
                                                     final List<Reason> walletReasons
    ) {
        PosCardholderInteractionInformation poscii = using(walletReasons);
        //TODO: Discuss it with the specification team
        if (isCvmEntered && !walletReasons.contains(Reason.MISSING_CD_CVM)) {
            poscii.indicateOfflinePinVerificationSuccessful();
        }
        return poscii;
    }

    /**
     * API to generate POSCII information of Approved M/Chip transaction
     *
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forApproveMchip() {
        return null;
    }

    /**
     * API to generate POSCII information of Aborted M/Chip transaction
     *
     * @param walletReasons Reasons provided by wallet for assessment
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forAbortMchip(
            final List<Reason> walletReasons) {
        return PosCardholderInteractionInformation.using(walletReasons);
    }

    /**
     * API to generate POSCII information of Declined M/Chip transaction
     *
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forDeclineMchip() {
        return PosCardholderInteractionInformation.withEmptyValues();
    }

    private static PosCardholderInteractionInformation withEmptyValues() {
        return new PosCardholderInteractionInformation();
    }

    /**
     * API to generate POSCII information of Error M/Chip transaction
     *
     * @param walletReasons Reasons provided by wallet for assessment
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forErrorMchip(
            final List<Reason> walletReasons) {
        return PosCardholderInteractionInformation.using(walletReasons);
    }

    /**
     * API to generate POSCII information of Authentication requested M/Chip transaction
     *
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forAuthenticateMchip() {
        return PosCardholderInteractionInformation.withEmptyValues();
    }


    /**
     * API to generate POSCII information of Approved Magstripe transaction
     *
     * @param walletReasons Reasons provided by wallet for assessment
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forApproveMagstripe(
            final boolean isCvmEntered, final List<Reason> walletReasons) {
        return PosCardholderInteractionInformation.using(isCvmEntered, walletReasons);
    }

    /**
     * API to generate POSCII information of Aborted Magstripe transaction
     *
     * @param walletReasons Reasons provided by wallet for assessment
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forAbortMagstripe(
            final boolean isCvmEntered, final List<Reason> walletReasons) {
        return PosCardholderInteractionInformation.using(isCvmEntered, walletReasons);
    }

    /**
     * API to generate POSCII information of Declined Magstripe transaction
     *
     * @param walletReasons Reasons provided by wallet for assessment
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forDeclineMagstripe(
            final boolean isCvmEntered, final List<Reason> walletReasons) {
        return setPosciiForDeclineOrErrorMagstripe(isCvmEntered, walletReasons);
    }

    /**
     * API to generate POSCII information of Error Magstripe transaction
     *
     * @param walletReasons Reasons provided by wallet for assessment
     * @return instance of PosCardholderInteractionInformation with appropriate bits set based on
     * wallet reasons
     */
    public static PosCardholderInteractionInformation forErrorMagstripe(
            final boolean isCvmEntered, final List<Reason> walletReasons) {
        return setPosciiForDeclineOrErrorMagstripe(isCvmEntered, walletReasons);
    }

    /**
     * Utility function to set the POSCII in case of Decline and Error.
     * In those cases we check for context conflict but we don't indicate missing cd cvm.
     */
    private static PosCardholderInteractionInformation setPosciiForDeclineOrErrorMagstripe(
            final boolean isCvmEntered, final List<Reason> walletReasons) {
        PosCardholderInteractionInformation poscii = new PosCardholderInteractionInformation();
        if (isCvmEntered && !walletReasons.contains(Reason.MISSING_CD_CVM)) {
            poscii.indicateOfflinePinVerificationSuccessful();
        }
        if (walletReasons.contains(Reason.UNSUPPORTED_TRANSIT)) {
            return poscii;
        }
        if (walletReasons.contains(Reason.CONTEXT_NOT_MATCHING)) {
            poscii.indicateContextIsConflicting();
        }
        return poscii;
    }

    /**
     * Default constructor. An empty POSCII is created
     */
    PosCardholderInteractionInformation() {
        this.mPoscii = new byte[POSCII_LENGTH];
    }

    /**
     * Indicate Offline Pin Verification Successful
     */
    final void indicateOfflinePinVerificationSuccessful() {
        mPoscii[1] |= 0x10;
    }

    /**
     * Indicate Context is Conflicting
     */
    final void indicateContextIsConflicting() {
        mPoscii[1] |= 0x08;
    }

    /**
     * Indicate Offline PIN change required
     */
    final void indicateOfflinePinChangeRequired() {
        mPoscii[1] |= 0x04;
    }

    /**
     * Indicate CD CVM required
     */
    final void indicateCdCvmRequired() {
        mPoscii[1] |= 0x01;
    }

    /**
     * Indicate Consent required
     */
    final void indicateConsentRequired() {
        mPoscii[1] |= 0x02;
    }

    /**
     * Set the POSCII to empty (e.g. all 00s)
     */
    final void clear() {
        mPoscii[1] = 0x00;
    }

    /**
     * Get the POSCII as byte array
     *
     * @return The content of the POSCII as byte array
     */
    final byte[] getBytes() {
        return mPoscii;
    }

    /**
     * Get the POSCII in TLV format
     *
     * @return The POSCII formatted as TLV
     */
    public final byte[] getTlv() {
        return Tlv.create(POSCII_TAG, mPoscii);
    }
}
