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

package com.mastercard.mcbp.api;

import android.util.Log;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.mdes.RemoteManagementRequestType;
import com.mastercard.mcbp.userinterface.MakeDefaultListener;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.McbpCardNotFound;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.api.BuildConfig;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.List;

/**
 * API class used for interacting with a card.
 */
public class McbpCardApi extends CommonMcbpCardApi {

    /**
     * Logger
     */
    private static final McbpLogger sLogger =
            McbpLoggerFactory.getInstance().getLogger(McbpCardApi.class);

    /**
     * Change the PIN for this card.Format of the pin is numeric.
     * MINIMUM LENGTH ALLOWED : 4
     * MAXIMUM LENGTH ALLOWED : 8
     * This length enforcement is according to
     * MasterCardCloudBasedPayments_IssuerCryptographicAlgorithms_v1-1 (Section 3.4)
     *
     * @param card   Instance of {@link McbpCard} to change the PIN for.
     * @param oldPin The entered old PIN in byte[] form.
     * @param newPin The entered new PIN in byte[] form.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @deprecated since 1.0.4
     */
    public static void changePin(McbpCard card,
                                 byte[] oldPin,
                                 byte[] newPin) throws AlreadyInProcessException {
        if (BuildConfig.WALLET_PIN) {
            throw new IllegalStateException("Unsupported operation in Wallet PIN mode");
        }

        if (oldPin != null) {
            // The old PIN is not null, thus let's check that it is of the right format
            if (oldPin.length < MINIMUM_MOBILE_PIN_LENGTH
                || oldPin.length > MAXIMUM_MOBILE_PIN_LENGTH) {
                throw new IllegalArgumentException("Invalid old PIN Length: " + oldPin.length);
            }
        }

        // Finally let's check that new PIN is of the right format
        if (newPin == null || newPin.length < MINIMUM_MOBILE_PIN_LENGTH
            || newPin.length > MAXIMUM_MOBILE_PIN_LENGTH) {
            throw new IllegalArgumentException("Invalid new PIN Length: "
                                               + ((newPin != null) ? newPin.length : "null"));
        }
        RemoteManagementServices.changePin((card == null ? null : card.getDigitizedCardId()),
                                           ((oldPin == null) ? null : ByteArray.of(oldPin)),
                                           ByteArray.of(newPin));
    }

    /**
     * Change the PIN for this card. Format of the pin is numeric.
     * MINIMUM LENGTH ALLOWED : 4
     * MAXIMUM LENGTH ALLOWED : 8
     * This length enforcement is according to
     * MasterCardCloudBasedPayments_IssuerCryptographicAlgorithms_v1-1 (Section 3.4)
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to change the pin.
     * @param oldPin               The entered old PIN in byte[] form.
     * @param newPin               The entered new PIN in byte[] form.
     * @throws McbpCardNotFound
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void changePin(final String tokenUniqueReference,
                                 final byte[] oldPin,
                                 final byte[] newPin) throws AlreadyInProcessException {
        if (BuildConfig.WALLET_PIN) {
            throw new IllegalStateException("Unsupported operation in Wallet PIN mode");
        }
        final McbpInitializer mcbpInitializer = McbpInitializer.getInstance();
        String digitizedCardId;
        try {
            digitizedCardId = mcbpInitializer.getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
        if (oldPin != null) {
            // The old PIN is not null, thus let's check that it is of the right format
            if (oldPin.length < MINIMUM_MOBILE_PIN_LENGTH
                || oldPin.length > MAXIMUM_MOBILE_PIN_LENGTH) {
                throw new IllegalArgumentException("Invalid old PIN Length: " + oldPin.length);
            }
        }

        // Finally let's check that new PIN is of the right format
        if (newPin == null || newPin.length < MINIMUM_MOBILE_PIN_LENGTH
            || newPin.length > MAXIMUM_MOBILE_PIN_LENGTH) {
            throw new IllegalArgumentException("Invalid new PIN Length: "
                                               + ((newPin != null) ? newPin.length : "null"));
        }
        RemoteManagementServices.changePin(digitizedCardId,
                                           ((oldPin == null) ? null : ByteArray.of(oldPin)),
                                           ByteArray.of(newPin));
    }

    /**
     * Set the initial PIN for the card.Format of the pin is numeric.
     * MINIMUM LENGTH ALLOWED : 4
     * MAXIMUM LENGTH ALLOWED : 8
     * This length enforcement is according to
     * MasterCardCloudBasedPayments_IssuerCryptographicAlgorithms_v1-1 (Section 3.4)
     *
     * @param card   Instance of {@link McbpCard} to set the PIN for.
     * @param newPin The entered new PIN in byte[] form.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @deprecated since 1.0.4
     */
    public static void setPin(McbpCard card, byte[] newPin) throws AlreadyInProcessException {
        changePin(card, null, newPin);
    }

