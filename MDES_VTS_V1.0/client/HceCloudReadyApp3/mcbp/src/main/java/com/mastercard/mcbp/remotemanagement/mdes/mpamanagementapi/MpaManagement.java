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

package com.mastercard.mcbp.remotemanagement.mdes.mpamanagementapi;

import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.models.MobileKeys;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

/**
 * Implementation of the MPA Management functions as per MDES 1.0.5 specification
 */
public class MpaManagement {
    /**
     * Instance of the LDE remote management interface
     */
    private final LdeRemoteManagementService mLdeRemoteManagementService;

    /**
     * Instance of the Crypto Service for keys decryption
     */
    private final CryptoService mCryptoService;

    /**
     * Build a new MpaManagement object. A reference to the LDE Remote Management Interface and the
     * Crypto Service must be provided
     *
     * @param ldeRemoteManagementService LDE Remote Management Interface
     * @param cryptoService              Crypto Service
     */
    public MpaManagement(final LdeRemoteManagementService ldeRemoteManagementService,
                         final CryptoService cryptoService) {
        mLdeRemoteManagementService = ldeRemoteManagementService;
        mCryptoService = cryptoService;
    }

    /**
     * Get the registration request parameters but without setting a new PIN
     *
     * @param publicKey The public key to be used to encrypt the random generated key to be sent to
     *                  the CMS-D
     * @return The registration parameters. Note the encryptedPinBlock would be set to null
     */
    public EncryptedRegistrationRequestParameters getRegistrationRequestParameters(
            final ByteArray publicKey)
            throws InvalidInput, McbpCryptoException {
        return getRegistrationRequestParameters(publicKey, null, null);
    }

    /***
     * Get the registration request parameters including the encrypted pin block.
     *
     * @param publicKey         The public key to be used to encrypt the random generated key to be sent to
     *                          the CMS-D
     * @param mobilePin         The Mobile PIN as ByteArray. Only digits [0-9] encoded as ASCII are accepted
     *                          If the mobile PIN is set to null the generation of the encrypted pin block
     *                          is ignored
     * @param paymentInstanceId To be used as PAN surrogate when generating the encrypted pin block
     * @return The registration parameters.
     */
    public EncryptedRegistrationRequestParameters getRegistrationRequestParameters(
            final ByteArray publicKey,
            final ByteArray mobilePin,
            final String paymentInstanceId)
            throws InvalidInput, McbpCryptoException {
        // Generate and encrypt the RGK
        validatePublicKey(publicKey);
        final ByteArray encryptedRgk = mCryptoService.buildRgkForRegistrationRequest(publicKey);

        // If a mobile pin has been supplied, let's calculate build the encrypt pin block
        final ByteArray encryptedPinBlock;
        if (mobilePin != null) {
            validateMobilePin(mobilePin);
            validatePaymentInstanceId(paymentInstanceId);
            encryptedPinBlock = getEncryptedPinBlockUsingRgk(mobilePin, paymentInstanceId);
        } else {
            encryptedPinBlock = null;
        }
        return new EncryptedRegistrationRequestParameters(encryptedRgk, encryptedPinBlock);
    }

    /**
     * Update the internal state and move into registered state
     *
     * @param mobileKeySetId      The Mobile Key Set Id as received from the CMS-D
     * @param mobileKeys          The set of encrypted mobile keys as received from the CMS-D
     * @param remoteManagementUrl The CMS-D remote management URL as received from the CMS-D
     * @throws McbpCryptoException In case of decryption errors
     * @throws InvalidInput        In case of malformed or invalid input
     */
    public void register(final String mobileKeySetId,
                         final MobileKeys mobileKeys,
                         final String remoteManagementUrl)
            throws McbpCryptoException, InvalidInput {
        validateMobileKeySetId(mobileKeySetId);
        validateMobileKeys(mobileKeys);
        validateRemoteManagementUrl(remoteManagementUrl);

        final CryptoService.MobileKeys decryptedMobileKeys =
                mCryptoService.decryptMobileKeys(mobileKeys.getTransportKey().getBytes(),
                                                 mobileKeys.getMacKey().getBytes(),
                                                 mobileKeys.getDataEncryptionKey().getBytes());

        // Decrypting TransportKey
        final ByteArray decryptedTransportKey = ByteArray.of(decryptedMobileKeys.getTransportKey());
        Utils.clearByteArray(decryptedMobileKeys.getTransportKey());

        // Decrypting MacKey
        final ByteArray decryptedMacKey = ByteArray.of(decryptedMobileKeys.getMacKey());
        Utils.clearByteArray(decryptedMobileKeys.getMacKey());

        // Decrypting DataEncryptionKey
        final ByteArray decryptedDataEncryptionKey =
                ByteArray.of(decryptedMobileKeys.getDataEncryptionKey());
        Utils.clearByteArray(decryptedMobileKeys.getDataEncryptionKey());

        try {
            // Store the keys
            storeMobileKeys(mobileKeySetId,
                            decryptedTransportKey,
                            decryptedMacKey,
                            decryptedDataEncryptionKey);
            // Store the remote management URL
            mLdeRemoteManagementService.updateRemoteManagementUrl(remoteManagementUrl);
            // If everything went well, mark the wallet status as registered
            mLdeRemoteManagementService.updateWalletState(WalletState.REGISTER);
        } finally {
            // Securely zeroes memory even if an exception occurs
            Utils.clearByteArray(decryptedTransportKey);
            Utils.clearByteArray(decryptedMacKey);
            Utils.clearByteArray(decryptedDataEncryptionKey);
        }
    }

