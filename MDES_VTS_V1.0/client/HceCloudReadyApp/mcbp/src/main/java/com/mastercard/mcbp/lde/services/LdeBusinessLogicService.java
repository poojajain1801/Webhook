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

package com.mastercard.mcbp.lde.services;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.lde.LdeInitParams;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeAlreadyInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;

import java.util.ArrayList;
import java.util.List;

/**
 * List out the services that LDE has exposed to business logic.
 */
public interface LdeBusinessLogicService {

    /**
     * Initializes the Lde with the initialization data and move it from
     * {@link com.mastercard.mcbp.lde.LdeState#UNINITIALIZED UNINITIALIZED}
     * to {@link com.mastercard.mcbp.lde.LdeState#INITIALIZED INITIALIZED}
     * state so that it is ready for Remote Management. Namely it shall: <li>
     * Create all data containers</li> <li>Store initialization data</li>
     * <p/>
     * Initialization of LDE required following data.
     * <li>CMS_MPA_ID</li>
     * <li>RNS_MPA_ID</li>
     * <li>LDE_STATE</li>
     * <li>REMOTE_MANAGEMENT_URL</li>
     * <li>MPA_FGP</li>
     * <li>MOBILE_CONF_KEY</li>
     * <li>MOBILE_MAC_KEY</li>
     *
     * @param initParams instance of {@link com.mastercard.mcbp.lde.LdeInitParams}
     * @throws McbpCryptoException
     * @throws InvalidInput
     * @throws LdeAlreadyInitialized - If LDE is not initialized and its API are being used.
     */
    void initializeLde(LdeInitParams initParams) throws McbpCryptoException,
            LdeAlreadyInitialized, InvalidInput;

    /**
     * Retrieve list of all {@link McbpCard}
     *
     * @param refresh true to retrieve the cards from the database; false to retrieve a cached
     *                version.
     * @return list of {@link McbpCard}
     * @throws LdeNotInitialized
     */
    ArrayList<McbpCard> getMcbpCards(final boolean refresh) throws LdeNotInitialized;

    /**
     * Stores data to be displayed to the user
     *
     * @param data stream encoded in readable format as ASCII
     * @throws LdeNotInitialized exception.
     */
    void storeInformationDelivery(String data) throws LdeNotInitialized;

    /**
     * Retrieves User Information that may have been sent as part of Remote Management.
     *
     * @return value
     * @throws LdeNotInitialized exception.
     */
    String fetchStoredInformationDelivery() throws LdeNotInitialized;

    /**
     * Retrieves last 10 transaction logs associated with digitized card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return List of TransactionLog.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    List<TransactionLog> getTransactionLogs(String digitizedCardId) throws LdeNotInitialized,
            InvalidInput;

    /**
     * Check LDE initialize state.
     *
     * @return true if LDE state is initialized; otherwise false.
     */
    boolean isLdeInitialized();

}
