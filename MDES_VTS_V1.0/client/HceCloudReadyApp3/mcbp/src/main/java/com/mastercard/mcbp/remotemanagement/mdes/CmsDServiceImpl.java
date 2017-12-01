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

import com.mastercard.mcbp.card.profile.ProfileState;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.CmsDService;
import com.mastercard.mcbp.remotemanagement.WalletState;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDChangeMobilePinResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDReplenishResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.GetTaskStatusResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.RemoteManagementSessionData;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.userinterface.MdesRemoteManagementEventListener;
import com.mastercard.mcbp.utils.PropertyStorageFactory;
import com.mastercard.mcbp.utils.crypto.CertificateHandler;
import com.mastercard.mcbp.utils.crypto.CertificateMetaData;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.DuplicateMcbpCard;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.McbpCardException;
import com.mastercard.mcbp.utils.http.HttpFactory;
import com.mastercard.mcbp.utils.http.HttpGetRequest;
import com.mastercard.mcbp.utils.http.HttpResponse;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.security.cert.CertificateException;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.UUID;

public class CmsDServiceImpl implements CmsDService, RetryEventListener {


    public static final String PAYMENT_APP_INSTANCE_ID = "paymentAppInstanceId";
    public static final String PAYMENT_APP_PROVIDER_ID = "paymentAppProviderId";
    public static final String CHANGE_PIN_RESPONSE_SUCCESSFULLY_RECEIVED =
            "CHANGE_PIN_RESPONSE_SUCCESSFULLY_RECEIVED";
    public static final String CHANGE_PIN_RESPONSE_NOT_RECEIVED =
            "CHANGE_PIN_RESPONSE_NOT_RECEIVED";
    public static final String DUMMY_TOKEN_UNIQUE_REFERENCE = "TUR";
    private static final String RETRY_ID = "RETRY-ID";

    public static final String MAJOR_VERSION = "1";
    public static final String MINOR_VERSION = "0";
    public static final String API_NAME = "paymentapp";
    public static final String API_VERSION = MAJOR_VERSION + "/" + MINOR_VERSION;
    public static final String BASE_REQUEST = "/" + API_NAME + "/" + API_VERSION;
    private static boolean sIsProcessing = false;

    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Lde Remote Management Service
     */
    private final LdeRemoteManagementService mLdeRemoteManagementService;
    /**
     * Cryptographic Service
     */
    private final CryptoService mCryptoService;
    /**
     * User Interface Listener
     */
    private MdesRemoteManagementEventListener mRemoteManagementEventListener;
    /**
     * Property file storage factory
     */
    private final PropertyStorageFactory mPropertyStorageFactory;
    /**
     * Session data
     */
    private SessionContext mSessionContext;
    /**
     * Http Factory to execute HttpGet & HttpPost request.
     */
    private final HttpFactory mHttpFactory;

    /**
     * Default Constructor
     */
    public CmsDServiceImpl(final HttpFactory androidHttpFactory,
                           final LdeRemoteManagementService remoteManagementService,
                           final PropertyStorageFactory storageFactory) {
        this.mLdeRemoteManagementService = remoteManagementService;
        this.mCryptoService = CryptoServiceFactory.getDefaultCryptoService();
        this.mPropertyStorageFactory = storageFactory;
        this.mHttpFactory = androidHttpFactory;
        RemoteManagementHandler
                .initialize(remoteManagementService,
                            new MdesCommunicator(androidHttpFactory, mCryptoService,
                                                 mLdeRemoteManagementService, this));
    }

    /**
     * Prepare and execute the registration request with CMS-D
     *
     * @param registrationCode registration code
     * @param pubKey           public key
     * @param responseHost     response host
     * @return RemoteManagementResponseHolder
     */
    public RemoteManagementResponseHolder registerToCmsD(String registrationCode,
                                                         String pubKey,
                                                         String responseHost) {

        CmsDRegisterRequestHolder cmsDRegisterRequestHolder = new CmsDRegisterRequestHolder();
        cmsDRegisterRequestHolder.mDRequestEnum = CmsDRequestEnum.REGISTER;
        cmsDRegisterRequestHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDRegisterRequestHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();
        cmsDRegisterRequestHolder.registrationCode = registrationCode;
        cmsDRegisterRequestHolder.responseHost = responseHost;
        cmsDRegisterRequestHolder.pubKey = pubKey;

        return RemoteManagementHandler.getInstance().execute(mSessionContext,
                                                             cmsDRegisterRequestHolder);

    }

    /**
     * Prepare and execute the provision request with CMS-D
     *
     * @param tokenUniqueReference token unique reference
     * @return RemoteManagementResponseHolder
     */
    private RemoteManagementResponseHolder provision(String tokenUniqueReference)
            throws InvalidInput {

        try {
            boolean isCardAlreadyProvision =
                    mLdeRemoteManagementService.isCardProfileAlreadyProvision(tokenUniqueReference);
            if (isCardAlreadyProvision) {
                throw new InvalidInput("Card already provisioned");
            }
        } catch (LdeNotInitialized ldeNotInitialized) {// Ignoring as shouldn't reach here
            mLogger.d(ldeNotInitialized.getMessage());
        }

        CmsDProvisionRequestHolder cmsDProvisionRequestHolder = new CmsDProvisionRequestHolder();
        cmsDProvisionRequestHolder.cardIdentifier = tokenUniqueReference;
        cmsDProvisionRequestHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();
        cmsDProvisionRequestHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDProvisionRequestHolder.mDRequestEnum = CmsDRequestEnum.PROVISION;
        String provisionRequest =
                new JsonUtils<CmsDProvisionRequestHolder>(CmsDProvisionRequestHolder.class)
                        .toJsonString(cmsDProvisionRequestHolder);
        addToRetryRegistry(CmsDRequestEnum.PROVISION, tokenUniqueReference, provisionRequest, 1);

        return executeRemoteManagementRequest(cmsDProvisionRequestHolder);
    }

