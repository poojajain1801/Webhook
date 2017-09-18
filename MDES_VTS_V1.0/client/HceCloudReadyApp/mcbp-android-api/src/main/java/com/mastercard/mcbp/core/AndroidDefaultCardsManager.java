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

package com.mastercard.mcbp.core;

import android.annotation.TargetApi;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.nfc.NfcAdapter;
import android.nfc.cardemulation.CardEmulation;
import android.os.Build;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.lde.services.LdeBusinessLogicService;
import com.mastercard.mcbp.userinterface.MakeDefaultListener;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;

/**
 * Manage the default card features and functionality
 */
public class AndroidDefaultCardsManager implements
        com.mastercard.mcbp.businesslogic.DefaultCardsManager {

    private static final String PREFERENCE_DEFAULT_CARD = "PREFERENCE_DEFAULT_CARD";
    private static final String KEY_DEFAULT_CONTACTLESS = "KEY_DEFAULT_CONTACTLESS";
    private static final String KEY_DEFAULT_REMOTE = "KEY_DEFAULT_REMOTE";
    /**
     * Context
     */
    private final Context mContext;
    /**
     * Class
     */
    private final Class mService;
    /**
     * Business Logic service
     */
    LdeBusinessLogicService mLdeBusinessLogicService;
    /**
     * MakeDefaultListener
     */
    private MakeDefaultListener mListener;

    public AndroidDefaultCardsManager(Context context,
                                      LdeBusinessLogicService ldeBusinessLogicService,
                                      Class<?> serviceClass) {
        this.mLdeBusinessLogicService = ldeBusinessLogicService;
        this.mContext = context;
        this.mService = serviceClass;
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public boolean isDefaultCardForContactlessPayment(McbpCard mcbpCard) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            CardEmulation cardEmulation = CardEmulation.getInstance(NfcAdapter.getDefaultAdapter
                    (mContext));
            return cardEmulation.isDefaultServiceForCategory(new ComponentName(mContext, mService),
                                                             CardEmulation.CATEGORY_PAYMENT)
                   && getDefaultCardForContactlessPayment() != null
                   && mcbpCard.getDigitizedCardId().equals(
                    getDefaultCardForContactlessPayment().getDigitizedCardId());
        }
        // TODO: Next releases may raise an exception to inform the UI that the functionality
        // is not supported
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDefaultCardForRemotePayment(McbpCard mcbpCard) {
        return getDefaultCardForRemotePayment() != null
               && mcbpCard.getDigitizedCardId()
                          .equals(getDefaultCardForRemotePayment().getDigitizedCardId());
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    public void setAsDefaultCardForContactlessPayment(McbpCard mcbpCard,
                                                      boolean setApplicationDefault,
                                                      MakeDefaultListener listener) {
        this.mListener = listener;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (setApplicationDefault) {
                // make the card as default card
                saveDefaultCard(KEY_DEFAULT_CONTACTLESS, mcbpCard.getDigitizedCardId());
            }
            listener.onSuccess();
        }
        // TODO: Next releases may raise an exception to inform the UI that the functionality
        // is not supported
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setAsDefaultCardForContactlessPayment(McbpCard mcbpCard,
                                                      MakeDefaultListener listener) {
        setAsDefaultCardForContactlessPayment(mcbpCard, true, listener);
    }

    /**
     * {@inheritDoc}
     */
    @TargetApi(Build.VERSION_CODES.KITKAT)
    @Override
    /**
     * Unsetting as default is currently unsupported on android:
     * launching the NFC payments settings view instead
     */
    public void unsetAsDefaultCardForContactlessPayment(McbpCard mcbpCard,
                                                        MakeDefaultListener listener) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            saveDefaultCard(KEY_DEFAULT_CONTACTLESS, null);
            listener.onSuccess();
        }
        // TODO: Next releases may raise an exception to inform the UI that the functionality
        // is not supported
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean setAsDefaultCardForRemotePayment(McbpCard mcbpCard) {
        saveDefaultCard(KEY_DEFAULT_REMOTE, mcbpCard.getDigitizedCardId());
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean unsetAsDefaultCardForRemotePayment(McbpCard mcbpCard) {
        saveDefaultCard(KEY_DEFAULT_REMOTE, null);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public McbpCard getDefaultCardForContactlessPayment() {
        if (mLdeBusinessLogicService == null) {
            return null;
        }

        String contactlessDefault = getDefaultCard(KEY_DEFAULT_CONTACTLESS);

        if (contactlessDefault != null) {
            try {
                for (McbpCard mcbpCard : mLdeBusinessLogicService.getMcbpCards(false)) {
                    if (mcbpCard.getDigitizedCardId().equals(contactlessDefault)) {
                        return mcbpCard;
                    }
                }
            } catch (final LdeNotInitialized e) {
                throw new IllegalStateException("LDE Not initialized: " + e);
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public McbpCard getDefaultCardForRemotePayment() {

        if (mLdeBusinessLogicService == null) {
            return null;
        }
        String remoteDefaultCardId = getDefaultCard(KEY_DEFAULT_REMOTE);
        if (remoteDefaultCardId != null) {
            try {
                for (McbpCard mcbpCard : mLdeBusinessLogicService.getMcbpCards(false)) {
                    if (mcbpCard.getDigitizedCardId().equals(remoteDefaultCardId)) {
                        return mcbpCard;
                    }
                }
            } catch (final LdeNotInitialized e) {
                throw new IllegalStateException("LDE Not initialized: " + e);
            }
        }
        return null;
    }

    private void saveDefaultCard(String key, String value) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFERENCE_DEFAULT_CARD,
                                                                     Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(key, value);
        editor.apply();
    }

    private String getDefaultCard(String key) {
        SharedPreferences sharedPref = mContext.getSharedPreferences(PREFERENCE_DEFAULT_CARD,
                                                                     Context.MODE_PRIVATE);
        return sharedPref.getString(key, null);
    }

    public MakeDefaultListener getListener() {
        return mListener;
    }
}
