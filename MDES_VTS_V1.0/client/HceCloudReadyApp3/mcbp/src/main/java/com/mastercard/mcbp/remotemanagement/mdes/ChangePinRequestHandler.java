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

import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDChangeMobilePinRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDChangeMobilePinResponse;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.exceptions.datamanagement.InvalidInput;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create Change pin request execute it and also process response.
 */
public class ChangePinRequestHandler extends AbstractRequestHandler {

    public static final String CHANGE_MOBILE_PIN = BASE_REQUEST + "/" + "changeMobilePin";

    /**
     * ctor
     *
     * @param cmsDRequestHolder  the request holder
     * @param sessionAwareAction the session aware action
     * @param requestId          the request Id
     */
    public ChangePinRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                   SessionAwareAction sessionAwareAction,
                                   String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {

        try {
            CmsDChangeMobilePinRequest cmsDChangeMobilePinRequest = prepareChangePinRequest();
            final String tokenUniqueReference =
                    cmsDChangeMobilePinRequest.getTokenUniqueReference();
            CmsDChangeMobilePinResponse cmsDChangeMobilePinResponse =
                    executeChangePinRequest(cmsDChangeMobilePinRequest.toJsonString());

            return processMcbpChangePinOutput(cmsDChangeMobilePinResponse, tokenUniqueReference);

        } catch (InvalidInput exception) {
            throw new ServiceException(McbpErrorCode.LDE_ERROR, exception.getMessage(), exception);
        } catch (McbpCryptoException exception) {
            throw new ServiceException(McbpErrorCode.CRYPTO_ERROR, exception.getMessage(),
                                       exception);
        }
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + CHANGE_MOBILE_PIN;
    }

    /**
     * Prepare Change Pin request. Convert CmsDChangePinRequestHolder to MDES Change Pin
     * request structure.
     *
     * @throws McbpCryptoException
     * @throws InvalidInput
     */
    private CmsDChangeMobilePinRequest prepareChangePinRequest() throws McbpCryptoException,
            InvalidInput {
        CmsDChangePinRequestHolder cmsDChangePinRequestHolder =
                (CmsDChangePinRequestHolder) getCmsDRequestHolder();

        CmsDChangeMobilePinRequest cmsDChangeMobilePinRequest =
                new CmsDChangeMobilePinRequest();
        cmsDChangeMobilePinRequest.setTokenUniqueReference(
                cmsDChangePinRequestHolder.tokenUniqueReference);
        cmsDChangeMobilePinRequest.setRequestId(getRequestId());
        cmsDChangeMobilePinRequest.setCurrentMobilePin(cmsDChangePinRequestHolder.oldPin);
        cmsDChangeMobilePinRequest.setNewMobilePin(cmsDChangePinRequestHolder.newPin);
        cmsDChangeMobilePinRequest.setTaskId(cmsDChangePinRequestHolder.taskId);
        return cmsDChangeMobilePinRequest;
    }

    private CmsDChangeMobilePinResponse executeChangePinRequest(String inputData)
            throws HttpException, ServiceException {
        ByteArray response = communicate(inputData, true);
        return CmsDChangeMobilePinResponse
                .valueOf(response);
    }

    private RemoteManagementResponseHolder
    processMcbpChangePinOutput(final CmsDChangeMobilePinResponse cmsDChangeMobilePinResponse,
                               final String tokenUniqueReference)
            throws ServiceException, InvalidInput {
        if (!cmsDChangeMobilePinResponse.isSuccess()) {
            throw new ServiceException(McbpErrorCode.SERVER_ERROR, cmsDChangeMobilePinResponse
                    .getErrorDescription());
        }
        //Update the response host so that next request in same session use this host
        mSessionContext.updateResponseHost(cmsDChangeMobilePinResponse.getResponseHost());

        String digitizedCardId = null;
        if (tokenUniqueReference != null) {
            digitizedCardId = getLdeRemoteManagementService()
                    .getCardIdFromTokenUniqueReference(tokenUniqueReference);
        }


        if (cmsDChangeMobilePinResponse.getResult().equalsIgnoreCase("SUCCESS")) {
            //For Wallet
            if (tokenUniqueReference == null) {
                //Delete all SUKs & transaction credential status regardless of card
                getLdeRemoteManagementService().wipeAllSuks();
                getLdeRemoteManagementService().wipeAllTransactionCredentialStatus();
            } else {
                //For a card
                getLdeRemoteManagementService().wipeDcSuk(ByteArray.of(digitizedCardId));
                getLdeRemoteManagementService()
                        .deleteAllTransactionCredentialStatus(digitizedCardId);
            }
            // #MCBP_LOG_BEGIN
            mLogger.d("Change Pin Complete:");
            // #MCBP_LOG_END
        }
        return RemoteManagementResponseHolder
                .generateSuccessResponse(getCmsDRequestHolder(),
                                         cmsDChangeMobilePinResponse);
    }
}