    /**
     * Generate a PIN encrypted block using the data encryption key.
     * Calling this API requires the LDE to be in a registered state.
     *
     * @param newMobilePin      The new mobile pin
     * @param paymentInstanceId The payment instance id to be used as pan surrogate in the pin block
     *                          generation
     * @return The encrypted pin block
     */
    public ByteArray getEncryptedPinBlockUsingDek(final ByteArray newMobilePin,
                                                  final String paymentInstanceId)
            throws McbpCryptoException, InvalidInput {
        if (!mLdeRemoteManagementService.isLdeInitialized()) {
            throw new IllegalStateException("Registration has not been completed");
        }
        validateMobilePin(newMobilePin);
        validatePaymentInstanceId(paymentInstanceId);

        final ByteArray dataEncryptionKey =
                ByteArray.of(mLdeRemoteManagementService.getDataEncryptionKey());

        return mCryptoService.encryptPinBlock(newMobilePin, paymentInstanceId, dataEncryptionKey);
    }

    /**
     * Check whether the SDK has been registered (e.g. mobile keys have been received)
     *
     * @return true if the SDK has been registered, false otherwise
     */
    public boolean isRegistered() {
        return mLdeRemoteManagementService.getMobileKeySetIdAsByteArray() != null;
    }

    /**
     * Un-register the existing MPA instance with CMSD.
     * This API call will delete all the keys that MPA has received during registration.
     */
    public void unregister() {
        //Remove all the data from database
        mLdeRemoteManagementService.resetMpaToInstalledState();
    }

    // ---------------------------------------------------------------------------------------------
    // Internal Utility Functions
    // ---------------------------------------------------------------------------------------------

    /**
     * Generate a PIN encrypted block using the random generated key
     * This is an internal API only to be used as utility function
     *
     * @param newMobilePin      The new mobile pin
     * @param paymentInstanceId The payment instance id to be used as pan surrogate in the pin block
     *                          generation
     * @return The encrypted pin block
     */
    private ByteArray getEncryptedPinBlockUsingRgk(final ByteArray newMobilePin,
                                                   final String paymentInstanceId)
            throws McbpCryptoException {
        return mCryptoService.encryptPinBlockUsingRgk(newMobilePin, paymentInstanceId);
    }

    /***
     * Insert Mobile Keys into the LDE
     *
     * @param mobileKeySetId    The mobile key set id as received from the CMS-D
     * @param transportKey      The Transport Key
     * @param macKey            The Mack Key
     * @param dataEncryptionKey The Data Encryption Key (aka dek)
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private void storeMobileKeys(final String mobileKeySetId,
                                 final ByteArray transportKey,
                                 final ByteArray macKey,
                                 final ByteArray dataEncryptionKey)
            throws McbpCryptoException, InvalidInput {

        //Insert Keys in DB.
        mLdeRemoteManagementService.insertMobileKeySetId(mobileKeySetId);
        mLdeRemoteManagementService.insertTransportKey(transportKey);
        mLdeRemoteManagementService.insertMacKey(macKey);
        mLdeRemoteManagementService.insertDataEncryptionKey(dataEncryptionKey);
    }

    private void validateMobilePin(final ByteArray mobilePin) throws InvalidInput {
        if (mobilePin == null || mobilePin.getLength() < 4 || mobilePin.getLength() > 8) {
            throw new InvalidInput("Invalid Mobile Pin");
        }
        for (int i = 0; i < mobilePin.getLength(); i++) {
            byte digit = (byte) (mobilePin.getByte(i) - 0x30);
            if (digit < 0 || digit > 9) {
                throw new InvalidInput("Invalid Mobile Pin digit");
            }
        }
    }

    private void validatePaymentInstanceId(String paymentInstanceId) throws InvalidInput {
        if (paymentInstanceId == null) {
            throw new InvalidInput("Invalid Mobile Payment Instance Id");
        }
    }

    private void validateMobileKeySetId(final String mobileKeySetId) throws InvalidInput {
        if (mobileKeySetId == null) {
            throw new InvalidInput("Invalid mobileKeySetId");
        }
    }

    private void validateRemoteManagementUrl(final String remoteManagementUrl) throws InvalidInput {
        if (remoteManagementUrl == null) {
            throw new InvalidInput("Invalid remoteManagementUrl");
        }
    }

    private void validateMobileKeys(final MobileKeys mobileKeys) throws InvalidInput {
        if (mobileKeys == null) {
            throw new InvalidInput("Invalid mobile keys");
        }
        final ByteArray transportKey = mobileKeys.getTransportKey();
        if (transportKey == null || transportKey.getLength() != 16) {
            throw new InvalidInput("Invalid Transport Key");
        }
        final ByteArray macKey = mobileKeys.getMacKey();
        if (macKey == null || macKey.getLength() != 16) {
            throw new InvalidInput("Invalid Mac Key");
        }
        final ByteArray dataEncryptionKey = mobileKeys.getDataEncryptionKey();
        if (dataEncryptionKey == null || dataEncryptionKey.getLength() != 16) {
            throw new InvalidInput("Invalid Data Encryption Key");
        }
    }

    private void validatePublicKey(final ByteArray publicKey) throws InvalidInput {
        if (publicKey == null || publicKey.isEmpty()) {
            throw new InvalidInput("Invalid Public Key");
        }
    }

}
