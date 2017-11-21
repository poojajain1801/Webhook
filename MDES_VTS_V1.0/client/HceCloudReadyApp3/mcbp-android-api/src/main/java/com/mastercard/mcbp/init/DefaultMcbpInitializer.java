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

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.mastercard.mcbp.businesslogic.ApplicationInfo;
import com.mastercard.mcbp.businesslogic.BusinessServices;
import com.mastercard.mcbp.core.AndroidDefaultCardsManager;
import com.mastercard.mcbp.keymanagement.CmsKeyAcquirer;
import com.mastercard.mcbp.keymanagement.KeyAcquirer;
import com.mastercard.mcbp.keymanagement.KeyManagementPolicy;
import com.mastercard.mcbp.keymanagement.KeyManagementPolicyThreshold;
import com.mastercard.mcbp.lde.services.LdeBusinessLogicService;
import com.mastercard.mcbp.lde.services.LdeMcbpCardService;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.lifecycle.McbpActivityLifecycleCallback;
import com.mastercard.mcbp.utils.crypto.PrngFixes;

/**
 * Initializes all the factories and services required to get the MCBP library up and running<br>
 * Singleton pattern is used in order to have access to one object instance throughout the
 * application.
 */
class DefaultMcbpInitializer {

    private SdkContext mSdkContext;

    /**
     * The business service
     */
    private BusinessServices mBusinessService;

    /**
     * Manage activity lifecycle so we can determine if the application is running
     */
    private McbpActivityLifecycleCallback mActivityLifecycleCallback;

    /**
     * Intent used by DefaultHceService when a first tap is detected
     */
    private Intent mFirstTapIntent;

    /**
     * The default key management policy to use for requesting new keys.
     */
    private KeyManagementPolicy mDefaultKeyManagementPolicy;

    /**
     * The default key acquirer to use when requesting new keys.
     */
    private KeyAcquirer mKeyAcquirer;

    /**
     * Reference to currently used remote protocol
     */
    private RemoteProtocol mRemoteProtocol;

    private Context mApplicationContext;

    /**
     * Private constructor so cannot be constructed from outside this class
     * Note: This constructor is used for MCBP remote protocol.
     *
     * @param application           application
     * @param notificationIconResId icon used for notifications
     * @param gcmId                 Google Cloud Messaging Id
     * @param hceServiceClass       class used for contactless interaction
     *                              Since 1.0.4g
     */
    DefaultMcbpInitializer(final Application application, int notificationIconResId,
                           String gcmId, Class<?> hceServiceClass) {
        // This is to fix the Android issue where random number generation may not receive
        // cryptographically strong values
        PrngFixes.apply();

        mApplicationContext = application;
        setupSdkContext(application, notificationIconResId, gcmId);
        setupBusinessService(hceServiceClass);
        setupActivityLifecycleCallback(application);

        mKeyAcquirer = new CmsKeyAcquirer();
        mDefaultKeyManagementPolicy = new KeyManagementPolicyThreshold();
    }

    /**
     * Private constructor so cannot be constructed from outside this class
     * Note: This constructor is used for MDES remote protocol as SDK is not handling
     * GCM registration.
     *
     * @param application     application
     * @param hceServiceClass class used for contactless interaction
     *                        Since 1.0.4g
     */
    DefaultMcbpInitializer(final Application application, Class<?> hceServiceClass) {
        // This is to fix the Android issue where random number generation may not receive
        // cryptographically strong values
        PrngFixes.apply();

        mApplicationContext = application;
        setupSdkContext(application);
        // Set the protocol to use
        setupBusinessService(hceServiceClass);
        setupActivityLifecycleCallback(application);

        mKeyAcquirer = new CmsKeyAcquirer();
        mDefaultKeyManagementPolicy = new KeyManagementPolicyThreshold();
    }

    /**
     * Initialize the sdk context
     * Note: This api is used for MCBP remote protocol.
     *
     * @param application           Application object
     * @param notificationIconResId Resource id of notification icon
     * @param gcmId                 Google cloud messaging id
     */
    private void setupSdkContext(Application application, int notificationIconResId, String gcmId) {
        mSdkContext = SdkContext.initialize(application, notificationIconResId, gcmId);
    }


    /**
     * Initialize the sdk context
     * Note: This api is used for MDES remote protocol as SDK is not handling GCM registration.
     *
     * @param application Application object
     *                    since 1.0.4g
     */
    private void setupSdkContext(Application application) {
        mSdkContext = SdkContext.initialize(application);
    }

    /**
     * Sets the remote protocol to use.
     *
     * @param remoteProtocol the remote protocol to use (MCBPv1 vs MDES)
     */
    public void setProtocol(RemoteProtocol remoteProtocol) {
        mRemoteProtocol = remoteProtocol;
    }

