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

package com.mastercard.mcbp.card.mpplite.apdu.emv;

import com.mastercard.mcbp.card.mpplite.apdu.RespApdu;
import com.mastercard.mcbp.card.mpplite.mcbpv1.cardriskmanagement
        .PosCardholderInteractionInformation;
import com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless.ContactlessTransactionContext;
import com.mastercard.mcbp.card.mpplite.mcbpv1.output.CryptogramOutput;
import com.mastercard.mcbp.card.profile.IccPrivateKeyCrtComponents;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.mpplite.MppLiteException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Tlv;
import com.mastercard.mobile_api.utils.Utils;

/**
 * The Class Generate AC Response APDU.
 */
public class GenerateAcResponseApdu extends RespApdu {
    /**
     * The Crypto Service library
     */
    private final static CryptoService sCryptoService =
            CryptoServiceFactory.getDefaultCryptoService();

    // Constants used for the Response APDU generation
    public static final ByteArray GENERATE_AC_RESPONSE_TAG = ByteArray.of((byte) 0x77);
    public static final ByteArray CID_TAG = ByteArray.of((char) 0x9F27);
    public static final ByteArray ATC_TAG = ByteArray.of((char) 0x9F36);
    public static final ByteArray APPLICATION_CRYPTOGRAM_TAG = ByteArray.of((char) 0x9F26);
    public static final ByteArray ISSUER_APPLICATION_DATA_TAG = ByteArray.of((char) 0x9F10);
    public static final ByteArray SIGNED_DYNAMIC_APPLICATION_DATA_TAG = ByteArray.of((char) 0x9F4B);
    public static final byte DDA_TRAILER = (byte) 0xBC;
    public static final byte DDA_HEADER = (byte) 0x6A;
    public static final byte PADDING_BYTE = (byte) 0xBB;
    public static final byte DAD_SIGNED_DATA_FORMAT = (byte) 0x05;
    public static final byte DAD_HASH_ALGORITHM_INDICATOR = (byte) 0x01;
    public static final byte DAD_ICC_DYNAMIC_DATA_LENGTH = (byte) 0x26;

    /**
     * The Generate AC Command APDU
     */
    private final GenerateAcCommandApdu mCommandApdu;

    /**
     * The Contactless Transaction Context for this transaction
     */
    private final ContactlessTransactionContext mContext;

    /**
     * The POS Cardholder Interaction Information for this transaction
     */
    private final PosCardholderInteractionInformation mPoscii;

    /**
     * The Cryptogram Output
     */
    private final CryptogramOutput mCryptogramOutput;

    /**
     * The IDN from the current set of credentials to be used
     */
    private final ByteArray mIdn;

    /**
     * The Private Key components of the card RSA key
     */
    private final IccPrivateKeyCrtComponents mPrivateKeyCrtComponents;

    /**
     * The length of the RSA key
     */
    private final int mRsaKeyLength;

    /**
     * Generate a R-APDU with CDA
     *
     * @param commandApdu      The Generate AC Command APDU
     * @param context          The Contactless Transaction Context for this transaction
     * @param poscii           The POS Cardholder Interaction Information for this transaction
     * @param cryptogramOutput The Cryptogram Output
     * @param idn              The IDN from the current set of credentials to be used
     * @param privateKey       The Private Key components of the card RSA key
     * @return The Generate AC Response APDU object
     */
    public static GenerateAcResponseApdu withCda(final GenerateAcCommandApdu commandApdu,
                                                 final ContactlessTransactionContext context,
                                                 final PosCardholderInteractionInformation poscii,
                                                 final CryptogramOutput cryptogramOutput,
                                                 final ByteArray idn,
                                                 final IccPrivateKeyCrtComponents privateKey) {
        return new GenerateAcResponseApdu(commandApdu,
                                          context,
                                          poscii,
                                          cryptogramOutput,
                                          idn,
                                          privateKey);
    }