    /**
     * Prepare and execute the notify provision request with CMS-D
     *
     * @param tokenUniqueReference token unique reference
     * @param result               provision result
     * @param errorCode            error code
     * @param errorDescription     error description
     * @return RemoteManagementResponseHolder
     */
    private RemoteManagementResponseHolder notifyProvisioningResult(String tokenUniqueReference,
                                                                    String result,
                                                                    String errorCode,
                                                                    String errorDescription) {

        CmsDNotifyProvisionResultRequestHolder cmsDNotifyProvisionResultRequestHolder = new
                CmsDNotifyProvisionResultRequestHolder();
        cmsDNotifyProvisionResultRequestHolder.mDRequestEnum = CmsDRequestEnum
                .NOTIFY_PROVISION_RESULT;
        cmsDNotifyProvisionResultRequestHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();
        cmsDNotifyProvisionResultRequestHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDNotifyProvisionResultRequestHolder.errorCode = errorCode;
        cmsDNotifyProvisionResultRequestHolder.errorDescription = errorDescription;
        cmsDNotifyProvisionResultRequestHolder.tokenUniqueReference = tokenUniqueReference;
        cmsDNotifyProvisionResultRequestHolder.result = result;

        String notifyProvisionRequest =
                new JsonUtils<CmsDNotifyProvisionResultRequestHolder>
                        (CmsDNotifyProvisionResultRequestHolder.class)
                        .toJsonString(cmsDNotifyProvisionResultRequestHolder);
        addToRetryRegistry(CmsDRequestEnum.NOTIFY_PROVISION_RESULT,
                           cmsDNotifyProvisionResultRequestHolder.tokenUniqueReference,
                           notifyProvisionRequest, 1);
        return executeRemoteManagementRequest(cmsDNotifyProvisionResultRequestHolder);
    }

