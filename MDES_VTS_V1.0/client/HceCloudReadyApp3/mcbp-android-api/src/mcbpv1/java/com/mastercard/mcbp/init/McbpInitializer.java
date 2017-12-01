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
import android.content.Intent;
import android.os.Build;

import com.mastercard.mcbp.core.AndroidMobileDeviceInfo;
import com.mastercard.mcbp.hce.DefaultHceService;
import com.mastercard.mcbp.remotemanagement.CmsConfiguration;
import com.mastercard.mcbp.utils.task.McbpAsyncTask;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;
import com.mastercard.mcbp.utils.task.McbpTaskListener;

/**
 * Initializes all the factories and services required to get the MCBP library up and running<br>
 * Singleton pattern is used in order to have access to one object instance throughout the
 * application.
 *
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
public class McbpInitializer extends DefaultMcbpInitializer {
    /**
     * Singleton reference.
     */
    private static McbpInitializer sInstance;

    /**
     * The credentials management service in case of MDES
     */
    private RemoteManagementServices mRemoteManagementServices;

    /**
     * Private constructor so cannot be constructed from outside this class
     *
     * @param application           application
     * @param notificationIconResId icon used for notifications
     * @param cmsConfiguration      configuration for the CMS service
     * @param gcmId                 Google Cloud Messaging Id
     * @param hceServiceClass       class used for contactless interaction
     *                              Since 1.0.4g
     */
    private McbpInitializer(final Application application, int notificationIconResId,
                            CmsConfiguration cmsConfiguration, String gcmId,
                            Class<?> hceServiceClass) {
        super(application, notificationIconResId, gcmId, hceServiceClass);
        setUpRemoteManagementService(cmsConfiguration);
        registerWithGcmServer();
    }

    private void registerWithGcmServer() {
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(
                new McbpTaskListener() {
                    @Override
                    public void onPreExecute() {

                    }

                    @Override
                    public void onRun() {
                        getSdkContext().getRnsService().registerApplication();
                    }

                    @Override
                    public void onPostExecute() {

                    }
                });
    }


    private void setUpRemoteManagementService(CmsConfiguration cmsConfiguration) {
        this.mRemoteManagementServices =
                new RemoteManagementServices(getSdkContext(),
                                             cmsConfiguration,
                                             createApplicationInfo(),
                                             getSdkContext().getRnsService());
    }


    /**
     * Setup method used in order to pass the necessary parameters to initialize the SDK services.
     *
     * @param application       application
     * @param notificationResId icon used for notifications
     * @param cmsConfiguration  configuration for the CMS service
     * @param gcmId             Google Cloud Messaging Id
     * @param firstTapIntent    Intent used by DefaultHceService when a first tap is detected
     *                          Since 1.0.4g
     */
    public synchronized static void setup(final Application application, int notificationResId,
                                          CmsConfiguration cmsConfiguration, String gcmId,
                                          Intent firstTapIntent) {

        Class<?> hceServiceClass = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hceServiceClass = DefaultHceService.class;
        }

        // Call our main setup method
        setup(application, notificationResId, cmsConfiguration, gcmId, hceServiceClass);

        // Update the class to use for first tap payments
        sInstance.setFirstTapIntent(firstTapIntent);
    }

    /**
     * Setup method used in order to pass the necessary parameters to initialize the SDK services.
     *
     * @param application       application
     * @param notificationResId icon used for notifications
     * @param cmsConfiguration  configuration for the CMS service
     * @param gcmId             Google Cloud Messaging Id
     * @param hceServiceClass   class used for contactless interaction
     *                          Since 1.0.4g
     */
    @SuppressWarnings("WeakerAccess")
    public synchronized static void setup(final Application application, int notificationResId,
                                          CmsConfiguration cmsConfiguration, String gcmId,
                                          Class<?> hceServiceClass) {
        // Check we haven't already been initialised
        if (sInstance != null) {
            return;
        }

        sInstance = new McbpInitializer(application, notificationResId, cmsConfiguration, gcmId,
                                        hceServiceClass);
    }

    /**
     * Returns only one instance of the object.
     * Singleton approach.
     *
     * @return static class instance
     */
    public synchronized static McbpInitializer getInstance() {
        return sInstance;
    }

    public RemoteManagementServices getRemoteManagementService() {
        return sInstance.mRemoteManagementServices;
    }

    /**
     * Set the mobile device info
     */
    public void setMobileDeviceInfo() {

        final AndroidMobileDeviceInfo deviceInfo =
                new AndroidMobileDeviceInfo(getSdkContext().getApplicationContext());

        getRemoteManagementService().getCmsService().setMobileDeviceInfo(deviceInfo);
    }
}
