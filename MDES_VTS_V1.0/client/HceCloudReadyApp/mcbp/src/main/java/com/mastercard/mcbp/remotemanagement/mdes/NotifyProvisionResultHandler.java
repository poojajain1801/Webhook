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

import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDNotifyProvisioningRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.GenericCmsDRemoteManagementResponse;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create Notify provision result request execute it and also process response.
 */
public class NotifyProvisionResultHandler extends AbstractRequestHandler {

    public static final String NOTIFY_PROVISIONING_RESULT = BASE_REQUEST + "/"
                                                            + "notifyProvisioningResult";

    public NotifyProvisionResultHandler(CmsDRequestHolder cmsDRequestHolder,
                                        SessionAwareAction sessionAwareAction,
                                        String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {
        CmsDNotifyProvisionResultRequestHolder cmsDNotifyProvisionResultRequestHolder =
                (CmsDNotifyProvisionResultRequestHolder) getCmsDRequestHolder();

        CmsDNotifyProvisioningRequest cmsDNotifyProvisioningRequest = new
                CmsDNotifyProvisioningRequest();
        cmsDNotifyProvisioningRequest.setRequestId(getRequestId());
        cmsDNotifyProvisioningRequest.setTokenUniqueReference
                (cmsDNotifyProvisionResultRequestHolder.tokenUniqueReference);
        cmsDNotifyProvisioningRequest
                .setErrorCode(cmsDNotifyProvisionResultRequestHolder.errorCode);
        cmsDNotifyProvisioningRequest
                .setErrorDescription(cmsDNotifyProvisionResultRequestHolder.errorDescription);
        cmsDNotifyProvisioningRequest.setResult(cmsDNotifyProvisionResultRequestHolder.result);

        ByteArray response = communicate(cmsDNotifyProvisioningRequest.toJsonString(), true);
        GenericCmsDRemoteManagementResponse genericCmsDRemoteManagementResponse =
                GenericCmsDRemoteManagementResponse.valueOf(response);

        if (!genericCmsDRemoteManagementResponse.isSuccess()) {
            throw new ServiceException(McbpErrorCode.SERVER_ERROR,
                                       genericCmsDRemoteManagementResponse
                                               .getErrorDescription());
        }
        //Update the response host so that next request in same session use this host
        mSessionContext.updateResponseHost(genericCmsDRemoteManagementResponse.getResponseHost());

        if (cmsDNotifyProvisionResultRequestHolder.result.equalsIgnoreCase("SUCCESS")) {
            //Since a card has been provisioned we need to update cache which holds all card.
            try {
                getLdeRemoteManagementService().updateDigitizedCardTemplate();
            } catch (InvalidInput e) {
                throw new ServiceException(McbpErrorCode.LDE_ERROR,
                                           genericCmsDRemoteManagementResponse
                                                   .getErrorDescription());
            } catch (McbpCryptoException e) {
                throw new ServiceException(McbpErrorCode.CRYPTO_ERROR,
                                           genericCmsDRemoteManagementResponse
                                                   .getErrorDescription());
            }
        }
        return RemoteManagementResponseHolder
                .generateSuccessResponse(getCmsDRequestHolder(),
                                         genericCmsDRemoteManagementResponse);
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + NOTIFY_PROVISIONING_RESULT;
    }
}