    /**
     * Prepare and execute the notify provision request with CMS-D
     *
     * @param tokenUniqueReference         token unique reference
     * @param transactionCredentialsStatus transaction credentials status
     * @return RemoteManagementResponseHolder
     */
    public RemoteManagementResponseHolder replenish(String tokenUniqueReference,
                                                    TransactionCredentialStatus[]
                                                            transactionCredentialsStatus) {

        CmsDReplenishRequestHolder cmsDReplenishRequestHolder = new CmsDReplenishRequestHolder();
        cmsDReplenishRequestHolder.mDRequestEnum = CmsDRequestEnum.REPLENISH;
        cmsDReplenishRequestHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDReplenishRequestHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();
        cmsDReplenishRequestHolder.tokenUniqueReference = tokenUniqueReference;
        cmsDReplenishRequestHolder.transactionCredentialsStatus = transactionCredentialsStatus;

        String replenishRequest =
                new JsonUtils<CmsDReplenishRequestHolder>(CmsDReplenishRequestHolder.class)
                        .toJsonString(cmsDReplenishRequestHolder);
        addToRetryRegistry(CmsDRequestEnum.REPLENISH,
                           cmsDReplenishRequestHolder.tokenUniqueReference, replenishRequest, 1);


        return executeRemoteManagementRequest(cmsDReplenishRequestHolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestForPaymentTokens(String digitizeCardId) throws InvalidInput,
            InvalidCardStateException {

        //Don't replenish, If token(card) is not in active state.
        ProfileState cardState = mLdeRemoteManagementService.getCardState(digitizeCardId);
        if (cardState.getValue() != ProfileState.INITIALIZED.getValue()) {
            throw new InvalidCardStateException("Card is not in active state");
        }

        // Caching in the temporary storage
        String tokenUniqueReference =
                mLdeRemoteManagementService.getTokenUniqueReferenceFromCardId(digitizeCardId);

        TransactionCredentialStatus[] allTransactionCredentialStatus =
                mLdeRemoteManagementService
                        .getAllTransactionCredentialStatus(tokenUniqueReference);

        onResponseArrived(replenish(tokenUniqueReference, allTransactionCredentialStatus));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestForDeleteToken(String digitizeCardId) throws InvalidInput {
        String tokenUniqueReference =
                mLdeRemoteManagementService.getTokenUniqueReferenceFromCardId(digitizeCardId);

        TransactionCredentialStatus[] allTransactionCredentialStatus =
                mLdeRemoteManagementService.getAllTransactionCredentialStatus(tokenUniqueReference);

        RemoteManagementResponseHolder deleteResponseHolder =
                delete(tokenUniqueReference, allTransactionCredentialStatus);

        onResponseArrived(deleteResponseHolder);
    }

    /**
     * Prepare and execute the delete card request with CMS-D
     *
     * @param tokenUniqueReference         token unique reference
     * @param transactionCredentialsStatus transaction credentials status
     * @return RemoteManagementResponseHolder
     */
    private RemoteManagementResponseHolder delete(String tokenUniqueReference,
                                                  TransactionCredentialStatus[]
                                                          transactionCredentialsStatus) {

        CmsDDeleteRequestHolder cmsDDeleteRequestHolder = new CmsDDeleteRequestHolder();
        cmsDDeleteRequestHolder.mDRequestEnum = CmsDRequestEnum.DELETE;
        cmsDDeleteRequestHolder.mTransactionCredentialStatuses = transactionCredentialsStatus;
        cmsDDeleteRequestHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();
        cmsDDeleteRequestHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDDeleteRequestHolder.tokenUniqueReference = tokenUniqueReference;
        String deleteRequest =
                new JsonUtils<CmsDDeleteRequestHolder>(CmsDDeleteRequestHolder.class)
                        .toJsonString(cmsDDeleteRequestHolder);
        addToRetryRegistry(CmsDRequestEnum.DELETE, cmsDDeleteRequestHolder.tokenUniqueReference,
                           deleteRequest, 1);
        return executeRemoteManagementRequest(cmsDDeleteRequestHolder);
    }

    /**
     * Execute Remote Management Request
     *
     * @param cmsDRequestHolder Instance of CmsDRequestHolder
     * @return RemoteManagementResponseHolder
     */
    private RemoteManagementResponseHolder executeRemoteManagementRequest(
            CmsDRequestHolder cmsDRequestHolder) {
        sIsProcessing = true;
        return RemoteManagementHandler.getInstance().execute(mSessionContext,
                                                             cmsDRequestHolder);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void handleNotification(ByteArray data) {

        // #MCBP_LOG_BEGIN
        mLogger.d("MDES_PROTOCOL;REMOTE_MANAGEMENT_SESSION;DATA([" + (data == null ? "Null" : data
                .toHexString()) + "])");
        // #MCBP_LOG_END

        // Deserialize RNS message
        RnsMessage rnsMessage = RnsMessage.valueOf(data);

        String responseHost = rnsMessage.getResponseHost();

        // Check if registration data is available or not
        if (rnsMessage.getRegistrationData() != null) {
            // #MCBP_LOG_BEGIN
            mLogger.d("MDES_PROTOCOL;REMOTE_MANAGEMENT_SESSION;REGISTRATION_DATA(["
                      + rnsMessage.getRegistrationData().toString() + "])");
            // #MCBP_LOG_END

            if (!processRegistration(rnsMessage)) return;
        }

        try {
            // Decrypt payload containing Session data with any pending action from CMS-D
            RemoteManagementSessionData remoteManagementSessionData =
                    getRemoteManagementSessionData(rnsMessage);

            // If session that we received is invalid then we will again ignore this push message
            if (!handleSessionData(remoteManagementSessionData, responseHost)) {
                return;
            }


            // Check if any pending action (From CMS-D) is left to execute previously or not
            if (remoteManagementSessionData.getPendingAction() != null) {

                processCMSDPendingActions(remoteManagementSessionData);
            }

            // Check if any pending action (From SDK) is left to execute previously or not
            if (RemoteManagementHandler.getInstance().isAnyActionPending()) {
                processSDKPendingActions(mSessionContext);

            }

        } catch (DuplicateMcbpCard e) {
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener
                        .onCardAddedFailure(e.getDigitizedCardId(), 0,
                                            McbpErrorCode.CARD_ALREADY_PROVISION);
            }
        } catch (InvalidInput | McbpCryptoException | LdeNotInitialized e) {
            mLogger.d(e.getMessage());
        }
    }

    /**
     * Process pending action received in Rns message.
     *
     * @param remoteManagementSessionData Remote management session data.
     */
    private void processCMSDPendingActions(RemoteManagementSessionData remoteManagementSessionData)
            throws InvalidInput {
        if (remoteManagementSessionData.isValid()) {

            final ArrayList<CmsDRequestHolder> pendingItems =
                    RemoteManagementHandler.getInstance().getPendingItems();


            String tokenUniqueReference = remoteManagementSessionData.getTokenUniqueReference();
            if (remoteManagementSessionData.getPendingAction().equalsIgnoreCase("PROVISION")) {

                RemoteManagementResponseHolder provisionResponseHolder =
                        provision(tokenUniqueReference);

                if (pendingItems != null && pendingItems.size() != 0) {
                    final CmsDRequestHolder cmsDRequestHolder = pendingItems.get(0);
                    if (cmsDRequestHolder.mDRequestEnum == CmsDRequestEnum.PROVISION) {
                        RemoteManagementHandler.getInstance().clearPendingAction();
                    }
                }

                onResponseArrived(provisionResponseHolder);
            } else if (remoteManagementSessionData.getPendingAction()
                                                  .equalsIgnoreCase("RESET_MOBILE_PIN")) {
                resetMobilePin(remoteManagementSessionData.getTokenUniqueReference());
            }
        }
    }

    private void processSDKPendingActions(final SessionContext sessionContext) {

        RemoteManagementResponseHolder remoteManagementResponseHolder =
                RemoteManagementHandler.getInstance().executePendingAction(sessionContext);
        RemoteManagementHandler.getInstance().clearPendingAction();
        onResponseArrived(remoteManagementResponseHolder);
    }

    /**
     * Process the notification message and execute register request.
     *
     * @param rnsMessage remote notification message
     * @return true if registration successful else return false.
     */
    private boolean processRegistration(RnsMessage rnsMessage) {
        if (isUserAlreadyRegister()) {
            long count = mLdeRemoteManagementService.getNumberOfCardsProvisioned();
            if (count == 0) {
                mLdeRemoteManagementService.unregister();
                if (mSessionContext != null) {
                    mSessionContext.clear();
                }
            } else {
                if (mRemoteManagementEventListener != null) {
                    mRemoteManagementEventListener
                            .onRegistrationFailure(0, McbpErrorCode.USER_ALREADY_REGISTER);
                }
                return false;
            }
        }
        PaymentAppRegistrationData registrationData = rnsMessage.getRegistrationData();

        //Get public key to received in Rns message
        CertificateMetaData certificateMetaData = retrievePublicKey(registrationData);
        if (certificateMetaData == null) return false;

        // Perform registration with CMS-D
        RemoteManagementResponseHolder registrationResponseHolder =
                registerToCmsD(registrationData.getRegistrationCode(),
                               certificateMetaData.getPublicKey().toHexString(),
                               rnsMessage.getResponseHost());
        // On unsuccessful registration with CMS-D we will simply ignore this push message
        if (!registrationResponseHolder.isSuccessful()) {
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener
                        .onRegistrationFailure(0, registrationResponseHolder.mErrorContext
                                .getErrorCode());
            }
            return false;
        }
        if (mRemoteManagementEventListener != null) {
            mRemoteManagementEventListener.onRegistrationCompleted();
        }
        return true;
    }

    /**
     * API check for validity of incoming session data
     *
     * @param remoteManagementSessionData instance of {@link RemoteManagementSessionData}
     * @return Boolean value base on Session data validation
     */
    private boolean handleSessionData(RemoteManagementSessionData remoteManagementSessionData,
                                      String responseHost) {
        //Check of remote management data is available
        if (remoteManagementSessionData == null) {
            return false;
        }
        // We are discarding the GCM message if it arrives after expiry timestamp
        if (!TimeUtils.isBefore(
                remoteManagementSessionData.getExpiryTimestamp())) {
            // #MCBP_LOG_BEGIN
            mLogger.d("Session is invalid:-" + remoteManagementSessionData.getExpiryTimestamp());
            // #MCBP_LOG_END
            return false;
        }

        if (mSessionContext == null) {
            mSessionContext = SessionContext.of(remoteManagementSessionData);
        } else {
            mLogger.d("Is current session is used? " + mSessionContext.isUsed());
            if (mSessionContext.isValidSession()) {
                mLogger.d("Current session is valid");
                if (!remoteManagementSessionData.getSessionCode()
                                                .isEqual(mSessionContext.getSessionCode())) {
                    mSessionContext.clear();
                    mSessionContext = null;
                    mSessionContext = SessionContext.of(remoteManagementSessionData);
                    // #MCBP_LOG_BEGIN
                    mLogger.d("Received new session");
                    mLogger.d("Counter will be reset.");
                } else {
                    // #MCBP_LOG_BEGIN
                    mLogger.d("Using old session");
                    mLogger.d("Counter will not be reset.");
                    // #MCBP_LOG_END
                }
            } else {
                mLogger.d("Current session is not valid");
                mSessionContext.clear();
                mSessionContext = null;
                mSessionContext = SessionContext.of(remoteManagementSessionData);
                // #MCBP_LOG_BEGIN
                mLogger.d("Received new session");
                mLogger.d("Counter will be reset.");
                // #MCBP_LOG_END
            }
        }
        mSessionContext.updateResponseHost(responseHost);
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void registerMdesRemoteManagementListener(MdesRemoteManagementEventListener listener) {
        this.mRemoteManagementEventListener = listener;
    }

    /**
     * Prepare and execute the change mobile pin request for card/wallet.
     *
     * @param tokenUniqueReference Token Unique Reference
     * @param currentMobilePin     Current Mobile Pin
     * @param newMobilePin         New Mobile Pin
     * @return RemoteManagementResponseHolder
     * @throws InvalidInput
     */
    public RemoteManagementResponseHolder changeMobilePin(String tokenUniqueReference,
                                                          ByteArray currentMobilePin,
                                                          ByteArray newMobilePin)
            throws InvalidInput, McbpCryptoException {
        String taskId = generateTaskId();

        updateTaskMetaData(tokenUniqueReference, taskId,
                           RemoteManagementRequestType.CHANGE_PIN.name(),
                           CHANGE_PIN_RESPONSE_NOT_RECEIVED);

        CmsDChangePinRequestHolder cmsDChangePinRequestHolder = new CmsDChangePinRequestHolder();
        cmsDChangePinRequestHolder.mDRequestEnum = CmsDRequestEnum.CHANGE_PIN;
        cmsDChangePinRequestHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDChangePinRequestHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();

        cmsDChangePinRequestHolder.tokenUniqueReference = tokenUniqueReference;
        cmsDChangePinRequestHolder.taskId = taskId;

        ByteArray decKey = mLdeRemoteManagementService.getDataEncryptionKey();

        //Encrypting pin here, so that plain pin will not remain in memory
        if (currentMobilePin != null && currentMobilePin.getLength() > 0) {

            cmsDChangePinRequestHolder.oldPin =
                    mCryptoService.encryptPinBlock(currentMobilePin,
                                                   getPaymentAppInstanceId(),
                                                   decKey);
        }

        cmsDChangePinRequestHolder.newPin =
                mCryptoService.encryptPinBlock(newMobilePin,
                                               getPaymentAppInstanceId(),
                                               decKey);

        Utils.clearByteArray(decKey);
        Utils.clearByteArray(currentMobilePin);
        Utils.clearByteArray(newMobilePin);

        return RemoteManagementHandler.getInstance().execute(mSessionContext,
                                                             cmsDChangePinRequestHolder);
    }

    /**
     * Generate Task Id
     *
     * @return Task Id.
     */
    private String generateTaskId() {
        return UUID.randomUUID().toString();
    }

    private void updateTaskMetaData(final String tur, final String taskId,
                                    final String requestType, final String metaData) {
        String value = (tur == null ? DUMMY_TOKEN_UNIQUE_REFERENCE : tur) + ","
                       + taskId + "," + (metaData == null ? "METADATA" : metaData);
        mPropertyStorageFactory.putProperty(requestType, value);
    }

    private String[] retrieveTaskMetaData(final String requestType) {
        String property = mPropertyStorageFactory.getProperty(requestType, null);
        String[] results = null;
        if (property != null) {
            results = new String[3];
            StringTokenizer stringTokenizer = new StringTokenizer(property, ",");
            String tokenUniqueReference = stringTokenizer.nextToken();
            results[0] = tokenUniqueReference;
            String taskId = stringTokenizer.nextToken();
            results[1] = taskId;
            String metaData = stringTokenizer.nextToken();
            results[2] = metaData;
        }
        return results;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void requestForMobilePinChange(String digitizedCardId, final ByteArray currentMobilePin,
                                          final ByteArray newMobilePin) throws InvalidInput,
            McbpCryptoException {


        if (newMobilePin == null || newMobilePin.isEmpty()) {
            throw new InvalidInput("New Pin is not valid");
        }

        String tokenUniqueReference = null;

        if (digitizedCardId != null) {
            //Change PIN for a card
            tokenUniqueReference =
                    mLdeRemoteManagementService.getTokenUniqueReferenceFromCardId(digitizedCardId);
        }

        RemoteManagementResponseHolder changePinResponseHolder =
                changeMobilePin(tokenUniqueReference, currentMobilePin, newMobilePin);

        onResponseArrived(changePinResponseHolder);
    }

    /**
     * Get task status.
     *
     * @param requestType Request type.
     */
    @Override
    public void requestForTaskStatus(RemoteManagementRequestType requestType)
            throws InvalidInput {
        String[] taskMetaData = retrieveTaskMetaData(requestType.name());
        if (taskMetaData == null || taskMetaData.length == 0) {
            return;
        }
        String tokenUniqueReference = taskMetaData[0];
        String taskId = taskMetaData[1];
        String metaData = taskMetaData[2];
        if (taskId == null || taskId.isEmpty()) {
            throw new McbpCardException("Invalid operation: No Change Mobile PIN request received");
        }

        CmsDGetTaskStatusStatusHolder cmsDGetTaskStatusStatusHolder = new
                CmsDGetTaskStatusStatusHolder();
        cmsDGetTaskStatusStatusHolder.mPaymentAppInstanceId = getPaymentAppInstanceId();
        cmsDGetTaskStatusStatusHolder.mPaymentAppProviderId = getPaymentAppProviderId();
        cmsDGetTaskStatusStatusHolder.taskId = taskId;
        cmsDGetTaskStatusStatusHolder.mDRequestEnum = CmsDRequestEnum.GET_TASK_STATUS;

        String getTaskStatusRequest =
                new JsonUtils<CmsDGetTaskStatusStatusHolder>(CmsDGetTaskStatusStatusHolder.class)
                        .toJsonString(cmsDGetTaskStatusStatusHolder);
        addToRetryRegistry(CmsDRequestEnum.GET_TASK_STATUS, tokenUniqueReference,
                           getTaskStatusRequest, 1);

        RemoteManagementResponseHolder getTaskStatusResponseHolder =
                executeRemoteManagementRequest(cmsDGetTaskStatusStatusHolder);
        onResponseArrived(getTaskStatusResponseHolder);
    }


    /**
     * Returns {@link com.mastercard.mcbp.remotemanagement.mdes.models.RemoteManagementSessionData}
     * from NotificationMessageData object.
     * <p/>
     * Implemented as per MDES API Specification v1.0.0 section 4.2.1
     *
     * @param rnsMessage The Remote Notification Message
     * @return Remote management session data.
     * @throws McbpCryptoException
     */
    public RemoteManagementSessionData getRemoteManagementSessionData(final RnsMessage rnsMessage)
            throws McbpCryptoException, InvalidInput, LdeNotInitialized {

        final ByteArray encryptedData = rnsMessage.getEncryptedDataInByteArray();

        final ByteArray transportKey = mLdeRemoteManagementService.getTransportKey();

        final ByteArray macKey = mLdeRemoteManagementService.getMacKey();

        // #MCBP_LOG_BEGIN
        mLogger.d("MDES_PROTOCOL;RETRIEVE_SESSION_DATA;ENCRYPTED_SESSION_PAYLOAD:" +
                  "([" + encryptedData.toHexString() + "])");
        // #MCBP_LOG_END

        return rnsMessage.getRemoteManagementSessionData(macKey, transportKey, mCryptoService);
    }

    /**
     * Return the stored payment app instance id
     *
     * @return PaymentAppInstanceId
     */
    private String getPaymentAppInstanceId() {
        return mPropertyStorageFactory.getProperty(PAYMENT_APP_INSTANCE_ID, null);
    }

    /**
     * Return the stored payment app provider id
     *
     * @return PaymentAppProviderId
     */
    private String getPaymentAppProviderId() {
        return mPropertyStorageFactory.getProperty(PAYMENT_APP_PROVIDER_ID, null);
    }

    /**
     * Handle the remote management operation responses based on request id.
     *
     * @param remoteManagementResponseHolder RemoteManagementResponseHolder instance.
     */
    private void onResponseArrived(RemoteManagementResponseHolder remoteManagementResponseHolder) {
        sIsProcessing = false;
        switch (remoteManagementResponseHolder.mCmsDRequestHolder.mDRequestEnum) {
            case PROVISION:
                handleProvisionResponse(remoteManagementResponseHolder);
                break;
            case NOTIFY_PROVISION_RESULT:
                handleNotifyProvisionResultResponse(remoteManagementResponseHolder);
                break;
            case CHANGE_PIN:
                handleChangePinResponse(remoteManagementResponseHolder);
                break;
            case REPLENISH:
                handleReplenishResponse(remoteManagementResponseHolder);
                break;
            case DELETE:
                handleDeleteResponse(remoteManagementResponseHolder);
                break;
            case GET_TASK_STATUS:
                handleGetTaskStatusResponse(remoteManagementResponseHolder);
                break;
        }
    }

    /**
     * Handle the Notify Provision Response and notify to the UI
     *
     * @param remoteManagementResponseHolder RemoteManagementResponseHolder instance.
     */
    private void handleNotifyProvisionResultResponse(
            RemoteManagementResponseHolder remoteManagementResponseHolder) {
        CmsDNotifyProvisionResultRequestHolder holder = (CmsDNotifyProvisionResultRequestHolder)
                remoteManagementResponseHolder.mCmsDRequestHolder;
        if (remoteManagementResponseHolder.isSuccessful()) {
            switch (remoteManagementResponseHolder.mServiceResult) {
                case OK:
                    if (mRemoteManagementEventListener != null) {
                        mRemoteManagementEventListener.onCardAdded(holder.tokenUniqueReference);
                    }
                    break;
                case WAITING_FOR_SESSION:
                    break;
            }
        } else {
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener
                        .onCardAddedFailure(holder.tokenUniqueReference, getCurrentRetryCount(),
                                            remoteManagementResponseHolder
                                                    .mErrorContext.getErrorCode());
            }
        }
    }

    /**
     * Handle the delete response and notify to the UI
     *
     * @param remoteManagementResponseHolder RemoteManagementResponseHolder instance.
     */
    private void handleDeleteResponse(
            RemoteManagementResponseHolder remoteManagementResponseHolder) {
        CmsDDeleteRequestHolder holder = (CmsDDeleteRequestHolder) remoteManagementResponseHolder
                .mCmsDRequestHolder;
        if (remoteManagementResponseHolder.isSuccessful()) {
            switch (remoteManagementResponseHolder.mServiceResult) {
                case OK:
                    if (mRemoteManagementEventListener != null) {
                        mRemoteManagementEventListener.onCardDelete(holder.tokenUniqueReference);
                    }
                    break;
                case WAITING_FOR_SESSION:
                    break;
            }
        } else {
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener
                        .onCardDeleteFailure(holder.tokenUniqueReference, getCurrentRetryCount(),
                                             remoteManagementResponseHolder
                                                     .mErrorContext.getErrorCode());
            }
        }
    }

    /**
     * Handle the replenish response and notify to the UI
     *
     * @param remoteManagementResponseHolder RemoteManagementResponseHolder instance.
     */
    private void handleReplenishResponse(
            RemoteManagementResponseHolder remoteManagementResponseHolder) {
        CmsDReplenishRequestHolder holder =
                (CmsDReplenishRequestHolder) remoteManagementResponseHolder
                        .mCmsDRequestHolder;
        if (remoteManagementResponseHolder.isSuccessful()) {
            switch (remoteManagementResponseHolder.mServiceResult) {
                case OK:
                    int count = 0;
                    if (((CmsDReplenishResponse) remoteManagementResponseHolder.mCmsdResponse)
                                .getTransactionCredentials() != null) {
                        count =
                                ((CmsDReplenishResponse) remoteManagementResponseHolder
                                        .mCmsdResponse)
                                        .getTransactionCredentials().length;
                    }
                    if (mRemoteManagementEventListener != null) {
                        mRemoteManagementEventListener
                                .onPaymentTokensReceived(holder.tokenUniqueReference, count);
                    }
                    break;
                case WAITING_FOR_SESSION:
                    //Do nothing
                    break;
            }
        } else {
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener
                        .onPaymentTokensReceivedFailure(holder.tokenUniqueReference,
                                                        getCurrentRetryCount(),
                                                        remoteManagementResponseHolder
                                                                .mErrorContext.getErrorCode());
            }
        }
    }

    /**
     * Handle the change pin response and notify to the UI
     *
     * @param respHandler ResponseHandler instance
     */
    private void handleChangePinResponse(RemoteManagementResponseHolder respHandler) {
        CmsDChangePinRequestHolder holder =
                (CmsDChangePinRequestHolder) respHandler.mCmsDRequestHolder;
        CmsDChangeMobilePinResponse response =
                (CmsDChangeMobilePinResponse) respHandler.mCmsdResponse;
        if (respHandler.isSuccessful()) {
            switch (respHandler.mServiceResult) {
                case OK:
                    if (holder.tokenUniqueReference != null) {
                        if (mRemoteManagementEventListener != null) {
                            mRemoteManagementEventListener
                                    .onCardPinChanged(holder.tokenUniqueReference,
                                                      response.getResult(),
                                                      response.getMobilePinTriesRemaining());
                        }
                    } else {
                        if (mRemoteManagementEventListener != null) {
                            mRemoteManagementEventListener
                                    .onWalletPinChange(response.getResult(),
                                                       response.getMobilePinTriesRemaining());
                        }
                    }
                    updateTaskMetaData(holder.tokenUniqueReference, holder.taskId, CmsDRequestEnum
                            .CHANGE_PIN.name(), CHANGE_PIN_RESPONSE_SUCCESSFULLY_RECEIVED);
                    break;
                case WAITING_FOR_SESSION:
                    break;
            }
        } else {
            if (holder.tokenUniqueReference != null) {
                if (mRemoteManagementEventListener != null) {
                    mRemoteManagementEventListener
                            .onCardPinChangedFailure(holder.tokenUniqueReference, 0,
                                                     respHandler.mErrorContext.getErrorCode());
                }
            } else {
                if (mRemoteManagementEventListener != null) {
                    mRemoteManagementEventListener
                            .onWalletPinChangeFailure(0, respHandler.mErrorContext.getErrorCode());
                }
            }
        }
    }

    /**
     * Handle the provision response and notify to the UI
     *
     * @param remoteManagementResponseHolder RemoteManagementResponseHolder instance.
     */
    private void handleProvisionResponse(RemoteManagementResponseHolder
                                                 remoteManagementResponseHolder) {
        CmsDProvisionRequestHolder cmsDProvisionRequestHolder =
                (CmsDProvisionRequestHolder) remoteManagementResponseHolder.mCmsDRequestHolder;
        if (remoteManagementResponseHolder.isSuccessful()) {
            switch (remoteManagementResponseHolder.mServiceResult) {
                case OK://Notify Success as provision result
                    RemoteManagementResponseHolder notifyProvisionResponse =
                            notifyProvisioningResult(cmsDProvisionRequestHolder.cardIdentifier,
                                                     "SUCCESS", null,
                                                     null);
                    onResponseArrived(notifyProvisionResponse);
                    break;
                case WAITING_FOR_SESSION://Do nothing
                    break;
            }
        } else {
            switch (remoteManagementResponseHolder.mServiceResult) {
                case COMMUNICATION_ERROR:// add to retry
                    if (mRemoteManagementEventListener != null) {
                        mRemoteManagementEventListener
                                .onCardAddedFailure(cmsDProvisionRequestHolder.cardIdentifier,
                                                    getCurrentRetryCount(),
                                                    remoteManagementResponseHolder.mErrorContext
                                                            .getErrorCode());
                    }
                    break;
                case SERVICE_INTERNAL_ERROR:// Notify Failed as provision result
                    if (remoteManagementResponseHolder.mErrorContext.getErrorCode() != McbpErrorCode
                            .SERVER_ERROR) {
                        RemoteManagementResponseHolder notifyProvisionResponse =
                                notifyProvisioningResult(cmsDProvisionRequestHolder.cardIdentifier,
                                                         "ERROR",
                                                         "PROVISION_FAILED",
                                                         "PROVISION_FAILED");
                        onResponseArrived(notifyProvisionResponse);
                    } else {
                        if (mRemoteManagementEventListener != null) {
                            mRemoteManagementEventListener
                                    .onCardAddedFailure(cmsDProvisionRequestHolder.cardIdentifier,
                                                        getCurrentRetryCount(),
                                                        remoteManagementResponseHolder
                                                                .mErrorContext.getErrorCode());
                        }
                    }
                    break;
            }
        }
    }

    /**
     * This method wipe all SUKs and transaction credential status after receiving RESET PIN
     * request.
     *
     * @param tokenUniqueReference card identifier. Null in case of reset PIN for wallet.
     */
    private void resetMobilePin(final String tokenUniqueReference) {

        //Reset PIN for wallet
        if (tokenUniqueReference == null) {
            try {
                mLdeRemoteManagementService.wipeAllSuks();
                mLdeRemoteManagementService.wipeAllTransactionCredentialStatus();
                if (mRemoteManagementEventListener != null) {
                    mRemoteManagementEventListener.onWalletPinReset();
                }
            } catch (LdeNotInitialized e) {
                if (mRemoteManagementEventListener != null) {
                    mRemoteManagementEventListener
                            .onWalletPinResetFailure(0, McbpErrorCode.LDE_ERROR);
                }
            }

            return;
        }
        //Reset PIN for a card
        try {
            String cardId = mLdeRemoteManagementService
                    .getCardIdFromTokenUniqueReference(tokenUniqueReference);
            mLdeRemoteManagementService.wipeDcSuk(ByteArray.of(cardId));
            mLdeRemoteManagementService.deleteAllTransactionCredentialStatus(cardId);
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener.onCardPinReset(tokenUniqueReference);
            }
        } catch (InvalidInput invalidInput) {
            if (mRemoteManagementEventListener != null) {
                mRemoteManagementEventListener
                        .onCardPinResetFailure(tokenUniqueReference, 0, McbpErrorCode.LDE_ERROR);
            }
        }
    }

