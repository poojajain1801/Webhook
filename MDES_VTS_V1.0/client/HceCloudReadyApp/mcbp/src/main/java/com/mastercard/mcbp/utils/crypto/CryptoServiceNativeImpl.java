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

package com.mastercard.mcbp.utils.crypto;

import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

/**
 * AndroidRSECryptoFactory
 */
enum CryptoServiceNativeImpl implements CryptoService {
    INSTANCE;

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getRandomByteArray(final int size) {
        return CryptoServiceImpl.INSTANCE.getRandomByteArray(size);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getRandom(final int size) {
        return CryptoServiceImpl.INSTANCE.getRandom(size);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final TransactionCryptograms buildGenerateAcCryptograms(byte[] cryptogramInput,
                                                                   byte[] umdSessionKey,
                                                                   byte[] mdSessionKey)
            throws McbpCryptoException {
        final byte[] cryptograms = generate_ac(cryptogramInput, umdSessionKey, mdSessionKey);
        final TransactionCryptograms result = extractTransactionCryptograms(cryptograms);
        Utils.clearByteArray(cryptograms);
        return result;
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final TransactionCryptograms buildComputeCcCryptograms(byte[] cryptogramInput,
                                                                  byte[] umdSessionKey,
                                                                  byte[] mdSessionKey)
            throws McbpCryptoException {
        final byte[] cryptograms = compute_cc(cryptogramInput, umdSessionKey, mdSessionKey);
        final TransactionCryptograms result = extractTransactionCryptograms(cryptograms);
        Utils.clearByteArray(cryptograms);
        return result;
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final ByteArray decryptDataEncryptedField(ByteArray data, ByteArray dataEncryptionKey)
            throws McbpCryptoException {
        final byte[] result =
                decryptDataEncryptedField(data.getBytes(), dataEncryptionKey.getBytes());
        return buildByteArray(result);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final byte[] decryptDataEncryptedField(byte[] data, byte[] dataEncryptionKey)
            throws McbpCryptoException {
        return decrypt_data_encrypted_field(data, dataEncryptionKey);
    }

    /**
     * {@inheritDoc}
     */
    public final ByteArray decryptIccComponent(ByteArray data, ByteArray decryptionKey) throws
            McbpCryptoException {
        return buildByteArray(decrypt_icc_component(data.getBytes(), decryptionKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    public final ByteArray decryptIccKey(ByteArray encryptedIccKey, ByteArray decryptionKey)
            throws McbpCryptoException {
        return buildByteArray(decrypt_icc_kek(encryptedIccKey.getBytes(),
                                              decryptionKey.getBytes()));
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final byte[] ldeEncryption(byte[] plainData, byte[] encryptionKey)
            throws McbpCryptoException {
        return lde_encryption(plainData, encryptionKey);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final byte[] ldeDecryption(byte[] encryptedData, byte[] decryptionKey)
            throws McbpCryptoException {
        return lde_decryption(encryptedData, decryptionKey);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final MobileKeys decryptMobileKeys(final byte[] encryptedTransportKey,
                                              final byte[] encryptedMacKey,
                                              final byte[] encryptedDataKey,
                                              final byte[] key) throws McbpCryptoException {
        final byte[] concatenatedKeys = decrypt_mobile_keys(encryptedTransportKey,
                                                            encryptedMacKey,
                                                            encryptedDataKey,
                                                            key);
        final byte[] transportKey = new byte[16];
        final byte[] macKey = new byte[16];
        final byte[] dataEncryptionKey = new byte[16];

        System.arraycopy(concatenatedKeys, 0, transportKey, 0, 16);
        System.arraycopy(concatenatedKeys, 16, macKey, 0, 16);
        System.arraycopy(concatenatedKeys, 32, dataEncryptionKey, 0, 16);

        final MobileKeys mobileKeys = new MobileKeys(transportKey, macKey, dataEncryptionKey);

        // Clear temporary variables from memory
        Utils.clearByteArray(concatenatedKeys);

        return mobileKeys;
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final MobileKeys decryptMobileKeys(final byte[] encryptedTransportKey,
                                              final byte[] encryptedMacKey,
                                              final byte[] encryptedDataKey)
            throws McbpCryptoException {
        throw new RuntimeException("Not Implemented yet");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public ByteArray buildRgkForRegistrationRequest(final ByteArray publicKey)
            throws McbpCryptoException {
        throw new RuntimeException("Not Implemented yet");
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public ByteArray encryptPinBlockUsingRgk(final ByteArray mobilePin,
                                             final String paymentInstanceId)
            throws McbpCryptoException {
        throw new RuntimeException("Not Implemented yet");
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final ByteArray encryptPinBlock(ByteArray pinData,
                                           String paymentInstanceId,
                                           ByteArray key)
            throws McbpCryptoException {
        return buildByteArray(encrypt_pin_block(pinData.getBytes(),
                                                paymentInstanceId.getBytes(),
                                                key.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray sha1(final ByteArray data) throws McbpCryptoException {
        return buildByteArray(sha1(data.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] sha1(final byte[] data) {
        return CryptoServiceImpl.INSTANCE.sha1(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray sha256(final ByteArray data) throws McbpCryptoException {
        return buildByteArray(sha256(data.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] sha256(final byte[] data) {
        return CryptoServiceImpl.INSTANCE.sha256(data);
    }

    /**
     * {@inheritDoc}
     */
    public final ByteArray rsa(final ByteArray data) throws McbpCryptoException {
        return CryptoServiceImpl.INSTANCE.rsa(data);
    }

    /**
     * {@inheritDoc}
     */
    public final byte[] rsa(final byte[] data) throws McbpCryptoException {
        return CryptoServiceImpl.INSTANCE.rsa(data);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final int initRsaPrivateKey(final ByteArray primeP,
                                       final ByteArray primeQ,
                                       final ByteArray primeExponentP,
                                       final ByteArray primeExponentQ,
                                       final ByteArray crtCoefficient) throws McbpCryptoException {
        return CryptoServiceImpl.INSTANCE.initRsaPrivateKey(primeP, primeQ, primeExponentP,
                                                            primeExponentQ, crtCoefficient);

    }


    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray buildServiceRequest(final ByteArray requestData,
                                               final ByteArray macKey,
                                               final ByteArray transportKey,
                                               final ByteArray sessionCode,
                                               final int counter) throws McbpCryptoException {
        final byte[] request = build_service_request(requestData.getBytes(),
                                                     macKey.getBytes(),
                                                     transportKey.getBytes(),
                                                     sessionCode.getBytes(),
                                                     counter);
        return buildByteArray(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] buildServiceRequest(final byte[] requestData,
                                            final byte[] macKey,
                                            final byte[] transportKey,
                                            final byte[] sessionCode,
                                            final int counter) throws McbpCryptoException {
        return build_service_request(requestData,
                                     macKey,
                                     transportKey,
                                     sessionCode,
                                     counter);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptServiceResponse(final ByteArray responseData,
                                                  final ByteArray macKey,
                                                  final ByteArray transportKey,
                                                  final ByteArray sessionCode)
            throws McbpCryptoException {
        final byte[] request = decrypt_service_response(responseData.getBytes(),
                                                        macKey.getBytes(),
                                                        transportKey.getBytes(),
                                                        sessionCode.getBytes());
        return buildByteArray(request);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] decryptServiceResponse(final byte[] responseData,
                                               final byte[] macKey,
                                               final byte[] transportKey,
                                               final byte[] sessionCode) throws McbpCryptoException {
        return decrypt_service_response(responseData,
                                        macKey,
                                        transportKey,
                                        sessionCode);
    }

    /**
     * {@inheritDoc}
     */
    public final ByteArray calculateAuthenticationCode(final ByteArray mobileKeySetId,
                                                       final ByteArray deviceFingerPrint,
                                                       final ByteArray sessionCode)
            throws McbpCryptoException {
        final byte[] request = calculateAuthenticationCode(mobileKeySetId.getBytes(),
                                                           deviceFingerPrint.getBytes(),
                                                           sessionCode.getBytes());
        return buildByteArray(request);
    }

    /**
     * {@inheritDoc}
     */
    public final byte[] calculateAuthenticationCode(final byte[] mobileKeySetId,
                                                    final byte[] deviceFingerPrint,
                                                    final byte[] sessionCode)
            throws McbpCryptoException {
        return calculate_authentication_code(mobileKeySetId, deviceFingerPrint, sessionCode);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptNotificationData(final ByteArray responseData,
                                                   final ByteArray macKey,
                                                   final ByteArray transportKey)
            throws McbpCryptoException {
        final byte[] result = decrypt_notification_data(responseData.getBytes(),
                                                        macKey.getBytes(),
                                                        transportKey.getBytes());
        return buildByteArray(result);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] decryptNotificationData(final byte[] responseData,
                                                final byte[] macKey,
                                                final byte[] transportKey)
            throws McbpCryptoException {
        return decrypt_notification_data(responseData, macKey, transportKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray encryptRandomGeneratedKey(final ByteArray data, final ByteArray key)
            throws McbpCryptoException {
        return buildByteArray(encrypt_random_generated_key(data.getBytes(), key.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void warmUp() {
        // Do nothing right now
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray encryptRetryRequestData(final ByteArray data, final ByteArray key)
            throws McbpCryptoException {
        return buildByteArray(encrypt_retry_request_data(data.getBytes(), key.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray decryptRetryRequestData(final ByteArray data, final ByteArray key)
            throws McbpCryptoException {
        return buildByteArray(decrypt_retry_request_data(data.getBytes(), key.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptMcbpV1NotificationData(final ByteArray encryptedData,
                                                         final ByteArray macData,
                                                         final ByteArray macKey,
                                                         final ByteArray transportKey)
            throws McbpCryptoException {
        return CryptoServiceImpl.INSTANCE.decryptMcbpV1NotificationData(encryptedData,
                                                                        macData,
                                                                        macKey,
                                                                        transportKey);
    }

    /**
     * {@inheritDoc}
     * */
    @Override
    public final ByteArray deriveSessionKey(ByteArray singleUseKey, ByteArray mobilePin)
            throws McbpCryptoException {
        final byte[] sessionKey = unlock_session_key(singleUseKey.getBytes(), mobilePin.getBytes());
        return buildByteArray(sessionKey);
    }

    /**
     * Utility function to extract UMD and MD cryptogram from the values returned by the native
     * methods computeCc and generateAc
     * */
    private static TransactionCryptograms extractTransactionCryptograms(final byte[] cryptograms) {
        final int singleCryptogramLength = cryptograms.length / 2;

        final byte[] umd = new byte[singleCryptogramLength];
        final byte[] md = new byte[singleCryptogramLength];

        System.arraycopy(cryptograms, 0, umd, 0, singleCryptogramLength);
        System.arraycopy(cryptograms, singleCryptogramLength, md, 0, singleCryptogramLength);
        Utils.clearByteArray(cryptograms);

        return new TransactionCryptograms(umd, md);
    }

    /**
     * Utility function that securely build a ByteArray and erase the input byte[]
     *
     * @param data The input byte[] that will be erased once the return value had been generated
     * @return A ByteArray with the value of the byte[]
     *
     * */
    private static ByteArray buildByteArray(final byte[] data) {
        final ByteArray result = ByteArray.of(data);
        Utils.clearByteArray(data);  // Erase the temporary variable
        return result;
    }

    /**
     * Returns both UMD and MD cryptograms as contacted byte array UMD | MD
     * */
    static native byte[] generate_ac(byte[] cryptogramInput,
                                    byte[] umdSessionKey,
                                    byte[] mdSessionKey)
            throws McbpCryptoException;

    /**
     * Returns both UMD and MD cryptograms as concatenated byte array UMD | MD
     * */
    static native byte[] compute_cc(byte[] cryptogramInput,
                                   byte[] umdSessionKey,
                                   byte[] mdSessionKey)
            throws McbpCryptoException;

    /**
     * This function is used to convert the single use key to session key using the mobile pin as
     * key
     * */
    static native byte[] unlock_session_key(byte[] singleUseKey, byte[] mobilePin)
            throws McbpCryptoException;

    /**
     * Returns all the three Mobile Keys in decrypted form as contiguous byte array
     * TRANSPORT_KEY | MAC_KEY | DATA_ENCRYPTION_KEY
     * */
    static native byte[] decrypt_mobile_keys(byte[] encryptedTransportKey,
                                             byte[] encryptedMacKey,
                                             byte[] encryptedDataEncryptionKey,
                                             byte[] encryptionKey) throws McbpCryptoException;

    /**
     * Decrypt the notification data message and verify the MAC
     * */
    static native byte[] decrypt_notification_data(byte[] responseData,
                                                   byte[] macKey,
                                                   byte[] transportKey) throws McbpCryptoException;

    /**
     * Encrypt data to be stored in the LDE
     * */
    static native byte[] lde_encryption(byte[] data, byte[] key) throws McbpCryptoException;

    /**
     * Decrypt data from the LDE
     * */
    static native byte[] lde_decryption(byte[] data, byte[] key) throws McbpCryptoException;

    /**
     * Encrypt a retry request data
     * */
    static native byte[] encrypt_retry_request_data(byte[] data, byte[] key)
            throws McbpCryptoException;

    /**
     * Encrypt a retry request data
     * */
    static native byte[] decrypt_retry_request_data(byte[] data, byte[] key)
            throws McbpCryptoException;

    /**
     * Build and encrypt a service request according to MDES APIs
     * */
    static native byte[] build_service_request(byte[] data,
                                               byte[] macKey,
                                               byte[] transportKey,
                                               byte[] sessionCode,
                                               int counter) throws McbpCryptoException;

    /**
     * Decrypt a CMS Service Response received by the MPA
     * */
    static native byte[] decrypt_service_response(byte[] service_data,
                                                  byte[] mac_key,
                                                  byte[] transport_key,
                                                  byte[] session_code) throws McbpCryptoException;
    /**
     * Decrypt the ICC Key Encryption Key (KEK)
     * */
    static native byte[] decrypt_icc_kek(byte[] data, byte[] key) throws McbpCryptoException;

    /**
     * Decrypt an ICC component
     * */
    static native byte[] decrypt_icc_component(byte[] data, byte[] key) throws McbpCryptoException;

    /**
     * Calculate the authentication code according to MDES specs
     * */
    static native byte[] calculate_authentication_code(byte[] mobileKeysetId,
                                                       byte[] deviceFingerPrint,
                                                       byte[] sessionCode)
            throws McbpCryptoException;

    /**
     * Encrypt the Mobile PIN according to MDES specs
     * */
    static native byte[] encrypt_pin_block(byte[] pinData,
                                           byte[] paymentInstanceId,
                                           byte[] key) throws McbpCryptoException;

    /**
     * Decrypt a data field that was encrypted using a data encryption key
     * */
    static native byte[] decrypt_data_encrypted_field(byte[] data, byte[] key)
            throws McbpCryptoException;

    /**
     * Encrypt the Random Generated Key as per MDES APIs
     * */
    static native byte[] encrypt_random_generated_key(byte[] data, byte[] key)
            throws McbpCryptoException;

    /**
     * Load the mcbp crypto service library. Please note that this is a Java project only and the
     * actual library is loaded as part of the mcbp-android package
     * */
    static {
        System.loadLibrary("mcbpcryptoservice-jni");
    }
}
