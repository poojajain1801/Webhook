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

package com.mastercard.mcbp.remotemanagement.mdes.credentials;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mcbp.card.credentials.SingleUseKeyWrapper;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

/**
 * Represents the container for {@link com.mastercard.mcbp.remotemanagement.mdes.credentials
 * .TransactionCredential}.
 * This class help to convert {@link com.mastercard.mcbp.remotemanagement.mdes.credentials
 * .TransactionCredential} to {@link SingleUseKey}.
 */
public class TransactionCredentialContainer implements SingleUseKeyWrapper {

    private final TransactionCredential mTransactionCredential;
    private final String mCardId;
    private final byte[] mDataEncryptionKey;
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);
    
    public TransactionCredentialContainer(final TransactionCredential transactionCredential,
                                          final String digitizedCardId,
                                          final ByteArray dataEncryptionKey) {
        if (transactionCredential == null || digitizedCardId == null) {
            throw new IllegalArgumentException("Invalid input");
        }
        this.mTransactionCredential = transactionCredential;
        this.mCardId = digitizedCardId;
        this.mDataEncryptionKey = dataEncryptionKey.getBytes();
    }

    @Override
    public String getCardId() {
        return mCardId;
    }

    @Override
    public SingleUseKey toSingleUseKey() {
        SingleUseKeyContent singleUseKeyContent = new SingleUseKeyContent();

        final byte[] clUmd = decrypt(mTransactionCredential.contactlessUmdSingleUseKey.getBytes());
        final byte[] clMd = decrypt(mTransactionCredential.contactlessMdSessionKey.getBytes());
        final byte[] dsrpUmd = decrypt(mTransactionCredential.dsrpUmdSingleUseKey.getBytes());
        final byte[] dsrpMd = decrypt(mTransactionCredential.dsrpMdSessionKey.getBytes());
        final ByteArray atc = ByteArray.of((char) mTransactionCredential.atc);
        final byte[] idnFull = decrypt(mTransactionCredential.idn.getBytes());
        final byte[] idn;

        if (idnFull != null && idnFull.length == 16) {
            idn = new byte[8]; // We currently use only the right most 8 bytes of the IDN
            System.arraycopy(idnFull, 8, idn, 0, 8);
        } else if (idnFull != null && idnFull.length == 8) {
            idn = idnFull;
        } else {
            throw new IllegalArgumentException("Invalid IDN");
        }

        singleUseKeyContent.setSukContactlessUmd(clUmd);
        singleUseKeyContent.setSessionKeyContactlessMd(clMd);
        singleUseKeyContent.setSukRemotePaymentUmd(dsrpUmd);
        singleUseKeyContent.setSessionKeyRemotePaymentMd(dsrpMd);

        singleUseKeyContent.setAtc(atc);
        singleUseKeyContent.setIdn(ByteArray.of(idn));

        singleUseKeyContent.setHash(ByteArray.of("0000"));
        singleUseKeyContent.setInfo(ByteArray.of("56"));

        // Clean up temporary used data
        Utils.clearByteArray(clUmd);
        Utils.clearByteArray(clMd);
        Utils.clearByteArray(dsrpUmd);
        Utils.clearByteArray(dsrpMd);
        Utils.clearByteArray(idnFull);
        Utils.clearByteArray(idn);

        //Note:Made this changes by referring the CredentialsDataMdesCmsC
        final String sukId = getCardId() + atc + "000000";

        SingleUseKey singleUseKey = new SingleUseKey();

        singleUseKey.setContent(singleUseKeyContent);
        singleUseKey.setId(ByteArray.of(sukId));
        singleUseKey.setDigitizedCardId(ByteArray.of(mCardId));

        return singleUseKey;
    }

    /**
     * @param encryptedData The encrypted data
     * @return The decrypted data using the internal Decryption Key
     */
    private byte[] decrypt(final byte[] encryptedData) {
        final CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        final byte[] result;
        try {
            result = cryptoService.decryptDataEncryptedField(encryptedData, mDataEncryptionKey);
        } catch (McbpCryptoException e) {
            mLogger.d(e.getMessage());
            return null;
        }
        return result;
    }
}
