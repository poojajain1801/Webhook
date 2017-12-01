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
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.TimeZone;

/**
 * Singleton generalized container to manage different remote management operation.
 */
public class RemoteManagementHandler implements SessionAwareAction {
    /**
     * Flag to indicate if any action is currently being executing.
     */
    private boolean isExecuting;

    private final ArrayList<CmsDRequestHolder> pendingItems = new ArrayList<>();

    private MdesCommunicator mMdesCommunicator;

    private LdeRemoteManagementService mLdeRemoteManagementService;
    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    private RemoteManagementHandler() {

    }

    /**
     * @return true if any pending action is remaining to execute, false otherwise.
     */
    public boolean isAnyActionPending() {
        return !this.pendingItems.isEmpty();
    }

    /**
     * @param mMdesCommunicator MdesCommunicator instance.
     */
    public void setMdesCommunicator(
            MdesCommunicator mMdesCommunicator) {
        this.mMdesCommunicator = mMdesCommunicator;
    }

    public LdeRemoteManagementService getLdeRemoteManagementService() {
        return mLdeRemoteManagementService;
    }

    public void setLdeRemoteManagementService(
            LdeRemoteManagementService mLdeRemoteManagementService) {
        this.mLdeRemoteManagementService = mLdeRemoteManagementService;
    }

    public boolean isExecuting() {
        return isExecuting;
    }

    private void setIsExecuting(boolean isExecuting) {
        this.isExecuting = isExecuting;
    }

    public void clearPendingAction() {
        this.pendingItems.clear();
    }

    private static class RemoteManagementHelper {
        public static final RemoteManagementHandler INSTANCE = new RemoteManagementHandler();
    }

    public static RemoteManagementHandler getInstance() {
        return RemoteManagementHelper.INSTANCE;
    }

    /**
     * Initialize singleton with required services.
     *
     * @param ldeRemoteManagementService Instance of {@link LdeRemoteManagementService}
     * @param mdesCommunicator           Instance of {@link MdesCommunicator}
     */
    public static void initialize(LdeRemoteManagementService ldeRemoteManagementService,
                                  MdesCommunicator mdesCommunicator) {
        getInstance().setLdeRemoteManagementService(ldeRemoteManagementService);
        getInstance().setMdesCommunicator(mdesCommunicator);
    }

    /**
     * Execute pending action if any.
     *
     * @param sessionContext Instance of {@link SessionContext}
     * @return RemoteManagementResponseHolder instance.
     */
    public RemoteManagementResponseHolder executePendingAction(SessionContext sessionContext) {
        CmsDRequestHolder cmsDRequestHolder = getInstance().pendingItems.get(0);
        return execute(sessionContext, cmsDRequestHolder);
    }


    /**
     * Execute remote management action base of provided request params.
     *
     * @param sessionContext    Instance of {@link SessionContext}
     * @param cmsDRequestHolder Instance of {@link CmsDRequestHolder}
     * @return Response of
     */
    public RemoteManagementResponseHolder execute(SessionContext sessionContext,
                                                  CmsDRequestHolder cmsDRequestHolder) {
        return execute(sessionContext, cmsDRequestHolder, generateRequestId());
    }


