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
package com.mastercard.mcbp.card.transactionlogging;


import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.apache.commons.codec.binary.Base64;

/**
 * A unique identifier for the transaction that is used to match a transaction event on the device
 * (for example, a contactless tap, or a DSRP payment) to a transaction details record
 * provided by the TDS.
 */
public enum TransactionIdentifier {
    INSTANCE;

    /**
     * Reference to the default Crypto Service Engine
     */
    private static CryptoService sCryptoService = CryptoServiceFactory.getDefaultCryptoService();

    /**
     * Generate the Transaction Identifier for M/Chip transactions
     * (contactless and DSRP with full EMV data)
     *
     * @param panInput              Pan number
     * @param atc                   ATC value
     * @param applicationCryptogram Application Cryptogram
     * @return transaction identifier for M/Chip transaction.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    public static ByteArray getMChip(
            final ByteArray panInput, final ByteArray atc, final ByteArray applicationCryptogram)
            throws McbpCryptoException, InvalidInput {

        if (panInput == null) {
            throw new InvalidInput("Pan is null");
        }
        if (atc == null) {
            throw new InvalidInput("Atc is null");
        }
        if (applicationCryptogram == null) {
            throw new InvalidInput("Application Cryptogram is null");
        }

        if (panInput.getLength() < 9 || panInput.getLength() > 19) {
            throw new InvalidInput("Pan length should be in between 9 to 19");
        }

        final byte nibbleValue = 0X0F;
        int count = 0;

        // Final byte array of panInput
        byte[] panFinalByteArray;
        boolean isPanOddLength = panInput.getLength() % 2 != 0;

        if (isPanOddLength) {
            panFinalByteArray = new byte[panInput.getLength() / 2 + 1];
        } else {
            panFinalByteArray = new byte[panInput.getLength() / 2];
        }
        for (int i = 0; i < panInput.getLength(); i = i + 2) {
            byte finalByte = panInput.getByte(i);
            finalByte = (byte) ((finalByte << 4) & 0xF0);
            if (isPanOddLength && (panInput.getLength() - i) == 1) {
                finalByte = (byte) (finalByte | nibbleValue);
            } else {
                byte secondNibbleValue = (byte) (panInput.getByte(i + 1) & 0x0F);
                finalByte = (byte) (finalByte | secondNibbleValue);
            }
            panFinalByteArray[count] = finalByte;
            count++;
        }

        // Append the panInput + atc + application cryptogram.
        ByteArray txnIdentifierArray = ByteArray.of(panFinalByteArray);
        txnIdentifierArray.append(atc);
        txnIdentifierArray.append(applicationCryptogram);

        return sCryptoService.sha256(txnIdentifierArray);
    }


    /**
     * Generate the Transaction Identifier for Magnetic Stripe transactions
     *
     * @param track1DynamicData Track1 Data
     * @param track2DynamicData Track2 Data
     * @return transaction identifier for Magnetic Stripe transaction.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    public static ByteArray getMagstripe(final ByteArray track1DynamicData,
                                         final ByteArray track2DynamicData)
            throws McbpCryptoException, InvalidInput {
        if ((track1DynamicData == null || track1DynamicData.isEmpty()) &&
            (track2DynamicData == null || track2DynamicData.isEmpty())) {
            // Both track 1 and track 2 dynamic data are not available
            return null;
        }

        // If track1 data is null then partial matching (start 16 byte with zero and last 16 byte of
        // track2)
        if (track1DynamicData == null || track1DynamicData.isEmpty()) {
            ByteArray track2With16ByteSha256 = trackDataWith16ByteSha256(track2DynamicData);
            return track1DataWith32Bytes(track2With16ByteSha256);
        }

        // If track2 data is null then partial matching (start 16 byte with track1 data and
        // last 16 byte with zero)
        if (track2DynamicData == null || track2DynamicData.isEmpty()) {
            ByteArray track1With16ByteSha256 = trackDataWith16ByteSha256(track1DynamicData);
            return track2DataWith32Bytes(track1With16ByteSha256);
        }

        // Append the track1Data + track2Data
        ByteArray txnIdentifierArray = trackDataWith16ByteSha256(track1DynamicData);
        txnIdentifierArray.append(trackDataWith16ByteSha256(track2DynamicData));

        return txnIdentifierArray;
    }

    /**
     * Generate 32 byte track1 data.
     *
     * @param track1Data Track1 Data
     * @return byte array of 32 byte track1
     */
    private static ByteArray track1DataWith32Bytes(ByteArray track1Data) {
        final byte[] trackInput = new byte[32];
        System.arraycopy(track1Data.getBytes(), 0, trackInput, 16, 16);
        return ByteArray.of(trackInput);
    }

