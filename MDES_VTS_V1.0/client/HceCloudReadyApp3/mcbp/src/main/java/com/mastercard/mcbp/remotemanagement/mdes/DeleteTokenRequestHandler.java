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

import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDDeleteRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.GenericCmsDRemoteManagementResponse;
import com.mastercard.mcbp.remotemanagement.mdes.models.TransactionCredentialStatus;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mcbp.utils.exceptions.lde.LdeNotInitialized;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create delete token request execute it and also process response.
 */
public class DeleteTokenRequestHandler extends AbstractRequestHandler {

    public static final String DELETE = BASE_REQUEST + "/" + "delete";

    public DeleteTokenRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                     SessionAwareAction sessionAwareAction,
                                     String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);

    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {
        CmsDDeleteRequestHolder cmsDDeleteRequestHolder =
                (CmsDDeleteRequestHolder) getCmsDRequestHolder();

        CmsDDeleteRequest cmsDDeleteRequest = new CmsDDeleteRequest();
        cmsDDeleteRequest.setTokenUniqueReference(cmsDDeleteRequestHolder.tokenUniqueReference);
        if (cmsDDeleteRequestHolder.mTransactionCredentialStatuses == null ||
            cmsDDeleteRequestHolder.mTransactionCredentialStatuses.length == 0) {
            cmsDDeleteRequest.setTransactionCredentialsStatus(new TransactionCredentialStatus[0]);
        } else {
            cmsDDeleteRequest.setTransactionCredentialsStatus(
                    cmsDDeleteRequestHolder.mTransactionCredentialStatuses);
        }

        cmsDDeleteRequest.setRequestId(getRequestId());

        ByteArray response = communicate(cmsDDeleteRequest.toJsonString(), true);

        GenericCmsDRemoteManagementResponse cmsDResponseFroDeleteToken =
                GenericCmsDRemoteManagementResponse.valueOf(response);

        if (!cmsDResponseFroDeleteToken.isSuccess()) {
            throw new ServiceException(McbpErrorCode.SERVER_ERROR, cmsDResponseFroDeleteToken
                    .getErrorDescription());
        }
        //Update the response host so that next request in same session use this host
        mSessionContext.updateResponseHost(cmsDResponseFroDeleteToken.getResponseHost());

        try {
            String digitizeCardId = getLdeRemoteManagementService()
                    .getCardIdFromTokenUniqueReference(cmsDDeleteRequestHolder
                                                               .tokenUniqueReference);

            if (cmsDResponseFroDeleteToken.isSuccess()) {
                getLdeRemoteManagementService().wipeDigitizedCard(ByteArray.of(digitizeCardId));
                getLdeRemoteManagementService()
                        .deleteAllTransactionCredentialStatus(digitizeCardId);
                getLdeRemoteManagementService().deleteTokenUniqueReference(digitizeCardId);
            }
            return RemoteManagementResponseHolder
                    .generateSuccessResponse(getCmsDRequestHolder(), cmsDResponseFroDeleteToken);

        } catch (InvalidInput | LdeNotInitialized exception) {
            throw new ServiceException(McbpErrorCode.LDE_ERROR, exception.getMessage(), exception);
        }
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + DELETE;
    }
}