    /**
     * Execute remote management action base of provided request params with specified requestId.
     *
     * @param sessionContext    Instance of {@link SessionContext}
     * @param cmsDRequestHolder Instance of {@link CmsDRequestHolder}
     * @param requestId         Desire requestId
     * @return RemoteManagementResponseHolder instance.
     */
    public RemoteManagementResponseHolder execute(SessionContext sessionContext,
                                                  CmsDRequestHolder cmsDRequestHolder,
                                                  String requestId) {
        RemoteManagementResponseHolder responseHolder = null;
        try {
            setIsExecuting(true);
            switch (cmsDRequestHolder.mDRequestEnum) {
                case REGISTER:
                    responseHolder = new RegisterRequestHandler(cmsDRequestHolder, null, requestId)
                            .withMdesCommunicator(mMdesCommunicator)
                            .withLdeRemoteManagementService(mLdeRemoteManagementService)
                            .withSessionContext(null).execute();
                    break;
                case PROVISION:
                    responseHolder = new ProvisionRequestHandler(cmsDRequestHolder, this, requestId)
                            .withMdesCommunicator(mMdesCommunicator)
                            .withLdeRemoteManagementService(mLdeRemoteManagementService)
                            .withSessionContext(sessionContext).execute();
                    break;
                case NOTIFY_PROVISION_RESULT:
                    responseHolder =
                            new NotifyProvisionResultHandler(cmsDRequestHolder, this, requestId)
                                    .withMdesCommunicator(mMdesCommunicator)
                                    .withLdeRemoteManagementService(mLdeRemoteManagementService)
                                    .withSessionContext(sessionContext).execute();
                    break;
                case CHANGE_PIN:
                    responseHolder = new ChangePinRequestHandler(cmsDRequestHolder, this, requestId)
                            .withMdesCommunicator(mMdesCommunicator)
                            .withLdeRemoteManagementService(mLdeRemoteManagementService)
                            .withSessionContext(sessionContext).execute();
                    break;
                case REPLENISH:
                    responseHolder =
                            new ReplenishmentRequestHandler(cmsDRequestHolder, this, requestId)
                                    .withMdesCommunicator(mMdesCommunicator)
                                    .withLdeRemoteManagementService(mLdeRemoteManagementService)
                                    .withSessionContext(sessionContext).execute();
                    break;
                case GET_TASK_STATUS:
                    responseHolder =
                            new GetTaskStatusRequestHandler(cmsDRequestHolder, this, requestId)
                                    .withMdesCommunicator(mMdesCommunicator)
                                    .withLdeRemoteManagementService(mLdeRemoteManagementService)
                                    .withSessionContext(sessionContext).execute();
                    break;
                case DELETE:
                    responseHolder =
                            new DeleteTokenRequestHandler(cmsDRequestHolder, this, requestId)
                                    .withMdesCommunicator(mMdesCommunicator)
                                    .withLdeRemoteManagementService(mLdeRemoteManagementService)
                                    .withSessionContext(sessionContext).execute();
                    break;
                case REQUEST_SESSION:
                    responseHolder =
                            new RequestSessionRequestHandler(cmsDRequestHolder, null, requestId)
                                    .withMdesCommunicator(mMdesCommunicator)
                                    .withLdeRemoteManagementService(mLdeRemoteManagementService)
                                    .withSessionContext(null).execute();
                    break;
            }
        } catch (HttpException e) {
            mLogger.d(e.getMessage());
            responseHolder = new RemoteManagementResponseHolder();
            responseHolder.mServiceResult = ServiceResult.COMMUNICATION_ERROR;
            responseHolder.mCmsDRequestHolder = cmsDRequestHolder;
            responseHolder.mErrorContext = e;
            //clear only when exception occurs, this is avoid clearing when request session
            // response is received. Hence not kept in finally block.
            clearPendingAction();
        } catch (ServiceException e) {
            mLogger.d(e.getMessage());
            responseHolder = new RemoteManagementResponseHolder();
            responseHolder.mServiceResult = ServiceResult.SERVICE_INTERNAL_ERROR;
            responseHolder.mCmsDRequestHolder = cmsDRequestHolder;
            responseHolder.mErrorContext = e;
            //clear only when exception occurs, this is avoid clearing when request session
            // response is received. Hence not kept in finally block.
            clearPendingAction();
        } finally {
            setIsExecuting(false);
        }

        return responseHolder;
    }

    @Override
    public void onSessionExpire(CmsDRequestHolder cmsDRequestHolder) throws HttpException {
        if (isAnyActionPending()) {
            clearPendingAction();
            throw new HttpException(McbpErrorCode.SERVER_ERROR,
                                    "Can not create a secure session with CMS-D");
        }
        addPendingItem(cmsDRequestHolder);
        CmsDRequestHolder cmsDRequest = new CmsDRequestHolder();
        cmsDRequest.mDRequestEnum = CmsDRequestEnum.REQUEST_SESSION;
        cmsDRequest.mPaymentAppInstanceId = cmsDRequestHolder.mPaymentAppInstanceId;
        cmsDRequest.mPaymentAppProviderId = cmsDRequestHolder.mPaymentAppProviderId;
        RemoteManagementResponseHolder remoteManagementResponseHolder =
                execute(null, cmsDRequest);

        if (remoteManagementResponseHolder.mErrorContext != null) {
            throw new HttpException(remoteManagementResponseHolder.mErrorContext.getErrorCode(),
                                    remoteManagementResponseHolder.mErrorContext.getMessage());
        }
    }

    private void addPendingItem(CmsDRequestHolder cmsDRequestHolder) {
        getInstance().pendingItems.clear();
        getInstance().pendingItems.add(cmsDRequestHolder);
    }

    private static String generateRequestId() {

        TimeZone timeZone = TimeZone.getTimeZone("UTC");
        Calendar calendar = Calendar.getInstance(timeZone);

        final String format = "yyyyMMddHHmmssSSS";
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(format);

        return simpleDateFormat.format(calendar.getTime());

    }

    /**
     * Possible response of CMSD request
     */
    public enum ServiceResult {
        WAITING_FOR_SESSION, OK, COMMUNICATION_ERROR, SERVICE_INTERNAL_ERROR
    }

    public ArrayList<CmsDRequestHolder> getPendingItems() {
        return pendingItems;
    }

}
