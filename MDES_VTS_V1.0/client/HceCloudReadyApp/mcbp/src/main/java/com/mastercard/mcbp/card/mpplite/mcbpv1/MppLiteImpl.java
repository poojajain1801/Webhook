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

package com.mastercard.mcbp.card.mpplite.mcbpv1;

import com.mastercard.mcbp.card.BusinessLogicTransactionInformation;
import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.mobilekernel.CryptogramInput;
import com.mastercard.mcbp.card.mobilekernel.TransactionOutput;
import com.mastercard.mcbp.card.mpplite.MppLite;
import com.mastercard.mcbp.card.mpplite.apdu.emv.DolRequestList;
import com.mastercard.mcbp.card.mpplite.mcbpv1.credentials.TransactionCredentialsManager;
import com.mastercard.mcbp.card.mpplite.mcbpv1.listener.ContactlessTransactionListener;
import com.mastercard.mcbp.card.mpplite.mcbpv1.state.MppLiteStateContext;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.transactiondecisionmanager.AdviceManager;
import com.mastercard.mcbp.transactiondecisionmanager.ConsentManager;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DsrpIncompatibleProfile;
import com.mastercard.mcbp.utils.exceptions.mpplite.InvalidState;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;
import java.util.List;

/**
 * {@inheritDoc}
 */
final class MppLiteImpl implements MppLite {
    /**
     * MppLite State Context
     */
    private final MppLiteStateContext mMppLiteStateContext;

    /**
     * McbpLogger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /***
     * Static factory method to build an MPP Lite with MCBP 1.0+ functionality
     *
     * @param mppLiteModule                   The MPP Lite module of the Card Profile
     * @param credentialsManager              The call back interface to manage credentials
     * @param chValidator                     The call back interface to manage user authentication
     * @param consentManager                  The call back interface to manage consent
     * @param adviceManager                   The advice manager that the MPP Lite will call back
     *                                        for transaction outcome
     * @param additionalPdolList              List of additional PDOLs to be requested by the MPP
     *                                        Lite to the POS
     * @param additionalUdolList              List of additional UDOLs to be requested by the MPP
     *                                        Lite to the POS
     * @param maskMchipInAipForUsTransactions Flag indicating whether M-CHIP support should be
     *                                        masked for US transactions
     * @return An instance of the MPP Lite configured to support MCBP 1.0+ functionality
     */
    static MppLiteImpl buildMppLiteForMcbp1Plus(
            final MppLiteModule mppLiteModule,
            final TransactionCredentialsManager credentialsManager,
            final ChValidator chValidator,
            final ConsentManager consentManager,
            final AdviceManager adviceManager,
            final List<DolRequestList.DolItem> additionalPdolList,
            final List<DolRequestList.DolItem> additionalUdolList,
            final boolean maskMchipInAipForUsTransactions) {
        return new MppLiteImpl(mppLiteModule,
                               credentialsManager,
                               chValidator,
                               consentManager,
                               adviceManager,
                               additionalPdolList,
                               additionalUdolList,
                               maskMchipInAipForUsTransactions);
    }

    /**
     * Build the MPP Lite to emulate MCBP 1.0 specification with the exception of transit support
     * that will be ON by default. An implementation may disable transit by using the Advice at
     * the Wallet/Card level
     *
     * @param mppLiteModule      The MPP Lite module of the Card Profile
     * @param credentialsManager The call back interface to manage credentials
     * @param chValidator        The call back interface to manage user authentication
     * @param consentManager     The call back interface to manage consent
     * @return An MPP Lite that is configured to operate as MCBP 1.0 specs with the additional
     * support for transit.
     */
    static MppLiteImpl buildMppLiteV1(final MppLiteModule mppLiteModule,
                                      final TransactionCredentialsManager credentialsManager,
                                      final ChValidator chValidator,
                                      final ConsentManager consentManager) {
        return new MppLiteImpl(mppLiteModule,
                               credentialsManager,
                               chValidator,
                               consentManager);

    }