    /**
     * Calculate Hash of track1 data
     *
     * @param trackData The Track data to be hashed (sha-256)
     * @return Hash of Track1 data of 16 bytes
     * @throws McbpCryptoException
     */
    private static ByteArray trackDataWith16ByteSha256(final ByteArray trackData)
            throws McbpCryptoException {
        ByteArray trackDataHash = sCryptoService.sha256(ByteArray.of(trackData.getBytes()));

        final byte[] result = new byte[16];
        System.arraycopy(trackDataHash.getBytes(), trackDataHash.getLength() - 16,
                         result, 0, result.length);
        return ByteArray.of(result);
    }

    /**
     * Generate 32 byte track2 data.
     *
     * @param track2Data Track2 Data
     * @return byte array of 32 byte track2
     * @throws McbpCryptoException
     */
    private static ByteArray track2DataWith32Bytes(final ByteArray track2Data)
            throws McbpCryptoException {
        final byte[] trackInput = new byte[32];
        System.arraycopy(track2Data.getBytes(), 0, trackInput, 0, 16);
        return ByteArray.of(trackInput);
    }

    /**
     * Generate the Transaction Identifier for DSRP transactions with UCAF data
     *
     * @param pan  Pan number
     * @param ucaf UCAF value
     * @return transaction identifier for DSRP transaction.
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    public static ByteArray generateDsrpWithUcafTransactionIdentifier(final ByteArray pan,
                                                                      final ByteArray ucaf)
            throws McbpCryptoException, InvalidInput {

        if (pan == null) {
            throw new InvalidInput("Pan is null");
        }
        if (ucaf == null) {
            throw new InvalidInput("Ucaf is null");
        }
        if (pan.getLength() < 9 || pan.getLength() > 19) {
            throw new InvalidInput("Pan length should be in between 9 to 19");
        }

        final byte nibbleValue = 0X0F;
        int count = 0;

        // Final byte array of pan
        final byte[] panByteArray;
        final boolean isPanOddLength = pan.getLength() % 2 != 0;

        if (isPanOddLength) {
            panByteArray = new byte[pan.getLength() / 2 + 1];
        } else {
            panByteArray = new byte[pan.getLength() / 2];
        }

        for (int i = 0; i < pan.getLength(); i = i + 2) {
            byte oneNibbleValue = pan.getByte(i);
            oneNibbleValue = (byte) ((oneNibbleValue << 4) & 0xF0);
            if (isPanOddLength && (pan.getLength() - i) == 1) {
                oneNibbleValue = (byte) (oneNibbleValue | nibbleValue);
            } else {
                byte nextPinByte = (byte) (pan.getByte(i + 1) & 0x0F);
                oneNibbleValue = (byte) (oneNibbleValue | nextPinByte);
            }
            panByteArray[count] = oneNibbleValue;
            count++;
        }

        final byte[] decodedByteArray = Base64.decodeBase64(ucaf.getBytes());
        final String ucafEncodedHex = ByteArray.of(decodedByteArray).toHexString();

        // Append the pan + ucaf.
        final ByteArray txnIdentifierArray = ByteArray.of(panByteArray);
        txnIdentifierArray.append(ByteArray.of(ucafEncodedHex));

        final CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        return cryptoService.sha256(txnIdentifierArray);
    }
}