    /**
     * Handle the get task status response and notify to the UI
     *
     * @param remoteManagementResponseHolder RemoteManagementResponseHolder instance.
     */
    private void handleGetTaskStatusResponse(final RemoteManagementResponseHolder
                                                     remoteManagementResponseHolder) {
        GetTaskStatusResponse getTaskStatusResponse = (GetTaskStatusResponse)
                remoteManagementResponseHolder.mCmsdResponse;
        if (remoteManagementResponseHolder.isSuccessful()) {
            switch (remoteManagementResponseHolder.mServiceResult) {
                case OK:
                    //Here we are checking only for COMPLETED status as if status is completed we
                    // are clearing SUKs as per token unique reference
                    if (getTaskStatusResponse.getStatus().equalsIgnoreCase("COMPLETED")) {
                        processPendingChangePinScenario();
                    }
                    if (mRemoteManagementEventListener != null) {
                        mRemoteManagementEventListener
                                .onTaskStatusReceived(getTaskStatusResponse.getStatus());
                    }
                    break;
                case WAITING_FOR_SESSION:
                    break;
            }
        } else {
            if (getTaskStatusResponse != null && getTaskStatusResponse.getErrorCode() != null
                && getTaskStatusResponse.getErrorCode().equalsIgnoreCase("INVALID_TASK_ID")) {
                if (mRemoteManagementEventListener != null) {
                    mRemoteManagementEventListener.onTaskStatusReceived("INVALID_TASK_ID");
                }
            } else {
                if (mRemoteManagementEventListener != null) {

                    mRemoteManagementEventListener
                            .onTaskStatusReceivedFailure(getCurrentRetryCount(),
                                                         remoteManagementResponseHolder
                                                                 .mErrorContext
                                                                 .getErrorCode());
                }
            }
        }
    }