    /**
     * Setup the Business service
     *
     * @param hceServiceClass class used for contactless interaction
     */
    private void setupBusinessService(Class<?> hceServiceClass) {
        // Determine if HCE can be used based on the platform version
        Class mcbpHceServiceClass = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            mcbpHceServiceClass = hceServiceClass;
        }
        // Create the Business service
        mBusinessService = new BusinessServices(getSdkContext().getRnsService(),
                                                getSdkContext().getLdeBusinessLogicService(),
                                                new AndroidDefaultCardsManager(
                                                        getSdkContext().getApplicationContext(),
                                                        getSdkContext()
                                                                .getLdeBusinessLogicService(),
                                                        mcbpHceServiceClass));
    }

    /**
     * Register a callback with the application to manage activity lifecycle
     *
     * @param application application
     */
    private void setupActivityLifecycleCallback(Application application) {
        mActivityLifecycleCallback = new McbpActivityLifecycleCallback();
        application.registerActivityLifecycleCallbacks(mActivityLifecycleCallback);
    }

    /**
     * Create the {@link ApplicationInfo} object required by the CMS
     * service
     *
     * @return Populated instance of {@link ApplicationInfo}
     */
    public ApplicationInfo createApplicationInfo() {
        ApplicationInfo applicationInfo = new ApplicationInfo();
        applicationInfo.setStatus("");
        applicationInfo.setRfu("");

        // Attempt to get the version from the manifest file
        try {
            PackageManager packageManager = mApplicationContext.getPackageManager();
            String packageName = mApplicationContext.getPackageName();
            applicationInfo.setVersion(packageManager.getPackageInfo(packageName, 0).versionName);
        } catch (NameNotFoundException e) {
            applicationInfo.setVersion("1.0");
        }

        return applicationInfo;
    }

    /**
     * Retrieve the application context.
     *
     * @return Application context.
     */
    public Context getApplicationContext() {
        return this.mApplicationContext;
    }

    /**
     * Retrieve the MCBP activity lifecycle callback.
     *
     * @return MCBP activity lifecycle callback.
     */
    public McbpActivityLifecycleCallback getMcbpActivityLifecycleCallback() {
        return this.mActivityLifecycleCallback;
    }

    /**
     * Retrieve MCBP business service object.
     *
     * @return MCBP Business service.
     */
    public BusinessServices getBusinessService() {
        return this.mBusinessService;
    }

    /**
     * Set the intent to publish when DefaultHceService detects a first tap.
     * <p/>
     * Since 1.0.4
     */
    void setFirstTapIntent(Intent value) {
        this.mFirstTapIntent = value;
    }

    /**
     * Retrieves the intent to publish when DefaultHceService detects a first tap.
     *
     * @return The intent to use when DefaultHceService detects a first tap.
     * Since 1.0.4
     */
    public Intent getFirstTapIntent() {
        return this.mFirstTapIntent;
    }

    /**
     * Retrieve the default key management policy.
     *
     * @return The implementation of {@link KeyManagementPolicy}
     * to use to determine if new keys are required.
     */
    public KeyManagementPolicy getDefaultKeyManagementPolicy() {
        return this.mDefaultKeyManagementPolicy;
    }

    /**
     * Change the default key management policy.
     *
     * @param keyManagementPolicy Implementation of
     *                            {@link KeyManagementPolicy} that
     *                            determines when new keys should be requested.
     */
    public void setDefaultKeyManagementPolicy(KeyManagementPolicy keyManagementPolicy) {
        this.mDefaultKeyManagementPolicy = keyManagementPolicy;
    }

    /**
     * Retrieve the key acquirer.
     *
     * @return The implementation of {@link KeyAcquirer} to use to
     * acquire new keys.
     */
    public KeyAcquirer getKeyAcquirer() {
        return this.mKeyAcquirer;
    }

    /**
     * Set the key acquirer.
     *
     * @param keyAcquirer Implementation of {@link KeyAcquirer}
     *                    that will be used to acquire new keys.
     */
    public void setKeyAcquirer(KeyAcquirer keyAcquirer) {
        this.mKeyAcquirer = keyAcquirer;
    }

    /**
     * Gets current remote protocol {@link RemoteProtocol}
     *
     * @return current remote protocol
     */
    public RemoteProtocol getRemoteProtocol() {
        return mRemoteProtocol;
    }

    /**
     * Setup the host name and certificate for HTTPS connection
     *
     * @param hostName         Host Name
     * @param certificateBytes Byte Array of certificate
     */
    public void setUpHttpsConnection(final String hostName, final byte[] certificateBytes) {
        mSdkContext.getHttpFactory().setCertificateBytes(certificateBytes);
        mSdkContext.getHttpFactory().setHostname(hostName);
    }

    /**
     * The remote protocol to use for communication.
     */
    public enum RemoteProtocol {
        McbpV1,
        Mdes
    }

    public void putProperty(String key, String value) {
        this.mSdkContext.getPropertyStorageFactory().putProperty(key, value);
    }

    public String getProperty(String key, String defaultValue) {
        return this.mSdkContext.getPropertyStorageFactory().getProperty(key, defaultValue);
    }

    public SdkContext getSdkContext() {
        return this.mSdkContext;
    }


    public LdeRemoteManagementService getLdeRemoteManagementService() {
        return getSdkContext().getLdeRemoteManagementService();
    }

    public LdeMcbpCardService getLdeMcbpCardService() {
        return getSdkContext().getLdeMcbpCardService();
    }

    public LdeBusinessLogicService getLdeBusinessLogicService() {
        return getSdkContext().getLdeBusinessLogicService();
    }

}