    /**
     * Set the initial PIN for the card.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to set pin.
     * @param newPin               The entered new PIN in byte[] form.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void setPin(final String tokenUniqueReference, final byte[] newPin)
            throws AlreadyInProcessException {
        changePin(tokenUniqueReference, null, newPin);
    }


    /**
     * Completely remove card from the CMS and the local database.
     *
     * @param card Instance of {@link McbpCard} to completely
     *             remove.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @deprecated since 1.0.4
     */
    public static void deleteCard(McbpCard card,
                                  boolean isDemoMode) throws AlreadyInProcessException {
        String tokenUniqueReference = null;
        try {
            tokenUniqueReference =
                    McbpInitializer.getInstance().getLdeRemoteManagementService()
                                   .getTokenUniqueReferenceFromCardId(
                                           card.getDigitizedCardId());
        } catch (InvalidInput invalidInput) {
            sLogger.d(Log.getStackTraceString(invalidInput));
        }
        // If this card is the default card for contactless or remote payments, then we need to
        // unset the default
        unsetIfDefaultCard(tokenUniqueReference);

        // Remove all the SUKs from the remote service
        remoteWipeSuksForCard(tokenUniqueReference);

        if (!isDemoMode) {
            RemoteManagementServices.delete(card.getDigitizedCardId());
        }

        // Remove the card from the remote service
        remoteWipeCard(tokenUniqueReference);
    }


