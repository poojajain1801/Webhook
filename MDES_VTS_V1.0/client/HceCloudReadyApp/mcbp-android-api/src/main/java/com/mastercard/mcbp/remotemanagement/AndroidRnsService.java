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

package com.mastercard.mcbp.remotemanagement;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;

import java.io.IOException;

public class AndroidRnsService implements RnsService {
    public static final String RNS_MPA_ID_PREFERENCE_NAME = "RNS_MPA_ID_PREFERENCE";
    public static final String RNS_MPA_ID = "RNS_MPA_ID";
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);
    private static final String TAG = "AndroidRnsService";
    /**
     * Context
     */
    private final Context mContext;
    /**
     * Rns Cms Id
     */
    private final String mRnsCmsId;
    /**
     * Shared Preferences
     */
    SharedPreferences mSharedPreferences;
    /**
     * Rns Mpa Id
     */
    private String mRnsMpaId;

    /**
     * Default constructor
     */
    public AndroidRnsService(Context context, final String rnsCmsId) {
        this.mContext = context;
        this.mRnsCmsId = rnsCmsId;
        this.mSharedPreferences = context.getSharedPreferences(RNS_MPA_ID_PREFERENCE_NAME,
                                                               Context.MODE_PRIVATE);
        this.mRnsMpaId = mSharedPreferences.getString(RNS_MPA_ID, null);
    }

    @Override
    public void registerApplication() {
        try {
            if (GooglePlayServicesUtil.isGooglePlayServicesAvailable(this.mContext)
                != ConnectionResult.SUCCESS) {
                return;
            }

            GoogleCloudMessaging gcm = GoogleCloudMessaging.getInstance(mContext);

            if (gcm == null) {
                return;
            }

            final String rnsMpaId = gcm.register(mRnsCmsId);

            if (rnsMpaId != null && !rnsMpaId.isEmpty()) {
                setRnsMpaId(rnsMpaId);
                mSharedPreferences.edit().putString(RNS_MPA_ID, rnsMpaId).apply();
            } else {
                Log.d(TAG, "mRnsMpaId is not set");
            }

        } catch (IOException e) {
            mLogger.d(e.getMessage());
        }
    }

    @Override
    public String getRegistrationId() {
        return mRnsMpaId;
    }

    /**
     * Set Rns Mpa Id
     *
     * @param gcmId Rnd Id
     */
    private void setRnsMpaId(String gcmId) {
        this.mRnsMpaId = gcmId;
    }
}
