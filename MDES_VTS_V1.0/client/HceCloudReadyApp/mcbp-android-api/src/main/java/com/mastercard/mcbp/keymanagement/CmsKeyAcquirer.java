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

package com.mastercard.mcbp.keymanagement;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;

/**
 * The standard implementation of a {@link KeyAcquirer} that uses
 * the CMS system to acquire new keys.
 */
public class CmsKeyAcquirer implements KeyAcquirer {
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    @Override
    public boolean acquireKeysForCard(McbpCard card)
            throws AlreadyInProcessException, InvalidCardStateException {
        // Check the application has network connectivity
        ConnectivityManager connectivityManager =
                (ConnectivityManager) McbpInitializer.getInstance().getApplicationContext()
                                                     .getSystemService(
                                                             Context.CONNECTIVITY_SERVICE);

        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isConnected = (activeNetworkInfo != null
                               && activeNetworkInfo.isConnectedOrConnecting());

        String digitizedCardId = card.getDigitizedCardId();
        // If we're connected then we can go online to inform CMS of our request
        if (isConnected) {
            try {
                digitizedCardId =
                        McbpInitializer.getInstance().getLdeRemoteManagementService()
                                       .getTokenUniqueReferenceFromCardId(digitizedCardId);

            } catch (InvalidInput invalidInput) {
                //            Suppressing this exception in case of MCBP
                mLogger.d(invalidInput.getMessage());
            }

            McbpCardApi.replenishForCardWithId(digitizedCardId);
        }

        // CMS will raise the event on our behalf so we don't need to manually do it
        return false;
    }
}
