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
import com.mastercard.mcbp.core.AndroidMobileDeviceInfo;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.userinterface.CmsActivationListener;
import com.mastercard.mcbp.userinterface.InitializationListener;

/**
 * API class used for non-specific interactions with the API.
 *
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
public class McbpApi extends CommonMcbpApi {

    /**
     * Determine if the MPA SDK has been initialized.
     *
     * @return true if the MPA SDK has been initialized; otherwise false.
     */
    public static boolean isInitialized() {
        McbpInitializer mcbpInitializer = McbpInitializer.getInstance();
        if (mcbpInitializer != null) {
            return mcbpInitializer.getLdeBusinessLogicService().isLdeInitialized();
        }
        return false;
    }

    /**
     * Determine if the MPA SDK is currently uninitialized.
     *
     * @return true if the MPA SDK is current uninitialized; otherwise false.
     */
    public static boolean isUninitialized() {
        return !isInitialized();
    }

    /**
     * Initializes the MPA SDK with the CMS system.
     *
     * @param listener Callback for initialization result, see
     *                 {@link InitializationListener}
     */
    public static void initialize(InitializationListener listener) {
        McbpInitializer.getInstance().getBusinessService().initializeMpa(listener);
    }

    /**
     * Registers the MPA SDK and the user to the CMS.
     *
     * @param userId         The user Id entered.
     * @param activationCode The activation code entered.
     * @param listener       Callback for events, see
     *                       {@link CmsActivationListener}
     */
    public static void registerToCms(final String userId,
                                     final String activationCode,
                                     final CmsActivationListener listener) {
        McbpInitializer.getInstance().getRemoteManagementService().registerToCms(
                new AndroidMobileDeviceInfo(McbpInitializer.getInstance().getApplicationContext()),
                userId,
                activationCode,
                listener);
    }

    /**
     * Replenish the SUKs for the specified card.
     * This API is deprecated now,use {@link McbpCardApi#replenishForCard(McbpCard)}
     *
     * @param digitizedCardId Identifier of the card to synchronize SUKs for.
     * @deprecated since 1.0.4
     */
    public static void replenishForCardWithId(
            String digitizedCardId) throws AlreadyInProcessException {
        McbpInitializer.getInstance().getRemoteManagementService().goOnlineForSync(digitizedCardId);
    }

    /**
     * Replenish the SUKs for the specified card.
     * This API is deprecated now,use {@link McbpCardApi#replenishForCard(McbpCard)}
     *
     * @param card Instance of {@link McbpCard} to synchronize
     *             SUKs for.
     * @deprecated since 1.0.4
     */
    public static void replenishForCard(McbpCard card) throws AlreadyInProcessException {
        replenishForCardWithId(card.getDigitizedCardId());
    }

}
