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

import com.mastercard.mcbp.card.mpplite.apdu.emv.GetProcessingOptionsCommandApdu;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ResponseApduFactory;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;
import com.mastercard.mcbp.card.profile.AlternateContactlessPaymentData;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.Arrays;

/**
 * Utility SingleTone object to build the GPO Response
 */
public enum GetProcessingOptions {
    INSTANCE;

    // Constants - Get Processing Options
    public static final byte GPO_AIP_OFFSET = 4;
    public static final byte GPO_AIP_LENGTH = 2;
    public static final byte[] US_COUNTRY_CODE = new byte[]{0x08, 0x40};
    public static final byte[] NO_COUNTRY_CODE = new byte[]{0x00, 0x00};
    private static final char GPO_AIP_MASK = 0xFF7F;

    /**
     * Working variable - Store the current Contactless Transaction context
     */
    private static ContactlessContext sContext = null;

    /***
     * Initialize the single tone object to handle the response. Working variables are populated
     * accordingly
     *
     * @param context The Contactless Transaction Context
     */
    private static void initialize(final ContactlessContext context) {
        sContext = context;
    }

    /**
     * Build the GPO Response APDU
     *
     * @param apdu    The GPO Command APDU
     * @param context The Contactless Transaction Context, which is modified before returning the
     *                GPO Response (AIP and new state)
     * @return The GPO Response APDU
     * @throws MppLiteException in case of errors
     * @since 1.0.3
     */
    public static synchronized byte[] response(final GetProcessingOptionsCommandApdu apdu,
                                        final ContactlessContext context) {
        initialize(context);
        // GPO.1.1 to GPO.2.2 or GPO.2.6 are performed when constructing the Command Apdu

        // GPO.2.4 or GPO.2.8
        setPdolValue(apdu);

        final byte[] response;
        try {
            // GPO.2.3 or GPO.2.7 and GPO.2.9
            response = ResponseApduFactory.successfulProcessing(buildGpoResponse(apdu));

        } catch (InvalidInput | NullPointerException e) {
            sContext = null;
            throw new MppLiteException("Invalid GPO Response");
        }

        // GPO.2.10
        context.setContactlessInitiatedState();
        sContext = null;
        return response;
    }

    /***
     * Build the GPO Response by setting the proper AIP value
     *
     * @param apdu The GPO Command APDU
     * @return The data to be included in the GPO Response
     */
    private static byte[] buildGpoResponse(final GetProcessingOptionsCommandApdu apdu) {
        // Get the GPO response as it is in the profile
        final ByteArray gpoResponseProfile = getGpoResponse();
        final int responseLength = getGpoResponse().getLength();

        // Copy the response that was in the profile
        final byte[] gpoResponse = new byte[responseLength];
        System.arraycopy(gpoResponseProfile.getBytes(), 0, gpoResponse, 0, responseLength);

        if (isTerminalCountryCodeUsOrEmpty(apdu.getTerminalCountryCode())) {
            // If the terminal is US or a bad one that did not returned any country code, try
            // to mask M-CHIP support
            if (sContext.isMaskMchipInAipForUsTransactions() && isMagStripeSupportedInProfile()) {
                final byte[] terminalRiskManagementData = apdu.getTerminalRiskManagementData();
                if (isTerminalRiskManagementDataEmpty(terminalRiskManagementData)) {
                    maskMchipSupportInAip(gpoResponse);
                }
            }
        }

        // Set the AIP in the Context
        sContext.getTransactionContext().setAip(ByteArray.of(getAipValue(gpoResponse)));

        return gpoResponse;
    }

    /**
     * Utility function to mask the M-CHIP support in the AIP
     *
     * @param gpoResponse The GPO Response content is modified accordingly to the masking operation
     */
    private static void maskMchipSupportInAip(final byte[] gpoResponse) {
        gpoResponse[GPO_AIP_OFFSET] &= (GPO_AIP_MASK >> 8);
        gpoResponse[GPO_AIP_OFFSET + 1] &= (GPO_AIP_MASK & 0x00FF);
    }

    /**
     * Check whether the terminal is a US Terminal or a malformed terminal
     *
     * @param terminalCountryCode The Terminal Country Code
     * @return True if the terminal country code information is available (not null) and it is
     *         either US or all zeroes. False otherwise.
     */
    private static boolean isTerminalCountryCodeUsOrEmpty(final byte[] terminalCountryCode) {
        // We do not have enough information. This tag has not been requested probably
        return !(terminalCountryCode == null || terminalCountryCode.length == 0) &&
               (Arrays.equals(terminalCountryCode, US_COUNTRY_CODE) ||
                Arrays.equals(terminalCountryCode, NO_COUNTRY_CODE));
    }


    /**
     * Check whether MagStripe data is present in the profile or not
     *
     * @return True if the Magstripe data is found in the profile, false otherwise.
     */
    private static boolean isMagStripeSupportedInProfile() {
        final ContactlessPaymentData contactlessPaymentData =
                sContext.getCardProfile().getContactlessPaymentData();

        final ByteArray ciacDeclineOnPpms = contactlessPaymentData.getCiacDeclineOnPpms();
        final ByteArray pinIvCvc3Track2Data = contactlessPaymentData.getPinIvCvc3Track2();

        return !(ciacDeclineOnPpms == null   || ciacDeclineOnPpms.isEmpty() ||
                 pinIvCvc3Track2Data == null || pinIvCvc3Track2Data.isEmpty());
    }

    /**
     * Utility function to generate the final AIP value to be added to the transaction context
     *
     * @param gpoResponse The GPO Response as prepared so far
     * @return The AIP value for the current transaction
     */
    private static byte[] getAipValue(final byte[] gpoResponse) {
        // GPO.2.7 or GPO.2.3
        final byte[] aip = new byte[GPO_AIP_LENGTH];
        System.arraycopy(gpoResponse, GPO_AIP_OFFSET, aip, 0, GPO_AIP_LENGTH);
        return aip;
    }

    /**
     * Get the GPO Response as it is stored in the Card Profile
     *
     * @return The GPO Response as stored in the card profile
     */
    private static ByteArray getGpoResponse() {
        final MppLiteModule profile = sContext.getCardProfile();
        final ContactlessPaymentData contactlessPaymentData = profile.getContactlessPaymentData();

        final ByteArray gpoResponse;

        if (sContext.getTransactionContext().isAlternateAid()) {
            final AlternateContactlessPaymentData alternateContactlessPaymentData =
                    contactlessPaymentData.getAlternateContactlessPaymentData();
            gpoResponse = alternateContactlessPaymentData.getGpoResponse();
        } else {
            gpoResponse = contactlessPaymentData.getGpoResponse();
        }
        return gpoResponse;
    }

    /**
     * Set the PDOL Values in the transaction context
     */
    private static void setPdolValue(GetProcessingOptionsCommandApdu apdu) {
        final ContactlessTransactionContext trxContext = sContext.getTransactionContext();
        trxContext.setPdolData(apdu.getPdol());
    }

    /**
     * Utility function to check whether the Risk Management Data Information is empty
     *
     * @param riskManagementData The Risk Management Data as received in the GPO C-APDU
     * @return True if the Terminal Risk Management Data is null, empty or all zeroes.
     */
    private static boolean isTerminalRiskManagementDataEmpty(final byte[] riskManagementData) {
        if (riskManagementData == null || riskManagementData.length == 0) {
            // We do not have this data element, or it is empty
            return true;
        }
        byte[] zeroes = new byte[riskManagementData.length];
        return Arrays.equals(riskManagementData, zeroes);
    }
}
