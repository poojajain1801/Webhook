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

package com.mastercard.mcbp.businesslogic;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.services.LdeBusinessLogicService;
import com.mastercard.mcbp.remotemanagement.RnsService;
import com.mastercard.mcbp.userinterface.InitializationListener;
import com.mastercard.mcbp.userinterface.McbpError;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp.utils.task.McbpTaskFactory;
import com.mastercard.mcbp.utils.task.McbpTaskListener;

import java.util.ArrayList;
import java.util.List;

/**
 * The business logic is a key module in the Mobile Payment Application and
 * behaves as the conductor of the modules which are combined to deliver the
 * Mobile Payment Application to the User. This is the main entry point for the
 * MPA UI components.<br>
 */
public class BusinessServices {

    /**
     * Remote Notification Service
     */
    private final RnsService mRnsService;
    /**
     * Logger
     */
    private final McbpLogger mLog = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Lde Interface for Business Service logic
     */
    private final LdeBusinessLogicService mLdeBusinessLogicService;
    /**
     * Handles default cards
     */
    private final DefaultCardsManager mDefaultCardsManager;
    /**
     * Current Card
     */
    private McbpCard mCurrentCard;

    /**
     * Default Constructor
     */
    public BusinessServices(final RnsService rnsService,
                            final LdeBusinessLogicService ldeBusinessLogicService,
                            final DefaultCardsManager defaultCardsManager) {
        this.mRnsService = rnsService;
        this.mLdeBusinessLogicService = ldeBusinessLogicService;
        this.mDefaultCardsManager = defaultCardsManager;
    }

    public DefaultCardsManager getDefaultCardsManager() {
        return mDefaultCardsManager;
    }

    public McbpCard getCurrentCard() {
        return mCurrentCard;
    }

    public void setCurrentCard(McbpCard card) {
        this.mCurrentCard = card;
    }

    /**
     * Lde retrieves all stored DC_IDs if Lde is in
     * {@link com.mastercard.mcbp.lde.LdeState#INITIALIZED INITIALIZED}
     * state. Then the API creates for each Digitized Card Id an object of McbpCard and
     * fills it with BusinessLogicModule
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     */
    public ArrayList<McbpCard> getAllCards(final boolean refresh) throws LdeNotInitialized{
        return mLdeBusinessLogicService.getMcbpCards(refresh);
    }

    /**
     * Returns all transaction logs associated with a Card
     */
    public List<TransactionLog> getTransactionLogs(String digitizedCardId) throws LdeNotInitialized,
            InvalidInput {
        return mLdeBusinessLogicService.getTransactionLogs(digitizedCardId);
    }

    /**
     * Retrieves User Information that may have been sent as part of Remote
     * Management. Typically called when Lde has been updated.
     */
    public String retrieveUserInformation() throws LdeNotInitialized {
        return mLdeBusinessLogicService.fetchStoredInformationDelivery();
    }

    /**
     * Initializes the MPA SDK with the CMS system.
     *
     * @param initListener Callback for initialization result
     */
    public void initializeMpa(final InitializationListener initListener) {
        // 2 When the application is activated, the Business Logic checks the
        // status of the application.
        // check if application is registered with the RNS service
        mLog.d("RNS Service already registered");
        // Check database
        // launching registration to CMS
        McbpTaskFactory.getMcbpAsyncTask().execute(new McbpTaskListener() {

            @Override
            public void onPreExecute() {

            }

            @Override
            public void onRun() {
                if (mRnsService.getRegistrationId() == null
                    || mRnsService.getRegistrationId().isEmpty()) {
                    mRnsService.registerApplication();
                }
            }

            @Override
            public void onPostExecute() {
                if (mRnsService.getRegistrationId() == null || mRnsService.getRegistrationId()
                                                                          .isEmpty()) {
                    initListener.onError(McbpError.RNS_REGISTRATION_TIMEOUT);
                    return;
                }
                if (mLdeBusinessLogicService.isLdeInitialized()) {
                    initListener.onMpaReady();
                } else {
                    initListener.onRegistrationNeeded();
                }
            }
        });
    }

    /**
     * Get RNS Service.
     *
     * @return Instance of RnsService.
     */
    public RnsService getRnsService() {
        return mRnsService;
    }
}