    /**
     * Generate a R-APDU without CDA
     *
     * @param commandApdu      The Generate AC Command APDU
     * @param context          The Contactless Transaction Context for this transaction
     * @param poscii           The POS Cardholder Interaction Information for this transaction
     * @param cryptogramOutput The Cryptogram Output
     * @return The Generate AC Response APDU object
     */
    public static GenerateAcResponseApdu withoutCda(final GenerateAcCommandApdu commandApdu,
                                                    final ContactlessTransactionContext context,
                                                    final PosCardholderInteractionInformation
                                                            poscii,
                                                    final CryptogramOutput cryptogramOutput) {
        return new GenerateAcResponseApdu(commandApdu, context, poscii, cryptogramOutput);
    }

    /**
     * Constructors are not directly accessible. Please use static factory methods instead.
     */
    private GenerateAcResponseApdu(final GenerateAcCommandApdu commandApdu,
                                   final ContactlessTransactionContext context,
                                   final PosCardholderInteractionInformation poscii,
                                   final CryptogramOutput cryptogramOutput,
                                   final ByteArray idn,
                                   final IccPrivateKeyCrtComponents privateKey) {
        this.mCommandApdu = commandApdu;
        this.mContext = context;
        this.mPoscii = poscii;
        this.mCryptogramOutput = cryptogramOutput;
        this.mIdn = idn;
        this.mPrivateKeyCrtComponents = privateKey;
        this.mRsaKeyLength = initRsaKey();

        // GAC.8.1, GAC.8.2, GAC.8.3, and GAC.8.4
        final byte[] response = buildGenerateAcResponseWithCda();
        setValueAndSuccess(ByteArray.of(response));
        // Clean-up temporary variables
        Utils.clearByteArray(response);
    }

    /**
     * Constructors are not directly accessible. Please use static factory methods instead.
     */
    private GenerateAcResponseApdu(final GenerateAcCommandApdu commandApdu,
                                   final ContactlessTransactionContext context,
                                   final PosCardholderInteractionInformation poscii,
                                   final CryptogramOutput cryptogramOutput) {
        this.mCommandApdu = commandApdu;
        this.mContext = context;
        this.mPoscii = poscii;
        this.mCryptogramOutput = cryptogramOutput;
        this.mIdn = null;
        this.mPrivateKeyCrtComponents = null;
        this.mRsaKeyLength = -1;

        // GAC.8.1, GAC.8.2, GAC.8.3, and GAC.8.4
        final byte[] response = buildResponseWithoutCda();
        setValueAndSuccess(ByteArray.of(response));
        // Clean-up temporary variables
        Utils.clearByteArray(response);
    }

    /***
     * Initialize the ICC Private Key and return its length
     * *
     *
     * @return the private key length
     * @throws MppLiteException if the initialization fails
     */
    private int initRsaKey() {
        try {
            return sCryptoService.initRsaPrivateKey(mPrivateKeyCrtComponents.getP(),
                                                    mPrivateKeyCrtComponents.getQ(),
                                                    mPrivateKeyCrtComponents.getDp(),
                                                    mPrivateKeyCrtComponents.getDq(),
                                                    mPrivateKeyCrtComponents.getU());
        } catch (McbpCryptoException e) {
            throw new MppLiteException("Unable to initialize the private key: " + e.getMessage());
        }
    }

    /***
     * Build the Generate AC Response APDU
     *
     * @return the Generate AC Response APDU
     */
    private byte[] buildGenerateAcResponseWithCda() {
        final byte[] signedDynamicApplicationData = buildSignedDynamicApplicationData();

        final ByteArray cid = ByteArray.of(mCryptogramOutput.getCid());
        // We need a copy of the ATC as the original value will be cleared when erasing credentials
        final ByteArray atc = ByteArray.of(mCryptogramOutput.getAtc());
        final ByteArray iad = mCryptogramOutput.getIssuerApplicationData();
        final ByteArray sdad = ByteArray.of(signedDynamicApplicationData);

        final ByteArray responseData = ByteArray.of(Tlv.create(CID_TAG, cid));
        responseData.append(Tlv.create(ATC_TAG, atc));
        responseData.append(Tlv.create(SIGNED_DYNAMIC_APPLICATION_DATA_TAG, sdad));
        responseData.append(Tlv.create(ISSUER_APPLICATION_DATA_TAG, iad));

        if (mPoscii != null) {
            responseData.append(ByteArray.of(mPoscii.getTlv()));
        }

        final ByteArray response = Tlv.create(GENERATE_AC_RESPONSE_TAG, responseData);

        // Clear sensitive data
        Utils.clearByteArray(cid);
        Utils.clearByteArray(atc);
        Utils.clearByteArray(iad);
        Utils.clearByteArray(sdad);
        Utils.clearByteArray(responseData);

        return response.getBytes();
    }

