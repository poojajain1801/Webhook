package com.comviva.mfs.promotion.modules.mpamanagement.model;

import com.comviva.mfs.promotion.util.aes.AESUtil;
import com.comviva.mfs.promotion.util.messagedigest.MessageDigestUtil;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import java.math.BigInteger;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;

/**
 * Created by tarkeshwar.v on 6/13/2017.
 */
public class MobilePinUtil {
    /***
     * Decrypt the PIN Block according to the ISO/FDIS - 9564 Format 4 PIN block encipher
     *
     * @param pinData           The PIN Block
     * @param paymentInstanceId The Payment Instance ID, which will be used to generate the PAN
     *                          surrogate
     * @param key               The Encryption Key
     * @return The decrypted PIN Block
     */
    public static final ByteArray decryptPinBlock(ByteArray pinData,
                                           String paymentInstanceId,
                                           ByteArray key) throws GeneralSecurityException, McbpCryptoException {
        final ByteArray panSurrogate = generatePanSubstituteValue(paymentInstanceId);
        //final ByteArray decipheredData = aesEcb(pinData, key, CryptoService.Mode.DECRYPT);
        final ByteArray decipheredData = ByteArray.of(AESUtil.cipherECB(pinData.getBytes(), key.getBytes(), AESUtil.Padding.NoPadding, false));

        final ByteArray generatePlainTextPanField = generatePlainTextPanField(panSurrogate);
        final ByteArray intermediateBlockA = ByteArray.of(Utils.doXor(decipheredData, generatePlainTextPanField, 16));
        //return aesEcb(intermediateBlockA, key, CryptoService.Mode.DECRYPT);
        return ByteArray.of(AESUtil.cipherECB(intermediateBlockA.getBytes(), key.getBytes(), AESUtil.Padding.NoPadding, false));
    }

    /**
     * ISO/FDIS 9564-1:2014(E) Format 4 specify PIN with encrypted with PAN.<br>
     * According to MDES API Spec section 4.1.3.2 we will be using substitute value for PAN.
     *
     * @param paymentAppInstanceId The Payment Application Instance Id
     * @return The PAN substitute value
     */
    private static ByteArray generatePanSubstituteValue(String paymentAppInstanceId) throws NoSuchAlgorithmException {
        final byte[] inputBytes = paymentAppInstanceId.getBytes(Charset.defaultCharset());
        byte[] sha1Output = MessageDigestUtil.sha1(inputBytes);
        final String decimalValue = new BigInteger(ByteArray.of(sha1Output).toHexString(), 16).toString();
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

    private static ByteArray generatePlainTextPanField(ByteArray panData)
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
