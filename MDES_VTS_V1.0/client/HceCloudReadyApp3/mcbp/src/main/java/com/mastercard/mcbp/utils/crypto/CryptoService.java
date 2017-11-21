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
 * Crypto Service
 */
public interface CryptoService {

    /**
     * Container for UMD and MD Transaction Cryptograms
     */
    class TransactionCryptograms {
        /**
         * The User and Mobile Device Cryptogram
         */
        private final byte[] mUmdCryptogram;

        /**
         * The Mobile Device Cryptogram
         */
        private final byte[] mMdCryptogram;

        /**
         * Initialize the Transaction Cryptograms. Both UMD and MD cryptograms must be provided
         *
         * @param umdCryptogram The UMD cryptogram as byte array
         * @param mdCryptogram  The MD Cryptogram as byte array
         */
        public TransactionCryptograms(byte[] umdCryptogram, byte[] mdCryptogram) {
            mUmdCryptogram = umdCryptogram;
            mMdCryptogram = mdCryptogram;
        }

        /**
         * Get the UMD Cryptogram
         *
         * @return the UMD Cryptogram as byte array
         */
        public byte[] getUmdCryptogram() {
            return mUmdCryptogram;
        }

        /**
         * Get the MD Cryptogram
         *
         * @return the MD Cryptogram as byte array
         */
        public byte[] getMdCryptogram() {
            return mMdCryptogram;
        }
    }

    /**
     * Utility container for the Mobile Keys object that are returned by any implementation of
     * the crypto library
     * */
    class MobileKeys {
        /**
         * The Transport Key
         * */
        private final byte[] mTransportKey;
        /**
         * The MAC Key
         * */
        private final byte[] mMacKey;
        /**
         * The Data Encryption Key
         * */
        private final byte[] mDataEncryptionKey;

        /**
         * Constructor. All the three keys must be provided
         * */
        MobileKeys(byte[] transportKey, byte[] macKey, byte[] dataEncryptionKey) {
            mTransportKey = transportKey;
            mMacKey = macKey;
            mDataEncryptionKey = dataEncryptionKey;
        }

        /**
         * Return the Transport Key
         * */
        public byte[] getTransportKey() {
            return mTransportKey;
        }

        /**
         * Return the MAC Key
         * */
        public byte[] getMacKey() {
            return mMacKey;
        }

        /**
         * Return the data encryption key
         * */
        public byte[] getDataEncryptionKey() {
            return mDataEncryptionKey;
        }

        /**
         * Zeroes the content of the Mobile Keys
         * */
        public void wipe() {
            Utils.clearByteArray(mTransportKey);
            Utils.clearByteArray(mMacKey);
            Utils.clearByteArray(mDataEncryptionKey);
        }
    }

    /**
     * Specify the operation supported by each API
     */
    enum Mode {
        ENCRYPT,
        DECRYPT
    }

    /**
     * Generate the random data with given input size.
     *
     * @param size size of random data.
     */
    ByteArray getRandomByteArray(int size);

    /**
     * Generate the random data with given input size.
     *
     * @param size size of random data.
     */
    byte[] getRandom(int size);

    /***
     * Generate both UMD and MD cryptograms for the Generate AC
     *
     * @param cryptogramInput The Input for the generate AC cryptogram calculation
     * @param umdSessionKey   The UMD Session Key
     * @param mdSessionKey    The MD Session Key
     * @return Both UMD and MD cryptograms
     * @throws McbpCryptoException
     */
    TransactionCryptograms buildGenerateAcCryptograms(byte[] cryptogramInput,
                                                      byte[] umdSessionKey,
                                                      byte[] mdSessionKey)
            throws McbpCryptoException;

    /***
     * Generate both UMD and MD cryptograms for the Compute Cryptographic Checksum operation
     *
     * @param cryptogramInput The Input for the ComputeCc cryptogram calculation
     * @param umdSessionKey   The UMD Session Key
     * @param mdSessionKey    The MD Session Key
     * @return Both UMD and MD cryptograms
     * @throws McbpCryptoException
     */
    TransactionCryptograms buildComputeCcCryptograms(byte[] cryptogramInput,
                                                     byte[] umdSessionKey,
                                                     byte[] mdSessionKey)
            throws McbpCryptoException;