    /**
     * Build the Signed Dynamic Application Data (GAC.8.4)
     *
     * @return The signed dynamic application data
     * @throws MppLiteException in case of a crypto error
     */
    private byte[] buildSignedDynamicApplicationData() {
        final byte[] dynamicAppData = buildDynamicApplicationData();
        final byte[] dynamicApplicationDataHash = getDynamicApplicationDataHash(dynamicAppData);

        final ByteArray inputForSignedDynamicApplicationData = ByteArray.of(DDA_HEADER);

        final int endOffset = mRsaKeyLength - dynamicApplicationDataHash.length - 2;
        final byte[] dynamicApplicationDataRangeInput = new byte[endOffset];
        System.arraycopy(dynamicAppData, 0, dynamicApplicationDataRangeInput, 0, endOffset);

        final ByteArray dynamicApplicationDataRange =
                ByteArray.of(dynamicApplicationDataRangeInput);

        final ByteArray dynamicApplicationDataHashInput = ByteArray.of(dynamicApplicationDataHash);

        inputForSignedDynamicApplicationData.append(dynamicApplicationDataRange);
        inputForSignedDynamicApplicationData.append(dynamicApplicationDataHashInput);
        inputForSignedDynamicApplicationData.append(ByteArray.of(DDA_TRAILER));

        try {
            return sCryptoService.rsa(inputForSignedDynamicApplicationData.getBytes());
        } catch (McbpCryptoException e) {
            throw new MppLiteException("Crypto Error: " + e.getMessage());
        } finally {
            // Clear sensitive data before returning
            Utils.clearByteArray(dynamicAppData);
            Utils.clearByteArray(inputForSignedDynamicApplicationData);
            Utils.clearByteArray(dynamicApplicationDataHashInput);
            Utils.clearByteArray(dynamicApplicationDataHash);
            Utils.clearByteArray(dynamicApplicationDataRangeInput);
        }
    }

    /**
     * Build the Dynamic Application Data
     */
    private byte[] buildDynamicApplicationData() {
        final byte[] padding = new byte[mRsaKeyLength - 63];
        for (int i = 0; i < padding.length; i++) padding[i] = PADDING_BYTE;

        final byte[] header = new byte[]{DAD_SIGNED_DATA_FORMAT,
                                         DAD_HASH_ALGORITHM_INDICATOR,
                                         DAD_ICC_DYNAMIC_DATA_LENGTH};

        final ByteArray idn = prepareIdn(mIdn);

        final ByteArray cid = ByteArray.of(mCryptogramOutput.getCid());
        final ByteArray cryptogram = ByteArray.of(mContext.getCryptogram());
        final ByteArray hash = ByteArray.of(buildHash());
        final ByteArray unpredictableNumber = ByteArray.of(mCommandApdu.getUnpredictableNumber());

        final ByteArray dynamicApplicationData = ByteArray.of(header);
        dynamicApplicationData.append(ByteArray.of((byte) idn.getLength()));
        dynamicApplicationData.append(idn);
        dynamicApplicationData.append(cid);
        dynamicApplicationData.append(cryptogram);
        dynamicApplicationData.append(hash);
        dynamicApplicationData.append(ByteArray.of(padding));
        dynamicApplicationData.append(unpredictableNumber);

        // I do not need the IDN any longer, can be cleared now
        Utils.clearByteArray(mIdn);
        // Need to clear also the temporary one in case I created one (long IDN)
        Utils.clearByteArray(idn);
        Utils.clearByteArray(cid);
        Utils.clearByteArray(cryptogram);
        Utils.clearByteArray(hash);
        Utils.clearByteArray(unpredictableNumber);

        return dynamicApplicationData.getBytes();
    }

