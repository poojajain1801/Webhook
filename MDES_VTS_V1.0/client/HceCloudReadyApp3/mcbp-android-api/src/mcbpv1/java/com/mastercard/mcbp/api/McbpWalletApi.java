/*******************************************************************************
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 * <p/>
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 * <p/>
 * Please refer to the file LICENSE.TXT for full details.
 * <p/>
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 ******************************************************************************/

package com.mastercard.mcbp.api;

import android.util.Log;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.keymanagement.KeyManagementPolicy;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * API class used for interacting with the wallet.
 *
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
public class McbpWalletApi {
    /**
     * Map for setting different key management policies per card.
     */
    private static HashMap<String, KeyManagementPolicy> sCardKeyManagementPolicies =
            new HashMap();
    /**
     * Logger
     */
    private static final McbpLogger sLogger =
            McbpLoggerFactory.getInstance().getLogger(McbpWalletApi.class);

    /**
     * Get the card that is set as the currently active one.
     *
     * @return Instance of {@link McbpCard} representing the
     * currently active card.
     */
    public static McbpCard getCurrentCard() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getCurrentCard();
    }

    /**
     * Set card as the currently active one.
     *
     * @param card Instance of {@link McbpCard} to set as the
     *             currently active card.
     */
    public static void setCurrentCard(McbpCard card) {
        McbpInitializer.getInstance().getBusinessService()
                       .setCurrentCard(card);
    }

    /**
     * Get the card that is currently set as the default one to use for contactless payments.
     *
     * @return Instance of {@link McbpCard} representing the
     * card currently set as the default one to use for contactless payments.
     */
    public static McbpCard getDefaultCardForContactlessPayment() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .getDefaultCardForContactlessPayment();
    }

    /**
     * Get the card that is currently set as the default one to use for remote payments.
     *
     * @return Instance of {@link McbpCard} representing the
     * card currently set as the default one to use for remote payments.
     */
    public static McbpCard getDefaultCardForRemotePayment() {
        return McbpInitializer.getInstance().getBusinessService()
                              .getDefaultCardsManager()
                              .getDefaultCardForRemotePayment();
    }

    /**
     * Get all the cards currently within the wallet.
     *
     * @return List of {@link McbpCard} instances currently
     * within the wallet.
     */
    public static ArrayList<McbpCard> getCards() {
        return getCards(false);
    }

    /**
     * Get all the cards currently within the wallet.
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return List of {@link McbpCard} instances currently
     * within the wallet.
     */
    public static ArrayList<McbpCard> getCards(boolean refresh) throws LdeNotInitialized {
        return McbpInitializer.getInstance().getBusinessService().getAllCards(refresh);
    }

    /**
     * Get all the cards within the wallet that are eligible for remote payments.
     *
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for remote payments.
     */
    public static ArrayList<McbpCard> getCardsEligibleForRemotePayment() {
        return getCardsEligibleForRemotePayment(false);
    }

    /**
     * Get all the cards within the wallet that are eligible for remote payments.
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for remote payments.
     */
    @SuppressWarnings({"SameParameterValue", "WeakerAccess"})
    public static ArrayList<McbpCard> getCardsEligibleForRemotePayment(boolean refresh) {
        // Get the entire list of cards in the wallet
        ArrayList<McbpCard> cards = getCards(refresh);

        // Don't try to process them if we don't have any at all
        if (cards == null) {
            return null;
        }

        // Build up a new list of eligible cards
        final ArrayList<McbpCard> eligibleCards = new ArrayList();

        // Loop through each card in the wallet and test whether remote payments are supported
        for (McbpCard card : cards) {
            if (card.isRpSupported()) {
                eligibleCards.add(card);
            }
        }

        return eligibleCards;
    }

    /**
     * Get all the cards within the wallet that are eligible for contactless payments.
     *
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for contactless payments.
     */
    public static ArrayList<McbpCard> getCardsEligibleForContactlessPayment() {
        return getCardsEligibleForContactlessPayment(false);
    }

    /**
     * Get all the cards within the wallet that are eligible for contactless payments.
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return List of {@link McbpCard} instances currently
     * within the wallet that are eligible for contactless payments.
     */
    public static ArrayList<McbpCard> getCardsEligibleForContactlessPayment(boolean refresh) {
        // Get the entire list of cards in the wallet
        ArrayList<McbpCard> cards = getCards(refresh);

        // Don't try to process them if we don't have any at all
        if (cards == null) {
            return null;
        }

        // Build up a new list of eligible cards
        ArrayList<McbpCard> eligibleCards = new ArrayList();

        // Loop through each card in the wallet and test whether contactless payments are supported
        for (McbpCard card : cards) {
            if (card.isClSupported()) {
                eligibleCards.add(card);
            }
        }

        return eligibleCards;
    }

    /**
     * Delete all cards within the wallet.
     *
     * @return true if deleting all the cards was successful; otherwise false.
     */
    public static boolean wipeWallet() {
        try {
            LdeRemoteManagementService ldeRemoteManagementService =
                    McbpInitializer.getInstance().getSdkContext().getLdeRemoteManagementService();

            ldeRemoteManagementService.remoteWipeWallet();
            ldeRemoteManagementService.resetMpaToInstalledState();

        } catch (LdeNotInitialized ldeNotInitialized) {
            // If the LDE has not been initialized there should nothing to be worried about
            // However, this should never happen. We log this an ignore it.
            sLogger.d(Log.getStackTraceString(ldeNotInitialized));
        }
        return true;
    }

    /**
     * Get the list of supported AIDs.<br>
     * NB: This is currently a hard coded list of AIDs, it is to be updated to calculate this based
     * on the updates provided by Android Lollipop.
     *
     * @return List of AIDs supported by the application.
     */
    public static List<String> getSupportedAids() {
        final ArrayList<String> aids = new ArrayList();

        aids.add("A0000000041010");
        aids.add("A0000000042203");

        return aids;
    }

    /**
     * Set a card specific {@link KeyManagementPolicy}.
     *
     * @param card                Instance of {@link McbpCard}
     *                            to set the key management policy for.
     * @param keyManagementPolicy The implementation of
     *                            {@link KeyManagementPolicy} to
     *                            set for this card.
     */
    public static void setKeyManagementPolicyForCard(McbpCard card,
                                                     KeyManagementPolicy keyManagementPolicy) {
        setKeyManagementPolicyForCardWithId(card.getDigitizedCardId(), keyManagementPolicy);
    }

    /**
     * Set a card specific {@link KeyManagementPolicy}.
     *
     * @param digitizedCardId     Identifier for the card to set the key management policy for.
     * @param keyManagementPolicy The implementation of
     *                            {@link KeyManagementPolicy} to
     *                            set for the card with this Id.
     */
    public static void setKeyManagementPolicyForCardWithId(String digitizedCardId,
                                                           KeyManagementPolicy
                                                                   keyManagementPolicy) {
        sCardKeyManagementPolicies.put(digitizedCardId, keyManagementPolicy);
    }

    /**
     * Reset this cards key management policy to the default.
     *
     * @param card Instance of {@link McbpCard} to reset the key
     *             management policy for.
     */
    public static void unsetKeyManagementPolicyForCard(McbpCard card) {
        unsetKeyManagementPolicyForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Reset the cards key management policy to the default.
     *
     * @param digitizedCardId Identifier for the card to reset the key management policy for.
     */
    public static void unsetKeyManagementPolicyForCardWithId(String digitizedCardId) {
        sCardKeyManagementPolicies.remove(digitizedCardId);
    }

    /**
     * Get the key management policy to use for this card.
     *
     * @param card Instance of {@link McbpCard} to get the key
     *             management policy for.
     * @return The {@link KeyManagementPolicy} to use for this
     * card.
     */
    public static KeyManagementPolicy getKeyManagementPolicyForCard(McbpCard card) {
        return getKeyManagementPolicyForCardWithId(card.getDigitizedCardId());
    }

    /**
     * Get the key management policy to use for this card.
     *
     * @param digitizedCardId Identifier for the card to get the key management policy for.
     * @return The {@link KeyManagementPolicy} to use for this
     * card.
     */
    public static KeyManagementPolicy getKeyManagementPolicyForCardWithId(String digitizedCardId) {
        KeyManagementPolicy keyManagementPolicy = McbpInitializer
                .getInstance().getDefaultKeyManagementPolicy();
        if (sCardKeyManagementPolicies.containsKey(digitizedCardId)) {
            keyManagementPolicy = sCardKeyManagementPolicies.get(digitizedCardId);
        }
        return keyManagementPolicy;
    }
}