    /***
     * Decrypt the Mobile Keys using the given key
     *
     * @param encryptedTransportKey The encrypted transport key
     * @param encryptedMacKey       The encrypted mac key
     * @param encryptedDataKey      The encrypted data key
     * @param key                   The key to use for decryption
     * @return The decrypted set of mobile keys
     */
    @Deprecated
    MobileKeys decryptMobileKeys(byte[] encryptedTransportKey,
                                 byte[] encryptedMacKey,
                                 byte[] encryptedDataKey,
                                 byte[] key) throws McbpCryptoException;

    /***
     * Decrypt the Mobile Keys using the Random Generated Key that is known to Crypto Service only
     *
     * @param encryptedTransportKey The encrypted transport key
     * @param encryptedMacKey       The encrypted mac key
     * @param encryptedDataKey      The encrypted data key
     * @return The decrypted set of mobile keys
     */
    MobileKeys decryptMobileKeys(byte[] encryptedTransportKey,
                                 byte[] encryptedMacKey,
                                 byte[] encryptedDataKey) throws McbpCryptoException;

    /***
     * Decrypt a field that was encrypted using the Data Encryption Key according to MDES APIs
     *
     * @param data              The data to be decrypted
     * @param dataEncryptionKey The key to be used
     * @return The decrypted data field
     * @throws McbpCryptoException
     */
    ByteArray decryptDataEncryptedField(ByteArray data, ByteArray dataEncryptionKey)
            throws McbpCryptoException;

    /***
     * Decrypt a field that was encrypted using the Data Encryption Key according to MDES APIs
     *
     * @param data              The data to be decrypted
     * @param dataEncryptionKey The key to be used
     * @return The decrypted data field
     * @throws McbpCryptoException
     */
    byte[] decryptDataEncryptedField(byte[] data, byte[] dataEncryptionKey)
            throws McbpCryptoException;

    /**
     * Decrypt the ICC Component Key using the algorithm and padding mode as per MDES APIs
     *
     * @param data The data to be decrypted
     * @param key  The key to be used
     * @return The decrypted ICC component
     */
    ByteArray decryptIccComponent(ByteArray data, ByteArray key) throws McbpCryptoException;

    /**
     * Decrypt the ICC Key using the algorithm and padding mode as per MDES APIs
     *
     * @param encryptedIccKey The ICC key to be decrypted
     * @param decryptionKey   The key to be used
     * @return The decrypted ICC component
     */
    ByteArray decryptIccKey(ByteArray encryptedIccKey, ByteArray decryptionKey)
            throws McbpCryptoException;

    /**
     * Decrypt data which has been stored into the database
     *
     * @param encryptedData The data to be decrypted
     * @param decryptionKey The key to be used
     * @return The decrypted ICC component
     */
    byte[] ldeDecryption(byte[] encryptedData, byte[] decryptionKey)
            throws McbpCryptoException;

    /**
     * Encryption data to be stored into the database
     *
     * @param plainData The data to be encrypted
     * @param encryptionKey The key to be used
     * @return The decrypted ICC component
     */
    byte[] ldeEncryption(byte[] plainData, byte[] encryptionKey)
            throws McbpCryptoException;



    /**
     * Encrypt a CMS-D MDES Request
     *
     * @param requestData  The data to be added to the requested formatted as JSON String. See
     *                     MDES APIs document for details
     * @param macKey       The diversified MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @return The request object as byte array
     */
    ByteArray buildServiceRequest(final ByteArray requestData,
                                  final ByteArray macKey,
                                  final ByteArray transportKey,
                                  final ByteArray sessionCode,
                                  final int counter) throws McbpCryptoException;

    /**
     * Encrypt a CMS Request
     *
     * @param requestData  The data to be added to the requested formatted as JSON String. See
     *                     MDES APIs document for details
     * @param macKey       The diversified MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @return The request object as byte array in the format of COUNTERS | ENC_DATA | MAC
     */
    byte[] buildServiceRequest(final byte[] requestData,
                               final byte[] macKey,
                               final byte[] transportKey,
                               final byte[] sessionCode,
                               final int counter) throws McbpCryptoException;

