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
import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.lde.TransactionLog;
import com.mastercard.mcbp.lde.data.SessionKey;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mcbp.utils.exceptions.lde.SessionKeysNotAvailable;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionLoggingError;
import com.mastercard.mcbp.utils.exceptions.lde.TransactionStorageLimitReach;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * List out the services that {@link McbpCard} requires from LDE.
 */
public interface LdeMcbpCardService {

    /**
     * Add transaction details to LDE.Below is transaction detail format as specified in MPA_FD
     * specification.
     * <table>
     * <tr>
     * <th>
     * Data Element
     * </th>
     * <th>
     * Length
     * </th>
     * <th>
     * Position
     * </th>
     * </tr>
     * <tr>
     * <td>DC_ID</td>
     * <td>17 bytes</td>
     * <td>1</td>
     * </tr>
     * <tr>
     * <td>Unpredictable Number</td>
     * <td>4 bytes</td>
     * <td>18</td>
     * </tr>
     * <tr>
     * <td>Application Transaction Counter</td>
     * <td>2 bytes</td>
     * <td>22</td>
     * </tr>
     * <tr>
     * <td>Cryptogram Format</td>
     * <td>1 bytes</td>
     * <td>24</td>
     * </tr>
     * <tr>
     * <td>Application Cryptogram</td>
     * <td>8 bytes</td>
     * <td>25</td>
     * </tr>
     * <tr>
     * <td>Is Device Jail Broken</td>
     * <td>1 bytes</td>
     * <td>33</td>
     * </tr>
     * <tr>
     * <td>Recent attack detected</td>
     * <td>1 bytes</td>
     * <td>34</td>
     * </tr>
     * <p/>
     * </table>
     *
     * @param log instance of {@link TransactionLog}
     * @throws TransactionStorageLimitReach
     * @throws LdeNotInitialized
     * @throws TransactionLoggingError
     */
    void addToLog(TransactionLog log)
            throws TransactionStorageLimitReach, LdeNotInitialized, TransactionLoggingError;

    /**
     * Retrieve next available session key for transaction for specified card.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return Instance of {@link SingleUseKey}
     * @throws SessionKeysNotAvailable
     * @throws McbpCryptoException
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    SingleUseKey getNextSessionKey(String digitizedCardId)
            throws SessionKeysNotAvailable, McbpCryptoException, LdeNotInitialized, InvalidInput;

    /**
     * Get the next session of remote payment session keys
     *
     * @param digitizedCardId The Digitized Card Id for which the Session Key is being requested
     * @return The next Session Key for Remote Payment
     * @throws InvalidInput
     * @throws McbpCryptoException
     * @throws SessionKeysNotAvailable
     * @throws LdeNotInitialized
     * @since 1.0.3
     */
    SessionKey getNextRemotePaymentSessionKeys(String digitizedCardId) throws
            InvalidInput, McbpCryptoException, SessionKeysNotAvailable, LdeNotInitialized;

    /**
     * Get the next set of contactless session keys
     *
     * @param digitizedCardId The Digitized Card ID for which the Session Key is being requested
     * @return The Next Session Key for Contactless Payment
     * @throws InvalidInput
     * @throws McbpCryptoException
     * @throws SessionKeysNotAvailable
     * @throws LdeNotInitialized
     * @since 1.0.3
     */
    SessionKey getNextContactlessSessionKeys(String digitizedCardId)
            throws InvalidInput, McbpCryptoException, SessionKeysNotAvailable, LdeNotInitialized;

    /**
     * Wipe specific Suk according to digitize card id and suk id from Lde.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param singleUseKeyId  suk id.
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    void wipeDcSuk(String digitizedCardId, String singleUseKeyId)
            throws LdeNotInitialized, InvalidInput;

    /**
     * Remove digitize card from database identified by card id.
     *
     * @param digitizedCardId - Identifier of digitize card
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    void wipeDigitizedCard(ByteArray digitizedCardId) throws LdeNotInitialized, InvalidInput;

    /**
     * Retrieve SUK count.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @return Single Use Key count
     * @throws LdeNotInitialized
     * @throws InvalidInput
     */
    int getSingleUseKeyCount(String digitizedCardId) throws LdeNotInitialized, InvalidInput;

    /**
     * Insertion of transaction credential status if status already exist it will update the
     * fields.
     *
     * @param digitizedCardId 17 bytes long identifier.
     * @param atc             Application Transaction Counter
     * @param status          Status of Transaction Credentials. Can be one of the following values
     *                        UNUSED_ACTIVE, UNUSED_DISCARDED, USED_FOR_CONTACTLESS, USED_FOR_DSRP
     * @throws InvalidInput
     */
    void insertOrUpdateTransactionCredentialStatus(final String digitizedCardId,
                                                   ByteArray atc,
                                                   TransactionCredentialStatus.Status status)
            throws InvalidInput;

    /**
     * This function returns the last 4 digits of the PAN for display by the wallet
     *
     * @param tokenUniqueReference tokenUniqueReference of the card for which the PAN is requested
     * @return String last 4 digits of the PAN for the wallet to display
     * @since 1.0.4
     */
    String getDisplayablePanDigits(final String tokenUniqueReference) throws InvalidInput;

    /**
     * This function fetches transaction identifier for a transaction using
     * transaction date and transaction atc.
     *
     * @param transactionDate transaction date
     * @param transactionAtc  transaction atc
     * @return TransactionIdentifier
     * @since 1.0.6a
     */
    ByteArray getTransactionIdentifier(ByteArray transactionDate, ByteArray transactionAtc);

}
