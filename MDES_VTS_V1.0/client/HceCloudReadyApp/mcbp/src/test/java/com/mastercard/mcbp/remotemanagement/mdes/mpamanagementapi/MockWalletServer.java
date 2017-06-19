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

import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Security;
import java.security.spec.MGF1ParameterSpec;
import java.util.Random;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.OAEPParameterSpec;
import javax.crypto.spec.PSource;
import javax.crypto.spec.SecretKeySpec;

/**
 * Mock up class to handle basic Wallet Server and CMS-D operations
 */
public class MockWalletServer {
    private final CryptoService mCryptoService;

    private final PrivateKey mPrivateKey;
    private final PublicKey mPublicKey;
    private final String mRemoteManagementUrl = "https://mywesite.com/sampleurl";
    private final String mPaymentInstanceId = "sample-mobile-payment-instance-id";
    // We buffer the mobile keys
    private final ByteArray mTransportKey;
    private final ByteArray mMacKey;
    private final ByteArray mDataEncryptionKey;

    private ByteArray mReceivedMobilePin;

    public MockWalletServer() {
        //add at runtime the Bouncy Castle Provider to perform RSA decryption using OAEP
        Security.addProvider(new BouncyCastleProvider());

        mCryptoService = CryptoServiceFactory.getDefaultCryptoService();

        // Generate a random RSA key pair
        KeyPair kp = generateRandomRsaKeyPair(2048);
        mPrivateKey = kp.getPrivate();
        mPublicKey = kp.getPublic();

        // Generate mobile keys
        mTransportKey = mCryptoService.getRandomByteArray(16);
        mMacKey = mCryptoService.getRandomByteArray(16);
        mDataEncryptionKey = mCryptoService.getRandomByteArray(16);
    }

    CmsRegisterResponse processRegistrationParameters(
            EncryptedRegistrationRequestParameters parameters)
            throws Exception {
        final String mobileKeySetId = "1234-abcd-5678";


        // Let's now decrypt what we have received
        final ByteArray receivedRgk =
                decryptRandomGeneratedKey(parameters.getEncryptedRandomGeneratedKey(), mPrivateKey);

        if (parameters.getEncryptedMobilePinBlock() != null) {
            final ByteArray pinBlock = ByteArray.of(decryptPinBlock(
                    parameters.getEncryptedMobilePinBlock(), mPaymentInstanceId, receivedRgk));
            mReceivedMobilePin = retrievePinFromPlainPinFormat(pinBlock);
        } else {
            mReceivedMobilePin = null;
        }

        final byte[] encryptedTransportKey = aesEcb(mTransportKey.getBytes(),
                                                    receivedRgk.getBytes(),
                                                    CryptoService.Mode.ENCRYPT);
        final byte[] encryptedMacKey = aesEcb(mMacKey.getBytes(),
                                              receivedRgk.getBytes(),
                                              CryptoService.Mode.ENCRYPT);
        final byte[] encryptedDataEncryptionKey = aesEcb(mDataEncryptionKey.getBytes(),
                                                         receivedRgk.getBytes(),
                                                         CryptoService.Mode.ENCRYPT);

        return new CmsRegisterResponse(mRemoteManagementUrl,
                                       ByteArray.of(encryptedTransportKey),
                                       ByteArray.of(encryptedMacKey),
                                       ByteArray.of(encryptedDataEncryptionKey),
                                       mobileKeySetId);
    }

    public void processSetPin(final ByteArray encryptedPinBlock) throws Exception {
        if (encryptedPinBlock != null) {
            final ByteArray pinBlock = ByteArray.of(decryptPinBlock(encryptedPinBlock,
                                                                    mPaymentInstanceId,
                                                                    mDataEncryptionKey));
            mReceivedMobilePin = retrievePinFromPlainPinFormat(pinBlock);
        } else {
            mReceivedMobilePin = null;
        }
    }

    public ByteArray getReceivedMobilePin() {
        return mReceivedMobilePin;
    }

    public ByteArray getDataEncryptionKey() {
        return mDataEncryptionKey;
    }

    public ByteArray getMacKey() {
        return mMacKey;
    }

    public ByteArray getTransportKey() {
        return mTransportKey;
    }

    public PublicKey getPublicKey() {
        return mPublicKey;
    }

    public String getPaymentInstanceId() {
        return mPaymentInstanceId;
    }

    final byte[] aesEcb(final byte[] data, final byte[] bKey, final CryptoService.Mode mode)
            throws Exception {
        return aes(data, bKey, mode, true);
    }

