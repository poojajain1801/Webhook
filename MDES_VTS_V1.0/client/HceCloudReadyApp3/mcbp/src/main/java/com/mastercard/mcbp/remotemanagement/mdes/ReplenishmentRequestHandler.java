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

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredential;
import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredentialContainer;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDReplenishRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDReplenishResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeCheckedException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create replenish request execute it and also process response.
 */
public class ReplenishmentRequestHandler extends AbstractRequestHandler {

    public static final String REPLENISH = BASE_REQUEST + "/" + "replenish";

    public ReplenishmentRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                       SessionAwareAction sessionAwareAction,
                                       String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {
        CmsDReplenishRequestHolder cmsDReplenishRequestHolder =
                (CmsDReplenishRequestHolder) getCmsDRequestHolder();

        CmsDReplenishRequest cmsDReplenishRequest = new CmsDReplenishRequest();
        cmsDReplenishRequest.setTokenUniqueReference(
                cmsDReplenishRequestHolder.tokenUniqueReference);
        cmsDReplenishRequest.setRequestId(getRequestId());
        try {
            TransactionCredentialStatus[] allTransactionCredentialStatus =
                    cmsDReplenishRequestHolder.transactionCredentialsStatus;

            cmsDReplenishRequest.setTransactionCredentialsStatus(allTransactionCredentialStatus);

            ByteArray response = communicate(cmsDReplenishRequest.toJsonString(), true);

            CmsDReplenishResponse cmsDReplenishResponse = CmsDReplenishResponse.valueOf(response);

            if (!cmsDReplenishResponse.isSuccess()) {
                throw new ServiceException(McbpErrorCode.SERVER_ERROR, cmsDReplenishResponse
                        .getErrorDescription());
            }
            //Update the response host so that next request in same session use this host
            mSessionContext.updateResponseHost(cmsDReplenishResponse.getResponseHost());

            String digitizeCardId = getLdeRemoteManagementService()
                    .getCardIdFromTokenUniqueReference(
                            cmsDReplenishRequestHolder.tokenUniqueReference);


            TransactionCredential[] transactionCredentials = cmsDReplenishResponse
                    .getTransactionCredentials();

            ByteArray decKey = getLdeRemoteManagementService().getDataEncryptionKey();

            // #MCBP_LOG_BEGIN
            mLogger.d("MCBP_PROTOCOL;PROVISION;DATA_ENCRYPTION_KEY: ([" + decKey + "])");
            // #MCBP_LOG_END
            getLdeRemoteManagementService()
                    .deleteTransactionCredentialStatusOtherThanActive(digitizeCardId);

            if (transactionCredentials != null) {
                for (TransactionCredential credential : transactionCredentials) {
                    TransactionCredentialContainer transactionCredentialContainer =
                            new TransactionCredentialContainer(credential,
                                                               digitizeCardId,
                                                               decKey);

                    SingleUseKey singleUseKey = transactionCredentialContainer.toSingleUseKey();

                    getLdeRemoteManagementService().provisionSingleUseKey(singleUseKey);
                    getLdeRemoteManagementService().insertOrUpdateTransactionCredentialStatus
                            (digitizeCardId, singleUseKey.getContent().getAtc(),
                             TransactionCredentialStatus.Status.UNUSED_ACTIVE);
                }
            }

            return RemoteManagementResponseHolder
                    .generateSuccessResponse(getCmsDRequestHolder(), cmsDReplenishResponse);

        } catch (InvalidInput | LdeCheckedException ex) {
            throw new ServiceException(McbpErrorCode.LDE_ERROR, ex.getMessage(), ex);
        } catch (McbpCryptoException ex) {
            throw new ServiceException(McbpErrorCode.CRYPTO_ERROR, ex.getMessage(), ex);
        }
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + REPLENISH;
    }
}
