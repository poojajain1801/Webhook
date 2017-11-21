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

import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDResponse;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.http.HttpFactory;
import com.mastercard.mcbp.utils.http.HttpPostRequest;
import com.mastercard.mcbp.utils.http.HttpResponse;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;

import org.apache.commons.codec.binary.Base64;

import java.nio.charset.Charset;

/**
 * Encapsulate the responsibility of encryption/decryption of data, which will be passes to
 * and fro between CMS-D and MPA in addition to of retry of failed communication.
 */
public class MdesCommunicator {

    /**
     * Logger
     */
    private final McbpLogger mLog = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Maximum retry count
     */
    public static final int DEFAULT_MAX_RETRY_COUNT = 3;

    final int DEFAULT_RETRY_TIME_IN_MILLIS = 5 * 1000;

    /**
     * Lde Remote Management Service
     */
    private final LdeRemoteManagementService mLdeRemoteManagementService;

    /**
     * Http factory
     */
    private final HttpFactory mHttpFactory;

    /**
     * Device finger print
     */
    private ByteArray mDeviceFingerPrint;

    /**
     * Crypto service
     */
    private final CryptoService mCryptoService;

    /**
     * Retry count
     */
    private int retryCount = DEFAULT_MAX_RETRY_COUNT;

    /**
     * Retry Event Listener
     */
    private final RetryEventListener mRetryEventListener;
    /**
     * Http post request property.
     */
    private static final String HTTP_POST_REQUEST_PROPERTY = "application/json";

    public MdesCommunicator(HttpFactory httpFactory, CryptoService cryptoService,
                            LdeRemoteManagementService ldeRemoteManagementService,
                            RetryEventListener retryEventListener) {
        this.mHttpFactory = httpFactory;
        this.mCryptoService = cryptoService;
        this.mLdeRemoteManagementService = ldeRemoteManagementService;
        this.mRetryEventListener = retryEventListener;
    }

    public CryptoService getCryptoService() {
        return this.mCryptoService;
    }

    /**
     * Encrypt if required and establish connection with specified URL and return response.
     *
     * @param data      Data needs to share to CMS-D
     * @param url       URL to desire service
     * @param toEncrypt Should request needs to be encrypted
     * @return Response in form of {@link ByteArray}
     * @throws HttpException
     */
    public ByteArray communicate(SessionContext sessionContext,
                                 String data,
                                 String url, boolean toEncrypt) throws HttpException {

        ByteArray dataReceived = null;

        try {
            HttpPostRequest httpPostRequest;
            if ( toEncrypt ) {
                try {
                    // Get Base64 encoded data
                    final String dataToSend = getEncryptedAndBase64EncodedServiceRequest(data,
                                                                                         sessionContext);
                    httpPostRequest = buildServiceRequest(dataToSend,
                                                          url,
                                                          sessionContext.getSessionCode());
                } catch ( LdeNotInitialized | McbpCryptoException | InvalidInput e ) {
                    throw new HttpException(e.getMessage(), e);
                }
            } else {
                httpPostRequest = buildPlainServiceRequest(data, url);
            }

            HttpResponse httpResponse;

            try {
                mLog.d("-- Execute Http Request --");
                httpResponse = mHttpFactory.execute(httpPostRequest);
            } catch ( HttpException e ) {
                try {
                    // Handle retry of communication
                    RetryHolder retryHolder = new RetryHolder();
                    retryHolder.data = data;
                    retryHolder.url = url;
                    retryHolder.toEncrypt = toEncrypt;
                    return handleRetryCommunication(e, retryHolder, sessionContext);
                } catch ( HttpException e1 ) {
                    throw e1;
                } catch ( Exception e1 ) {
                    throw new HttpException(e1.getMessage(), e1);
                }
            }

            ByteArray httpResponseContent = httpResponse.getContent();

            if ( toEncrypt ) {
                try {
                    dataReceived = getDecryptedServiceRequest(httpResponseContent, sessionContext);
                } catch ( McbpCryptoException | InvalidInput e ) {
                    throw new HttpException(e.getMessage(), e);
                }
            } else {
                dataReceived = httpResponseContent;
            }

        } finally {
            Utils.clearByteArray(mDeviceFingerPrint);
            retryCount = DEFAULT_MAX_RETRY_COUNT;
        }

        return dataReceived;
    }

