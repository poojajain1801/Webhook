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

import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.mdes.models.MobileKeys;
import com.mastercard.mcbp.remotemanagement.mdes.mpamanagementapi.EncryptedRegistrationRequestParameters;
import com.mastercard.mcbp.remotemanagement.mdes.mpamanagementapi.MpaManagement;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

/**
 * MDES CMS-D MPA Management API
 */
public enum MpaManagementApi {
    INSTANCE;

    /**
     * Flag specifying whether this APIs layer has been initialized
     */
    private static boolean sIsInitialized = false;

    /**
     * Reference to the internal Management APIs implementation
     */
    private static MpaManagement sMdesManagementApi = null;

    /**
     * Initialize this API layer.
     * <p/>
     * This API should not be called by a programmer. It is called by internal SDK methods.
     *
     * @param ldeRemoteManagementService The Remote Management Interface of the LDE
     * @param cryptoService              The SDK Crypto service
     * @throws IllegalStateException
     */
    public static void initialize(final LdeRemoteManagementService ldeRemoteManagementService,
                                  final CryptoService cryptoService) throws IllegalStateException {

        // We proceed with initialization only if we have the right flag set
        if (!BuildConfig.MPA_MANAGEMENT_API) return;

        if (sIsInitialized) {
            throw new IllegalStateException("The MpaManagement APIs have been already initialized");
        }

        sMdesManagementApi = new MpaManagement(ldeRemoteManagementService, cryptoService);
        sIsInitialized = true;
    }

    /***
     * Prepare the parameters for the Registration Request
     *
     * @param mobilePin         The Mobile Pin to be encrypted (as byte[]
     * @param paymentInstanceId The Payment Application Instance Id as assigned by MDES
     * @return The parameters for the registration request
     */
    public static RegistrationRequestParameters
    getRegistrationRequestParameters(final byte[] publicKeyBytes, final byte[] mobilePin,
                                     final String paymentInstanceId) {
        validate();
        final ByteArray pin = ByteArray.of(mobilePin);
        try {
            final EncryptedRegistrationRequestParameters parameters =
                    sMdesManagementApi
                            .getRegistrationRequestParameters(ByteArray.of(publicKeyBytes),
                                                              pin,
                                                              paymentInstanceId);
            return new RegistrationRequestParameters(parameters.getEncryptedRandomGeneratedKey(),
                                                     parameters.getEncryptedMobilePinBlock());
        } catch (InvalidInput | McbpCryptoException e) {
            // TODO: Discuss the best exceptions to raise
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            Utils.clearByteArray(pin);
        }
    }

    /***
     * Prepare the parameters for the Registration Request
     * <p/>
     * This function returns an empty encryptedPinBlock. Use the getEncryptedPinBlock to perform
     * the pin encryption separately from the registration.
     *
     * @return The parameters for the registration request
     */
    public static RegistrationRequestParameters
    getRegistrationRequestParameters(byte[] publicKeyBytes) {
        validate();
        try {
            final EncryptedRegistrationRequestParameters parameters =
                    sMdesManagementApi
                            .getRegistrationRequestParameters(ByteArray.of(publicKeyBytes));

            return new RegistrationRequestParameters(parameters.getEncryptedRandomGeneratedKey(),
                                                     parameters.getEncryptedMobilePinBlock());
        } catch (InvalidInput | McbpCryptoException e) {
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /***
     * Put the SDK (and the LDE) into the Registered State
     *
     * @param mobileKeySetId             The Mobile Key Set Id as received in the registration response
     * @param encryptedTransportKey      The encrypted Transport Key as received in the registration
     *                                   response
     * @param encryptedDataEncryptionKey The encrypted Data Encryption Key as received in the
     *                                   registration response
     * @param encryptedMacKey            The encrypted Mac Key as received in the registration response
     * @param remoteManagementUrl        The Remote Management URL that will be then used by the SDK for
     *                                   subsequent communications with the MDES CMS-D
     */
    public static void register(final String mobileKeySetId,
                                final String encryptedTransportKey,
                                final String encryptedMacKey,
                                final String encryptedDataEncryptionKey,
                                final String remoteManagementUrl) {
        validate();
        try {
            MobileKeys mobileKeys = new MobileKeys(ByteArray.of(encryptedTransportKey),
                                                   ByteArray.of(encryptedDataEncryptionKey),
                                                   ByteArray.of(encryptedMacKey));
            sMdesManagementApi.register(mobileKeySetId, mobileKeys, remoteManagementUrl);
        } catch (McbpCryptoException | InvalidInput e) {
            // TODO: Discuss the best exceptions to raise
            throw new IllegalArgumentException(e.getMessage());
        }
    }

    /**
     * Encrypt the Mobile PIN using the Random Generated Key.
     * <p/>
     * This function should not be called to change the PIN. In that case you should use changePin
     * {@link RemoteManagementServices}
     * <p/>
     * This function should be called only in case the registration was performed using
     * {@link #getRegistrationRequestParameters(byte[] publicKeyBytes)}, and it shall be called after {@link #register}
     *
     * @param mobilePin         The mobile PIN value
     * @param paymentInstanceId The Payment Instance Id
     * @return the encrypted PIN Block using the data encryption key
     */
    public static ByteArray getEncryptedPinBlock(final byte[] mobilePin,
                                                 final String paymentInstanceId) {
        validate();
        final ByteArray pin = ByteArray.of(mobilePin);
        try {
            return sMdesManagementApi.getEncryptedPinBlockUsingDek(pin, paymentInstanceId);
        } catch (final McbpCryptoException | InvalidInput e) {
            // TODO: Discuss the best exceptions to raise
            throw new IllegalArgumentException(e.getMessage());
        } finally {
            Utils.clearByteArray(pin);
        }
    }

    /**
     * Check whether the instance of the SDK has been registered (i.e. it contains a valid set of
     * mobile keys)
     *
     * @return true, if the SDK has a valid set of mobile keys, false otherwise
     */
    public static boolean isRegistered() {
        return sMdesManagementApi.isRegistered();
    }

    /**
     * Unregister the existing MPA instance with CMS-D.
     * <p/>
     * This function should be called only in case the registration was performed using
     * {@link #getRegistrationRequestParameters(byte[] publicKeyBytes)}, and it shall be called
     * after {@link #register}
     */
    public static void unregister() {
        validate();
        McbpWalletApi.resetMpaToInstalledState();
    }

    // ---------------------------------------------------------------------------------------------
    // Internal Utility Functions
    // ---------------------------------------------------------------------------------------------

    /**
     * Utility function used to validate whether or not the APIs can be called
     */
    private static void validate() throws IllegalStateException {
        if (!sIsInitialized) {
            throw new IllegalStateException("The MpaManagement APIs have not been initialized");
        }
        if (!BuildConfig.MPA_MANAGEMENT_API) {
            throw new IllegalStateException("MPA Management APIs have not been enabled");
        }
    }
}
