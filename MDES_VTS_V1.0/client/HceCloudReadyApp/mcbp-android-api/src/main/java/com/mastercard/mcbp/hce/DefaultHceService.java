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

package com.mastercard.mcbp.hce;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.PowerManager;

import com.mastercard.mcbp.api.McbpApi;
import com.mastercard.mcbp.api.McbpCardApi;
import com.mastercard.mcbp.api.McbpWalletApi;
import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.card.mpplite.apdu.Iso7816;
import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.listeners.FirstTapListener;
import com.mastercard.mcbp_android.BuildConfig;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.ArrayList;

@SuppressLint("Registered")
public class DefaultHceService extends AndroidHceService {
    /**
     * Current digitized card
     */
    private McbpCard mCurrentCard;

    /**
     * Flag indicating whether initialization has been performed (and has been successful)
     */
    private boolean mIsInitialized = false;

    /**
     * A reference to the system's power manager. Required to detect screen interactivity
     */
    private PowerManager mPowerManager;

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected void init() {
        if (!McbpApi.isInitialized()) {
            return;
        }
                // Get the power manager
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);



        // If the app is running, the current card is the one that is currently visible
        if (McbpApi.isAppRunning()) {
            mCurrentCard = McbpWalletApi.getCurrentCard();
        }

        // If the app wasn't running, first attempt to get the default contactless card
        if (mCurrentCard == null) {
            mCurrentCard = McbpWalletApi.getDefaultCardForContactlessPayment();
        }

        // If we still don't have a card, get the first card that supports contactless and has
        // a SUK left to use for payment
        if (mCurrentCard == null) {
            ArrayList<McbpCard> cards = McbpWalletApi.getCardsEligibleForContactlessPayment(true);
            if (cards == null || cards.size() == 0) {
                mIsInitialized = false;
                return;
            }

            // Look for a card with one or more SUKs
            for (McbpCard card : cards) {
                if (card.numberPaymentsLeft() > 0) {
                    mCurrentCard = card;
                    prepareFirstTap();
                    mIsInitialized = true;
                    return;
                }
            }
        }
        if (mCurrentCard == null) {
            mIsInitialized = false;
        } else {
            prepareFirstTap();
            mIsInitialized = true;
        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    @TargetApi(Build.VERSION_CODES.KITKAT)
    protected byte[] processApdu(final byte[] apdu) {
        //System.out.println("C-APDU: " + ByteArray.of(apdu));
        // Make sure we have been initialised
        if (!mIsInitialized) {
                init();

        }

        // If we don't have a card that supports contactless payment or screen is not on and we do
        // not allow screen OFF mode
        if (mCurrentCard == null || (!BuildConfig.SCREEN_OFF_ALLOWED && !isDeviceInteractive())) {
            // FIXME: Later releases may inform the UI layer that there is no available card
            return ByteArray.of(Iso7816.SW_CONDITIONS_NOT_SATISFIED).getBytes();
        }

        final byte[] responseApdu = mCurrentCard.processApdu(apdu);

        // Return the response
        //System.out.println("R-APDU: " + ByteArray.of(responseApdu));
        return responseApdu;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void processOnDeactivated() {
        // Reset our initialized flag
        mIsInitialized = false;

        // If we had a card, clean it up
        if (mCurrentCard != null) {
            mCurrentCard.processOnDeactivated();
            mCurrentCard = null;
        }

    }

    /**
     * Define the listener that will be used by the MCBP Card during the first tap transaction
     */
    private void prepareFirstTap() {
        if (mCurrentCard == null) {
            throw new IllegalArgumentException("No valid card available for first tap");
        }
        // Prepare the card for handling a first tap event
        McbpCardApi.prepareCardForFirstTap(mCurrentCard, new FirstTapListener() {
            @Override
            @TargetApi(Build.VERSION_CODES.KITKAT)
            public void onFirstTap(String amount, String currencyCode, String digitizedCardId) {
                Intent firstTapIntent = McbpInitializer.getInstance().getFirstTapIntent();
                if (firstTapIntent != null) {
                    firstTapIntent.setAction(ACTION_FIRST_TAP);
                    firstTapIntent.putExtra(PARAM_AMOUNT, Integer.valueOf(amount));
                    firstTapIntent.putExtra(PARAM_CURRENCY, Integer.valueOf(currencyCode));
                    firstTapIntent.putExtra(PARAM_CURRENT, digitizedCardId);
                        startActivity(firstTapIntent);

                }
            }
        });
    }

    /**
     * Check whether the device screen is on or not.
     * It is used to decide whether or not we should process an incoming APDU
     */
    private boolean isDeviceInteractive() {
        final boolean isDeviceInteractive;
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.KITKAT_WATCH) {
            isDeviceInteractive = mPowerManager.isScreenOn();
        } else {
            isDeviceInteractive = mPowerManager.isInteractive();
        }
        return isDeviceInteractive;
    }
}