    /**
     * Return the current retry count
     *
     * @return retry count
     */
    private int getCurrentRetryCount() {
        if (getPendingRequest() != null) {
            int retryCount = getPendingRequest().getRetryCount();
            if (retryCount > 3) {
                return 0;
            }
            return retryCount;
        }
        return 0;
    }

    /**
     * Handle the pending scenario of change pin
     */
    private void processPendingChangePinScenario() {
        String[] taskMetaData = retrieveTaskMetaData(CmsDRequestEnum.CHANGE_PIN.name());
        if (taskMetaData == null) {
            return;
        }
        String tokenUniqueReference = taskMetaData[0];
        String taskId = taskMetaData[1];
        String metaData = taskMetaData[2];
        if (metaData != null && metaData.equalsIgnoreCase(CHANGE_PIN_RESPONSE_NOT_RECEIVED)) {
            //For Wallet PIN scenario
            if (tokenUniqueReference.equalsIgnoreCase(DUMMY_TOKEN_UNIQUE_REFERENCE)) {
                //Delete all SUKs & transaction credential status regardless of card
                mLdeRemoteManagementService.wipeAllSuks();
                mLdeRemoteManagementService.wipeAllTransactionCredentialStatus();
            } else {
                //For a card PIN scenario
                try {
                    String cardId = mLdeRemoteManagementService.getCardIdFromTokenUniqueReference
                            (tokenUniqueReference);
                    mLdeRemoteManagementService.wipeDcSuk(ByteArray.of(cardId));
                    mLdeRemoteManagementService.deleteAllTransactionCredentialStatus(cardId);
                } catch (InvalidInput invalidInput) {
                    mLogger.d(invalidInput.getMessage());
                }
            }
            updateTaskMetaData(tokenUniqueReference, taskId, CmsDRequestEnum.CHANGE_PIN.name(),
                               CHANGE_PIN_RESPONSE_SUCCESSFULLY_RECEIVED);
        }
    }

