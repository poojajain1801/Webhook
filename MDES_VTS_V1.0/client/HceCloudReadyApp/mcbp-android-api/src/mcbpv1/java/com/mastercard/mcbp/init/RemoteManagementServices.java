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

package com.mastercard.mcbp.init;

import com.mastercard.mcbp.businesslogic.ApplicationInfo;
import com.mastercard.mcbp.businesslogic.MobileDeviceInfo;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.lde.LdeInitParams;
import com.mastercard.mcbp.lde.services.LdeBusinessLogicService;
import com.mastercard.mcbp.remotemanagement.CmsConfiguration;
import com.mastercard.mcbp.remotemanagement.CmsService;
import com.mastercard.mcbp.remotemanagement.RnsService;
import com.mastercard.mcbp.remotemanagement.mcbpV1.CmsActivationData;
import com.mastercard.mcbp.remotemanagement.mcbpV1.CmsRegisterResult;
import com.mastercard.mcbp.remotemanagement.mcbpV1.CmsServiceImpl;
import com.mastercard.mcbp.userinterface.CmsActivationListener;
import com.mastercard.mcbp.userinterface.UserInterfaceListener;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.utils.task.McbpAsyncTask;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;
import com.mastercard.mcbp.utils.task.McbpTaskListener;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * @deprecated Use MDES build flavour instead
 */
@Deprecated
public class RemoteManagementServices {

    private static final String NOT_YET_SUPPORTED = "Not yet supported";

    private static final String LDE_ERROR = "LDE Error";

    private static final String PLEASE_CHECK_URL = "Please check URL";

    private static final String APPLICATION_NOT_INITIALIZE =
            "Application is not initialized " + "properly. Please close and reopen the "
            + "application.";

    private static boolean sIsProcessing;

    private final CmsService mCmsService;
    private final RnsService mRnsService;
    private final LdeBusinessLogicService mLdeBusinessLogicService;
    /**
     * Logger
     */
    private final McbpLogger mLog = McbpLoggerFactory.getInstance().getLogger(this);

    public RemoteManagementServices(SdkContext sdkContext,
                                    CmsConfiguration cmsConfiguration,
                                    ApplicationInfo applicationInfo, RnsService rnsService) {

        mCmsService = new CmsServiceImpl(sdkContext.getHttpFactory(),
                                         cmsConfiguration,
                                         sdkContext.getLdeRemoteManagementService(),
                                         applicationInfo);
        mRnsService = rnsService;
        mLdeBusinessLogicService = sdkContext.getLdeBusinessLogicService();
    }

    /**
     * Registers the MPA SDK and the user to the CMS.
     *
     * @param mobileDeviceInfo   Mobile Device Info
     * @param usrId              The user Id entered.
     * @param actId              The activation code entered.
     * @param activationListener Callback for events.
     */
    public void registerToCms(final MobileDeviceInfo mobileDeviceInfo,
                              final String usrId,
                              final String actId,
                              final CmsActivationListener activationListener) {

        // launching registration to CMS
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();
        mcbpAsyncTask.execute(new McbpTaskListener() {

            // Registration result
            private CmsRegisterResult result;

            @Override
            public void onPreExecute() {
                // 14 Activating Wallet
                activationListener.onActivationStarted();
            }

            @Override
            public void onRun() {
                result = mCmsService.registerToCms(usrId, actId);
                switch (result.getStatus()) {
                    case SUCCESS:
                        mCmsService.insertDummyMobileKeySetId();
                        result =
                                mCmsService.sendInformation(mRnsService.getRegistrationId(), usrId);
                        break;
                    case ERROR_NETWORK:
                        activationListener.onNetWorkError();
                        break;
                    case ERROR_MCBP:
                        activationListener.onActivationError(result.getErrorMessage());
                        break;
                    case ERROR_URL:
                        activationListener.onActivationError(PLEASE_CHECK_URL);
                        break;
                    case INVALID_RNS_MPA_ID:
                        activationListener.onActivationError(APPLICATION_NOT_INITIALIZE);
                        break;
                }
            }

            @Override
            public void onPostExecute() {
                CmsActivationData activationData = result.getActivationData();
                switch (result.getStatus()) {
                    case SUCCESS:
                        mLog.d("User registration has been successfully completed");
                        // initializes Lde
                        // MCBP MPA spec 1.0 page no 52
                        ByteArray cmsMpaId = ByteArray.of(activationData.getCmsMpaId());
                        ByteArray rnsMpaId =
                                ByteArray.of(mRnsService.getRegistrationId().getBytes());
                        String url = activationData.getNotificationUrl();
                        LdeInitParams params = null;
                        try {
                            params = new LdeInitParams(cmsMpaId, mobileDeviceInfo
                                    .calculateDeviceFingerPrint());
                            params.setRnsMpaId(rnsMpaId);
                            params.setUrlRemoteManagement(url);

                            mLdeBusinessLogicService.initializeLde(params);
                            activationListener.onWalletActivated();
                        } catch (LdeAlreadyInitialized | InvalidInput | McbpCryptoException e) {
                            activationListener.onActivationError(LDE_ERROR);
                        }
                        if (params != null) {
                            params.wipe();
                        }
                        break;
                    case ERROR_NETWORK:
                        activationListener.onNetWorkError();
                        break;
                    case ERROR_MCBP:
                        activationListener.onActivationError(result.getErrorMessage());
                        break;
                    case ERROR_URL:
                        activationListener.onActivationError(PLEASE_CHECK_URL);
                        break;
                    case INVALID_RNS_MPA_ID:
                        activationListener.onActivationError(APPLICATION_NOT_INITIALIZE);
                        break;
                }
            }
        });
    }

    /**
     * Go online and sync the SUKs for the specified card or all cards if no card specified.
     *
     * @param digitizedCardId Instance of digitized card id.
     */
    public void goOnlineForSync(final String digitizedCardId) throws AlreadyInProcessException {
        final McbpAsyncTask mcbpAsyncTask = McbpTaskFactory.getMcbpAsyncTask();

        if (sIsProcessing) {
            throw new AlreadyInProcessException("CMS request is already in process");
        }

        mcbpAsyncTask.execute(new McbpTaskListener() {

            @Override
            public void onPreExecute() {


            }

            @Override
            public void onRun() {
                mCmsService.goOnlineForSync();
            }

            @Override
            public void onPostExecute() {
                sIsProcessing = false;
            }

        });
    }

    /**
     * Perform a CMS Session. This method must be called when a RNS message is
     * received
     *
     * @param rnsMessage rns message.
     */
    public void openRemoteManagementSession(String rnsMessage) {
        mCmsService.openRemoteSession(ByteArray.of(rnsMessage));
    }

    public void unRegisterUiListener() {
        mCmsService.registerUiListener(null);
    }

    public void registerListener(UserInterfaceListener userInterfaceListener) {
        this.mCmsService.registerUiListener(userInterfaceListener);
    }

    public CmsService getCmsService() {
        return this.mCmsService;
    }
}