    private MppLiteImpl(final MppLiteModule mppLiteModule,
                        final TransactionCredentialsManager credentialsManager,
                        final ChValidator cardholderValidator,
                        final ConsentManager consentManager,
                        final AdviceManager adviceManager,
                        final List<DolRequestList.DolItem> additionalPdolList,
                        final List<DolRequestList.DolItem> additionalUdolList,
                        final boolean maskMchipInAipForUsTransactions) {
        mMppLiteStateContext = new MppLiteStateContext(mppLiteModule,
                                                       credentialsManager,
                                                       cardholderValidator,
                                                       consentManager,
                                                       adviceManager,
                                                       buildAdditionalPdolList(additionalPdolList),
                                                       buildAdditionalUdolList(additionalUdolList),
                                                       maskMchipInAipForUsTransactions);
        mMppLiteStateContext.setInitializedState();
    }

    private MppLiteImpl(final MppLiteModule mppLiteModule,
                        final TransactionCredentialsManager credentialsManager,
                        final ChValidator cardholderValidator,
                        final ConsentManager consentManager) {
        mMppLiteStateContext = new MppLiteStateContext(mppLiteModule,
                                                       credentialsManager,
                                                       cardholderValidator,
                                                       consentManager,
                                                       null,
                                                       null,
                                                       null,
                                                       true);
        mMppLiteStateContext.setInitializedState();
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final void startRemotePayment()
            throws DsrpIncompatibleProfile {
        mMppLiteStateContext.getState().startRemotePayment();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void startContactLessPayment(final ContactlessTransactionListener listener,
                                              final BusinessLogicTransactionInformation trxInfo)
            throws MppLiteException, InvalidInput {
        mMppLiteStateContext.getState().startContactLessPayment(listener, trxInfo);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void cancelPayment() throws MppLiteException {
        try {
            mMppLiteStateContext.getState().cancelPayment();
        } catch (InvalidState invalidState) {
            // This seems to happen when an exception is raised and cancel payment is called within
            // the MPP Lite. At the same time processOnDeactivate is received.
            // FIXME: For now we ignore it, but we could improve the state machine to account for
            // those cases.
            mLogger.d(invalidState.getMessage());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final TransactionOutput createRemoteCryptogram(final CryptogramInput input) {
        try {
            return mMppLiteStateContext.getState().createRemoteCryptogram(input);
        } catch (final McbpCryptoException | InvalidInput e) {
            mLogger.d(e.getMessage());
            return null;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void stop() {
        mMppLiteStateContext.getState().stop();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray processApdu(final ByteArray apdu) {
        return ByteArray.of(mMppLiteStateContext.getState().processApdu(apdu.getBytes()));
    }

    /**
     * Utility function to build the additional PDOL List as per MCBP 1.0+ specification
     *
     * @param pdolListFromCard Additional PDOLs requested for this card
     * @return The updated list of PDOLs
     */
    private List<DolRequestList.DolItem> buildAdditionalPdolList(
            final List<DolRequestList.DolItem> pdolListFromCard) {
        final List<DolRequestList.DolItem> additionalPdolList = new ArrayList<>();

        // Mobile Support Indicator (for MCHIP as Magstripe has it in the UDOL)
        additionalPdolList.add(new DolRequestList.DolItem("9F7E", 0x01));
        // Terminal Risk Management Data
        additionalPdolList.add(new DolRequestList.DolItem("9F1D", 0x08));

        // Add all the items the Card has told us to add
        if (pdolListFromCard != null) {
            additionalPdolList.addAll(pdolListFromCard);
        }

        return additionalPdolList;
    }

    /**
     * Utility function to build the additional UDOL List as per MCBP 1.0+ specification
     *
     * @param udolListFromCard Additional UDOLs requested for this card
     * @return The updated list of UDOLs
     */

    private List<DolRequestList.DolItem> buildAdditionalUdolList(
            final List<DolRequestList.DolItem> udolListFromCard) {
        final List<DolRequestList.DolItem> additionalUdolList = new ArrayList<>();

        // Terminal Capabilities
        additionalUdolList.add(new DolRequestList.DolItem("9F33", 0x03));

        // Add all the items the Card has told us to add
        if (udolListFromCard != null) {
            additionalUdolList.addAll(udolListFromCard);
        }

        return additionalUdolList;
    }
}
