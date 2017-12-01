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

import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRequestSession;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create request session execute it and also process response.
 */
public class RequestSessionRequestHandler extends AbstractRequestHandler {

    public static final String REQUEST_SESSION = BASE_REQUEST + "/" + "requestSession";

    public RequestSessionRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                        SessionAwareAction sessionAwareAction,
                                        String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {
        CmsDRequestHolder cmsDRequestHolder = getCmsDRequestHolder();

        CmsDRequestSession cmsDRequestSession = new CmsDRequestSession();
        cmsDRequestSession.setPaymentAppInstanceId(cmsDRequestHolder.mPaymentAppInstanceId);
        cmsDRequestSession.setPaymentAppProviderId(cmsDRequestHolder.mPaymentAppProviderId);
        cmsDRequestSession.setMobileKeysetId(new String(getLdeRemoteManagementService()
                                                                .getMobileKeySetIdAsByteArray()
                                                                .getBytes()));
        communicate(cmsDRequestSession.toJsonString(), false);
        // We don't get anything from server in case of successful session request
        return RemoteManagementResponseHolder
                .generateSuccessResponse(getCmsDRequestHolder(), null);
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + REQUEST_SESSION;
    }
}