    private static byte[] aes(final byte[] data,
                              final byte[] bKey,
                              final CryptoService.Mode mode,
                              final boolean ecbMode) throws Exception {
        final SecretKey secretKey = new SecretKeySpec(bKey, "AES");

        final byte[] iV = new byte[16];
        final String blockType;
        if (ecbMode) {
            blockType = "ECB";
        } else {
            blockType = "CBC";
        }
        final Cipher cipher = Cipher.getInstance("AES/" + blockType + "/NoPadding");
        if (mode == CryptoService.Mode.ENCRYPT) {
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
    }

    private static ByteArray decryptRandomGeneratedKey(final ByteArray data, PrivateKey privateKey)
            throws Exception {
        final byte[] result;

        // Please refer to http://stackoverflow.com/questions/32161720/breaking-down-rsa-ecb-oaepwithsha-256andmgf1padding
        // information on the need to provide parameters during decryption
        Cipher oaepFromInit = Cipher.getInstance("RSA/ECB/OAEPPadding");
        OAEPParameterSpec oaepParams = new OAEPParameterSpec("SHA-256", "MGF1",
                                                             new MGF1ParameterSpec("SHA-1"),
                                                             // FIXME: Note this is set as SHA-1 because Java by default encrypts with SHA-1
                                                             PSource.PSpecified.DEFAULT);
        oaepFromInit.init(Cipher.DECRYPT_MODE, privateKey, oaepParams);
        result = oaepFromInit.doFinal(data.getBytes());

        final ByteArray decryptedData = ByteArray.of(result);
        Utils.clearByteArray(result);  // We need to clean up temporary variables
        return decryptedData;
    }

    private static KeyPair generateRandomRsaKeyPair(final int keySizeInBits) {

        KeyPairGenerator kpg;
        try {
            kpg = KeyPairGenerator.getInstance("RSA");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Unable to find RSA algorithms: " + e.getMessage());
        }

        // Initialize the key, but pass the size in bits
        kpg.initialize(keySizeInBits);
        return kpg.genKeyPair();
    }

    private static ByteArray retrievePinFromPlainPinFormat(ByteArray pinFormatData) throws
            InvalidInput {
        if (pinFormatData == null || pinFormatData.getLength() != 16) {
            throw new InvalidInput("Invalid data");
        }

        byte firstByte = pinFormatData.getByte(0);
        int pinLength = (firstByte & 0x0F);
        byte pinArray[] = new byte[pinLength];
        boolean isPinOddLength = pinLength % 2 != 0;
        int index = 1;
        int count = 0;
        int computedPinLength = (isPinOddLength ? pinLength - 1 : pinLength) / 2;
        for (; index <= computedPinLength; index++) {
            byte currentByte = (byte) ((pinFormatData.getByte(index) & 0xF0) >> 4);
            pinArray[count++] = (byte) (currentByte | 0x30);
            byte nextByte = (byte) (pinFormatData.getByte(index) & 0x0F);
            pinArray[count++] = (byte) (nextByte | 0x30);
        }
        if (isPinOddLength) {
            int currentByte = (pinFormatData.getByte(index) & 0xF0) >> 4;
            pinArray[count] = (byte) (currentByte | 0x30);
        }
        return ByteArray.of(pinArray);
    }

    private byte[] decryptPinBlock(ByteArray pinData,
                                         String paymentInstanceId,
                                         ByteArray key)
            throws Exception {
        final ByteArray panSurrogate = generatePanSubstituteValue(paymentInstanceId);
        final byte[] decipheredData =
                aesEcb(pinData.getBytes(), key.getBytes(), CryptoService.Mode.DECRYPT);
        final ByteArray generatePlainTextPanField = generatePlainTextPanField(panSurrogate);
        final ByteArray intermediateBlockA =
                ByteArray.of(Utils.doXor(ByteArray.of(decipheredData), generatePlainTextPanField,
                                         16));
        return aesEcb(intermediateBlockA.getBytes(), key.getBytes(), CryptoService.Mode.DECRYPT);
    }

    private byte[] sha1(final byte[] data) {
        try {
            final MessageDigest md = MessageDigest.getInstance("SHA-1");
            md.update(data);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e.toString());
        }
    }

    private ByteArray generatePanSubstituteValue(String paymentAppInstanceId) {
        final byte[] inputBytes = paymentAppInstanceId.getBytes(Charset.defaultCharset());
        final byte[] sha1Output = sha1(inputBytes);
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

    private ByteArray generatePlainTextPinField(ByteArray pinData)
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

        // Fill rest of plain PIN block with random digits between 0 and 15
        Random random = new Random();
        for (int index = 8; index < OUTPUT_PLAIN_PIN_BLOCK_LENGTH; index++) {
            // We can reduce this operation to single line by
            // setting value to (byte) (random.nextInt() % 255);
            byte finalByte = (byte) (random.nextInt() % 15);
            finalByte = (byte) (finalByte << 4);
            finalByte = (byte) (finalByte | (byte) (random.nextInt() % 15));
            plainTextPinBlock[index] = finalByte;
        }

        return ByteArray.of(plainTextPinBlock);
    }

    private ByteArray generatePlainTextPanField(ByteArray panData)
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
