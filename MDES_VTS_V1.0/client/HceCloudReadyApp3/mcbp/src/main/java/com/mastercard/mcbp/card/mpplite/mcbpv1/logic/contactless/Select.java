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

package com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless;

import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.mpplite.apdu.emv.SelectCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.SelectResponseApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.card.profile.AlternateContactlessPaymentData;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.Arrays;
import java.util.List;

/**
 * Single Tone Utility Object to build the SELECT Response APDU
 */
public class Select {
    enum AidType {
        PRIMARY_AID,
        ALTERNATE_AID,
        PPSE_AID,
        UNKNOWN
    }

    /**
     * The PPSE AID. This is a constant as it is not supposed to change
     */
    static final byte[] PPSE_AID = ByteArray.of("325041592E5359532E4444463031").getBytes();

    /**
     * The Select C-APDU
     */
    private final SelectCommandApdu mCommandApdu;

    /**
     * The Contactless Payment data as in the card profile
     */
    private final ContactlessPaymentData mContactlessPaymentData;

    /**
     * The Alternate Contactless Payment data as in the card profile, if present. Null otherwise
     */
    private final AlternateContactlessPaymentData mAlternateContactlessPaymentData;

    /**
     * The Contactless Context
     */
    private final ContactlessContext mContext;

    /**
     * The Primary AID as read from the profile
     */
    private final byte[] mPrimaryAid;

    /**
     * The Alternate AID as read from the profile, if any
     */
    private final byte[] mAlternateAid;

    /**
     * The type of the AID requested in this C-APDU
     */
    private final AidType mAidType;

    /**
     * Build a Select object that will be used to generate the SELECT APDU response
     *
     * @param commandApdu The Select Command APDU
     * @param profile     The MPP Lite Profile which is used to build the response and initialize
     *                    the
     *                    context appropriately
     * @param context     The Contactless Transaction Context, which is modified before returning
     *                    the
     *                    Select Response (new state)
     */
    public Select(final SelectCommandApdu commandApdu,
                  final MppLiteModule profile,
                  final ContactlessContext context) {
        this.mCommandApdu = commandApdu;
        this.mContactlessPaymentData = profile.getContactlessPaymentData();
        this.mAlternateContactlessPaymentData =
                mContactlessPaymentData.getAlternateContactlessPaymentData();
        this.mContext = context;

        // Initialize both primary and alternate Aid
        this.mPrimaryAid = mContactlessPaymentData.getAid().getBytes();

        if (mAlternateContactlessPaymentData != null) {
            final ByteArray alternateAid = mAlternateContactlessPaymentData.getAid();
            mAlternateAid = (alternateAid == null) ? null : alternateAid.getBytes();
        } else {
            mAlternateAid = null;
        }

        this.mAidType = determineAid();
    }

    private AidType determineAid() {
        final byte[] fileName = mCommandApdu.getFileName();
        if (isAidMatching(fileName, PPSE_AID)) {
            return AidType.PPSE_AID;
        }
        if (isAidMatching(fileName, mPrimaryAid)) {
            return AidType.PRIMARY_AID;
        }
        if (isAidMatching(fileName, mAlternateAid)) {
            return AidType.ALTERNATE_AID;
        }
        return AidType.UNKNOWN;
    }

    /**
     * Build the SELECT Response APDU
     *
     * @return The SELECT Response APDU
     * @throws MppLiteException in case of errors
     * @since 1.0.6a
     */
    public final byte[] response() {
        switch (mAidType) {
            case PPSE_AID:
                // SEL.1.2 and SEL.1.3
                return getPpseResponse();
            case PRIMARY_AID:
                // SEL.1.4, SEL.1.5, SEL.1.6, SEL.1.12
                return getPrimaryAidResponse(mContext.getAdditionalPdolList());
            case ALTERNATE_AID:
                // SEL 1.8, SEL.1.10, SEL.1.11
                return getAlternateAidResponse(mContext.getAdditionalPdolList());
            case UNKNOWN:
            default:
                // SEL.1.9
                mContext.setContactlessNotSelectedState();
                return ResponseApduFactory.fileNotFound();
        }
    }

    /**
     * Utility function to build the PPSE Response
     */
    private byte[] getPpseResponse() {
        mContext.setContactlessNotSelectedState();
        final byte[] ppseFci = mContactlessPaymentData.getPpseFci().getBytes();
        final byte[] response;

        // SEL.1.3
        try {
            response = ResponseApduFactory.successfulProcessing(ppseFci);
        } catch (InvalidInput e) {
            // Something went wrong, we will throw an MPP Lite exception
            throw new MppLiteException(e.getMessage());
        }
        return response;
    }

    /**
     * Utility function to build the Primary AID Response
     */
    private byte[] getPrimaryAidResponse(final List<DolRequestList.DolItem> pdolAdditionalItems) {
        // SEL.1.5
        mContext.getTransactionContext().setAlternateAid(false);

        // SEL.1.6
        final byte[] paymentFci = mContactlessPaymentData.getPaymentFci().getBytes();
        return prepareAidResponse(pdolAdditionalItems, paymentFci);
    }

    /**
     * Utility function to build the Alternate AID Response
     */
    private byte[] getAlternateAidResponse(final List<DolRequestList.DolItem> pdolAdditionalItems) {
        // SEL.1.5
        mContext.getTransactionContext().setAlternateAid(true);

        // SEL.1.10
        final byte[] paymentFci = mAlternateContactlessPaymentData.getPaymentFci().getBytes();
        return prepareAidResponse(pdolAdditionalItems, paymentFci);
    }

    /**
     * Generic utility function to build the AID response given the Payment FCI and the additional
     * PDOL List
     */
    private byte[] prepareAidResponse(final List<DolRequestList.DolItem> pdolAdditionalItems,
                                      final byte[] paymentFci) {
        // SEL.1.11 or SEL.1.6
        final SelectResponseApdu selectResponseApdu =
                SelectResponseApdu.of(paymentFci, pdolAdditionalItems);

        if (selectResponseApdu == null || selectResponseApdu.getPdolList() == null) {
            // Something went wrong as we have got an empty SELECT R-APDU with an empty PDOL List
            throw new MppLiteException("Unable to build a valid SELECT R-APDU");
        }

        mContext.getTransactionContext().setPdolList(selectResponseApdu.getPdolList());

        // SEL.1.12 - Set the next internal state before returning
        mContext.setContactlessSelectedState();
        return selectResponseApdu.getBytes();
    }

    /**
     * Utility function to check if the AID is matching with a given filename
     *
     * @param fileName The Filename as received in the C-APDU
     * @param aid      The AID value as in the profile
     * @return True if the fileName and aid are equal. False otherwise.
     */
    private boolean isAidMatching(final byte[] fileName, final byte[] aid) {
        return !(fileName == null || aid == null) && Arrays.equals(fileName, aid);
    }
}
