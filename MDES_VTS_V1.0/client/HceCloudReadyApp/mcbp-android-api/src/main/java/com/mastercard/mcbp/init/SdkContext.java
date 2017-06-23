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

package com.mastercard.mcbp.init;

import android.content.Context;

import com.mastercard.mcbp.core.AndroidPropertyStorageFactoryImpl;
import com.mastercard.mcbp.lde.LdeAndroidFactory;
import com.mastercard.mcbp.lde.services.LdeBusinessLogicService;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.AndroidRnsService;
import com.mastercard.mcbp.remotemanagement.RnsService;
import com.mastercard.mcbp.utils.PropertyStorageFactory;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.http.AndroidHttpFactory;
import com.mastercard.mcbp.utils.http.HttpFactory;
import com.mastercard.mcbp.utils.logs.AndroidMcbpLoggerFactory;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.utils.task.AndroidMcbpAsyncTaskFactory;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;
import com.mastercard.mcbp_android.BuildConfig;

public class SdkContext {

    public RnsService getRnsService() {
        return mRnsService;
    }

    /**
     * The application context.
     */
    private Context mApplicationContext;

    /**
     * The cryptographic service to use
     */
    private final CryptoService mCryptoService;

    /**
     * The Database Remote Management Service Interface
     */
    private LdeRemoteManagementService mLdeRemoteManagementService;

    /**
     * The Database Business Logic Service Interface
     */
    private LdeBusinessLogicService mLdeBusinessLogicService;

    /**
     * The Database Mcbp Card Service Interface
     */
    private LdeMcbpCardService mLdeMcbpCardService;
    /**
     * Http factory
     */
    private HttpFactory mHttpFactory;

    private RnsService mRnsService;

    private PropertyStorageFactory mPropertyStorageFactory;

    private SdkContext() {
        // The private constructor can be used only by factory methods

        // FIXME: This works, but it is a bit tricky.
        // Since the Context is created at the very beginning we can set the proper type of Crypto
        // service in a way that all the other services around it, including other static classes
        // can retrieve the correct value.
        // This is to allow both unit test to use a pure Java solution and run on a PC. At the same
        // time an Android implementation can use different implementations
        if (BuildConfig.NATIVE_CRYPTO_SERVICE) {
            CryptoServiceFactory.enableNativeCryptoService();
        }
        mCryptoService = CryptoServiceFactory.getDefaultCryptoService();

        // Load in memory all the data structure related to RSA, SHA1 and DES3 operations
        mCryptoService.warmUp();
    }

    /**
     * Initialize the sdk context.
     * Note: This api is used for MCBP remote protocol.
     *
     * @param context               Application context
     * @param notificationIconResId Resource id of notification icon
     * @param gcmId                 Google cloud messaging id
     * @return The SDK Context itself
     */
    public static SdkContext initialize(Context context, int notificationIconResId, String gcmId) {
        SdkContext sdkContext = new SdkContext();
        sdkContext.setPropertyStorageFactory(new AndroidPropertyStorageFactoryImpl(context));
        sdkContext.setMcbpLoggerFactory(new AndroidMcbpLoggerFactory(), context);
        sdkContext.setApplicationContext(context);

        // Configure the Crypto Service
        if (BuildConfig.NATIVE_CRYPTO_SERVICE) {
            CryptoServiceFactory.enableNativeCryptoService();
        }

        sdkContext.setHttpFactory(new AndroidHttpFactory());

        // Set LDE Services
        sdkContext.setLdeBusinessLogicService(LdeAndroidFactory.getDefaultMcbpDatabase(context));
        sdkContext.setLdeRemoteManagementService(LdeAndroidFactory.getDefaultMcbpDatabase(context));
        sdkContext.setLdeMcbpCardService(LdeAndroidFactory.getDefaultMcbpDatabase(context));

        sdkContext.mRnsService = new AndroidRnsService(context, gcmId);

        // Bind the McbpTaskFactory with the Android Async Task Factory
        McbpTaskFactory.initializeAsyncEngine(AndroidMcbpAsyncTaskFactory.INSTANCE);

        return sdkContext;

    }

    /**
     * Initialize the sdk context.
     * Note: This api is used for MDES remote protocol as SDK is not handling GCM registration.
     *
     * @param context Application context
     * @return SdkContext
     * Since 1.0.4g
     */
    public static SdkContext initialize(Context context) {
        SdkContext sdkContext = new SdkContext();

        sdkContext.setPropertyStorageFactory(new AndroidPropertyStorageFactoryImpl(context));
        sdkContext.setMcbpLoggerFactory(new AndroidMcbpLoggerFactory(), context);
        sdkContext.setApplicationContext(context);
        sdkContext.setHttpFactory(new AndroidHttpFactory());

        // Set LDE Services
        sdkContext.setLdeBusinessLogicService(LdeAndroidFactory.getDefaultMcbpDatabase(context));
        sdkContext.setLdeRemoteManagementService(LdeAndroidFactory.getDefaultMcbpDatabase(context));
        sdkContext.setLdeMcbpCardService(LdeAndroidFactory.getDefaultMcbpDatabase(context));

        // Bind the McbpTaskFactory with the Android Async Task Factory
        McbpTaskFactory.initializeAsyncEngine(AndroidMcbpAsyncTaskFactory.INSTANCE);

        return sdkContext;

    }

    public Context getApplicationContext() {
        return mApplicationContext;
    }

    private void setApplicationContext(Context mApplicationContext) {
        this.mApplicationContext = mApplicationContext;
    }

    public CryptoService getCryptoService() {
        return mCryptoService;
    }

    private void setMcbpLoggerFactory(McbpLoggerFactory mMcbpLoggerFactory, Context context) {
        McbpLoggerFactory.setInstance(mMcbpLoggerFactory, context);
    }

    private void setLdeRemoteManagementService(LdeRemoteManagementService remoteManagementService) {
        mLdeRemoteManagementService = remoteManagementService;
    }

    private void setLdeBusinessLogicService(LdeBusinessLogicService ldeBusinessLogicService) {
        mLdeBusinessLogicService = ldeBusinessLogicService;
    }

    private void setLdeMcbpCardService(LdeMcbpCardService ldeMcbpCardService) {
        mLdeMcbpCardService = ldeMcbpCardService;
    }

    public HttpFactory getHttpFactory() {
        return mHttpFactory;
    }

    private void setHttpFactory(HttpFactory mHttpFactory) {
        this.mHttpFactory = mHttpFactory;
    }


    public LdeRemoteManagementService getLdeRemoteManagementService() {
        return mLdeRemoteManagementService;
    }

    public LdeMcbpCardService getLdeMcbpCardService() {
        return mLdeMcbpCardService;
    }

    public LdeBusinessLogicService getLdeBusinessLogicService() {
        return mLdeBusinessLogicService;
    }

    public PropertyStorageFactory getPropertyStorageFactory() {
        return mPropertyStorageFactory;
    }

    private void setPropertyStorageFactory(PropertyStorageFactory propertyStorageFactory) {
        mPropertyStorageFactory = propertyStorageFactory;
        PropertyStorageFactory.setInstance(propertyStorageFactory);
    }
}