    /***
     * Build the hash data input for the hash function in CDA
     *
     * @return The data input for the calculation of the hash function in CDA
     */
    private byte[] buildHash() {
        final ByteArray hashInput;
        if (mContext.getPdolData() != null) {
            hashInput = ByteArray.of(mContext.getPdolData());
            hashInput.append(ByteArray.of(mCommandApdu.getCdol()));
        } else {
            hashInput = ByteArray.of(mCommandApdu.getCdol());
        }

        hashInput.append(Tlv.create(CID_TAG, ByteArray.of(mCryptogramOutput.getCid())));
        hashInput.append(Tlv.create(ATC_TAG, mCryptogramOutput.getAtc()));
        hashInput.append(Tlv.create(ISSUER_APPLICATION_DATA_TAG,
                                    mCryptogramOutput.getIssuerApplicationData()));

        if (mPoscii != null) {
            hashInput.append(ByteArray.of(mPoscii.getTlv()));
        }

        try {
            return sCryptoService.sha1(hashInput.getBytes());
        } catch (McbpCryptoException e) {
            throw new MppLiteException("Crypto error occurred: " + e.getMessage());
        }
    }

    /**
     * Utility function to scale down the IDN to 8 bytes, if needed. In certain configurations
     * IDN is received from the CMS as 8 bytes data object, in other configurations it could be
     * received as 16 bytes object
     *
     * @param idnCredentials The IDN credential object as received from the CMS
     * @return The IDN to be used for the transaction
     */
    private ByteArray prepareIdn(final ByteArray idnCredentials) {
        return idnCredentials.getLength() == 16 ? idnCredentials.copyOfRange(8, 16)
                                                : idnCredentials;
    }

    /**
     * Utility function to build the SHA-1 hash of the dynamic application data
     *
     * @param dynamicApplicationData The dynamic application data to be signed
     * @return The SHA-1 of the dynamic application data
     */
    private byte[] getDynamicApplicationDataHash(final byte[] dynamicApplicationData) {
        final byte[] dynamicApplicationDataHash;
        try {
            dynamicApplicationDataHash = sCryptoService.sha1(dynamicApplicationData);
        } catch (McbpCryptoException e) {
            throw new MppLiteException("Crypto error: " + e.getMessage());
        }
        return dynamicApplicationDataHash;
    }

    /**
     * Utility function to build a response without CDA
     */
    private byte[] buildResponseWithoutCda() {
        final ByteArray cid = ByteArray.of(mCryptogramOutput.getCid());
        final ByteArray atc = mCryptogramOutput.getAtc();
        final ByteArray cryptogram = mCryptogramOutput.getCryptogram();
        final ByteArray iad = mCryptogramOutput.getIssuerApplicationData();

        ByteArray responseData = ByteArray.of(Tlv.create(CID_TAG, cid));
        responseData.append(Tlv.create(ATC_TAG, atc));
        responseData.append(Tlv.create(APPLICATION_CRYPTOGRAM_TAG, cryptogram));
        responseData.append(Tlv.create(ISSUER_APPLICATION_DATA_TAG, iad));

        if (mPoscii != null) {
            responseData.append(ByteArray.of(mPoscii.getTlv()));
        }

        ByteArray response = Tlv.create(GENERATE_AC_RESPONSE_TAG, responseData);

        // Clear sensitive data
        Utils.clearByteArray(cid);
        Utils.clearByteArray(atc);
        Utils.clearByteArray(cryptogram);
        Utils.clearByteArray(iad);
        Utils.clearByteArray(responseData);

        return response.getBytes();
    }
}