    /**
     * Add the Retry data into storage
     *
     * @param requestType          CmsDRequestEnum
     * @param tokenUniqueReference Token Unique Reference
     * @param data                 Retry request data
     * @param retryRemain          Remaining retry value
     */
    void addToRetryRegistry(final CmsDRequestEnum requestType, final String tokenUniqueReference,
                            String data, int retryRemain) {
        // #MCBP_LOG_BEGIN
        mLogger.d("Final value to be stored :" + data);
        // #MCBP_LOG_END
        PendingRetryRequest pendingRetryRequest = new PendingRetryRequest(requestType,
                                                                          tokenUniqueReference,
                                                                          data, retryRemain);
        ByteArray encryptedRegistryData;
        String jsonString = new JsonUtils<PendingRetryRequest>(PendingRetryRequest.class)
                .toJsonString(pendingRetryRequest);
        try {
            encryptedRegistryData = encryptData(jsonString.getBytes(Charset.defaultCharset()));
        } catch (McbpCryptoException | InvalidInput e) {
            mLogger.d(e.getMessage());
            return;
        }

        mPropertyStorageFactory.putProperty(RETRY_ID, encryptedRegistryData.toHexString());
    }

    /**
     * Encrypt data by AES algorithm.
     *
     * @param data byte array of data which need to encrypt.
     * @return ByteArray encrypted data.
     */
    private ByteArray encryptData(final byte[] data) throws McbpCryptoException, InvalidInput {
        if (data == null) {
            throw new InvalidInput("Invalid input data");
        }
        final ByteArray key = mLdeRemoteManagementService.getDataEncryptionKey();

        try {
            return mCryptoService.encryptRetryRequestData(ByteArray.of(data), key);
        } finally {
            Utils.clearByteArray(key);
        }
    }


