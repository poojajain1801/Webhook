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

package com.mastercard.mcbp.remotemanagement.mcbpV1;

import com.mastercard.mcbp.businesslogic.ApplicationInfo;
import com.mastercard.mcbp.businesslogic.MobileDeviceInfo;
import com.mastercard.mcbp.card.credentials.SingleUseKeyWrapper;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.data.mobilecheck.DigitizeCardProfileLogs;
import com.mastercard.mcbp.lde.data.mobilecheck.DigitizeCardProfileTransactionLog;
import com.mastercard.mcbp.lde.data.mobilecheck.MobileCheckResponse;
import com.mastercard.mcbp.lde.data.mobilecheck.MpaData;
import com.mastercard.mcbp.lde.data.mobilecheck.MpaSpecificData;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.CmsActivationTask;
import com.mastercard.mcbp.remotemanagement.CmsConfiguration;
import com.mastercard.mcbp.remotemanagement.CmsService;
import com.mastercard.mcbp.remotemanagement.mcbpV1.credentials.SingleUseKeyMcbpV1;
import com.mastercard.mcbp.remotemanagement.mcbpV1.models.ActivationRequest;
import com.mastercard.mcbp.remotemanagement.mcbpV1.models.AuthenticationRequest;
import com.mastercard.mcbp.remotemanagement.mcbpV1.models.GoOnlineRequest;
import com.mastercard.mcbp.remotemanagement.mcbpV1.models.PostActivationRequest;
import com.mastercard.mcbp.remotemanagement.mcbpV1.models.SendInformationRequest;
import com.mastercard.mcbp.remotemanagement.mcbpV1.profile.DigitizedCardProfileMcbpV1;
import com.mastercard.mcbp.userinterface.UserInterfaceListener;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.McbpCheckedException;
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

import java.nio.charset.Charset;
import java.util.List;

