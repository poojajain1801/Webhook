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

import android.text.TextUtils;

import com.mastercard.mcbp.init.McbpInitializer;
import com.mastercard.mcbp.lde.LdeInitParams;
import com.mastercard.mcbp.utils.PropertyStorageFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.gcm.GcmRegistrationFailed;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * API class used for non-specific interactions with the API.
 */
public class McbpApi extends CommonMcbpApi {

    /**
     * Determine if the MPA SDK is currently uninitialized.
     *
     * @return true if the MPA SDK is current uninitialized; otherwise false.
     */
    public static boolean isUninitialized() {
        return !isInitialized();
    }

    /**
     * Determine if the MPA SDK has been initialized.
     *
     * @return true if the MPA SDK has been initialized; otherwise false.
     */
    public static boolean isInitialized() {
        PropertyStorageFactory propertyStorageFactory = PropertyStorageFactory.getInstance();
        McbpInitializer mcbpInitializer = McbpInitializer.getInstance();
        if (propertyStorageFactory == null || mcbpInitializer == null) {
            return false;
        }

        String paymentAppInstanceId = propertyStorageFactory.getProperty(
                McbpInitializer.PAYMENT_APP_INSTANCE_ID, null);
        String paymentAppProviderId = propertyStorageFactory.getProperty(
                McbpInitializer.PAYMENT_APP_PROVIDER_ID, null);

        if (TextUtils.isEmpty(paymentAppInstanceId) || TextUtils.isEmpty(paymentAppProviderId)) {
            return false;
        }
        return mcbpInitializer.getLdeBusinessLogicService().isLdeInitialized();
    }

    /**
     * Initializes the MPA SDK with the CMS system.
     *
     * @param paymentAppInstanceId Unique identifier for the specific Mobile Payment App instance
     *                             being provisioned to, as assigned by the Payment App Provider.
     * @param paymentAppProviderId Globally unique identifier for the Payment App Provider,
     *                             as assigned by the Digitization Service.
     * @param deviceFingerPrint    The unique device fingerprint.
     * @throws InvalidInput          throws when required parameters is missing
     * @throws GcmRegistrationFailed throws when GCM registration failed
     * @throws LdeAlreadyInitialized throws when SDK is already initialized
     * @throws McbpCryptoException   throws when any cryptographic error occur
     */
    public static void initialize(String paymentAppInstanceId, String paymentAppProviderId,
                                  ByteArray deviceFingerPrint)
            throws InvalidInput, McbpCryptoException, LdeAlreadyInitialized, GcmRegistrationFailed {

        if (TextUtils.isEmpty(paymentAppInstanceId) || TextUtils.isEmpty(paymentAppProviderId)) {
            throw new InvalidInput("Invalid inputs");
        }

        if (deviceFingerPrint == null || deviceFingerPrint.isEmpty()) {
            throw new InvalidInput("Invalid inputs");
        }

        McbpInitializer mcbpInitializer = McbpInitializer.getInstance();

        mcbpInitializer.putProperty(McbpInitializer.PAYMENT_APP_INSTANCE_ID, paymentAppInstanceId);
        mcbpInitializer.putProperty(McbpInitializer.PAYMENT_APP_PROVIDER_ID, paymentAppProviderId);
        LdeInitParams ldeInitParams =
                new LdeInitParams(ByteArray.of(paymentAppInstanceId.getBytes()),
                                  deviceFingerPrint);
        mcbpInitializer.getSdkContext().getLdeBusinessLogicService().initializeLde(ldeInitParams);
    }

}