    /**
     * Decrypt a CMS Response
     *
     * @param responseData The data as received from the CMS, which is expected to be in the format
     *                     of COUNTERS | ENC_DATA | MAC
     * @param macKey       The diversified MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @param sessionCode  The session code used to diversify Transport and Mac keys.
     * @return The unencrypted response data
     * @throws McbpCryptoException In case of errors (e.g. MAC verification fails)
     */
    byte[] decryptServiceResponse(final byte[] responseData,
                                  final byte[] macKey,
                                  final byte[] transportKey,
                                  final byte[] sessionCode) throws McbpCryptoException;

    /**
     * Decrypt a CMS Response
     *
     * @param responseData The data as received from the CMS, which is expected to be in the format
     *                     of COUNTERS | ENC_DATA | MAC
     * @param macKey       The diversified MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @param sessionCode  The session code used to diversify Transport and Mac keys
     * @return The unencrypted response data
     * @throws McbpCryptoException In case of errors (e.g. MAC verification fails)
     */
    ByteArray decryptServiceResponse(final ByteArray responseData,
                                     final ByteArray macKey,
                                     final ByteArray transportKey,
                                     final ByteArray sessionCode) throws McbpCryptoException;


    /**
     * Decrypt the data contained in the notification message. The data is considered to be
     * encrypted with AES CBC with Padding
     *
     * @param responseData The data as received from the CMS, which is expected to be in the format
     *                     of ENC_DATA | MAC
     * @param macKey       The MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @return The unencrypted response data
     * @throws McbpCryptoException In case of errors (e.g. MAC verification fails)
     */
    ByteArray decryptNotificationData(final ByteArray responseData,
                                      final ByteArray macKey,
                                      final ByteArray transportKey) throws McbpCryptoException;

    /**
     * Decrypt the data contained in the notification message. The data is considered to be
     * encrypted with AES CBC with Padding
     *
     * @param responseData The data as received from the CMS, which is expected to be in the format
     *                     of ENC_DATA | MAC
     * @param macKey       The MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @return The unencrypted response data
     * @throws McbpCryptoException In case of errors (e.g. MAC verification fails)
     */
    byte[] decryptNotificationData(final byte[] responseData,
                                   final byte[] macKey,
                                   final byte[] transportKey) throws McbpCryptoException;

    /**
     * Decrypt the data contained in the notification message. The data is considered to be
     * encrypted with AES CBC with Padding
     *
     * @param encryptedData The encrypted data as received from the CMS, which is expected to be in
     *                      the format of SESSION_ID | ENC_DATA | MAC
     * @param macData       The MAC of the data within the payload formed as
     *                      SESSION_ID | ENC_DATA | MAC
     * @param macKey        The MAC key to be used to calculate the MAC of the message
     * @param transportKey  The transport key used to encrypt the request data.
     * @return The unencrypted response data
     * @throws McbpCryptoException In case of errors (e.g. MAC verification fails)
     */
    ByteArray decryptMcbpV1NotificationData(final ByteArray encryptedData,
                                            final ByteArray macData,
                                            final ByteArray macKey,
                                            final ByteArray transportKey)
            throws McbpCryptoException;

    /**
     * Calculate the Authentication Code as specified in the MCBP MDES CMS-D APIs
     *
     * @param mobileKeySetId    The Mobile Key Set Id (as byte array)
     * @param deviceFingerPrint The Device Finger Print
     * @param sessionCode       The Session Code
     * @return A byte array containing the authentication code
     */
    ByteArray calculateAuthenticationCode(final ByteArray mobileKeySetId,
                                          final ByteArray deviceFingerPrint,
                                          final ByteArray sessionCode) throws McbpCryptoException;

    /**
     * Calculate the Authentication Code as specified in the MCBP MDES CMS-D APIs
     *
     * @param mobileKeySetId    The Mobile Key Set Id (as byte array)
     * @param deviceFingerPrint The Device Finger Print
     * @param sessionCode       The Session Code
     * @return A byte array containing the authentication code
     */
    byte[] calculateAuthenticationCode(final byte[] mobileKeySetId,
                                       final byte[] deviceFingerPrint,
                                       final byte[] sessionCode) throws McbpCryptoException;


    /**
     * Creates a SHA1 hash of the data.
     *
     * @param data the data to hash as ByteArray
     * @return the hash as ByteArray
     * @throws McbpCryptoException
     */
    ByteArray sha1(ByteArray data) throws McbpCryptoException;

    /**
     * Creates a SHA1 hash of the data.
     *
     * @param data the data to hash as byte[]
     * @return the hash as byte[]
     * @throws McbpCryptoException
     */
    byte[] sha1(byte[] data) throws McbpCryptoException;

