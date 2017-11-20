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

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;

/**
 * API class used for interacting with a card.
 *
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
public class McbpCardApi extends CommonMcbpCardApi {

    /**
     * Completely remove card from the CMS and the local database.
     *
     * @param card Instance of {@link McbpCard} to completely
     *             remove.
     */
    public static void deleteCard(McbpCard card) {
        // If this card is the default card for contactless or remote payments, then we need to
        // unset the default
        unsetIfDefaultCard(card);

        // Remove all the SUKs from the remote service
        remoteWipeSuksForCard(card);

        wipeCard(card);

        // Remove the card from the remote service
        remoteWipeCard(card);
    }

    /**
     * Replenish the SUKs for the specified card.
     *
     * @param digitizedCardId Identifier of the card to synchronize SUKs for.
     */
    public static void replenishForCardWithId(
            String digitizedCardId) throws AlreadyInProcessException {
        McbpInitializer.getInstance().getRemoteManagementService().goOnlineForSync(digitizedCardId);
    }

    /**
     * Replenish the SUKs for the specified card.
     *
     * @param card Instance of {@link McbpCard} to synchronize
     *             SUKs for.
     */
    public static void replenishForCard(McbpCard card) throws AlreadyInProcessException {
        replenishForCardWithId(card.getDigitizedCardId());
    }

}
