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
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.ErrorCode;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Represents abstract handler for CMS-D request.This implementation provides set of basic
 * infrastructure capability to create different request handler to just to implementing only
 * business functionality.
 */
public abstract class AbstractRequestHandler {

    public static final String MAJOR_VERSION = "1";
    public static final String MINOR_VERSION = "0";
    public static final String API_NAME = "paymentapp";
    public static final String API_VERSION = MAJOR_VERSION + "/" + MINOR_VERSION;
    public static final String BASE_REQUEST = "/" + API_NAME + "/" + API_VERSION;

    /**
     * Logger
     */
    final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);
    /**
     * Unique request id
     */
    private final String mRequestId;
    /**
     * Request params
     */
    private CmsDRequestHolder mCmsDRequestHolder;
    /**
     * If action requires session data to be used to encrypt data before sending to CMS-D
     */
    private SessionAwareAction mSessionAwareAction;
    /**
     * Session context
     */
    //kept Scope as package private so that sub classes can access it.
    SessionContext mSessionContext;
    /**
     * Http request handler with support for MDES related communication protocol
     */
    private MdesCommunicator mMdesCommunicator;
    /**
     * LDE remote management service
     */
    private LdeRemoteManagementService mLdeRemoteManagementService;

    public AbstractRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                  SessionAwareAction sessionAwareAction, String requestId) {
        this.mCmsDRequestHolder = cmsDRequestHolder;
        this.mSessionAwareAction = sessionAwareAction;
        this.mRequestId = requestId;
    }

    /**
     * Start validating session data and communicating with CMS-D
     *
     * @return Instance of {@link RemoteManagementResponseHolder}
     * @throws HttpException    throws if any error related to communication occurred
     * @throws ServiceException throws if any error at SDK level occurred
     */
    public final RemoteManagementResponseHolder execute() throws HttpException, ServiceException {
        if (mSessionAwareAction != null && !isSessionAvailableAndValid()) {
            if (mSessionContext != null) {
                mSessionContext.clear();
                mSessionContext = null;
            }
            try {
                mSessionAwareAction.onSessionExpire(mCmsDRequestHolder);
            } catch (HttpException e) {
                return RemoteManagementResponseHolder
                        .generateWaitingForSessionResponse(mCmsDRequestHolder, e);
            }

            return RemoteManagementResponseHolder
                    .generateWaitingForSessionResponse(mCmsDRequestHolder, null);
        }

        //handle invalid session thrown by server: Session is valid from SDK but by the time
        // session reaches to CMS-D it expires. CMS-D gives Http Status code 401  and based on
        // that request a new session.
        try {
            return handle();
        } catch (HttpException exception) {
            //catch error code 401 for invalid session, refer MPSDK-628
            //for Provision we don't give this support, as whenever session is arrived form
            // CMS-D, Provision is specified as pending action in notification.
            if (exception.getErrorCode() == ErrorCode.SESSION_EXPIRED_ERROR_CODE &&
                mCmsDRequestHolder.mDRequestEnum != CmsDRequestEnum.PROVISION) {
                return handleInvalidSessionCmsdError();
            } else {
                throw exception;
            }
        }
    }

    /**
     * Validate session data's validity
     *
     * @return true if Session is valid false otherwise
     */
    public boolean isSessionAvailableAndValid() {
        return (mSessionContext != null && mSessionContext.isValidSession());
    }

    /**
     * Hook point to integrate business logic to process remote management service
     *
     * @return RemoteManagementResponseHolder instance.
     * @throws HttpException
     * @throws ServiceException
     */
    public abstract RemoteManagementResponseHolder handle() throws HttpException, ServiceException;

    public CmsDRequestHolder getCmsDRequestHolder() {
        return this.mCmsDRequestHolder;
    }

    public String getRequestId() {
        return this.mRequestId;
    }

    public AbstractRequestHandler withSessionContext(SessionContext sessionContext) {
        this.mSessionContext = sessionContext;
        return this;
    }

    public AbstractRequestHandler withMdesCommunicator(MdesCommunicator mdesCommunicator) {
        this.mMdesCommunicator = mdesCommunicator;
        return this;
    }

    public AbstractRequestHandler withLdeRemoteManagementService(
            LdeRemoteManagementService ldeRemoteManagementService) {
        this.mLdeRemoteManagementService = ldeRemoteManagementService;
        return this;
    }

    public CryptoService getCryptoService() {
        return this.mMdesCommunicator.getCryptoService();
    }

    public LdeRemoteManagementService getLdeRemoteManagementService() {
        return mLdeRemoteManagementService;
    }

    /**
     * Establish connection to provided URL point.
     *
     * @param data      Data that need to share to server
     * @param toEncrypt should service request be encrypted or not
     * @return HTTP Response contents
     * @throws HttpException
     */
    ByteArray communicate(String data, boolean toEncrypt) throws HttpException {
        mLogger.d("MDES_PROTOCOL;PLAIN_CMS_D_REQUEST;SENDER:MPA:" + "([" + data + "])");
        final String url = getRequestUrl();
        mLogger.d("Final Host:" + url);

        if (mSessionContext != null) {
            // Update expiry timestamp after the session code is used for the first time
            if (!mSessionContext.isUsed()) {
                mSessionContext.createExpiryTimeStamp();
            }
            this.mSessionContext.setUsed(true);
        }
        return getMdesCommunicator()
                .withDeviceFingerPrint(mLdeRemoteManagementService.getMpaFingerPrint())
                .communicate(this.mSessionContext, data, url, toEncrypt);
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

    private MdesCommunicator getMdesCommunicator() {
        return mMdesCommunicator;
    }

    public abstract String getRequestUrl();

    private RemoteManagementResponseHolder handleInvalidSessionCmsdError() {
        try {
            mSessionAwareAction.onSessionExpire(mCmsDRequestHolder);
        } catch (HttpException e) {
            return RemoteManagementResponseHolder
                    .generateWaitingForSessionResponse(getCmsDRequestHolder(), e);
        }
        return RemoteManagementResponseHolder
                .generateWaitingForSessionResponse(getCmsDRequestHolder(), null);
    }
}