    /**
     * Creates a SHA256 hash of the data.
     *
     * @param data the data to hash as ByteArray
     * @return the hash as ByteArray
     * @throws McbpCryptoException
     */
    ByteArray sha256(ByteArray data) throws McbpCryptoException;

    /**
     * Creates a SHA256 hash of the data.
     *
     * @param data the data to hash as byte[]
     * @return the hash as byte[]
     * @throws McbpCryptoException
     */
    byte[] sha256(byte[] data) throws McbpCryptoException;

    /**
     * Encrypts the data with the RSA cipher
     *
     * @param data the data to sign as ByteArray
     * @return the signed data as ByteArray
     * @throws McbpCryptoException
     */
    ByteArray rsa(ByteArray data) throws McbpCryptoException;

    /**
     * Encrypts the data with the RSA cipher
     *
     * @param data the data to sign as byte[]
     * @return the signed data as byte[]
     * @throws McbpCryptoException
     */
    byte[] rsa(byte[] data) throws McbpCryptoException;

    /**
     * Initialize RSA private key.
     *
     * @param primeP         The Prime P
     * @param primeQ         The Prime Q
     * @param primeExponentP The Prime Exponent P
     * @param primeExponentQ The Prime Exponent Q
     * @param crtCoefficient The Certificate Coefficient
     * @return the cipher text
     * @throws McbpCryptoException
     */
    int initRsaPrivateKey(final ByteArray primeP,
                          final ByteArray primeQ,
                          final ByteArray primeExponentP,
                          final ByteArray primeExponentQ,
                          final ByteArray crtCoefficient)
            throws McbpCryptoException;

    /**
     * Encrypt using the RSA Public
     */
    @Deprecated
    ByteArray encryptRandomGeneratedKey(ByteArray data, ByteArray key) throws McbpCryptoException;

    /**
     * Load libraries and perform dummy operations to make sure core crypto libraries are in memory
     * when needed.
     * <p/>
     * Such method may help improving the response time of MPP Lite cryptogram generation especially
     * for the first transaction after the application has been started
     */
    void warmUp();

    /***
     * Encrypt the PIN Block according to the ISO/FDIS - 9564 Format 4 PIN block encipher
     *
     * @param pinData           The PIN Block
     * @param paymentInstanceId The Payment Instance ID, which will be used to generate the PAN
     *                          surrogate
     * @param key               The Encryption Key
     * @return The encrypted PIN Block
     */
    ByteArray encryptPinBlock(ByteArray pinData, String paymentInstanceId, ByteArray key)
            throws McbpCryptoException;

    /***
     * Encrypt the PIN Block according to the ISO/FDIS - 9564 Format 4 PIN block encipher
     *
     * @param pinData           The PIN Block
     * @param paymentInstanceId The Payment Instance ID, which will be used to generate the PAN
     *                          surrogate
     * @return The encrypted PIN Block
     */
    ByteArray encryptPinBlockUsingRgk(ByteArray pinData, String paymentInstanceId)
            throws McbpCryptoException;

    /***
     * Derive the session key by combining the single use key and the mobile pin
     * @param singleUseKey The Single Use Key
     * @param mobilePin The Mobile Pin
     * @return The Session Key
     */
    ByteArray deriveSessionKey(ByteArray singleUseKey, ByteArray mobilePin)
            throws McbpCryptoException;

    /**
     * Build a Random Generated Key and encrypt it with the CMS-D Public Key
     * */
    ByteArray buildRgkForRegistrationRequest(final ByteArray publicKey) throws McbpCryptoException;

    /**
     * Encrypt the retry request data
     *
     * @param data input data
     * @param key  encryption key
     * @return Encrypted retry request data
     * @throws McbpCryptoException
     *
     * This API will be removed in future releases
     */
    @Deprecated
    ByteArray encryptRetryRequestData(ByteArray data, ByteArray key) throws McbpCryptoException;

    /**
     * Decrypt the retry request data
     *
     * @param data input encrypted data
     * @param key  decryption key
     * @return Decrypted retry request data
     * @throws McbpCryptoException
     *
     * This API will be removed in future releases
     */
    @Deprecated
    ByteArray decryptRetryRequestData(ByteArray data, ByteArray key) throws McbpCryptoException;
}