    /**
     * Completely remove card from the CMS and the local database.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to be delete.
     * @throws McbpCardNotFound
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void deleteCard(final String tokenUniqueReference, final boolean isDemoMode)
            throws AlreadyInProcessException {
        LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer.getInstance()
                                                                               .getLdeRemoteManagementService();

        // If this card is the default card for contactless or remote payments, then we need to
        // unset the default

        unsetIfDefaultCard(tokenUniqueReference);

        // Remove all the SUKs from the remote service
        remoteWipeSuksForCard(tokenUniqueReference);

        try {
            String digitizedCardId = ldeRemoteManagementService.getCardIdFromTokenUniqueReference
                    (tokenUniqueReference);
            if (isDemoMode) {
                // Remove the card from the remote service
                remoteWipeCard(tokenUniqueReference);
                ldeRemoteManagementService.deleteTokenUniqueReference(digitizedCardId);
            } else {
                RemoteManagementServices.delete(digitizedCardId);
            }
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
    }

    /**
     * Get status of last requested task. Currently it is only supported for change pin request.
     *
     * @param requestType Any supported type from {@link RemoteManagementRequestType}
     *                    The return value is provided through a callback.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void getTaskStatus(final RemoteManagementRequestType requestType)
            throws AlreadyInProcessException {
        RemoteManagementServices.getTaskStatus(requestType);
    }

    /**
     * This function returns the last 4 digits of the PAN for display by the wallet
     *
     * @param tokenUniqueReference tokenUniqueReference of the card for which the PAN is requested
     * @return String last 4 digits of the PAN for the wallet to display
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    public static String getDisplayablePanDigits(final String tokenUniqueReference)
            throws McbpCardNotFound {
        try {
            final LdeMcbpCardService ldeMcbpCardService =
                    McbpInitializer.getInstance().getLdeMcbpCardService();

            return ldeMcbpCardService.getDisplayablePanDigits(tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound(
                    "Card not found for token unique reference " + tokenUniqueReference);
        }

    }


    /**
     * The function enables the wallet to inform the SDK that the card is suspended.
     * The wallet receives this information from the Payment App Server.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to be suspended
     * @return true if status is updated successfully, false otherwise
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    public static boolean suspendCard(final String tokenUniqueReference) throws McbpCardNotFound {
        try {
            final LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getLdeRemoteManagementService();

            String digitizedCardId = ldeRemoteManagementService
                    .getCardIdFromTokenUniqueReference(tokenUniqueReference);

            ldeRemoteManagementService.suspendCard(digitizedCardId);
        } catch (InvalidInput exception) {
            throw new McbpCardNotFound("No card found for tokenUniqueReference "
                                       + tokenUniqueReference);
        }
        return true;
    }

    /**
     * The function enables the wallet to inform the SDK that the card is active/un suspended.
     * The wallet receives this information from the Payment App Server.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to be activated/un suspended
     * @return true if status is updated successfully, false otherwise
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    public static boolean activateCard(final String tokenUniqueReference) throws McbpCardNotFound {
        try {
            final LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getLdeRemoteManagementService();

            String digitizedCardId = ldeRemoteManagementService
                    .getCardIdFromTokenUniqueReference(tokenUniqueReference);
            ldeRemoteManagementService.activateProfile(digitizedCardId);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound(
                    "No card found for tokenUniqueReference " + tokenUniqueReference);
        } catch (McbpCryptoException e) {
            return false;
        }
        return true;
    }

    /**
     * Replenish the SUKs for the specified card.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to synchronize SUKs for.
     * @throws McbpCardNotFound
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void replenishForCardWithId(String tokenUniqueReference) throws
            AlreadyInProcessException, InvalidCardStateException {
        String digitizedCardId;
        try {
            LdeRemoteManagementService ldeRemoteManagementService = McbpInitializer
                    .getInstance().getLdeRemoteManagementService();
            digitizedCardId = ldeRemoteManagementService.getCardIdFromTokenUniqueReference(
                    tokenUniqueReference);
            //Don't replenish, If token(card) is not in active state.
            ProfileState cardState = ldeRemoteManagementService.getCardState(digitizedCardId);
            if (cardState.getValue() != ProfileState.INITIALIZED.getValue()) {
                throw new InvalidCardStateException("Card is not in active state");
            }
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
        RemoteManagementServices.replenish(digitizedCardId);
    }

    /**
     * Replenish the SUKs for the specified card.
     *
     * @param card Instance of {@link McbpCard} to synchronize SUKs for.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @deprecated since 1.0.4
     */
    public static void replenishForCard(McbpCard card)
            throws AlreadyInProcessException, InvalidCardStateException {
        replenishForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Replenish the SUKs for the specified card.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to be replenish
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     */
    public static void replenishForCard(final String tokenUniqueReference)
            throws AlreadyInProcessException, InvalidCardStateException {
        replenishForCardWithId(tokenUniqueReference);
    }


    /**
     * Determines if card is the default payment card for contactless or remote payments and un-sets
     * if (normally before being deleted).
     *
     * @param tokenUniqueReference tokenUniqueReference of the card
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    public static void unsetIfDefaultCard(String tokenUniqueReference) {
        if (isDefaultCardForContactlessPayment(tokenUniqueReference)) {
            unsetDefaultContactlessCard();
        }

        // If this card is the default card for remote payments, then we need to unset it
        if (isDefaultCardForRemotePayment(tokenUniqueReference)) {
            unsetDefaultRemoteCard();
        }
    }

    /**
     * Wipe all the SUKs on the CMS linked to card.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to wipe all the
     *                             SUKs for.
     * @return true if the wipe was successful, false otherwise.
     * @since 1.0.4
     */
    public static boolean remoteWipeSuksForCard(String tokenUniqueReference) {
        return remoteWipeSuksForCardWithId(tokenUniqueReference);
    }

    /**
     * Wipe all the data on the CMS linked to card.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to wipe all the
     *                             data for.
     * @return true if the wipe was successful, false otherwise.
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    @SuppressWarnings({"WeakerAccess", "UnusedReturnValue"})
    public static boolean remoteWipeCard(String tokenUniqueReference) {
        return remoteWipeCardWithId(tokenUniqueReference);
    }

    /**
     * Determine if the digitized card Id is the identifier of the default card to use for
     * contactless payments.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to get transaction logs for.
     * @return true if the digitized card Id is the Id of the default card to use for contactless
     * payments.
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    public static boolean isDefaultCardForContactlessPayment(String tokenUniqueReference) {
        String digitizedCardId;
        try {
            digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }

        McbpCard defaultContactlessCard = McbpWalletApi.getDefaultCardForContactlessPayment();
        return defaultContactlessCard != null && defaultContactlessCard.getDigitizedCardId()
                                                                       .equalsIgnoreCase(
                                                                               digitizedCardId);
    }


    /**
     * Sets card as the default payment card for contactless payments.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card for contactless payments.
     * @param listener             Callback for success/failure handling, see
     *                             {@link MakeDefaultListener}.
     * @throws McbpCardNotFound
     * @since 1.0.4
     */
    public static void setAsDefaultCardForContactlessPayment(String tokenUniqueReference,
                                                             MakeDefaultListener listener) {
        McbpCard mcbpCard = getMcbpCard(tokenUniqueReference);
        McbpInitializer.getInstance().getBusinessService()
                       .getDefaultCardsManager()
                       .setAsDefaultCardForContactlessPayment(mcbpCard, listener);
    }


    /**
     * Sets card as the default payment card for contactless payments.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card  to
     *                             set as the default card for contactless payments.
     * @param applicationDefault   Whether this card should be permanently set as the default.
     * @param listener             Callback for success/failure handling, see
     *                             {@link MakeDefaultListener}.
     * @since 1.0.4
     */
    @SuppressWarnings("unused")
    public static void setAsDefaultCardForContactlessPayment(String tokenUniqueReference,
                                                             boolean applicationDefault,
                                                             MakeDefaultListener listener) {
        McbpCard mcbpCard = getMcbpCard(tokenUniqueReference);
        McbpInitializer.getInstance().getBusinessService()
                       .getDefaultCardsManager()
                       .setAsDefaultCardForContactlessPayment(mcbpCard, applicationDefault,
                                                              listener);
    }


    /**
     * Determine if card is the default one to use for remote payments.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card  to check if its
     *                             the default card to use for remote payments.
     * @return true if card is the default payment card for remote payments.
     * @since 1.0.4
     */
    public static boolean isDefaultCardForRemotePayment(String tokenUniqueReference) {
        McbpCard mcbpCard = getMcbpCard(tokenUniqueReference);
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager().isDefaultCardForRemotePayment(mcbpCard);
    }


    /**
     * Sets card as the default payment card for remote payments.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to set as the
     *                             default card for remote payments.
     * @return true if the card was successfully set as the default card for remote
     * payments.
     * @since 1.0.4
     */
    public static boolean setAsDefaultCardForRemotePayment(String tokenUniqueReference) {
        McbpCard mcbpCard = getMcbpCard(tokenUniqueReference);
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .setAsDefaultCardForRemotePayment(mcbpCard);
    }


    /**
     * Wipe all the SUKs on the CMS linked to the card identified by tokenUniqueReference.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to remove the SUKs for.
     * @return true if the wipe was successful, false otherwise.
     * of the wipe.
     * @since 1.0.4
     */
    public static boolean remoteWipeSuksForCardWithId(String tokenUniqueReference) {
        String digitizedCardId;
        try {
            digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }

        try {
            McbpInitializer.getInstance()
                           .getLdeRemoteManagementService()
                           .wipeDcSuk(ByteArray.of(digitizedCardId));
            McbpInitializer.getInstance()
                           .getLdeRemoteManagementService()
                           .deleteAllTransactionCredentialStatus(digitizedCardId);
        } catch (LdeNotInitialized | InvalidInput e) {
            sLogger.d(Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    /**
     * Wipe all the data on the CMS linked to the card identified by digitizedCardId.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to remove all data for.
     * @return true if the wipe was successful, false otherwise.
     * @since 1.0.4
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean remoteWipeCardWithId(String tokenUniqueReference) {
        String digitizedCardId;
        try {
            digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
        try {
            McbpInitializer.getInstance()
                           .getLdeRemoteManagementService()
                           .wipeDigitizedCard(ByteArray.of(digitizedCardId));
        } catch (InvalidInput | LdeNotInitialized e) {
            sLogger.d(Log.getStackTraceString(e));
            return false;
        }
        return true;
    }

    /**
     * Remove the card identified by digitizedCardId from the local database.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to remove from the local
     *                             database.
     * @return true if the wipe was successful, false otherwise.
     * @since 1.0.4
     */
    @SuppressWarnings("WeakerAccess")
    public static boolean wipeCard(String tokenUniqueReference) {
        String digitizedCardId;
        try {
            digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
        try {
            final LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getSdkContext().getLdeRemoteManagementService();
            ldeRemoteManagementService.wipeDigitizedCard(ByteArray.of(digitizedCardId));
        } catch (LdeNotInitialized | InvalidInput e) {
            sLogger.d(Log.getStackTraceString(e));
            return false;
        } catch (NullPointerException e) {
            // Intentional no-op - it is most likely because it has already been removed
            // but we log it in any case as it should not happen
            sLogger.d(Log.getStackTraceString(e));
        }
        return true;
    }

    /**
     * Returns all the transaction logs associated with card.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to get the
     *                             transaction logs for.
     * @return List of {@link TransactionLog} for card.
     * @since 1.0.4
     */
    public static List<TransactionLog> getTransactionLogsForCard(String tokenUniqueReference) {
        return getTransactionLogsForCardWithId(tokenUniqueReference);
    }


    /**
     * Returns all the transaction logs associated with the card identified by digitizedCardId.
     *
     * @param tokenUniqueReference tokenUniqueReference of the card to get transaction logs for.
     * @return List of {@link TransactionLog} for the card identified
     * by digitizedCardId.
     * @throws IllegalStateException    If the LDE has not been initialized
     * @throws IllegalArgumentException If the input parameters are not valid
     * @since 1.0.4
     */
    public static List<TransactionLog> getTransactionLogsForCardWithId(
            String tokenUniqueReference) {
        final List<TransactionLog> transactionLogs;

        String digitizedCardId;
        try {
            digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
        try {
            transactionLogs = McbpInitializer.getInstance().getBusinessService()
                                             .getTransactionLogs(digitizedCardId);
        } catch (LdeNotInitialized e) {
            throw new IllegalStateException("The LDE Database has not been initialized");
        } catch (InvalidInput e) {
            throw new IllegalArgumentException("Invalid Input: " + e);
        }
        return transactionLogs;
    }


    /**
     * Return the Mcbp Card of given token unique reference
     *
     * @param tokenUniqueReference tokenUniqueReference of the card
     * @return McbpCard instance
     */
    public static McbpCard getMcbpCard(String tokenUniqueReference) {
        String digitizedCardId;
        try {
            digitizedCardId = McbpInitializer.getInstance().getLdeRemoteManagementService()
                                             .getCardIdFromTokenUniqueReference
                                                     (tokenUniqueReference);
        } catch (InvalidInput invalidInput) {
            throw new McbpCardNotFound("Card not found for token unique reference "
                                       + tokenUniqueReference);
        }
        for (McbpCard mcbpCard : McbpInitializer.getInstance().getLdeBusinessLogicService()
                                                .getMcbpCards(false)) {
            if (mcbpCard.getDigitizedCardId().equals(digitizedCardId)) {
                return mcbpCard;
            }
        }
        return null;
    }
}
