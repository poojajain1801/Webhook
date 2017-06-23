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
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.interfaces.RSAPrivateKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAPrivateCrtKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.Mac;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.DESKeySpec;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import static com.mastercard.mcbp.utils.crypto.CryptoService.Mode.ENCRYPT;

/**
 * AndroidRSECryptoFactory
 */
enum CryptoServiceImpl implements CryptoService {
    INSTANCE;

    /**
     * Default block size
     */
    private final static int DEFAULT_BLOCK_SIZE = 16;
    /**
     * The RSA cipher. This variable is set by initRSA before the transaction to
     * optimize the transaction time.
     */
    private static Cipher rsaCipher = null;

    /**
     * Buffer to temporarily store the random generated key
     */
    private static ByteArray sRandomGeneratedKey = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray getRandomByteArray(final int size) {
        return ByteArray.of(getRandom(size));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] getRandom(final int size) {
        final byte[] randomVector = new byte[size];
        try {
            SecureRandom s = SecureRandom.getInstance("SHA1PRNG");
            s.nextBytes(new byte[1]); // force seed
            s.nextBytes(randomVector);
        } catch (NoSuchAlgorithmException e) {
            new Random().nextBytes(randomVector);
        }
        return randomVector;
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final TransactionCryptograms buildGenerateAcCryptograms(byte[] cryptogramInput,
                                                                   byte[] umdSessionKey,
                                                                   byte[] mdSessionKey)
            throws McbpCryptoException {
        final byte[] umdCryptogram = mac(cryptogramInput, umdSessionKey);
        final byte[] mdCryptogram = mac(cryptogramInput, mdSessionKey);
        return new TransactionCryptograms(umdCryptogram, mdCryptogram);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final TransactionCryptograms buildComputeCcCryptograms(byte[] cryptogramInput,
                                                                  byte[] umdSessionKey,
                                                                  byte[] mdSessionKey)
            throws McbpCryptoException {
        final byte[] umdCryptogram = des3(cryptogramInput, umdSessionKey, Mode.ENCRYPT);
        final byte[] mdCryptogram = des3(cryptogramInput, mdSessionKey, Mode.ENCRYPT);
        return new TransactionCryptograms(umdCryptogram, mdCryptogram);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final MobileKeys decryptMobileKeys(final byte[] encryptedTransportKey,
                                              final byte[] encryptedMacKey,
                                              final byte[] encryptedDataKey,
                                              final byte[] key) throws McbpCryptoException {
        final byte[] transportKey = aesEcb(encryptedTransportKey, key, Mode.DECRYPT);
        final byte[] macKey = aesEcb(encryptedMacKey, key, Mode.DECRYPT);
        final byte[] dataEncryptionKey = aesEcb(encryptedDataKey, key, Mode.DECRYPT);

        return new MobileKeys(transportKey, macKey, dataEncryptionKey);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final MobileKeys decryptMobileKeys(final byte[] encryptedTransportKey,
                                              final byte[] encryptedMacKey,
                                              final byte[] encryptedDataKey)
            throws McbpCryptoException {
        if (sRandomGeneratedKey == null) {
            throw new McbpCryptoException("RGK not available");
        }
        final byte[] key = sRandomGeneratedKey.getBytes();
        final byte[] transportKey = aesEcb(encryptedTransportKey, key, Mode.DECRYPT);
        final byte[] macKey = aesEcb(encryptedMacKey, key, Mode.DECRYPT);
        final byte[] dataEncryptionKey = aesEcb(encryptedDataKey, key, Mode.DECRYPT);

        Utils.clearByteArray(sRandomGeneratedKey);
        sRandomGeneratedKey = null;

        return new MobileKeys(transportKey, macKey, dataEncryptionKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] ldeEncryption(byte[] encryptedData, byte[] encryptionKey)
            throws McbpCryptoException {
        return aesEcbWithPadding(encryptedData, encryptionKey, Mode.ENCRYPT);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] ldeDecryption(byte[] encryptedData, byte[] decryptionKey)
            throws McbpCryptoException {
        return aesEcbWithPadding(encryptedData, decryptionKey, Mode.DECRYPT);

    }

    /**
     * Calculate the Mac of the byte array <code>dataToMac</code> using SHA256
     *
     * @param dataToMac the data to mac as ByteArray
     * @param key       the key as ByteArray
     * @return the MAC SHA 256 as ByteArray
     * @throws McbpCryptoException
     */
    final ByteArray macSha256(ByteArray dataToMac, ByteArray key)
            throws McbpCryptoException {
        return ByteArray.of(macSha256(dataToMac.getBytes(), key.getBytes()));
    }

    /**
     * Calculate the Mac of the byte array <code>dataToMac</code> using SHA256
     *
     * @param dataToMac the data to mac as byte[]
     * @param key       the key as byte[]
     * @return the  MAC SHA 256 as ByteArray as byte[]
     * @throws McbpCryptoException
     */
    final byte[] macSha256(byte[] dataToMac, byte[] key) throws McbpCryptoException {
        final String algorithm = "HmacSHA256";
        final SecretKeySpec secretKey = new SecretKeySpec(key, "AES");
        try {
            final Mac sha256Hmac = Mac.getInstance(algorithm);
            sha256Hmac.init(secretKey);
            return sha256Hmac.doFinal(dataToMac);
        } catch (InvalidKeyException | NoSuchAlgorithmException e) {
            throw new McbpCryptoException(e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] decryptDataEncryptedField(byte[] data, byte[] dataEncryptionKey)
            throws McbpCryptoException {
        return aesEcb(data, dataEncryptionKey, Mode.DECRYPT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptDataEncryptedField(ByteArray data, ByteArray dataEncryptionKey)
            throws McbpCryptoException {
        final byte[] decryptedData = decryptDataEncryptedField(data.getBytes(),
                                                               dataEncryptionKey.getBytes());
        final ByteArray returnValue = ByteArray.of(decryptedData);
        Utils.clearByteArray(decryptedData);
        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptIccComponent(ByteArray data, ByteArray decryptionKey) throws
            McbpCryptoException {
        return aesEcbWithPadding(data, decryptionKey, Mode.DECRYPT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptIccKey(ByteArray encryptedIccKey, ByteArray decryptionKey)
            throws McbpCryptoException {
        return aesEcbWithPadding(encryptedIccKey, decryptionKey, Mode.DECRYPT);
    }

    /**
     * Encrypts or decrypts with an AES cipher in CBC mode and manually add/remove padding as needed
     *
     * @param data the data to encrypt or decrypt as byte[]
     * @param bKey the encryption/decryption key as byte[]
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as byte[]
     * @throws McbpCryptoException
     */
    final byte[] aesCbcWithPadding(final byte[] data, final byte[] bKey, final Mode mode)
            throws McbpCryptoException {
        if (mode == Mode.ENCRYPT) {
            byte[] dataWithPadding = addIso7816Padding(data);
            return aesCbc(dataWithPadding, bKey, Mode.ENCRYPT);
        }
        return removeIso7816Padding(aesCbc(data, bKey, Mode.DECRYPT));
    }

    /**
     * Encrypts or decrypts with an AES in ECB mode cipher.
     *
     * @param data the data to encrypt or decrypt as byte[]
     * @param bKey the encryption/decryption key as byte[]
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as byte[]
     * @throws McbpCryptoException
     */
    final byte[] aesCbc(final byte[] data,
                        final byte[] bKey,
                        final Mode mode) throws McbpCryptoException {
        return aes(data, bKey, mode, false);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray sha1(final ByteArray data) throws McbpCryptoException {
        return ByteArray.of(sha1(data.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] sha1(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray sha256(final ByteArray data) throws McbpCryptoException {
        return ByteArray.of(sha256(data.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] sha256(final byte[] data) {
        try {
            return MessageDigest.getInstance("SHA-256").digest(data);
        } catch (NoSuchAlgorithmException e) {
            // This should never happen in reality as we rely on a standard Java distribution
            throw new RuntimeException(e.toString());
        }
    }

    /**
     * {@inheritDoc}
     */
    public final ByteArray rsa(final ByteArray data) throws McbpCryptoException {
        return ByteArray.of(rsa(data.getBytes()));
    }

    /**
     * {@inheritDoc}
     */
    public final byte[] rsa(final byte[] data) throws McbpCryptoException {
        try {
            return rsaCipher.doFinal(data);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new McbpCryptoException(e.toString());
        }
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
        try {
            final BigInteger p = new BigInteger(primeP.toHexString(), 16);
            final BigInteger q = new BigInteger(primeQ.toHexString(), 16);
            final BigInteger dp = new BigInteger(primeExponentP.toHexString(), 16);
            final BigInteger dq = new BigInteger(primeExponentQ.toHexString(), 16);
            final BigInteger a = new BigInteger(crtCoefficient.toHexString(), 16);

            final BigInteger n = p.multiply(q);
            final BigInteger e = dp.modInverse(p.subtract(BigInteger.ONE));

            final BigInteger d = e.modInverse(
                    p.subtract(BigInteger.ONE)
                     .multiply(q.subtract(BigInteger.ONE))
                     .divide((p.subtract(BigInteger.ONE))
                                     .gcd(q.subtract(BigInteger.ONE))));

            final RSAPrivateKey rsaKey =
                    (RSAPrivateKey) KeyFactory
                            .getInstance("RSA")
                            .generatePrivate(new RSAPrivateCrtKeySpec(n, e, d, p, q, dp, dq, a));

            initRsaPrivate(rsaKey);

            return n.bitLength() / 8;

        } catch (final NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new McbpCryptoException(e.toString());
        }
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
        final byte[] serviceRequest = buildServiceRequest(requestData.getBytes(),
                                                          macKey.getBytes(),
                                                          transportKey.getBytes(),
                                                          sessionCode.getBytes(),
                                                          counter);

        // We need to use a temporary return variable to ensure we can securely zeroes used memory
        final ByteArray returnValue = ByteArray.of(serviceRequest);
        Utils.clearByteArray(serviceRequest);

        return returnValue;
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

        final byte[] derivedTransportKey = deriveMobileSessionKey(transportKey, sessionCode);
        final byte[] derivedMacKey = deriveMobileSessionKey(macKey, sessionCode);

        final byte[] encryptedData =
                encryptServiceRequest(requestData, derivedTransportKey, counter);
        final byte[] mac = aesCbcMac(encryptedData, derivedMacKey);
        final byte[] counters = buildIvFromCounters(counter, true);

        final byte[] serviceRequest = new byte[3 + encryptedData.length + mac.length];

        System.arraycopy(counters, 1, serviceRequest, 0, 3);
        System.arraycopy(encryptedData, 0, serviceRequest, 3, encryptedData.length);
        System.arraycopy(mac, 0, serviceRequest, 3 + encryptedData.length, mac.length);

        Utils.clearByteArray(derivedTransportKey);
        Utils.clearByteArray(derivedMacKey);
        Utils.clearByteArray(encryptedData);
        Utils.clearByteArray(mac);
        Utils.clearByteArray(counters);

        return serviceRequest;
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
        final byte[] serviceResponse = decryptServiceResponse(responseData.getBytes(),
                                                              macKey.getBytes(),
                                                              transportKey.getBytes(),
                                                              sessionCode.getBytes());

        // We need to use a temporary return variable to ensure we can securely zeroes used memory
        final ByteArray returnValue = ByteArray.of(serviceResponse);
        Utils.clearByteArray(serviceResponse);

        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] decryptServiceResponse(final byte[] responseData,
                                               final byte[] macKey,
                                               final byte[] transportKey,
                                               final byte[] sessionCode) throws
            McbpCryptoException {

        final byte[] derivedTransportKey = deriveMobileSessionKey(transportKey, sessionCode);
        final byte[] derivedMacKey = deriveMobileSessionKey(macKey, sessionCode);

        final byte[] serviceResponse = validateMacAndDecryptServiceResponse(responseData,
                                                                            derivedMacKey,
                                                                            derivedTransportKey);

        Utils.clearByteArray(derivedTransportKey);
        Utils.clearByteArray(derivedMacKey);

        return serviceResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray buildRgkForRegistrationRequest(final ByteArray publicKey)
            throws McbpCryptoException {
        if (sRandomGeneratedKey != null) {
            // We ignore existing registration, and we re-start
            Utils.clearByteArray(sRandomGeneratedKey);
        }
        sRandomGeneratedKey = getRandomByteArray(16);
        return encryptRandomGeneratedKey(sRandomGeneratedKey, publicKey);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray encryptPinBlockUsingRgk(final ByteArray mobilePin,
                                             final String paymentInstanceId)
            throws McbpCryptoException {
        if (sRandomGeneratedKey == null) {
            throw new IllegalStateException("Unable to find a valid RGK. Have you generated one?");
        }
        return encryptPinBlock(mobilePin, paymentInstanceId, sRandomGeneratedKey);
    }

    /**
     * Validate the MAC and decrypt the service response from the CMS
     *
     * @param responseData The data as received from the CMS, which is expected to be in the format
     *                     of COUNTERS | ENC_DATA | MAC
     * @param macKey       The diversified MAC key to be used to calculate the MAC of the message
     * @param transportKey The transport key used to encrypt the request data.
     * @return The unencrypted response data
     * @throws McbpCryptoException In case of errors (e.g. MAC verification fails)
     */
    final byte[] validateMacAndDecryptServiceResponse(final byte[] responseData,
                                                      final byte[] macKey,
                                                      final byte[] transportKey)
            throws McbpCryptoException {

        final int encryptedDataLength = responseData.length - 3 - 8;
        if (encryptedDataLength <= 0) {
            throw new McbpCryptoException("Invalid responseData message");
        }
        final byte[] counters = new byte[3];
        final byte[] encryptedData = new byte[encryptedDataLength];
        final byte[] receivedMac = new byte[8];

        System.arraycopy(responseData, 0, counters, 0, 3);
        System.arraycopy(responseData, 3, encryptedData, 0, encryptedDataLength);
        System.arraycopy(responseData, 3 + encryptedDataLength, receivedMac, 0, 8);

        // Note: counters[x] & 0x00FF makes sure we do not end up with problems in case of negative
        //       byte values (e.g. 0x3F) which may be affected when the byte is promoted to int
        //       for the actual calculation
        final int counter = ((counters[1] & 0x00FF) << 8) + (counters[2] & 0x00FF);


        final byte[] calculatedMac = aesCbcMac(encryptedData, macKey);
        if (!Arrays.equals(receivedMac, calculatedMac)) {
            throw new McbpCryptoException("Calculated MAC does not match the received one");
        }

        final byte[] serviceResponse =
                decryptServiceResponse(encryptedData, transportKey, counter);

        Utils.clearByteArray(counters);
        Utils.clearByteArray(encryptedData);
        Utils.clearByteArray(receivedMac);
        Utils.clearByteArray(calculatedMac);

        return serviceResponse;
    }

    /**
     * {@inheritDoc}
     */
    public final ByteArray calculateAuthenticationCode(final ByteArray mobileKeySetId,
                                                       final ByteArray sessionCode,
                                                       final ByteArray deviceFingerPrint) {
        final byte[] authenticationCode = calculateAuthenticationCode(mobileKeySetId.getBytes(),
                                                                      sessionCode.getBytes(),
                                                                      deviceFingerPrint.getBytes());

        // We need to use a temporary return variable to ensure we can securely zeroes used memory
        final ByteArray returnValue = ByteArray.of(authenticationCode);
        Utils.clearByteArray(authenticationCode);

        return returnValue;
    }

    /**
     * {@inheritDoc}
     */
    public final byte[] calculateAuthenticationCode(final byte[] mobileKeySetId,
                                                    final byte[] sessionCode,
                                                    final byte[] deviceFingerPrint) {
        final byte[] dataToHash = new byte[mobileKeySetId.length +
                                           sessionCode.length +
                                           deviceFingerPrint.length];

        int start = 0;

        System.arraycopy(mobileKeySetId, 0, dataToHash, start, mobileKeySetId.length);
        start += mobileKeySetId.length;
        System.arraycopy(sessionCode, 0, dataToHash, start, sessionCode.length);
        start += sessionCode.length;
        System.arraycopy(deviceFingerPrint, 0, dataToHash, start, deviceFingerPrint.length);

        final byte[] authenticationCode = sha256(dataToHash);

        Utils.clearByteArray(dataToHash);
        return authenticationCode;
    }

    /**
     * Encrypt a Service Request according to Mcbp Communication Protocol
     * <p/>
     * The counter value is used to generate the appropriate IV for AES. This implementation assumes
     * that the encryption is always done by the MPA when sending data to the CMS
     *
     * @param data    The Service data to be encrypted as ByteArray
     * @param key     The key to be used for encryption as ByteArray
     * @param counter The counter value associated with this message (used for IV calculation)
     * @return the encrypted service data as ByteArray
     */
    final ByteArray encryptServiceRequest(final ByteArray data,
                                          final ByteArray key,
                                          final int counter)
            throws McbpCryptoException {
        return ByteArray.of(encryptServiceRequest(data.getBytes(), key.getBytes(), counter));
    }

    /**
     * Encrypt a Service Request according to Mcbp Communication Protocol
     * <p/>
     * The counter value is used to generate the appropriate IV for AES. This implementation assumes
     * that the encryption is always done by the MPA when sending data to the CMS
     * <p/>
     * Note: It always removes padding in ISO/IEC 7816-4 format (e.g. 0x80, 0x00, ..., 0x00)
     *
     * @param data    The Service data to be encrypted as byte[]
     * @param key     The key to be used for encryption as byte[]
     * @param counter The counter value associated with this message (used for IV calculation)
     * @return the encrypted service data as byte[]
     */
    final byte[] encryptServiceRequest(final byte[] data,
                                       final byte[] key,
                                       final int counter) throws McbpCryptoException {
        // True as we are sending from the MPA
        final byte[] iv = buildIvFromCounters(counter, true);
        return aesCtrNoPadding(data, iv, key, Mode.ENCRYPT);
    }

    /**
     * {@inheritDoc}
     */
    final ByteArray decryptServiceResponse(final ByteArray data,
                                           final ByteArray key,
                                           final int counter) throws McbpCryptoException {
        return ByteArray.of(decryptServiceResponse(data.getBytes(), key.getBytes(), counter));
    }

    /**
     * {@inheritDoc}
     */
    final byte[] decryptServiceResponse(final byte[] data,
                                        final byte[] key,
                                        final int counter)
            throws McbpCryptoException {
        // false as we are receiving from CMS
        final byte[] iv = buildIvFromCounters(counter, false);
        return aesCtrNoPadding(data, iv, key, Mode.DECRYPT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final ByteArray decryptNotificationData(final ByteArray responseData,
                                                   final ByteArray macKey,
                                                   final ByteArray transportKey)
            throws McbpCryptoException {
        final byte[] result = decryptNotificationData(responseData.getBytes(),
                                                      macKey.getBytes(),
                                                      transportKey.getBytes());
        final ByteArray notificationData = ByteArray.of(result);
        // Clear temporary arrays
        Utils.clearByteArray(result);
        return notificationData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final byte[] decryptNotificationData(final byte[] responseData,
                                                final byte[] macKey,
                                                final byte[] transportKey)
            throws McbpCryptoException {

        final int encryptedDataLength = responseData.length - 8;
        if (encryptedDataLength <= 16) {
            throw new McbpCryptoException("Invalid responseData message");
        }

        final byte[] encryptedData = new byte[encryptedDataLength];
        final byte[] receivedMac = new byte[8];

        System.arraycopy(responseData, 0, encryptedData, 0, encryptedDataLength);
        System.arraycopy(responseData, encryptedDataLength, receivedMac, 0, 8);

        final byte[] calculatedMac = aesCbcMac(encryptedData, macKey);
        if (!Arrays.equals(receivedMac, calculatedMac)) {
            throw new McbpCryptoException("Calculated MAC does not match the received one");
        }

        final byte[] serviceResponse = aesCbcWithPadding(encryptedData, transportKey, Mode.DECRYPT);

        Utils.clearByteArray(encryptedData);
        Utils.clearByteArray(receivedMac);

        return serviceResponse;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("all")  // Used to avoid warnings on ECB mode.
    public final ByteArray encryptRandomGeneratedKey(final ByteArray data, final ByteArray key)
            throws McbpCryptoException {
        final byte[] result;
        try {
            final Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
            final X509EncodedKeySpec x509EncodedKeySpec = new X509EncodedKeySpec(key.getBytes());

            cipher.init(Cipher.ENCRYPT_MODE, KeyFactory.getInstance("RSA").
                    generatePublic(x509EncodedKeySpec));

            result = cipher.doFinal(data.getBytes());
        } catch (NoSuchAlgorithmException | InvalidKeySpecException | NoSuchPaddingException |
                InvalidKeyException | BadPaddingException | IllegalBlockSizeException e) {
            throw new McbpCryptoException(e.getMessage());
        }
        final ByteArray encryptedData = ByteArray.of(result);
        Utils.clearByteArray(result);  // We need to clean up temporary variables
        return encryptedData;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray encryptRetryRequestData(final ByteArray data, final ByteArray key)
            throws McbpCryptoException {
        return ByteArray.of(aesEcbWithPadding(data.getBytes(), key.getBytes(), Mode.ENCRYPT));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ByteArray decryptRetryRequestData(final ByteArray data, final ByteArray key)
            throws McbpCryptoException {
        return ByteArray.of(aesEcbWithPadding(data.getBytes(), key.getBytes(), Mode.DECRYPT));
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
        // Calculate MAC over encrypted message to validate with the received RNS MAC
        final ByteArray calculatedMac = aesCbcMac(encryptedData, macKey);
        if (!Arrays.equals(calculatedMac.getBytes(), macData.getBytes())) {
            // MAC mismatch, ignore the message
            throw new McbpCryptoException("MAC mismatch");
        }
        return aesEcb(encryptedData, transportKey, Mode.DECRYPT);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public final void warmUp() {
        final byte[] inputMac = getRandom(DEFAULT_BLOCK_SIZE * 5);
        final byte[] desKey = getRandom(16);
        byte[] mac;
        try {
            mac = mac(inputMac, desKey);
            mac = des3(mac, desKey, Mode.ENCRYPT);
        } catch (McbpCryptoException e) {
            // We can ignore here if something goes wrong
            return;
        }

        /*
        final KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (final NoSuchAlgorithmException e) {
            // We can ignore here if something goes wrong
            return;
        }
        */

        // TODO: Refine the warm up function as with Random Key (or random data) the library
        // may return exceptions (on Android only).
        final String p = "CDCF9FDA4FC8BDBE4F641A39CD858BF0C64C80CC2055C041FF32B53E6BD8DC51B3"
                         + "AFB13BF0D5E5DAB7537C63A84D3C19";
        final String q = "C89EB6CFA22566083268CE3F975850E0F3695FF199791A27394EB8E9137619C6DA"
                         + "65056F4D9BA4D733ACED9108F48443";
        final String dp = "8935153C35307E7EDF98117BDE5907F5D98855DD6AE3D58154CC78D447E5E8367"
                          + "7CA7627F5E3EE91CF8CFD97C588D2BB";
        final String dq = "85BF248A6C18EEB0219B342A64E58B40A2463FF66650BC1A26347B460CF966849"
                          + "198AE4A33BD188F77C89E60B0A302D7";
        final String a = "BDFF1436301672F1B29C3EC7A4C6C4A5F54058A5925393BEAFB1EAA83050BBF27E"
                         + "C745ACBF2BA0B10FBE89E99B057725";

        final int privateModulusSize;
        try {
            privateModulusSize =
                    initRsaPrivateKey(ByteArray.of(p),
                                      ByteArray.of(q),
                                      ByteArray.of(dp),
                                      ByteArray.of(dq),
                                      ByteArray.of(a));

        } catch (final McbpCryptoException e) {
            // We can ignore here if something goes wrong
            return;
        }
        final byte[] rsaEncryptedData;
        try {
            rsaEncryptedData = rsa(new byte[privateModulusSize]);
        } catch (McbpCryptoException e) {
            // We can ignore here if something goes wrong
            return;
        }
        final byte[] inputHash = new byte[rsaEncryptedData.length + mac.length];
        System.arraycopy(mac, 0, inputHash, 0, mac.length);
        System.arraycopy(rsaEncryptedData, 0, inputHash, mac.length, rsaEncryptedData.length);
    }

    /***
     * {@inheritDoc}
     */
    @Override
    public final ByteArray encryptPinBlock(ByteArray pinData,
                                           String paymentInstanceId,
                                           ByteArray key)
            throws McbpCryptoException {
        final ByteArray panSurrogate = generatePanSubstituteValue(paymentInstanceId);
        final ByteArray generatePlainTextPinField = generatePlainTextPinField(pinData);
        final ByteArray generatePlainTextPanField = generatePlainTextPanField(panSurrogate);
        final ByteArray intermediateBlockA = aesEcb(generatePlainTextPinField, key, ENCRYPT);
        final ByteArray byteArrayBlockB =
                ByteArray.of(Utils.doXor(intermediateBlockA, generatePlainTextPanField, 16));
        return aesEcb(byteArrayBlockB, key, ENCRYPT);
    }

    /***
     * Decrypt the PIN Block according to the ISO/FDIS - 9564 Format 4 PIN block encipher
     *
     * @param pinData           The PIN Block
     * @param paymentInstanceId The Payment Instance ID, which will be used to generate the PAN
     *                          surrogate
     * @param key               The Encryption Key
     * @return The decrypted PIN Block
     */
    public final ByteArray decryptPinBlock(ByteArray pinData,
                                           String paymentInstanceId,
                                           ByteArray key)
            throws McbpCryptoException {
        final ByteArray panSurrogate = generatePanSubstituteValue(paymentInstanceId);
        final ByteArray decipheredData = aesEcb(pinData, key, Mode.DECRYPT);
        final ByteArray generatePlainTextPanField = generatePlainTextPanField(panSurrogate);
        final ByteArray intermediateBlockA =
                ByteArray.of(Utils.doXor(decipheredData, generatePlainTextPanField, 16));
        return aesEcb(intermediateBlockA, key, Mode.DECRYPT);
    }

    @Override
    public final ByteArray deriveSessionKey(ByteArray singleUseKey, ByteArray mobilePin) {
        // First shift the PIN
        final byte[] shiftedPin = new byte[mobilePin.getLength()];
        for (int i = 0; i < shiftedPin.length; i++) {
            shiftedPin[i] = (byte) (mobilePin.getByte(i) << 1);
        }
        // Now combine the shifted PIN with the single use key to get the session key
        final ByteArray sessionKey = ByteArray.of(singleUseKey);
        final byte data[] = sessionKey.getBytes(); // We just use this to simplify notation

        int len = mobilePin.getLength() < 8 ? mobilePin.getLength() : 8;

        for (int i = 0; i < len; i++) {
            data[i] = (byte) (singleUseKey.getByte(i) ^ shiftedPin[i]);
            data[i + 8] = (byte) (singleUseKey.getByte(i + 8) ^ shiftedPin[i]);
        }

        return sessionKey;
    }

    /**
     * ISO/FDIS 9564-1:2014(E) Format 4 specify PIN with encrypted with PAN.<br>
     * According to MDES API Spec section 4.1.3.2 we will be using substitute value for PAN.
     *
     * @param paymentAppInstanceId The Payment Application Instance Id
     * @return The PAN substitute value
     */
    static ByteArray generatePanSubstituteValue(String paymentAppInstanceId) {
        final byte[] inputBytes = paymentAppInstanceId.getBytes(Charset.defaultCharset());
        final byte[] sha1Output = INSTANCE.sha1(inputBytes);
        final String decimalValue =
                new BigInteger(ByteArray.of(sha1Output).toHexString(), 16).toString();
        Utils.clearByteArray(sha1Output);

        // Now build the final value depending on how long the actual decimal value is
        final StringBuilder builder = new StringBuilder();
        if (decimalValue.length() < 16) {
            for (int i = decimalValue.length(); i < 16; i++) {
                builder.append("0");
            }
            builder.append(decimalValue);
        } else {
            final int start = decimalValue.length() - 16;
            final int end = decimalValue.length();
            builder.append(decimalValue.substring(start, end));
        }
        // Note: Convert to byte array (no from HEX String!)
        return ByteArray.of(builder.toString().getBytes(Charset.defaultCharset()));
    }

    /**
     * Build the iv vector for the AES CTR with No Padding using to encrypt / decrypt the service
     * data between CMS and MPA
     *
     * @param counters   The current value of the counters
     * @param isMpaToCms Flag to specify whether the message if sent or received by the MPA
     * @return the iv vector as byte[]
     */
    private static byte[] buildIvFromCounters(final int counters, final boolean isMpaToCms)
            throws McbpCryptoException {
        final byte[] iv = new byte[16];

        iv[0] = isMpaToCms ? (byte) 0x00 : (byte) 0x01;

        if (counters <= 0xFF) {
            iv[3] = (byte) (counters);
        } else if (counters <= 0xFFFF) {
            iv[2] = (byte) ((counters & 0xFF00) >> 8);
            iv[3] = (byte) (counters & 0x00FF);
        } else {
            // Never Expected.
            throw new McbpCryptoException("Invalid M2C");
        }
        return iv;
    }

    /**
     * Perform the AES CTR No Padding Encryption and Decryption
     *
     * @param data The input data to be encrypted / decrypted as byte[]
     * @param iv   The Initialization Vector (IV)
     * @param key  The encryption / decryption key
     * @param mode True Encryption Mode (ENCRYPT or DECRYPT)
     * @return The encrypted / decrypted data
     */
    private static byte[] aesCtrNoPadding(final byte[] data,
                                          final byte[] iv,
                                          final byte[] key,
                                          final Mode mode)
            throws McbpCryptoException {
        // Initialize the algorithm
        final SecretKey secretKey = new SecretKeySpec(key, "AES");
        final IvParameterSpec ivSpec = new IvParameterSpec(iv);
        final Cipher cipherCtr;
        try {
            cipherCtr = Cipher.getInstance("AES/CTR/NoPadding");
            if (mode == Mode.ENCRYPT) {
                cipherCtr.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
            } else {
                cipherCtr.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
            }

            return cipherCtr.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                InvalidAlgorithmParameterException | IllegalBlockSizeException |
                BadPaddingException e) {
            throw new McbpCryptoException(e.getMessage());
        }
    }

    /**
     * Initializes the RSA cipher <code>rsaCipher</code> in Encryption mode.
     *
     * @param key the RSA private key
     * @throws McbpCryptoException
     */
    @SuppressWarnings("all")  // Used to avoid warnings on ECB mode.
    private static void initRsaPrivate(final RSAPrivateKey key) throws McbpCryptoException {
        try {
            rsaCipher = Cipher.getInstance("RSA/ECB/NOPADDING");
            rsaCipher.init(Cipher.ENCRYPT_MODE, key);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException e) {
            throw new McbpCryptoException(e.toString());
        }
    }

    /**
     * Add ISO/IEC 7816-4 Padding scheme with a default block size (N) of DEFAULT_BLOCK_SIZE
     *
     * @param input The input data to which the padding should be applied to
     * @return A new byte[] with the padded vector
     */
    private static byte[] addIso7816Padding(final byte[] input) {
        return addIso7816Padding(input, 16);
    }

    /**
     * Remove ISO/IEC 7816-4 Padding with a default block size (N) of DEFAULT_BLOCK_SIZE
     *
     * @param input The input data to which the padding should be removed from
     * @return A new byte[] with the un-padded vector, the input byte[] if no padding is present
     * @throws McbpCryptoException If padding not found
     */
    private static byte[] removeIso7816Padding(final byte[] input) throws McbpCryptoException {
        return removeIso7816Padding(input, DEFAULT_BLOCK_SIZE);
    }

    /**
     * Add ISO/IEC 7816-4 Padding scheme with a specific block size (N)
     *
     * @param input     The input data to which the padding should be applied to
     * @param blockSize The block size to be used for padding
     * @return A new byte[] with the padded vector
     */
    private static byte[] addIso7816Padding(final byte[] input, final int blockSize) {
        final int paddedLength = input.length + blockSize - (input.length % blockSize);
        final byte[] dataWithPadding = new byte[paddedLength];
        System.arraycopy(input, 0, dataWithPadding, 0, input.length);
        dataWithPadding[input.length] = (byte) 0x80;
        return dataWithPadding;
    }

    /**
     * Remove ISO/IEC 7816-4 Padding with a specific block size (N)
     *
     * @param input     The input data to which the padding should be removed from
     * @param blockSize The block size that had been used for padding
     * @return A new byte[] with the un-padded vector, the input byte[] if no padding is present
     * @throws McbpCryptoException If padding not found
     */
    private static byte[] removeIso7816Padding(final byte[] input, final int blockSize)
            throws McbpCryptoException {
        // find if there is padding
        int paddingBytes = 0;
        boolean found = false;

        if (input == null || input.length < blockSize) {
            throw new McbpCryptoException("Invalid input size");
        }

        // Check the last block for padding
        for (int i = input.length - 1; i >= input.length - blockSize; i--) {
            paddingBytes++;
            if (input[i] == (byte) 0x00) {
                continue;
            }
            if (input[i] == (byte) 0x80) {
                found = true;
                break;
            }
            // throw new McbpCryptoException("Padding not found");
        }
        if (found) {
            int resultLength = input.length - paddingBytes;
            byte[] result = new byte[resultLength];
            System.arraycopy(input, 0, result, 0, resultLength);
            return result;
        }
        // throw new McbpCryptoException("Padding not found");
        return input;
    }

    /**
     * Encrypts or decrypts with an AES in ECB mode cipher.
     *
     * @param data the data to encrypt or decrypt as byte[]
     * @param bKey the encryption/decryption key as byte[]
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as byte[]
     * @throws McbpCryptoException
     */
    final byte[] aesEcb(final byte[] data, final byte[] bKey, final Mode mode)
            throws McbpCryptoException {
        return aes(data, bKey, mode, true);
    }

    private static byte[] aes(final byte[] data,
                              final byte[] bKey,
                              final Mode mode,
                              final boolean ecbMode)
            throws McbpCryptoException {
        final SecretKey secretKey = new SecretKeySpec(bKey, "AES");
        try {
            final byte[] iV = new byte[16];
            final String blockType;
            if (ecbMode) {
                blockType = "ECB";
            } else {
                blockType = "CBC";
            }
            final Cipher cipher = Cipher.getInstance("AES/" + blockType + "/NoPadding");
            if (mode == Mode.ENCRYPT) {
                // Encrypt the data
                if (ecbMode) {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey);
                } else {
                    cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(iV));
                }
            } else {
                // Decrypt the data
                if (ecbMode) {
                    cipher.init(Cipher.DECRYPT_MODE, secretKey);
                } else {
                    cipher.init(Cipher.DECRYPT_MODE, secretKey, new IvParameterSpec(iV));
                }
            }
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException |
                BadPaddingException | IllegalBlockSizeException |
                InvalidAlgorithmParameterException e) {
            throw new McbpCryptoException(e.toString());
        }
    }

    /**
     * Calculate the Mac of the byte array <code>dataToMac</code>.
     *
     * @param dataToMac the data to mac as ByteArray
     * @param key       the key as ByteArray
     * @return the mac as ByteArray
     * @throws McbpCryptoException
     */
    final ByteArray mac(final ByteArray dataToMac,
                        final ByteArray key) throws McbpCryptoException {
        return ByteArray.of(mac(dataToMac.getBytes(), key.getBytes()));
    }

    /**
     * Calculate the Mac of the byte array <code>dataToMac</code>.
     *
     * @param dataToMac the data to mac as byte[]
     * @param key       the key as byte[]
     * @return the mac as byte[]
     * @throws McbpCryptoException
     */
    final byte[] mac(final byte[] dataToMac, final byte[] key) throws McbpCryptoException {
        // First create an array of MAC BLOCK Size all set to 0x00
        final int macSize = (int) Math.ceil(((double) dataToMac.length + 1) / 8) * 8;
        final byte[] mac = new byte[macSize];
        System.arraycopy(dataToMac, 0, mac, 0, dataToMac.length);

        // Add the padding at the relevant location. The padding is defined 0x80, 0x00 ... 0x00
        mac[dataToMac.length] = (byte) 0x80;

        final byte[] keyL = Arrays.copyOfRange(key, 0, key.length / 2);
        final byte[] keyR = Arrays.copyOfRange(key, key.length / 2, key.length);

        // ----- Perform DES3 Encryption to Calculate MAC ---------
        final byte[] desResult = new byte[8];
        // The the 8 most right bytes of the first desCbc encryption
        System.arraycopy(desCbc(mac, keyL, Mode.ENCRYPT), macSize - 8, desResult, 0, 8);
        final byte[] bMac = des(desResult, keyR, Mode.DECRYPT);
        final byte[] result = des(bMac, keyL, Mode.ENCRYPT);

        // Clear temporary data structures
        Utils.clearByteArray(bMac);
        Utils.clearByteArray(mac);
        Utils.clearByteArray(keyL);
        Utils.clearByteArray(keyR);
        Utils.clearByteArray(desResult);

        return result;
    }

    /**
     * Encrypts or decrypts with a DES cipher in CBC mode without padding of the
     * data.
     *
     * @param data the data as byte[]
     * @param bKey the b key as byte[]
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as byte[]
     * @throws McbpCryptoException
     */
    final byte[] desCbc(byte[] data, byte[] bKey, Mode mode)
            throws McbpCryptoException {
        try {
            final SecretKey key = SecretKeyFactory.getInstance("DES")
                                                  .generateSecret(new DESKeySpec(bKey));
            final Cipher cipher = Cipher.getInstance("DES/CBC/noPadding");

            final IvParameterSpec ips = new IvParameterSpec(new byte[8]);
            if (mode == Mode.ENCRYPT) {
                cipher.init(Cipher.ENCRYPT_MODE, key, ips);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, ips);
            }
            return cipher.doFinal(data);
        } catch (InvalidKeySpecException | NoSuchAlgorithmException | InvalidKeyException |
                IllegalBlockSizeException | BadPaddingException |
                InvalidAlgorithmParameterException | NoSuchPaddingException e) {
            throw new McbpCryptoException(e.toString());
        }
    }

    /**
     * Encrypts or decrypts with a DES cipher in ECB mode without padding of the
     * data.
     *
     * @param data the data to encrypt or decrypt as ByteArray
     * @param bKey the cipher key as ByteArray
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as ByteArray
     * @throws McbpCryptoException
     */
    final ByteArray des(final ByteArray data, final ByteArray bKey, final Mode mode)
            throws McbpCryptoException {
        return ByteArray.of(des(data.getBytes(), bKey.getBytes(), mode));
    }

    /**
     * Encrypts or decrypts with a DES cipher in ECB mode without padding of the
     * data.
     *
     * @param data the data to encrypt or decrypt as byte[]
     * @param bKey the cipher key as byte[]
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as byte[]
     * @throws McbpCryptoException
     */
    @SuppressWarnings("all")  // Used to avoid warnings on ECB mode.
    final byte[] des(final byte[] data, final byte[] bKey, final Mode mode)
            throws McbpCryptoException {
        final SecretKey key = new SecretKeySpec(bKey, "DES");
        try {
            // Note that we use ECB as per specs, although deprecated in Android.
            // However, in this case is it safe due to particular set of inputs we use
            // (fixed length)
            final Cipher cipher = Cipher.getInstance("DES/ECB/noPadding");
            if (mode == Mode.ENCRYPT) {
                cipher.init(Cipher.ENCRYPT_MODE, key);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key);
            }

            return cipher.doFinal(data);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidKeyException e) {
            throw new McbpCryptoException(e.toString());
        }
    }

    /**
     * Encrypts or decrypts with a triple DES cipher in CBC mode without padding
     * of the data.
     *
     * @param data the data to encrypt or decrypt as ByteArray
     * @param bKey the encryption/decryption key as ByteArray
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as ByteArray
     * @throws McbpCryptoException the general security exception
     */
    final ByteArray des3(ByteArray data, ByteArray bKey, Mode mode)
            throws McbpCryptoException {
        return ByteArray.of(des3(data.getBytes(), bKey.getBytes(), mode));
    }

    /**
     * Encrypts or decrypts with a triple DES cipher in CBC mode without padding
     * of the data.
     *
     * @param data the data to encrypt or decrypt as byte[]
     * @param bKey the encryption/decryption key as byte[]
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as byte[]
     * @throws McbpCryptoException the general security exception
     */
    final byte[] des3(final byte[] data, final byte[] bKey, final Mode mode)
            throws McbpCryptoException {

        if (bKey.length != 24 && bKey.length != 16) {
            throw new McbpCryptoException("Invalid 3DES key length: " + bKey.length);
        }

        // We store the key in a temporary vector in case we need to extend it
        final byte[] extendedKey = new byte[24];

        // Extend the key to 24 bytes, if only 16 are provided
        System.arraycopy(bKey, 0, extendedKey, 0, bKey.length);
        if (bKey.length == 16) {
            System.arraycopy(bKey, 0, extendedKey, 16, 8);
        }

        final SecretKey key = new SecretKeySpec(extendedKey, "DESede");
        try {
            Cipher cipher = Cipher.getInstance("DESede/CBC/noPadding");
            final IvParameterSpec ips = new IvParameterSpec(new byte[8]);
            if (mode == Mode.ENCRYPT) {
                cipher.init(Cipher.ENCRYPT_MODE, key, ips);
            } else {
                cipher.init(Cipher.DECRYPT_MODE, key, ips);
            }
            return cipher.doFinal(data);
        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException |
                BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new McbpCryptoException(e.toString());
        } finally {
            // We clear the temporary key. For secure implementation the caller must delete the
            // original key used for encryption / decryption
            Utils.clearByteArray(extendedKey);
        }
    }

    /**
     * Encrypts or decrypts with an AES cipher.
     *
     * @param data the data to encrypt or decrypt as ByteArray
     * @param bKey the encryption/decryption key as ByteArray
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as ByteArray
     * @throws McbpCryptoException
     */
    final ByteArray aesEcb(final ByteArray data, final ByteArray bKey, final Mode mode)
            throws McbpCryptoException {
        return ByteArray.of(aesEcb(data.getBytes(), bKey.getBytes(), mode));
    }

    /**
     * Encrypts or decrypts with an AES cipher and manually add/remove padding as needed
     *
     * @param data the data to encrypt or decrypt as ByteArray
     * @param bKey the encryption/decryption key as ByteArray
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as ByteArray
     * @throws McbpCryptoException
     */
    final ByteArray aesEcbWithPadding(final ByteArray data,
                                      final ByteArray bKey,
                                      final Mode mode) throws McbpCryptoException {
        return ByteArray.of(aesEcbWithPadding(data.getBytes(), bKey.getBytes(), mode));
    }

    /**
     * Encrypts or decrypts with an AES cipher and manually add/remove padding as needed
     *
     * @param data the data to encrypt or decrypt as ByteArray
     * @param bKey the encryption/decryption key as ByteArray
     * @param mode the operation mode (ENCRYPT or DECRYPT)
     * @return the encrypted/decrypted byte array as ByteArray
     * @throws McbpCryptoException
     */
    final byte[] aesEcbWithPadding(final byte[] data, final byte[] bKey, final Mode mode)
            throws McbpCryptoException {
        if (mode == Mode.ENCRYPT) {
            byte[] dataWithPadding = addIso7816Padding(data);
            return aesEcb(dataWithPadding, bKey, Mode.ENCRYPT);
        }
        return removeIso7816Padding(aesEcb(data, bKey, Mode.DECRYPT));
    }

    /**
     * Calculate mac using AES.
     *
     * @param data the data to mac as ByteArray
     * @param bKey the mac key as ByteArray
     * @return the mac as ByteArray
     * @throws McbpCryptoException
     */
    final ByteArray aesCbcMac(final ByteArray data, final ByteArray bKey)
            throws McbpCryptoException {
        return ByteArray.of(aesCbcMac(data.getBytes(), bKey.getBytes()));
    }

    /**
     * Decrypt the retry request data
     *
     * @param data input encrypted data
     * @param bKey decryption key
     * @return Decrypted retry request data
     * @throws McbpCryptoException
     */
    final byte[] aesCbcMac(final byte[] data, final byte[] bKey) throws McbpCryptoException {

        final byte[] dataWithPadding = addIso7816Padding(data);
        final SecretKey secretKey = new SecretKeySpec(bKey, "AES");

        byte[] intermediate = new byte[16];
        byte[] xorData = null;

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
            // iv is all 0x00
            cipher.init(Cipher.ENCRYPT_MODE, secretKey, new IvParameterSpec(new byte[16]));
            for (int i = 0, j; i < (dataWithPadding.length / 16); i++) {
                j = i * 16;
                xorData = Utils.doXor(dataWithPadding, j, intermediate, 0, 16);
                Utils.clearByteArray(intermediate);  // We need to clear the old temporary value
                // re-assign a new value to it
                intermediate = cipher.doFinal(xorData);
                Utils.clearByteArray(xorData); // We need to clear the intermediate xor data
            }
            final byte[] macBytes = new byte[8];
            System.arraycopy(intermediate, 0, macBytes, 0, 8);

            return macBytes;
        } catch (final InvalidKeyException | NoSuchAlgorithmException |
                NoSuchPaddingException | InvalidAlgorithmParameterException |
                IllegalBlockSizeException | BadPaddingException e) {
            throw new McbpCryptoException(e.toString());
        } finally {
            // Clear temporary data structures before returning
            Utils.clearByteArray(intermediate);
            Utils.clearByteArray(dataWithPadding);
            Utils.clearByteArray(xorData);
        }
    }

    /**
     * Derive the session key given a certain session code
     *
     * @param key         The mobile key
     * @param sessionCode The Session Code
     * @return The derived session key
     */
    private byte[] deriveMobileSessionKey(final byte[] key, final byte[] sessionCode)
            throws McbpCryptoException {
        final byte[] hashedKey = macSha256(sessionCode, key);
        final byte[] derivedKey = new byte[16];
        System.arraycopy(hashedKey, 0, derivedKey, 0, 16);
        Utils.clearByteArray(hashedKey);
        return derivedKey;
    }

    /**
     * @param pinData Mobile PIN digits. The length must be between 4 to 8 according to
     *                ISO/FDIS - 9564 Format 4 PIN block encipher. This length enforcement is
     *                independent of the one applied by MCBP according to
     *                MasterCardCloudBasedPayments_IssuerCryptographicAlgorithms_v1-1 (Section 3.4)
     * @return plain pin block data
     * @throws McbpCryptoException
     */
    static ByteArray generatePlainTextPinField(ByteArray pinData)
            throws McbpCryptoException {

        final int OUTPUT_PLAIN_PIN_BLOCK_LENGTH = 16;

        if (pinData == null || pinData.getLength() == 0) {
            throw new McbpCryptoException("Pin is null");
        }

        if (pinData.getLength() < 4 || pinData.getLength() > 12) {
            throw new McbpCryptoException("Invalid pin length");
        }

        // Initializing final 16 bytes plain PIN block
        byte[] plainTextPinBlock = new byte[OUTPUT_PLAIN_PIN_BLOCK_LENGTH];

        //First upper nibble as control field
        final byte controlField = 0X04;
        //Fill digit
        final byte fillDigit = 0X0A;
        final byte fillByte = (byte) (0XAA);

        // First byte of Plain PIN block is represented as 'C|N'
        // Right shift by 4 to set upper nibble
        byte firstByte = (byte) (controlField << 4);
        // OR with pin length to create first byte of plain PIN block
        firstByte = (byte) (firstByte | pinData.getLength());

        plainTextPinBlock[0] = firstByte;

        // Set PIN data in to plain Pin block<br>
        // If PIN length is odd,last digit of PIN should be OR with FILL DIGIT (0X0A)
        int count = 1;
        boolean isPinOddLength = pinData.getLength() % 2 != 0;
        for (int i = 0; i < pinData.getLength(); i = i + 2) {
            byte finalByte = pinData.getByte(i);
            finalByte = (byte) ((finalByte << 4) & 0xF0);
            if (isPinOddLength && (pinData.getLength() - i) == 1) {
                finalByte = (byte) (finalByte | fillDigit);
            } else {
                byte nextPinByte = (byte) (pinData.getByte(i + 1) & 0x0F);
                finalByte = (byte) (finalByte | nextPinByte);
            }
            plainTextPinBlock[count] = finalByte;
            count++;
        }
        // Calculate how many FILL DIGIT needs to be added
        int pinDiff = 12 - (isPinOddLength ? (pinData.getLength() + 1) : pinData.getLength());
        for (int i = 0; i < pinDiff; i++) {
            plainTextPinBlock[count + i] = fillByte;
        }
        // Setting 8th byte to FILL Byte
        plainTextPinBlock[7] = fillByte;

        // Fill rest of plain PIN block with random bytes
        byte[] randomBytes = CryptoServiceFactory.getDefaultCryptoService()
                                                 .getRandom(OUTPUT_PLAIN_PIN_BLOCK_LENGTH - 8);
        System.arraycopy(randomBytes, 0, plainTextPinBlock, 8, OUTPUT_PLAIN_PIN_BLOCK_LENGTH - 8);

        return ByteArray.of(plainTextPinBlock);
    }

    static ByteArray generatePlainTextPanField(ByteArray panData)
            throws McbpCryptoException {

        final int OUTPUT_PLAIN_PAN_BLOCK_LENGTH = 16;
        final int DEFAULT_PAN_LENGTH = 12;

        if (panData == null || panData.getLength() == 0) {
            throw new McbpCryptoException("Input data is null");
        }
        if (panData.getLength() > 19) {
            throw new McbpCryptoException("Invalid length of input data");
        }

        int incomingPanDataLength = panData.getLength();
        byte[] plainTextPanBlock = new byte[OUTPUT_PLAIN_PAN_BLOCK_LENGTH];
        byte m_bit;
        if (incomingPanDataLength < DEFAULT_PAN_LENGTH) {
            m_bit = 0;
            byte[] newPanData = new byte[DEFAULT_PAN_LENGTH];
            int offset = DEFAULT_PAN_LENGTH - incomingPanDataLength;
            System.arraycopy(panData.getBytes(), 0, newPanData, offset, incomingPanDataLength);
            panData = ByteArray.of(newPanData);
            incomingPanDataLength = panData.getLength();
        } else {
            m_bit = (byte) (panData.getLength() - DEFAULT_PAN_LENGTH);
        }
        boolean isPanEvenLength = incomingPanDataLength % 2 == 0;
        plainTextPanBlock[0] = m_bit;
        plainTextPanBlock[0] = (byte) (plainTextPanBlock[0] << 4);
        byte firstPanByte = (byte) (panData.getByte(0) & 0x0F);
        plainTextPanBlock[0] = (byte) (plainTextPanBlock[0] | firstPanByte);
        int count = 1;
        int numberOfIteration = isPanEvenLength ? incomingPanDataLength - 1 : incomingPanDataLength;
        for (int i = 1; i < numberOfIteration; i = i + 2) {
            plainTextPanBlock[count] = panData.getByte(i);
            plainTextPanBlock[count] = (byte) (plainTextPanBlock[count] << 4 & 0xF0);
            byte nextPanByte = (byte) (panData.getByte(i + 1) & 0x0F);
            plainTextPanBlock[count] = (byte) (plainTextPanBlock[count] | nextPanByte);
            count++;
        }

        if (isPanEvenLength) {
            plainTextPanBlock[count] = panData.getByte(incomingPanDataLength - 1);
            plainTextPanBlock[count] = (byte) (plainTextPanBlock[count] << 4);
        }
        return ByteArray.of(plainTextPanBlock);
    }
}
