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

import com.mastercard.mcbp.api.MpaManagementApi;
import com.mastercard.mcbp.api.RemoteManagementServices;
import com.mastercard.mcbp.hce.DefaultHceService;
import com.mastercard.mcbp.lde.LdeInitParams;
import com.mastercard.mcbp.remotemanagement.mdes.CmsDServiceImpl;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Initializes all the factories and services required to get the MCBP library up and running<br>
 * Singleton pattern is used in order to have access to one object instance throughout the
 * application.
 */
public class McbpInitializer extends DefaultMcbpInitializer {

    public static final String PAYMENT_APP_INSTANCE_ID = CmsDServiceImpl.PAYMENT_APP_INSTANCE_ID;
    public static final String PAYMENT_APP_PROVIDER_ID = CmsDServiceImpl.PAYMENT_APP_PROVIDER_ID;

    /**
     * Singleton reference.
     */
    private static McbpInitializer sInstance;

    /**
     * Private constructor so cannot be constructed from outside this class
     *
     * @param application     application
     * @param hceServiceClass class used for contactless interaction
     *                        Since 1.0.4g
     */
    private McbpInitializer(final Application application, Class<?> hceServiceClass) {
        super(application, hceServiceClass);
        setUpRemoteManagementService();
    }

    /**
     * Setup the MDES CMS-D Remote management functionality
     */
    private void setUpRemoteManagementService() {
        final SdkContext context = getSdkContext();
        RemoteManagementServices.initialize(context.getHttpFactory(),
                                            context.getLdeRemoteManagementService(),
                                            context.getPropertyStorageFactory());
        MpaManagementApi.initialize(context.getLdeRemoteManagementService(),
                                    context.getCryptoService());
    }

    /**
     * Setup method used in order to pass the necessary parameters to initialize the SDK services.
     *
     * @param application    application
     * @param firstTapIntent Intent used by DefaultHceService when a first tap is detected
     *                       <p/>
     *                       Since 1.0.4g
     */
    public synchronized static void setup(final Application application, Intent firstTapIntent) {
        Class<?> hceServiceClass = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            hceServiceClass = DefaultHceService.class;
        }

        // Check we haven't already been initialised
        if (sInstance != null) {
            return;
        }

        sInstance = new McbpInitializer(application, hceServiceClass);

        // Update the class to use for first tap payments
        sInstance.setFirstTapIntent(firstTapIntent);
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

    /**
     * This method is implemented only to maintain compatibility with
     */
    public void setMobileDeviceInfo() {
        throw new UnsupportedOperationException("Setting mobile device in this way is not "
                                                + "supported for MDES mode.");
    }

    /**
     * Perform the operations and set the variables related to the Wallet activation
     *
     * @param paymentAppInstanceId The Payment Application Instance Id
     * @param registrationId       The Registration Id
     * @param serverUrl            The CMS-D Server Url
     * @param deviceFingerPrint    The Device Finger Print as calculated by the MPA
     * @return true if the wallet activation was successful, false otherwise
     */
    public boolean activate(final String paymentAppInstanceId,
                            final String registrationId,
                            final String serverUrl,
                            final String deviceFingerPrint) {
        try {

            LdeInitParams ldeInitParams =
                    new LdeInitParams(ByteArray.of(paymentAppInstanceId.getBytes()),
                                      ByteArray.of(deviceFingerPrint));
            ldeInitParams.setUrlRemoteManagement(serverUrl);
            ldeInitParams.setRnsMpaId(ByteArray.of(registrationId.getBytes()));
            getSdkContext().getLdeBusinessLogicService().initializeLde(ldeInitParams);
        } catch (McbpCryptoException | InvalidInput | LdeAlreadyInitialized e) {
            return false;
        }
        return true;
    }
}