    /**
     * Decrypt data by AES algorithm.
     *
     * @param data byte array of data which need to decrypt.
     * @return ByteArray decrypted data.
     */
    private ByteArray decryptData(final byte[] data) throws McbpCryptoException, InvalidInput {
        if (data == null || data.length == 0) {
            throw new InvalidInput("Invalid input data");
        }
        final ByteArray key = mLdeRemoteManagementService.getDataEncryptionKey();

        try {
            return mCryptoService.decryptRetryRequestData(ByteArray.of(data), key);
        } finally {
            Utils.clearByteArray(key);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PendingRetryRequest getPendingRequest() {
        String property = mPropertyStorageFactory.getProperty(RETRY_ID, null);

        ByteArray decryptedPropertyData;
        if (property == null) {
            return null;
        }
        try {
            decryptedPropertyData = decryptData(ByteArray.of(property).getBytes());

        } catch (McbpCryptoException | InvalidInput e) {
            mLogger.d(e.getMessage());
            return null;
        }
        return new JsonUtils<PendingRetryRequest>(PendingRetryRequest.class)
                .valueOf(decryptedPropertyData.getBytes());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void cancelPendingRequest() {
        RemoteManagementHandler.getInstance().clearPendingAction();
        sIsProcessing = false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void forceRetry() {
        PendingRetryRequest pendingRetryRequest = getPendingRequest();
        CmsDRequestHolder cmsDRequestHolder = null;
        if (pendingRetryRequest != null) {

            String metaData = pendingRetryRequest.getMetaData();
            switch (pendingRetryRequest.getRequestType()) {
                case PROVISION:
                    cmsDRequestHolder = new JsonUtils<CmsDProvisionRequestHolder>(
                            CmsDProvisionRequestHolder.class).valueOf(
                            metaData.getBytes(Charset.defaultCharset()));
                    break;
                case NOTIFY_PROVISION_RESULT:
                    cmsDRequestHolder =
                            new JsonUtils<CmsDNotifyProvisionResultRequestHolder>(
                                    CmsDNotifyProvisionResultRequestHolder.class)
                                    .valueOf(metaData.getBytes(Charset.defaultCharset()));
                    break;
                case REPLENISH:
                    cmsDRequestHolder = new JsonUtils<CmsDReplenishRequestHolder>
                            (CmsDReplenishRequestHolder.class)
                            .valueOf(metaData.getBytes(Charset.defaultCharset()));
                    break;
                case DELETE:
                    cmsDRequestHolder = new JsonUtils<CmsDDeleteRequestHolder>
                            (CmsDDeleteRequestHolder.class)
                            .valueOf(metaData.getBytes(Charset.defaultCharset()));
                    break;
                case GET_TASK_STATUS:
                    cmsDRequestHolder = new JsonUtils<CmsDGetTaskStatusStatusHolder>(
                            CmsDGetTaskStatusStatusHolder.class)
                            .valueOf(metaData.getBytes(Charset.defaultCharset()));
                    break;
                case REQUEST_SESSION:
                    break;
            }

            executeRemoteManagementRequest(cmsDRequestHolder);

            // Update retry counter in storage
            updateRetryCount(pendingRetryRequest);
        }
    }

    /**
     * Update the retry count into storage
     *
     * @param pendingRetryRequest PendingRetryRequest
     */
    private void updateRetryCount(final PendingRetryRequest pendingRetryRequest) {
        int remainingRetryCount = pendingRetryRequest.getRetryCount();
        if (remainingRetryCount == 1) {
            cancelPendingRequest();
        } else {
            pendingRetryRequest.setRetryCount(remainingRetryCount - 1);
        }
        addToRetryRegistry(pendingRetryRequest.getRequestType(), pendingRetryRequest
                                   .getCardId(), pendingRetryRequest.getMetaData(),
                           pendingRetryRequest.getRetryCount());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void onRetry(final int retryRemain) {
            PendingRetryRequest pendingRetryRequest = getPendingRequest();
            if (pendingRetryRequest != null) {
                addToRetryRegistry(pendingRetryRequest.getRequestType(),
                                   pendingRetryRequest.getCardId(),
                                   pendingRetryRequest.getMetaData(), retryRemain);
            }
        }

    /**
     * Get Public key from PaymentAppRegistrationData
     *
     * @param registrationData PaymentAppRegistrationData
     * @return Public key as string.
     */
    private CertificateMetaData retrievePublicKey(
            final PaymentAppRegistrationData registrationData) {
        CertificateMetaData certificateMetaData = null;
        try {
            final String pkCertificateUrl = registrationData.getPkCertificateUrl();
            if (pkCertificateUrl != null && !pkCertificateUrl.isEmpty()) {
                //Download certificate, Extract public key
                CertificateHandler certificateHandler =
                        new CertificateHandler(pkCertificateUrl, mHttpFactory);
                certificateMetaData = certificateHandler.getCertificateMetaData();
            } else {
                //To provide backward compatibility
                certificateMetaData = new CertificateMetaData() {
                    @Override
                    public ByteArray getPublicKey() {
                        return ByteArray.of(registrationData.getPublicKey());
                    }
                };
            }
        } catch (CertificateException e) {
            mLogger.d(e.getMessage());
            //Inform wallet UI.
            mRemoteManagementEventListener
                    .onRegistrationFailure(0, McbpErrorCode.FAILED_TO_RETRIEVE_CERTIFICATE);
        } catch (HttpException e) {
            //Inform wallet UI.
            mRemoteManagementEventListener.onRegistrationFailure(0, e.getErrorCode());
        } catch (McbpCryptoException e) {
            mLogger.d(e.getMessage());
            //Inform wallet UI.
            mRemoteManagementEventListener.onRegistrationFailure(0, McbpErrorCode.CRYPTO_ERROR);
        }

        return certificateMetaData;
    }

    /**
     * Get the status of user is register or not.
     *
     * @return true if user is already register else return false.
     */
    private boolean isUserAlreadyRegister() {
        WalletState walletState = mLdeRemoteManagementService.getWalletState();
        return walletState == WalletState.REGISTER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void getSystemHealth() {
        String healthUrl = getBaseUrl() + BASE_REQUEST + "/health";
        //Get HttpGet Request
        HttpGetRequest httpGetRequest = mHttpFactory.getHttpGetRequest(healthUrl);

        HttpResponse httpResponse;
        try {
            httpResponse = mHttpFactory.execute(httpGetRequest);
            if (httpResponse.getStatusCode() == HttpURLConnection.HTTP_OK) {
                mRemoteManagementEventListener.onSystemHealthCompleted();
            } else {
                mRemoteManagementEventListener.onSystemHealthFailure(httpResponse.getStatusCode());
            }
        } catch (HttpException e) {
            mLogger.d(e.getMessage());
            mRemoteManagementEventListener.onSystemHealthFailure(0);
        }
    }

    /**
     * @return base URL from appropriate source.
     */
    public String getBaseUrl() {
        if (mSessionContext != null && mSessionContext.getResponseHost() != null &&
            !mSessionContext.getResponseHost().isEmpty()) {
            mLogger.d("Using response Host:" + mSessionContext.getResponseHost());
            return mSessionContext.getResponseHost();

        }
        final String url = mLdeRemoteManagementService.getUrlRemoteManagement();
        mLogger.d("Using Remote Management Host:" + url);
        return url;
    }

    /**
     * @return true if any pending action is remaining to execute, false otherwise.
     */
    public boolean isAnyActionPending() {
        return RemoteManagementHandler.getInstance().isAnyActionPending();
    }

    /**
     * @return true if a Remote Management request is processing and have not received response
     * from CMS-D.
     */
    public boolean isProcessing() {
        return sIsProcessing;
    }

}
