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

package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.MobileKeys;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create register request,execute it and process response
 */
public class RegisterRequestHandler extends AbstractRequestHandler {

    /**
     * Register request URL
     */
    public static final String REGISTER = BASE_REQUEST + "/register";

    public RegisterRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                  SessionAwareAction sessionAwareAction,
                                  String requestId)
            throws ServiceException {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    /**
     * Prepares the data, ready for a Register request to be sent
     */
    private CmsDRegisterRequest prepareRequest() throws ServiceException {

        CmsDRegisterRequestHolder cmsDRegisterRequestHolder = getRegisterParams();

        // Generate a random key for the CMS registration and encrypt it using the public key
        // certificate
        final ByteArray encryptedRgk;
        try {
            final ByteArray publicKey = ByteArray.of(cmsDRegisterRequestHolder.pubKey);
            encryptedRgk = getCryptoService().buildRgkForRegistrationRequest(publicKey);
        } catch (McbpCryptoException e) {
            throw new ServiceException(McbpErrorCode.CRYPTO_ERROR, e.getMessage(), e);
        }

        // Populate the common registration request input object

        // Create a register request using the input data
        CmsDRegisterRequest cmsDRegisterRequest = new CmsDRegisterRequest();
        cmsDRegisterRequest
                .setDeviceFingerprint(getLdeRemoteManagementService().getMpaFingerPrint());
        cmsDRegisterRequest
                .setPaymentAppInstanceId(cmsDRegisterRequestHolder.mPaymentAppInstanceId);
        cmsDRegisterRequest
                .setPaymentAppProviderId(cmsDRegisterRequestHolder.mPaymentAppProviderId);
        cmsDRegisterRequest.setRegistrationCode(cmsDRegisterRequestHolder.registrationCode);
        cmsDRegisterRequest.setRgk(encryptedRgk);

        return cmsDRegisterRequest;
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {

        // Make the request
        CmsDRegisterRequest cmsDRegisterRequest = prepareRequest();

        //Execute register request
        CmsDRegisterResponse cmsDRegisterResponse =
                executeRegisterRequest(cmsDRegisterRequest.toJsonString());

        // Process the response
        return processMcbpCmsDRegisterOutput(cmsDRegisterResponse);
    }

    @Override
    public String getRequestUrl() {
        return getRegisterParams().responseHost + REGISTER;
    }

    private CmsDRegisterRequestHolder getRegisterParams() {
        return (CmsDRegisterRequestHolder) this.getCmsDRequestHolder();
    }

    /**
     * Execute register request
     *
     * @param inputData register request data as JSON
     * @throws HttpException
     */
    private CmsDRegisterResponse executeRegisterRequest(String inputData)
            throws HttpException {

        // Execute the request - we'll use MDES Communicator to manage URLs
        ByteArray response = communicate(inputData, false);

        // #MCBP_LOG_BEGIN
        mLogger.d("MDES_PROTOCOL;REGISTER_CMS_D;REGISTER_RESPONSE_CMS-D;SENDER:CMS-D;" +
                  "REGISTER_RESPONSE_CMS-D:([" + new String(response.getBytes()) + "])");
        // #MCBP_LOG_END

        return CmsDRegisterResponse.valueOf(response);
    }

    /**
     * Takes the MCBP CMS-D Register output, decrypts the mobile keys and saves to the database
     *
     * @param cmsDRegisterResponse the output received from the CMS-R Register request
     * @return the generic remote management response holder
     * @throws ServiceException
     */
    private RemoteManagementResponseHolder processMcbpCmsDRegisterOutput(
            CmsDRegisterResponse cmsDRegisterResponse) throws ServiceException {

        if (!cmsDRegisterResponse.isSuccess()) {
            throw new ServiceException(McbpErrorCode.SERVER_ERROR,
                                       cmsDRegisterResponse.getErrorDescription());
        }
        setAndUpdateRemoteManagementUrl(cmsDRegisterResponse.getRemoteManagementUrl());

        //Get mobile keySetId
        String mobileKeySetId = cmsDRegisterResponse.getMobileKeysetId();

        // #MCBP_LOG_BEGIN
        mLogger.d("MCBP_PROTOCOL;REGISTER_CMS_D;SENDER:CMS;MOBILE_KEY_SET_ID: ([" +
                  mobileKeySetId + "])");
        // #MCBP_LOG_END

        //Decrypt and remove padding
        MobileKeys encryptedMobileKeys = cmsDRegisterResponse.getMobileKeys();

        // Decrypting TransportKey
        ByteArray decryptedTransportKey;

        // Decrypting MacKey
        ByteArray decryptedMacKey;

        // Decrypting DataEncryptionKey
        ByteArray decryptedDataEncryptionKey;

        final CryptoService.MobileKeys mobileKeys;
        try {
            mobileKeys = getCryptoService()
                    .decryptMobileKeys(encryptedMobileKeys.getTransportKey().getBytes(),
                                       encryptedMobileKeys.getMacKey().getBytes(),
                                       encryptedMobileKeys.getDataEncryptionKey().getBytes());
        } catch (McbpCryptoException e) {
            throw new ServiceException(McbpErrorCode.CRYPTO_ERROR, e.getMessage(), e);
        }
        decryptedTransportKey = ByteArray.of(mobileKeys.getTransportKey());
        Utils.clearByteArray(mobileKeys.getTransportKey());

        decryptedMacKey = ByteArray.of(mobileKeys.getMacKey());
        Utils.clearByteArray(mobileKeys.getMacKey());

        decryptedDataEncryptionKey = ByteArray.of(mobileKeys.getDataEncryptionKey());
        Utils.clearByteArray(mobileKeys.getDataEncryptionKey());

        // #MCBP_LOG_BEGIN
        mLogger.d(
                "MCBP_PROTOCOL;REGISTER_CMS_D;MOBILE_KEYS;SENDER:CMS;DECRYPTED_TRANSPORT_KEY: " +
                "([" + decryptedTransportKey + "])");
        mLogger.d("MCBP_PROTOCOL;REGISTER_CMS_D;MOBILE_KEYS;SENDER:CMS;DECRYPTED_MAC_KEY: "
                  + "([" + decryptedMacKey + "])");
        mLogger.d("MCBP_PROTOCOL;REGISTER_CMS_D;MOBILE_KEYS;SENDER:CMS;" +
                  "DECRYPTED_DATA_ENCRYPTION_KEY: ([" + decryptedDataEncryptionKey + "])");
        // #MCBP_LOG_END
        try {
            //Update Wallet status to Register.
            getLdeRemoteManagementService().updateWalletState(WalletState.REGISTER);

            // First let's make sure we insert the mobile KeySetId
            getLdeRemoteManagementService().insertMobileKeySetId(mobileKeySetId);
            getLdeRemoteManagementService().insertTransportKey(decryptedTransportKey);
            getLdeRemoteManagementService().insertMacKey(decryptedMacKey);
            getLdeRemoteManagementService().insertDataEncryptionKey(decryptedDataEncryptionKey);

        } catch (McbpCryptoException | InvalidInput e) {
            throw new ServiceException(McbpErrorCode.LDE_ERROR, e.getMessage(), e);
        }

        return RemoteManagementResponseHolder.generateSuccessResponse(getCmsDRequestHolder(),
                                                                      cmsDRegisterResponse);
    }

    /**
     * Update remote management URL in to database
     *
     * @param url URL for remote management operation
     */
    private void setAndUpdateRemoteManagementUrl(String url) {
        if (url == null || url.length() == 0) {
            return;
        }
        try {
            getLdeRemoteManagementService().updateRemoteManagementUrl(url);
        } catch (InvalidInput invalidInput) {
            mLogger.d(invalidInput.getMessage());
        }
    }
}
