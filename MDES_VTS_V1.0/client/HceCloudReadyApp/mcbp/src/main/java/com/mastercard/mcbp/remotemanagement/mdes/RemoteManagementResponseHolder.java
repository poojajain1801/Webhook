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

import com.mastercard.mcbp.remotemanagement.mdes.models.GenericCmsDRemoteManagementResponse;
import com.mastercard.mobile_api.utils.exceptions.http.ErrorContext;

/**
 * Encapsulate response of CMS-D service along with request params and exception if any occurred.
 * Note:Reason why we are caching the request as CMSD is not sending token unique reference in
 * response
 */
public class RemoteManagementResponseHolder {

    /**
     * Data class to represent common attribute in CMSD request
     */
    public CmsDRequestHolder mCmsDRequestHolder;

    /**
     * Possible response of CMSD request
     */
    public RemoteManagementHandler.ServiceResult mServiceResult;

    /**
     * Generic CMSD response message
     */
    public GenericCmsDRemoteManagementResponse mCmsdResponse;

    /**
     * Error context to verify if any exception occurred or not
     */
    public ErrorContext mErrorContext;

    /**
     * Generate the remote management response holder to wait for session.
     *
     * @param cmsDRequestHolder cmsDRequestHolder instance
     * @return RemoteManagementResponseHolder instance with waiting service result
     */
    public static RemoteManagementResponseHolder generateWaitingForSessionResponse(
            CmsDRequestHolder cmsDRequestHolder, ErrorContext errorContext) {
        RemoteManagementResponseHolder remoteManagementResponseHolder = new
                RemoteManagementResponseHolder();
        remoteManagementResponseHolder.mCmsDRequestHolder = cmsDRequestHolder;
        remoteManagementResponseHolder.mServiceResult = RemoteManagementHandler.ServiceResult
                .WAITING_FOR_SESSION;
        remoteManagementResponseHolder.mErrorContext = errorContext;
        return remoteManagementResponseHolder;
    }

    /**
     * Generate the remote management response holder for successful remote operation.
     *
     * @param cmsDRequestHolder CmsDRequestHolder instance
     * @param response          GenericCmsDRemoteManagementResponse instance
     * @return RemoteManagementResponseHolder instance with successful operation
     */
    public static RemoteManagementResponseHolder generateSuccessResponse(
            CmsDRequestHolder cmsDRequestHolder, GenericCmsDRemoteManagementResponse response) {
        RemoteManagementResponseHolder result = new RemoteManagementResponseHolder();
        result.mCmsDRequestHolder = cmsDRequestHolder;
        result.mCmsdResponse = response;
        result.mServiceResult = RemoteManagementHandler.ServiceResult.OK;
        return result;
    }

    /**
     * Check whether the remote management operation is successful or not
     *
     * @return true if operation successful otherwise return false
     */
    public boolean isSuccessful() {
        return (this.mErrorContext == null);
    }

}