/**
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
public class CmsServiceImpl implements CmsService {
    /**
     * Dummy mobilekey set id to use while insertion of a mobile key. After successful
     * registration of MPA.
     * Used for backward architecture compatibility
     */
    public static final String DUMMY_MOBILE_KEY_SET_ID = "dummyMobileKeysetId12345";
    /**
     * Response OK.
     */
    public static final String RESPONSE_OK = "OK";

    /**
     * Http post request property.
     */
    private static final String HTTP_POST_REQUEST_PROPERTY = "application/json";

    /**
     * Http Factory abstraction to handle http requests and responses
     */
    private final HttpFactory mHttpFactory;
    /**
     * CMS Configuration
     */
    private final CmsConfiguration mCmsConfiguration;
    /**
     * Device information
     */
    private MobileDeviceInfo mDeviceInfo;
    /**
     * Remote Management Service
     */
    private final LdeRemoteManagementService mRemoteManagementService;
    /**
     * Logger
     */
    private final McbpLogger mLog = McbpLoggerFactory.getInstance().getLogger(this);
    /**
     * Cryptographic services
     */
    private final CryptoService mCryptoService;
    /**
     * Listener to update UI
     */
    private UserInterfaceListener mUserInterfaceListener;
    private ApplicationInfo mApplicationInfo;
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Default Constructor
     */
    public CmsServiceImpl(HttpFactory httpFactory, CmsConfiguration cmsConfiguration,
                          LdeRemoteManagementService remoteManagementService,
                          ApplicationInfo applicationInfo) {
        this.mHttpFactory = httpFactory;
        this.mCmsConfiguration = cmsConfiguration;
        this.mRemoteManagementService = remoteManagementService;
        this.mCryptoService = CryptoServiceFactory.getDefaultCryptoService();
        this.mApplicationInfo = applicationInfo;
    }

    @Override
    public CmsRegisterResult registerToCms(final String userId, final String activationCode) {
        CmsRegisterResult result;

        // Building Http Request
        final HttpPostRequest activationRequest = buildActivationRequest(userId, activationCode);

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;ACTIVATION;REQUEST;SENDER:CMS;HTTP_REQUEST;DATA(["
               + activationRequest.toString() + "])");
        // #MCBP_LOG_END

        // posting data to CMS
        final HttpResponse activationResponse;
        try {
            activationResponse = mHttpFactory.execute(activationRequest);
            // #MCBP_LOG_BEGIN
            mLog.d("MCBP_PROTOCOL;ACTIVATION;RESPONSE;SENDER:CMS;HTTP_STATUS_CODE:(["
                   + activationResponse.getStatusCode() + "])");
            // #MCBP_LOG_END
            result = new CmsRegisterResult(RegisterResultStatus.SUCCESS);
        } catch (HttpException e) {
            result = handleRegistrationErrorCondition(e);
        }

        return result;
    }

    private CmsRegisterResult handleRegistrationErrorCondition(HttpException e) {
        final CmsRegisterResult result;
        switch (e.getErrorCode()) {
            case HttpResponse.SC_NOT_FOUND:
                result = new CmsRegisterResult(RegisterResultStatus.ERROR_URL);
                break;
            case HttpResponse.SC_BAD_REQUEST:
            case HttpResponse.SC_UNAUTHORIZED:
            case HttpResponse.SC_INTERNAL_SERVER_ERROR:
                result = new CmsRegisterResult(RegisterResultStatus.ERROR_MCBP);
                break;
            default:
                result = new CmsRegisterResult(RegisterResultStatus.ERROR_NETWORK);
                break;
        }
        result.setErrorMessage(e.getMessage());
        return result;
    }

    private HttpPostRequest buildActivationRequest(String userId, String activationCode) {
        final HttpPostRequest req = mHttpFactory.getHttpPostRequest(mCmsConfiguration.urlInit()
                                                                    + CmsApi.ACTIVATE_URI);

        ActivationRequest activationRequest = new ActivationRequest();
        activationRequest.activationCode = activationCode;
        activationRequest.userId = userId;
        req.withRequestData(activationRequest.toJsonString());
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);
        return req;
    }

    @Override
    public CmsRegisterResult sendInformation(final String rnsMpaId, String userId) {

        if (rnsMpaId == null || rnsMpaId.isEmpty()) {
            return new CmsRegisterResult(RegisterResultStatus.INVALID_RNS_MPA_ID);
        }

        final HttpPostRequest req = buildSendInformationRequest(rnsMpaId, userId);
        // posting data to CMS
        final HttpResponse resp;
        CmsRegisterResult result;
        try {
            resp = mHttpFactory.execute(req);
            ServiceRequest serviceRequest = ServiceRequest.valueOf(resp.getContent().getBytes());

            // #MCBP_LOG_BEGIN
            mLog.d("MCBP_PROTOCOL;ACTIVATION;RESPONSE;SENDER:CMS;DATA("
                   + "REQUEST_ID:" + serviceRequest.getServiceRequestId()
                   + ",SERVICE_ID:" + serviceRequest.getServiceId()
                   + ",SERVICE_DATA:" + serviceRequest.getServiceData() + ")");
            // #MCBP_LOG_END

            // activation is successful

            CmsActivationData deserializeCmsActivationData = CmsActivationData
                    .valueOf(serviceRequest.getServiceData().getBytes(Charset.defaultCharset()));

            //Insertion of Mobile keys in LDE
            insertKeys(deserializeCmsActivationData.getConfidentialityKey(),
                       deserializeCmsActivationData.getMacKey());

            result = new CmsRegisterResult(deserializeCmsActivationData);
        } catch (HttpException e) {
            result = handleRegistrationErrorCondition(e);
        }

        return result;
    }

    @Override
    public void openRemoteSession(ByteArray rnsMessage) {
        ByteArray mpaFgp;
        ByteArray sessionId = null;
        ByteArray generatedAuthCode;
        RnsMessage message;
        CmsToken cmsToken = null;

        // checking lde state
        if (!getLdeRemoteManagementService().isLdeInitialized()) {
            mLog.d("Ignoring Message as Lde is Uninitialized");
            return;
        }
        final ByteArray macKey;
        final ByteArray confidentialityKey;
        final ByteArray notificationData;

        try {
            message = new RnsMessage(rnsMessage);

            // Get Mac Key to decrypt cms messages
            macKey = ByteArray.of(getLdeRemoteManagementService().getMacKey());

            confidentialityKey =
                    ByteArray.of(getLdeRemoteManagementService().getConfidentialityKey());

            notificationData = mCryptoService
                    .decryptMcbpV1NotificationData(message.getEncryptedSession(),
                                                   message.getMac(),
                                                   macKey,
                                                   confidentialityKey);

        } catch (McbpCryptoException | InvalidInput e) {
            // Most likely it is a MAC mismatch
            mLogger.d(e.getMessage());
            return;
        }

        try {
            sessionId = notificationData.copyOfRange(3, 32);
            mpaFgp = getLdeRemoteManagementService().getMpaFingerPrint();

            // #MCBP_LOG_BEGIN
            mLog.d("MCBP_PROTOCOL;DECRYPTED_RNS_MESSAGE;DATA(["
                   + "REM_MGT_INFO:" + notificationData.copyOfRange(0, 3).toHexString()
                   + ",SESSION_ID:" + sessionId + "])");
            mLog.d("MCBP_PROTOCOL;GENERATING_AUTHENTICATION_CODE;DATA(["
                   + "CMS_MPA_ID:" + getLdeRemoteManagementService().getCmsMpaId().toHexString()
                   + ",SESSION_ID:" + sessionId.toHexString()
                   + ",DEVICE_FINGER_PRINT:" + mpaFgp.toHexString() + "])");
            // #MCBP_LOG_END

            // Generate Auth Code
            generatedAuthCode =
                    new FnGenAuthCode().withMcbpCryptoService(mCryptoService).
                            withCmsMpaId(getLdeRemoteManagementService().getCmsMpaId())
                                       .withSessionId(sessionId)
                                       .withDeviceFingerPrint(mpaFgp)
                                       .generateCode();

            // #MCBP_LOG_BEGIN
            mLog.d("MCBP_PROTOCOL;AUTHENTICATION_CODE:" + generatedAuthCode.toHexString());
            // #MCBP_LOG_END

            final HttpPostRequest req =
                    buildAuthRequest(getLdeRemoteManagementService().getUrlRemoteManagement(),
                                     getLdeRemoteManagementService().getCmsMpaId().toHexString(),
                                     generatedAuthCode.toHexString());

            // #MCBP_LOG_BEGIN
            mLog.d("MCBP_PROTOCOL;HTTP_REQ;DATA([" + req.toString() + "])");
            // #MCBP_LOG_END

            // posting data to CMS
            HttpResponse resp = mHttpFactory.execute(req);
            if (resp.getStatusCode() == HttpResponse.SC_NOT_FOUND) {
                if (mUserInterfaceListener != null) {
                    mUserInterfaceListener
                            .onCardUpdated(ServiceRequestUtils.ServiceRequestEnum.NETWORK_ERROR,
                                           null);
                    return;
                }
            }
            cmsToken = new CmsToken();
            CmsActivationTask executeTokenJob = null;

            while (resp.getStatusCode() == HttpResponse.SC_OK) {
                if (executeTokenJob != null) {
                    executeTokenJob.execute();
                }

                String responseDataHex = new String(resp.getContent().getBytes());
                ByteArray cipheredPtp = ByteArray.of(responseDataHex);

                CmsPayload cmsPtp = new CmsPayload(cipheredPtp);

                ByteArray cmsToMpaCounter = cmsPtp.getCmsToMpaCounter();
                ByteArray encryptedData = cmsPtp.getEncryptedData();

                int cmsToMpaCounterReceived = Integer.parseInt(cmsToMpaCounter.toHexString(), 16);

                // Stop if the counter is not valid
                if (cmsToken.getRefCmsToMpa() >= cmsToMpaCounterReceived) {
                    break;
                }


                // Set the reference counter to the received counter
                cmsToken.setRefCmsToMpa(cmsToMpaCounterReceived);
                final ByteArray decryptedPtp;
                try {
                    decryptedPtp = mCryptoService.decryptServiceResponse(cipheredPtp,
                                                                         macKey,
                                                                         confidentialityKey,
                                                                         sessionId);
                } catch (McbpCryptoException e) {
                    // Something went wrong (e.g. MAC did not match)
                    mLogger.d(e.getMessage());
                    break;
                }

                ServiceRequest serviceRequest = ServiceRequest.valueOf(decryptedPtp.getBytes());

                // Stop if it contains an Error ID Or RESULTS_ID
                if (serviceRequest.getServiceId().
                        equalsIgnoreCase(ServiceRequestUtils.ERROR_ID) || serviceRequest
                            .getServiceId().equalsIgnoreCase(ServiceRequestUtils.RESULTS_ID)) {
                    break;
                }

                // #MCBP_LOG_BEGIN
                mLog.d("DECRYPTED_DATA_CMS (HEX): " + decryptedPtp.toHexString());
                mLog.d("MCBP_PROTOCOL;SENDER:CMS;DATA(["
                       + "REQUEST_ID:" + serviceRequest.getServiceRequestId()
                       + ",SERVICE_ID:" + serviceRequest.getServiceId()
                       + ",SERVICE_DATA:" + serviceRequest.getServiceData() + "])");
                // #MCBP_LOG_END

                cmsToken.setServiceRequest(serviceRequest);
                executeTokenJob = executeToken(cmsToken);
                resp = sendActivationProof(cmsToken,
                                           executeTokenJob,
                                           confidentialityKey,
                                           macKey,
                                           sessionId);
            }

            if (resp.getStatusCode() == HttpResponse.SC_NOT_FOUND) {
                if (mUserInterfaceListener != null) {
                    mUserInterfaceListener
                            .onCardUpdated(ServiceRequestUtils.ServiceRequestEnum.NETWORK_ERROR,
                                           null);
                    return;
                }
            }

            if (mUserInterfaceListener != null && executeTokenJob != null
                && executeTokenJob.getJobId() != null) {
                mUserInterfaceListener.onCardUpdated(
                        ServiceRequestUtils.getServiceRequestIntValue(executeTokenJob.getJobId()),
                        executeTokenJob.getResult());
            }
        } catch (McbpCryptoException e) {
            mLogger.d(e.getMessage());
        } catch (Exception e) {
            // Errors are not forwarded to the UI, but showed here
            // TODO: add the logic to forward errors to the UI (if needed)
            mLogger.d(e.getMessage());
            mLog.e("Error AuthenticatingWithToken " + e.getMessage());
        } finally {
            Utils.clearByteArray(sessionId);
            Utils.clearByteArray(message.getRnsMessageId());
            Utils.clearByteArray(message.getEncryptedSession());
            Utils.clearByteArray(message.getMac());

            if (cmsToken != null) {
                cmsToken.wipe();
            }
        }
    }

    @Override
    public void registerUiListener(UserInterfaceListener listener) {
        this.mUserInterfaceListener = listener;
    }

    @Override
    public void goOnlineForSync() {
        HttpPostRequest buildGoOnlineSyncRequest =
                buildGoOnlineSyncRequest(getLdeRemoteManagementService()
                                                 .getCmsMpaId()
                                                 .toHexString());
        try {
            HttpResponse resp = mHttpFactory.execute(buildGoOnlineSyncRequest);
        } catch (HttpException e) {
            mLogger.d(e.getMessage());
        }
    }

    @Override
    public void setMobileDeviceInfo(MobileDeviceInfo mobileDeviceInfo) {
        this.mDeviceInfo = mobileDeviceInfo;
    }

    @Override
    public void insertDummyMobileKeySetId() {
        try {
            getLdeRemoteManagementService().insertMobileKeySetId(DUMMY_MOBILE_KEY_SET_ID);
        } catch (McbpCryptoException | InvalidInput e) {
            mLogger.d(e.getMessage());
        }
    }

    private HttpPostRequest buildGoOnlineSyncRequest(String cmsMpaId) {
        final HttpPostRequest req = mHttpFactory.getHttpPostRequest(mCmsConfiguration.urlInit()
                                                                    + CmsApi.REQUEST_MOBILE_CHECK);
        GoOnlineRequest goOnlineRequest = new GoOnlineRequest();
        goOnlineRequest.cmsMpaId = cmsMpaId;

        req.withRequestData(goOnlineRequest.toJsonString());
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);
        return req;
    }

    private HttpPostRequest buildAuthRequest(String url, String cmsMpaId, String authCode) {
        final HttpPostRequest req = mHttpFactory.getHttpPostRequest(url + CmsApi.AUTHENTICATE_URI);

        AuthenticationRequest authRequest = new AuthenticationRequest();
        authRequest.authenticationCode = authCode;
        authRequest.cmsMpaId = cmsMpaId;

        req.withRequestData(authRequest.toJsonString());
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);
        return req;
    }

    /**
     * Executes the Token received from the CMS
     *
     * @param cmsToken The token received from the CMS
     * @return CmsActivationTask
     * @throws com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException
     */
    private CmsActivationTask executeToken(CmsToken cmsToken) throws McbpCryptoException,
            InvalidInput {
        CmsActivationTask job = null;
        RemMgtInfo deserializeRemMgtInfo;

        String serviceId = cmsToken.getServiceRequest().getServiceId();
        String serviceData = cmsToken.getServiceRequest().getServiceData();

        ByteArray serviceDataBytes = null;
        // Prepare the service data as Byte Array (as it may be needed for later)
        if (serviceData != null) {
            serviceDataBytes = ByteArray.of(serviceData.getBytes());
        }

        switch (ServiceRequestUtils.getServiceRequestIntValue(serviceId)) {
            case ACTIVETRANSACTIONCREDENTIALS:
                break;
            case CHANGEMOBILEPIN:
                deserializeRemMgtInfo = RemMgtInfo.valueOf(serviceDataBytes.getBytes());
                job = changePin(deserializeRemMgtInfo.getData());
                break;
            case DELETE:
                deserializeRemMgtInfo = RemMgtInfo.valueOf(serviceDataBytes.getBytes());
                job = remoteWipeDigitizedCard(deserializeRemMgtInfo.getData());
                break;
            case GETDEVICEINFORMATION:
                job = mobileCheck();
                break;
            case INITIALIZEMPA:

                break;
            case PROVISIONCP:
                try {
                    DigitizedCardProfileMcbpV1 cardProfileMcbpV1 = DigitizedCardProfileMcbpV1
                            .valueOf(serviceData.getBytes(Charset.defaultCharset()));

                    job = provisionCardProfile(cardProfileMcbpV1);
                } catch (LdeNotInitialized ldeNotInitialized) {
                    //Shouldn't reach here.
                    mLogger.d(ldeNotInitialized.getMessage());
                }
                break;
            case PROVISIONSUK:
                try {
                    SingleUseKeyMcbpV1 singleUseKeyMcbpV1McbpV1 = SingleUseKeyMcbpV1.valueOf
                            (serviceData.getBytes(Charset.defaultCharset()));
                    job = provisionSuk(singleUseKeyMcbpV1McbpV1);

                } catch (McbpCheckedException e) {
                    mLogger.d(e.getMessage());
                }
                break;
            case REGISTERUSER:
                break;
            case REPLENISH:
                break;
            case RESETMPA:
                job = resetMpaToInstalledState();
                break;
            case REMOTEWIPE:
                job = remoteWipeWallet();
                break;
            case RESULTS:
                break;
            case RESUME:
                deserializeRemMgtInfo = RemMgtInfo.valueOf(serviceDataBytes.getBytes());
                job = resume(deserializeRemMgtInfo.getData());
                break;

            case SUSPEND:
                deserializeRemMgtInfo = RemMgtInfo.valueOf(serviceDataBytes.getBytes());
                job = suspend(deserializeRemMgtInfo.getData());
                break;
            default:
                break;
        }
        // Finally sending proof
        return job;
    }

    private HttpResponse sendActivationProof(CmsToken cmsToken, CmsActivationTask job,
                                             ByteArray confidentialityKey,
                                             ByteArray macKey,
                                             ByteArray sessionCode) throws
            McbpCryptoException, InvalidInput, HttpException {
        cmsToken.incrementMpaToCms();

        // Building activation proof
        ByteArray activationProof = ByteArray.get(0);

        ServiceRequestUtils.ServiceRequestEnum serviceRequestValue = ServiceRequestUtils
                .getServiceRequestIntValue(cmsToken.getServiceRequest().getServiceId());
        switch (serviceRequestValue) {
            case PROVISIONCP:
            case PROVISIONSUK:
            case SUSPEND:
            case CHANGEMOBILEPIN:
            case RESUME:
            case DELETE:
                // Version Control and Format
                activationProof.appendByte(CmsApi.VERSION_CONTROL);

                // Send "OK" status code
                // TODO: Future release may also capture error conditions
                ByteArray hashValue = mCryptoService.sha256(
                        ByteArray.of(RESPONSE_OK.getBytes(Charset.forName("UTF-8"))));
                activationProof.append(hashValue);

                // Retrieve the number of available SUKs
                String digitizedCardId = (String) job.getResult();
                int sukCount = 0;
                try {
                    sukCount =
                            getLdeRemoteManagementService().getSingleUseKeyCount(digitizedCardId);
                } catch (LdeNotInitialized ldeNotInitialized) {
                    mLogger.d(ldeNotInitialized.getMessage());
                }
                byte ptpSukCount = (byte) (sukCount & 0x00FF);

                // Add each ATC.
                activationProof.appendByte(ptpSukCount);
                ByteArray atcs;
                try {
                    atcs = getLdeRemoteManagementService().getAvailableATCs(digitizedCardId);
                } catch (LdeNotInitialized ldeNotInitialized) {
                    mLogger.d(ldeNotInitialized.getMessage());
                    break;
                }
                activationProof.append(atcs);
                Utils.clearByteArray(atcs);
                // #MCBP_LOG_BEGIN
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF:" + activationProof.toHexString());
                // #MCBP_LOG_END
                break;
            case GETDEVICEINFORMATION:
            case RESETMPA:
            case REMOTEWIPE:
                ByteArray hash = mCryptoService.sha256(
                        ByteArray.of(RESPONSE_OK.getBytes(Charset.forName("UTF-8"))));
                activationProof.append(hash);
                // #MCBP_LOG_BEGIN
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF:" + activationProof.toHexString());
                // #MCBP_LOG_END
                break;
            case REPLENISH:
                break;
            case RESETMOBILEPIN:
                break;
            case RESULTS:
                break;
            default:
                break;
        }

        ProofContainer activationProofContainer = new ProofContainer();
        activationProofContainer.setActivationProof(activationProof.toHexString());
        Utils.clearByteArray(activationProof);

        if (serviceRequestValue == ServiceRequestUtils.ServiceRequestEnum.GETDEVICEINFORMATION) {
            MobileCheckResponse mobileCheckResponse = (MobileCheckResponse) job.getResult();

            String serializeMpaData = mobileCheckResponse.toJsonString();

            activationProofContainer.setDeviceInformation(serializeMpaData);
        } else {
            activationProofContainer.setDeviceInformation(null);
        }

        String serializedProofContainer = activationProofContainer.toJsonString();

        ServiceResponse serviceResponse = new ServiceResponse();
        serviceResponse.setServiceData(serializedProofContainer);
        serviceResponse.setServiceResponseCode(RESPONSE_OK);
        serviceResponse.setServiceRequestId(cmsToken.getServiceRequest().getServiceRequestId());

        String serializedServiceResponse = serviceResponse.toJsonString();

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;SERVICE_RESPONSE:DATA([" + serializedServiceResponse + "])");
        // #MCBP_LOG_END

        final ByteArray serviceData = ByteArray.of(serializedServiceResponse.getBytes());
        final ByteArray mobileToCmsMessageBytes =
                mCryptoService.buildServiceRequest(serviceData, macKey, confidentialityKey,
                                                   sessionCode, cmsToken.getMpaToCmsCounter());
        HttpPostRequest postProof =
                buildPostProofRequest(serviceRequestValue, mobileToCmsMessageBytes);

        Utils.clearByteArray(mobileToCmsMessageBytes);

        return mHttpFactory.execute(postProof);
    }

    private CmsActivationTask changePin(final ByteArray digitizedCardId) {
        return new CmsActivationTask() {
            @Override
            public void execute() {
                mLog.d("------PIN CHANGED------");
                // remove all suks associated
                try {
                    getLdeRemoteManagementService().wipeDcSuk(digitizedCardId);
                    if (mUserInterfaceListener != null) {
                        mUserInterfaceListener.onCardUpdated(
                                ServiceRequestUtils.ServiceRequestEnum.CHANGEMOBILEPIN,
                                digitizedCardId.toHexString());
                    }
                } catch (InvalidInput | LdeNotInitialized e) {
                    mLogger.d(e.getMessage());
                }
            }

            @Override
            public Object getResult() {
                return digitizedCardId.toHexString();
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.CHANGE_MOBILE_PIN_ID;
            }
        };

    }

    private CmsActivationTask remoteWipeDigitizedCard(final ByteArray digitizedCardId) {

        return new CmsActivationTask() {
            @Override
            public void execute() {
                try {
                    getLdeRemoteManagementService().wipeDigitizedCard(digitizedCardId);
                } catch (LdeNotInitialized | InvalidInput e) {
                    mLogger.d(e.getMessage());
                }
                mLog.d("-----Delete-----");
            }

            @Override
            public Object getResult() {
                return digitizedCardId.toHexString();
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.DELETE_ID;
            }
        };

    }

    private CmsActivationTask mobileCheck() throws McbpCryptoException, InvalidInput {
        mLog.d("-----MOBILE CHECK INITIALIZED------");
        MpaSpecificData mpaSpecificData = new MpaSpecificData();
        mpaSpecificData.setRfu(mApplicationInfo.getRfu());
        mpaSpecificData.setStatus(mApplicationInfo.getStatus());
        mpaSpecificData.setVersion(mApplicationInfo.getVersion());
        try {
            List<String> listOfAvailableCardId = getLdeRemoteManagementService()
                    .getListOfAvailableCardId();
            DigitizeCardProfileLogs[] cpLogs = new DigitizeCardProfileLogs[listOfAvailableCardId
                    .size()];
            int j = 0;
            for (String digitizedCardId : listOfAvailableCardId) {
                int totalSuks =
                        getLdeRemoteManagementService().getSingleUseKeyCount(digitizedCardId);

                DigitizeCardProfileLogs dcCpLogs = new DigitizeCardProfileLogs();
                dcCpLogs.setDigitizedCardId(digitizedCardId);
                dcCpLogs.setNumberOfKeysLoaded(totalSuks);

                List<TransactionLog> transactionLogs = getLdeRemoteManagementService()
                        .getTransactionLogs(
                                digitizedCardId);

                if (!transactionLogs.isEmpty()) {
                    DigitizeCardProfileTransactionLog[] logArray = new
                            DigitizeCardProfileTransactionLog[transactionLogs.size()];
                    for (int i = 0; i < logArray.length; i++) {
                        logArray[i] = new DigitizeCardProfileTransactionLog();
                        TransactionLog transactionLogTemp = transactionLogs.get(i);
                        logArray[i].setAmount(transactionLogTemp.getAmount().toHexString());
                        logArray[i].setAtc(transactionLogTemp.getAtc().toHexString());
                        logArray[i].setCryptogramFormat(transactionLogTemp.getCryptogramFormat());
                        logArray[i].setCurrencyCode(transactionLogTemp.getCurrencyCode()
                                                                      .toHexString
                                                                              ());
                        logArray[i].setDate(transactionLogTemp.getDate().toHexString());
                        logArray[i].setDigitizedCardId(transactionLogTemp.getDigitizedCardId());
                        logArray[i].setHostingMEJailbroken(transactionLogTemp
                                                                   .isHostingMeJailBroken());
                        logArray[i].setRecentAttack(transactionLogTemp.isRecentAttack());
                        logArray[i].setUnpredictableNumber(transactionLogTemp
                                                                   .getUnpredictableNumber()
                                                                   .toHexString());
                    }
                    dcCpLogs.setTransactionData(logArray);
                } else {
                    dcCpLogs.setTransactionData(null);
                }
                cpLogs[j] = dcCpLogs;
                j++;
            }
            final MpaData mpaData = new MpaData();
            mpaData.setCardProfiles(cpLogs);
            mpaData.setMobileDeviceData(mDeviceInfo);
            mpaData.setMpaSpecificData(mpaSpecificData);

            final MobileCheckResponse mobileCheckResponse = new MobileCheckResponse();
            mobileCheckResponse
                    .setCmsMpaId(getLdeRemoteManagementService().getCmsMpaId().toHexString());
            mobileCheckResponse.setMpaData(mpaData);
            return new CmsActivationTask() {
                @Override
                public void execute() {
                    mLog.d("MOBILE CHECK ACTIVATION TASK");
                }

                @Override
                public Object getResult() {
                    return mobileCheckResponse;
                }

                @Override
                public String getJobId() {
                    return ServiceRequestUtils.GET_DEVICE_INFORMATION_ID;
                }
            };
        } catch (LdeNotInitialized e) {
            mLogger.d(e.getMessage());
        }
        return null;
    }

    /**
     * This method provision card profile.
     *
     * @param cardProfile The Card Profile wrapper object
     * @return Job to execute after receiving activation prof response.
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeNotInitialized
     */
    public CmsActivationTask provisionCardProfile(McbpDigitizedCardProfileWrapper cardProfile)
            throws McbpCryptoException,
            InvalidInput, LdeNotInitialized {
        mLog.i("------PROVISION_CARD_PROFILE-----");
        getLdeRemoteManagementService().provisionDigitizedCardProfile(cardProfile);
        final String digitizedCardId = cardProfile.getCardId();
        // task called when server acknowledges activation
        return new CmsActivationTask() {
            @Override
            public void execute() {
                mLog.d("ACTIVATE_CARD_PROFILE");
                try {
                    getLdeRemoteManagementService().activateProfile(digitizedCardId);
                    if (mUserInterfaceListener != null) {
                        mUserInterfaceListener
                                .onCardUpdated(ServiceRequestUtils.ServiceRequestEnum.PROVISIONCP,
                                               digitizedCardId);
                    }
                } catch (LdeNotInitialized | McbpCryptoException | InvalidInput e) {
                    mLogger.d(e.getMessage());//TODO:Do we need to update UI according to exception.
                }
            }

            @Override
            public Object getResult() {
                return digitizedCardId;
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.PROVISION_CP_ID;
            }

        };
    }

    public CmsActivationTask provisionSuk(SingleUseKeyWrapper singleUseKeyWrapper)
            throws McbpCheckedException {
        mLog.i("------PROVISION SUK-------");

        final String digitizedCardId = singleUseKeyWrapper.getCardId();
        getLdeRemoteManagementService().provisionSingleUseKey(singleUseKeyWrapper.toSingleUseKey());

        return new CmsActivationTask() {

            @Override
            public Object getResult() {
                return digitizedCardId;
            }

            @Override
            public void execute() {
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.PROVISION_SUK_ID;
            }
        };
        // updates UI if registered
    }

    private CmsActivationTask resetMpaToInstalledState() {

        return new CmsActivationTask() {
            @Override
            public void execute() {
                mLog.d("-----RESET MPA TO INSTALLED STATE-----");
                try {
                    getLdeRemoteManagementService().resetMpaToInstalledState();
                } catch (LdeNotInitialized ldeNotInitialized) {
                    mLogger.d(ldeNotInitialized.getMessage());
                }
            }

            @Override
            public Object getResult() {
                return null;
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.RESET_MPA_ID;
            }
        };

    }

    private CmsActivationTask remoteWipeWallet() {

        return new CmsActivationTask() {
            @Override
            public void execute() {
                mLog.d("-----REMOTE WIPE WALLET-----");
                try {
                    getLdeRemoteManagementService().remoteWipeWallet();
                } catch (LdeNotInitialized ldeNotInitialized) {
                    //Ignoring exception as we shouldn't reach here.
                    mLogger.d(ldeNotInitialized.getMessage());
                }
            }

            @Override
            public Object getResult() {
                return null;
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.REMOTE_WIPE;
            }
        };

    }

    private CmsActivationTask resume(final ByteArray digitizedCardId) {

        return new CmsActivationTask() {
            @Override
            public void execute() {
                if (mUserInterfaceListener != null) {
                    mUserInterfaceListener
                            .onCardUpdated(ServiceRequestUtils.ServiceRequestEnum.RESUME,
                                           digitizedCardId.toHexString());
                }
            }

            @Override
            public Object getResult() {
                return digitizedCardId.toHexString();
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.RESUME_ID;
            }
        };
    }

    public CmsActivationTask suspend(final ByteArray digitizedCardId) {
        mLog.i("------PROVISION SUSPEND-------");
        return new CmsActivationTask() {
            @Override
            public void execute() {
                // remove all suks associated
                try {
                    getLdeRemoteManagementService().wipeDcSuk(digitizedCardId);
                } catch (LdeNotInitialized | InvalidInput e) {
                    mLogger.d(e.getMessage());
                }
            }

            @Override
            public Object getResult() {
                return digitizedCardId.toHexString();
            }

            @Override
            public String getJobId() {
                return ServiceRequestUtils.SUSPEND_ID;
            }
        };

    }

    private static String getCounterAsHex(int count) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(Integer.toHexString(count));
        int diff = 6 - stringBuilder.length();
        if (diff > 0) {
            for (int i = 0; i < diff; i++) {
                stringBuilder.insert(0, "0");
            }
        }
        return stringBuilder.toString();
    }

    private HttpPostRequest buildPostProofRequest(ServiceRequestUtils.ServiceRequestEnum remMgt,
                                                  ByteArray proof) {
        // Build request
        HttpPostRequest req = mHttpFactory
                .getHttpPostRequest(getLdeRemoteManagementService().getUrlRemoteManagement()
                                    + CmsApi.ACTIVATION_PROOF_URI);

        PostActivationRequest postActivationRequest = new PostActivationRequest();
        postActivationRequest.cmsMpaId =
                getLdeRemoteManagementService().getCmsMpaId().toHexString();
        postActivationRequest.proofRequest = proof.toHexString();

        req.withRequestData(postActivationRequest.toJsonString());
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);

        // Log data related to this request
        logRequest(remMgt, proof);
        return req;
    }

    private void logRequest(ServiceRequestUtils.ServiceRequestEnum remMgt, ByteArray proof) {
        // #MCBP_LOG_BEGIN
        switch (remMgt) {
            case PROVISIONCP:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;PROVISIONING_CARD_PROFILE;REQUEST;" +
                       "SENDER:MPA;DATA(["
                       + CmsApi.CMS_MPA_ID_PARAM
                       + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString()
                       + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM
                       + ":"
                       + proof.toHexString() + "])");
                break;
            case PROVISIONSUK:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;PROVISIONING_SUK;REQUEST;SENDER:MPA;DATA(["
                       + CmsApi.ISSUER_IDENTIFIER_PARAM + ":"
                       + CmsApi.CMS_MPA_ID_PARAM + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString() + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM + ":"
                       + proof.toHexString() + "])");
                break;
            case GETDEVICEINFORMATION:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;PROVISIONING_MOBILE_CHECK;REQUEST;" +
                       "SENDER:MPA;DATA(["
                       + CmsApi.ISSUER_IDENTIFIER_PARAM
                       + ":"
                       + CmsApi.CMS_MPA_ID_PARAM
                       + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString()
                       + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM
                       + ":"
                       + proof.toHexString() + "])");
                break;
            case CHANGEMOBILEPIN:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;PROVISIONING_MOBILE_PIN;REQUEST;" +
                       "SENDER:MPA;" +
                       "DATA(["
                       + CmsApi.ISSUER_IDENTIFIER_PARAM
                       + ":"
                       + CmsApi.CMS_MPA_ID_PARAM
                       + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString()
                       + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM
                       + ":"
                       + proof.toHexString() + "])");
                break;
            case RESETMPA:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;PROVISIONING_REMOTE_WIPE;REQUEST;" +
                       "SENDER:MPA;DATA(["
                       + CmsApi.ISSUER_IDENTIFIER_PARAM
                       + ":"
                       + CmsApi.CMS_MPA_ID_PARAM
                       + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString()
                       + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM
                       + ":"
                       + proof.toHexString() + "])");
                break;
            case SUSPEND:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;PROVISIONING_SUSPEND;REQUEST;SENDER:MPA;" +
                       "DATA(["
                       + CmsApi.ISSUER_IDENTIFIER_PARAM + ":"
                       + CmsApi.CMS_MPA_ID_PARAM + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString() + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM + ":"
                       + proof.toHexString() + "])");
                break;
            default:
                mLog.d("MCBP_PROTOCOL;ACTIVATION_PROOF;REQUEST;SENDER:MPA;DATA(["
                       + CmsApi.ISSUER_IDENTIFIER_PARAM + ":"
                       + CmsApi.CMS_MPA_ID_PARAM + ":"
                       + getLdeRemoteManagementService().getCmsMpaId().toHexString() + ","
                       + CmsApi.ACTIVATION_PROOF_PARAM + ":"
                       + proof.toHexString() + "])");
        }
        // #MCBP_LOG_END
    }

    private HttpPostRequest buildSendInformationRequest(
            String rnsId, String userId) {
        final HttpPostRequest req = mHttpFactory.getHttpPostRequest(mCmsConfiguration.urlInit()
                                                                    + CmsApi.ACTIVATE_URI);

        SendInformationRequest sendInformationRequest = new SendInformationRequest();
        sendInformationRequest.userId = userId;
        sendInformationRequest.mobileId = rnsId;
        sendInformationRequest.osName = mDeviceInfo.getOsName();
        sendInformationRequest.osVersion = mDeviceInfo.getOsVersion();
        sendInformationRequest.osFirmwarebuild = mDeviceInfo.getOsFirmwareBuild();
        sendInformationRequest.manufacturer = mDeviceInfo.getManufacturer();
        sendInformationRequest.model = mDeviceInfo.getModel();
        sendInformationRequest.product = mDeviceInfo.getProduct();
        sendInformationRequest.osUniqueIdentifier = mDeviceInfo.getOsUniqueIdentifier();
        sendInformationRequest.imei = mDeviceInfo.getImei();
        sendInformationRequest.macAddress = mDeviceInfo.getMacAddress();
        sendInformationRequest.nfcSupport = mDeviceInfo.getNfcSupport();
        sendInformationRequest.screenSize = mDeviceInfo.getScreenSize();

        req.withRequestData(sendInformationRequest.toJsonString());
        req.withRequestProperty(HTTP_POST_REQUEST_PROPERTY);

        // #MCBP_LOG_BEGIN
        mLog.d("MCBP_PROTOCOL;ACTIVATION;REQUEST;SENDER:MPA;DATA([" + CmsApi.MOBILE_ID_PARAM + ":" +
               rnsId + ","
               + CmsApi.DEVICE_INFO_OS_NAME_PARAM + ":"
               + mDeviceInfo.getOsName() + ","
               + CmsApi.DEVICE_INFO_OS_VERSION_PARAM + ":"
               + mDeviceInfo.getOsVersion() + ","
               + CmsApi.DEVICE_INFO_OS_FIRMWARE_BUILD_PARAM + ":"
               + mDeviceInfo.getOsFirmwareBuild() + ","
               + CmsApi.DEVICE_INFO_DEVICE_MANUFACTURER_PARAM + ":"
               + mDeviceInfo.getManufacturer() + ","
               + CmsApi.DEVICE_INFO_MODEL_PARAM + ":"
               + mDeviceInfo.getModel() + ","
               + CmsApi.DEVICE_INFO_PRODUCT_PARAM + ":"
               + mDeviceInfo.getProduct() + ","
               + CmsApi.DEVICE_INFO_OS_UNIQUE_IDENTIFIER_PARAM + ":"
               + mDeviceInfo.getOsUniqueIdentifier() + ","
               + CmsApi.DEVICE_INFO_IMEI_PARAM + ":"
               + mDeviceInfo.getImei() + ","
               + CmsApi.DEVICE_INFO_MAC_ADDRESS_PARAM + ":"
               + mDeviceInfo.getMacAddress() + ","
               + CmsApi.DEVICE_INFO_NFC_SUPPORT_PARAM + ":"
               + mDeviceInfo.getNfcSupport() + ","
               + CmsApi.DEVICE_INFO_SCREEN_SIZE_PARAM + ":"
               + mDeviceInfo.getScreenSize() + "])");
        // #MCBP_LOG_END

        return req;
    }

    /**
     * Insertion of confidentiality key and mac key into data base.
     *
     * @param confidentialityKey Confidentiality key
     * @param macKey             mac key
     */
    private void insertKeys(ByteArray confidentialityKey, ByteArray macKey) {
        try {
            getLdeRemoteManagementService().insertConfidentialityKey(confidentialityKey);
            getLdeRemoteManagementService().insertMacKey(macKey);
        } catch (McbpCryptoException | InvalidInput e) {
            // TODO: Handle exceptions
            mLogger.d(e.getMessage());
        }
    }

    public LdeRemoteManagementService getLdeRemoteManagementService() {
        return mRemoteManagementService;
    }
}
