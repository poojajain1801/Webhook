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

import com.mastercard.mcbp.card.mpplite.apdu.emv.GenerateAcCommandApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.apdu.emv.ComputeCcCommandApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.ContactlessContext;

/**
 * Factory class to build Card Risk Management objects
 */
public enum CardRiskManagementFactory {
    INSTANCE;

    /**
     * * Build an Card Risk Management object for Remote Payment transactions
     *
     * @param cvr                           The Cardholder Verification Results for this transaction
     * @param ciacDecline                   The CIAC Decline as in the MPP Lite profile
     * @param transactionCredentialsManager The Transaction Credentials Manager
     * @return The Card Risk Management Interface
     */
    public static CardRiskManagement forRemotePayment(
            final CardVerificationResults cvr,
            final byte[] ciacDecline,
            final TransactionCredentialsManager transactionCredentialsManager) {
        return new RemotePaymentCardRiskManagement(cvr,
                                                   ciacDecline,
                                                   transactionCredentialsManager);
    }

    /**
     * Build an Card Risk Management object for Magstripe transactions
     *
     * @param commandApdu                   The Magstripe Command APDU
     * @param context                       The contactless context that is used to retrieve
     *                                      information about the business logic values for
     *                                      amount and currency, if any
     * @param ciacDeclineOnPpms             The CIAC Decline on PPMS as per Card Profile
     * @param countryCode                   The Country Code as per Card Profile Risk Management
     *                                      Data
     * @param transactionCredentialsManager The Transaction Credentials Manager
     * @return The Card Risk Management Interface
     */
    public static CardRiskManagement forMagstripe(
            final ComputeCcCommandApdu commandApdu,
            final ContactlessContext context,
            final byte[] ciacDeclineOnPpms,
            final byte[] countryCode,
            final TransactionCredentialsManager transactionCredentialsManager) {
        return new MagstripeCardRiskManagement(commandApdu,
                                               context,
                                               ciacDeclineOnPpms,
                                               countryCode,
                                               transactionCredentialsManager);
    }

    /**
     * Build an Card Risk Management object for M-Chip transactions
     *
     * @param commandApdu                   The Generate AC Command APDU
     * @param context                       The contactless context that is used to retrieve
     *                                      information about the business logic values for
     *                                      amount and currency, if any
     * @param cvr                           The Cardholder Verification Result for this transaction
     * @param ciacDecline                   The CIAC Decline as per card profile
     * @param transactionCredentialsManager The Transaction Credentials Manager
     * @return The Card Risk Management interface
     */
    public static CardRiskManagement forMchip(
            final GenerateAcCommandApdu commandApdu,
            final ContactlessContext context,
            final CardVerificationResults cvr,
            final byte[] ciacDecline,
            final TransactionCredentialsManager transactionCredentialsManager) {
        return new MchipCardRiskManagement(commandApdu,
                                           context,
                                           cvr,
                                           ciacDecline,
                                           transactionCredentialsManager);
    }
}
