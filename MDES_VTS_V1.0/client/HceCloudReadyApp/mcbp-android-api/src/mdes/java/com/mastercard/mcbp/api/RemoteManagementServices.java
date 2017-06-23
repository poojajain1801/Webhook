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

package com.mastercard.mcbp.api;

import android.util.Log;

import com.mastercard.mcbp.data.PendingResult;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.lde.services.LdeRemoteManagementService;
import com.mastercard.mcbp.remotemanagement.CmsDService;
import com.mastercard.mcbp.remotemanagement.mdes.CmsDRequestEnum;
import com.mastercard.mcbp.remotemanagement.mdes.CmsDServiceImpl;
import com.mastercard.mcbp.remotemanagement.mdes.PendingRetryRequest;
import com.mastercard.mcbp.remotemanagement.mdes.RemoteManagementRequestType;
import com.mastercard.mcbp.userinterface.MdesRemoteManagementEventListener;
import com.mastercard.mcbp.utils.PropertyStorageFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.McbpCardException;
import com.mastercard.mcbp.utils.http.HttpFactory;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.utils.task.McbpAsyncTask;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;
import com.mastercard.mcbp.utils.task.McbpTaskListener;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Contains methods for remote management operation
 */
public enum RemoteManagementServices {
    INSTANCE;

    /**
     * Async abstraction layer used to perform background tasks
     */
    private static CmsDService mCmsDService;
    /**
     * McbpLogger
     */
    private static final McbpLogger mLogger =
            McbpLoggerFactory.getInstance().getLogger("RemoteManagementServices");

    /**
     * Flag indicating whether the Remote Management Services have been initialized
     */
    private static boolean sIsInitialized = false;

    public static void initialize(final HttpFactory httpFactory,
                                  final LdeRemoteManagementService remoteManagementService,
                                  final PropertyStorageFactory storageFactory) {
        if (sIsInitialized) {
            throw new RuntimeException("The module has been already initialized");
        }
        mCmsDService = new CmsDServiceImpl(httpFactory, remoteManagementService, storageFactory);
    }

    static void openRemoteManagementSession(String data) {
        mCmsDService.handleNotification(ByteArray.of(data.getBytes()));
    }

    static void changePin(final String cardId,
                          final ByteArray oldPin,
                          final ByteArray newPin) throws AlreadyInProcessException {

        if (mCmsDService.isAnyActionPending() || mCmsDService.isProcessing()) {
            throw new AlreadyInProcessException("CMS-D request is already in process");
        }

        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onRun() {
                try {
                    mCmsDService.requestForMobilePinChange(cardId, oldPin, newPin);
                } catch (InvalidInput | McbpCryptoException exception) {
                    // FIXME: Future releases may pass this error to the upper layers
                    mLogger.d(Log.getStackTraceString(exception));
                }
            }

            @Override
            public void onPostExecute() {

            }

        });
    }

    static void getTaskStatus(final RemoteManagementRequestType requestType)
            throws AlreadyInProcessException {

        if (mCmsDService.isAnyActionPending() || mCmsDService.isProcessing()) {
            throw new AlreadyInProcessException("CMS-D request is already in process");
        }
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onRun() {
                try {
                    mCmsDService.requestForTaskStatus(requestType);
                } catch (InvalidInput | McbpCardException e) {
                    // FIXME: Future releases may pass this error to the upper layers
                    mLogger.d(Log.getStackTraceString(e));
                }
            }

            @Override
            public void onPostExecute() {

            }

        });
    }

    /**
     * Sends request to CMS-D to replenish more keys to MPA.
     *
     * @param digitizedCardId digitized card id.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     */
    static void replenish(final String digitizedCardId) throws AlreadyInProcessException {

        if (mCmsDService.isAnyActionPending() || mCmsDService.isProcessing()) {
            throw new AlreadyInProcessException("CMS-D request is already in process");
        }
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onRun() {
                try {
                    mCmsDService.requestForPaymentTokens(digitizedCardId);
                } catch (InvalidInput | InvalidCardStateException exception) {
                    mLogger.d(Log.getStackTraceString(exception));
                }
            }

            @Override
            public void onPostExecute() {
            }

        });
    }

    /**
     * Sends request to CMS-D to delete token from MPA.
     *
     * @param digitizedCardId digitized card id.
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     */
    static void delete(final String digitizedCardId) throws AlreadyInProcessException {

        if (mCmsDService.isAnyActionPending() || mCmsDService.isProcessing()) {
            throw new AlreadyInProcessException("CMS-D request is already in process");
        }
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {
            @Override
            public void onPreExecute() {

            }

            @Override
            public void onRun() {
                try {
                    mCmsDService.requestForDeleteToken(digitizedCardId);
                } catch (InvalidInput e) {
                    // FIXME: Future releases may pass this error to the upper layers
                    mLogger.d(Log.getStackTraceString(e));
                }
            }

            @Override
            public void onPostExecute() {

            }
        });
    }

    /**
     * Registers a Mobile Pin Event listener
     */
    static void registerMdesRemoteManagementEventListener(
            MdesRemoteManagementEventListener listener) {
        mCmsDService.registerMdesRemoteManagementListener(listener);
    }

    /**
     * Unregister UI listeners
     */
    static void unRegisterUiListener() {
        mCmsDService.registerMdesRemoteManagementListener(null);
    }

    /**
     * Force retry of pending action.
     *
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     */
    static void forceRetry() throws AlreadyInProcessException {
        if (mCmsDService.isAnyActionPending() || mCmsDService.isProcessing()) {
            throw new AlreadyInProcessException("CMS-D request is already in process");
        }
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onRun() {
                mCmsDService.forceRetry();
            }

            @Override
            public void onPostExecute() {
            }
        });
    }

    /**
     * Return the pending request
     *
     * @return PendingRetryRequest
     */
    static PendingResult getPendingRequest() {
        final PendingRetryRequest pendingRetryRequest = mCmsDService.getPendingRequest();
        if (pendingRetryRequest == null) {
            return null;
        }
        return new PendingResult() {
            @Override
            public CmsDRequestEnum getRequestType() {
                return pendingRetryRequest.getRequestType();
            }

            @Override
            public String getCardIdentifier() {
                return pendingRetryRequest.getCardId();
            }

            @Override
            public int getRetryCount() {
                return pendingRetryRequest.getRetryCount();
            }
        };
    }

    /**
     * Cancel the pending request
     */
    static boolean cancelPendingRequest() {
        if (!mCmsDService.isAnyActionPending()) {
            return false;
        }

        mCmsDService.cancelPendingRequest();
        return true;
    }

    /**
     * Check the general status of a Mobile Payment API host. 3.3.4
     *
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     */
    static void getSystemHealth() throws AlreadyInProcessException {
        if (mCmsDService.isAnyActionPending() || mCmsDService.isProcessing()) {
            throw new AlreadyInProcessException("CMS-D request is already in process");
        }
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {
            @Override
            public void onPreExecute() {
            }

            @Override
            public void onRun() {
                mCmsDService.getSystemHealth();
            }

            @Override
            public void onPostExecute() {
            }
        });
    }
}
