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

import com.mastercard.mcbp.remotemanagement.mdes.models.GetTaskStatusRequest;
import com.mastercard.mcbp.remotemanagement.mdes.models.GetTaskStatusResponse;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;
import com.mastercard.mobile_api.utils.exceptions.http.ServiceException;

/**
 * Create get task request execute it and also process response.
 */
public class GetTaskStatusRequestHandler extends AbstractRequestHandler {

    public static final String GET_TASK_STATUS = BASE_REQUEST + "/" + "getTaskStatus";

    public GetTaskStatusRequestHandler(CmsDRequestHolder cmsDRequestHolder,
                                       SessionAwareAction sessionAwareAction,
                                       String requestId) {
        super(cmsDRequestHolder, sessionAwareAction, requestId);
    }

    @Override
    public RemoteManagementResponseHolder handle() throws HttpException, ServiceException {
        CmsDGetTaskStatusStatusHolder cmsDGetTaskStatusStatusHolder =
                (CmsDGetTaskStatusStatusHolder) getCmsDRequestHolder();
        GetTaskStatusRequest getTaskStatusRequest = new GetTaskStatusRequest();
        getTaskStatusRequest.setRequestId(getRequestId());
        getTaskStatusRequest.setTaskId(cmsDGetTaskStatusStatusHolder.taskId);
        ByteArray response = communicate(getTaskStatusRequest.toJsonString(), true);

        GetTaskStatusResponse getTaskStatusResponse = GetTaskStatusResponse.valueOf(response);

        if (!getTaskStatusResponse.isSuccess()) {
            throw new ServiceException(McbpErrorCode.SERVER_ERROR, getTaskStatusResponse
                    .getErrorDescription());
        }
        //Update the response host so that next request in same session use this host
        mSessionContext.updateResponseHost(getTaskStatusResponse.getResponseHost());

        return RemoteManagementResponseHolder
                .generateSuccessResponse(getCmsDRequestHolder(), getTaskStatusResponse);
    }

    @Override
    public String getRequestUrl() {
        return getBaseUrl() + GET_TASK_STATUS;
    }
}