    /**
     * @param data Data to be share with CMS-D
     * @return Encrypted request data with Base64 encoded.
     * @throws McbpCryptoException
     * @throws LdeNotInitialized
     */
    private String getEncryptedAndBase64EncodedServiceRequest(String data,
                                                              SessionContext sessionContext)
            throws McbpCryptoException, LdeNotInitialized, InvalidInput {

        //Increment mpa to cms counter
        sessionContext.incrementMpaToCmsCounter();

        final ByteArray requestData = ByteArray.of(data.getBytes(Charset.defaultCharset()));
        final int counter = sessionContext.getMpaToCmsCounter();

        final ByteArray serviceRequest =
                mCryptoService.buildServiceRequest(requestData,
                                                   getMacKey(),
                                                   getTransportKey(),
                                                   sessionContext.getSessionCode(),
                                                   counter);
        return serviceRequest.toBase64String();
    }

    private ByteArray getTransportKey() throws McbpCryptoException, InvalidInput {
        return mLdeRemoteManagementService.getTransportKey();
    }

    private ByteArray getMacKey() throws McbpCryptoException, InvalidInput {
        return mLdeRemoteManagementService.getMacKey();
    }

    /**
     * Decrypt response if require
     *
     * @param content Data to be share with CMS-D
     * @return Encrypted request data with Base64 encoded.
     */
    public ByteArray getDecryptedServiceRequest(ByteArray content,
                                                SessionContext sessionContext)
            throws HttpException, McbpCryptoException, InvalidInput {
        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;CMS_D_RESPONSE;RESPONSE;SENDER:CMS;HTTP_RESPONSE:(["
               + content.toString() + "])");
        // #MCBP_LOG_END

        String responseJsonString = new String(content.getBytes(), Charset.defaultCharset());

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;CMS_D_RESPONSE;RESPONSE;SENDER:CMS;CMS_D_RESPONSE_JSON:(["
               + responseJsonString + "])");
        // #MCBP_LOG_END

        CmsDResponse cmsDResponse = CmsDResponse.valueOf(responseJsonString);

        if ( !cmsDResponse.isSuccess() ) {
            throw new HttpException(cmsDResponse.getErrorDescription());
        }

        return getDecryptedData(cmsDResponse, sessionContext);
    }

    /**
     * Parse the encryptedData JSON field. First convert from Base64 to HEX and then decrypt
     * the message content
     *
     * @param cmsDResponse   The CMS-D encryptedData field in the CMS-D response
     * @param sessionContext The Session Context
     * @return The decrypted message as ByteArray
     */
    public ByteArray getDecryptedData(CmsDResponse cmsDResponse, SessionContext sessionContext)
            throws HttpException, McbpCryptoException, InvalidInput {

        String encryptedData = cmsDResponse.getEncryptedData();

        //Base 64 decode
        byte[] decodedData = Base64.decodeBase64(encryptedData.getBytes(Charset.defaultCharset()));
        ByteArray decodedEncryptedData = ByteArray.of(decodedData);

        // #MCBP_LOG_BEGIN
        String decodedDataHexString = decodedEncryptedData.toHexString();
        mLog.d("MCBP_PROTOCOL;CMS_D_RESPONSE;CMS_D_RESPONSE;CMS_D_RESPONSE_ENCRYPTED_DATA:([" +
               decodedDataHexString + "])");
        // #MCBP_LOG_END

        //        Get Cms to Mpa counter(First 3 byte of data) and verify it from Mpa to Cms
        // counter.
        String cmsToMpaCounterHex = decodedEncryptedData.copyOfRange(0, 3).toHexString();
        int cmsToMpaCounter = Integer.parseInt(cmsToMpaCounterHex, 16);

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;CMS_D_RESPONSE;CMS_TO_MPA_COUNTER_EXPECTED:([" + cmsToMpaCounter +
               "])");
        mLog.d("MCBP_PROTOCOL;CMS_D_RESPONSE;CMS_TO_MPA_COUNTER_ACTUAL:([" + sessionContext
                .getCmsToMpaCounter() + "])");
        // #MCBP_LOG_END

        if ( sessionContext.getCmsToMpaCounter() >= cmsToMpaCounter ) {
            throw new HttpException(HttpResponse.SC_FORBIDDEN, "Http error");
        }

        sessionContext.setCmsToMpaCounter(cmsToMpaCounter);

        return mCryptoService.decryptServiceResponse(decodedEncryptedData,
                                                     getMacKey(),
                                                     getTransportKey(),
                                                     sessionContext.getSessionCode());
    }

    private HttpPostRequest buildServiceRequest(final String encryptedServiceRequest,
                                                final String url,
                                                final ByteArray sessionCode)
            throws McbpCryptoException, InvalidInput {
        final HttpPostRequest req = mHttpFactory.getHttpPostRequest(url);

        String jsonCmsDedicatedRequest = buildCmsRequestJson(encryptedServiceRequest, sessionCode);

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;CMS_D_REQUEST;SENDER:MPA;CMS_D_REQUEST_DATA_JSON:"
               + jsonCmsDedicatedRequest + "])");
        // #MCBP_LOG_END

        req.withRequestData(jsonCmsDedicatedRequest);
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);
        return req;
    }

    /**
     * Utility function to build the authentication code
     */
    private ByteArray buildAuthenticationCode(final ByteArray sessionCode)
            throws McbpCryptoException {
        return mCryptoService.calculateAuthenticationCode(
                mLdeRemoteManagementService.getMobileKeySetIdAsByteArray(),
                sessionCode,
                mDeviceFingerPrint);
    }

    private String buildCmsRequestJson(final String encryptedServiceRequest,
                                       final ByteArray sessionCode)
            throws McbpCryptoException {
        CmsDRequest cmsDRequest = new CmsDRequest();
        cmsDRequest.setEncryptedData(encryptedServiceRequest);

        // Build the authentication code
        final ByteArray authenticationCode =
                buildAuthenticationCode(sessionCode);
        cmsDRequest.setAuthenticationCode(authenticationCode);

        // A bit of overhead as we need to convert the mobileKeySetId back to String
        cmsDRequest.setMobileKeysetId(
                new String(mLdeRemoteManagementService.getMobileKeySetIdAsByteArray().getBytes()));

        return cmsDRequest.toJsonString();
    }

    private HttpPostRequest buildPlainServiceRequest(String data, String url) {
        final HttpPostRequest req = mHttpFactory.getHttpPostRequest(url);

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;CMS_D_REQUEST;SENDER:MPA;CMS_D_REQUEST_DATA_JSON:"
               + data + "])");
        // #MCBP_LOG_END

        req.withRequestData(data);
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);
        return req;
    }

    /**
     * Handle retry of communication if required
     *
     * @param e           Exception occurred
     * @param retryHolder Retry holder instance
     * @return Data received.
     */
    private ByteArray handleRetryCommunication(final HttpException e,
                                               final RetryHolder retryHolder,
                                               SessionContext mSessionContext)
            throws HttpException {

        ByteArray dataReceived;

        if ( e.getErrorCode() == HttpResponse.SC_SERVICE_UNAVAILABLE &&
             e.getRetryAfterTime() != 0 && retryCount > 0 ) {

            mLog.d("Retry call with count : " + retryCount);

            dataReceived = retryAfter(e.getRetryAfterTime() * 1000,
                                      retryHolder,
                                      mSessionContext);

        } else if ( isErrorCodeAndRetryCountValidForRetry(e.getErrorCode()) ) {
            mLog.d("Retry call with count : " + retryCount);
            dataReceived = retryAfter(DEFAULT_RETRY_TIME_IN_MILLIS,
                                      retryHolder,
                                      mSessionContext);

        } else {
            throw new HttpException(e.getErrorCode(), e.getMessage(), e);
        }
        return dataReceived;
    }

    private boolean isErrorCodeAndRetryCountValidForRetry(int code) {

        return (code == HttpResponse.SC_MOVED_TEMPORARILY ||
                code == HttpResponse.SC_INTERNAL_SERVER_ERROR ||
                code == HttpResponse.SC_REQUEST_TIMEOUT ||
                code == HttpResponse.SC_SERVICE_UNAVAILABLE ||
                code == HttpResponse.SC_GATEWAY_TIMEOUT ||
                code == McbpErrorCode.SSL_ERROR_CODE) && retryCount > 0;
    }

    private ByteArray retryAfter(long timeInMillis,
                                 RetryHolder retryHolder,
                                 SessionContext mSessionContext) throws HttpException {

        ByteArray dataReceived = null;
        try {
            Thread.sleep(timeInMillis);
        } catch ( InterruptedException e ) {
            throw new HttpException(e.getMessage(), e);
        }
        retryCount--;
        mRetryEventListener.onRetry(retryCount);
        try {
            dataReceived = communicate(mSessionContext,
                                       retryHolder.data,
                                       retryHolder.url,
                                       retryHolder.toEncrypt);
        } catch ( HttpException e ) {
            //need not to do any thing, its under retry.
            //its recursive, only last retry exception should be taken care, which is in
            // communicate method
        }
        return dataReceived;
    }

    public MdesCommunicator withDeviceFingerPrint(ByteArray mpaFingerPrint) {
        this.mDeviceFingerPrint = ByteArray.of(mpaFingerPrint);
        return this;
    }

    /**
     * Class to store Retry params of communication
     */
    private class RetryHolder {
        public String data;
        public String url;
        public boolean toEncrypt;
    }

}
