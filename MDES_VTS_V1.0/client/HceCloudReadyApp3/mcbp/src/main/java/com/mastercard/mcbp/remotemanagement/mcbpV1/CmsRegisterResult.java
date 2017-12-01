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

package com.mastercard.mcbp.remotemanagement.mcbpV1;

/**
 * Response to
 * {@link com.mastercard.mcbp.remotemanagement.CmsService#registerToCms(String, String)}
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class CmsRegisterResult {

    private final RegisterResultStatus status;
    private CmsActivationData activationData;
    private String errorMessage;

    public CmsRegisterResult(RegisterResultStatus status) {
        this.status = status;
    }

    public CmsRegisterResult(RegisterResultStatus status, String errorMessage) {
        this(status);
        this.errorMessage = errorMessage;
    }

    public CmsRegisterResult(CmsActivationData cmsActivationData) {
        this(RegisterResultStatus.SUCCESS);
        this.activationData = cmsActivationData;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public CmsActivationData getActivationData() {
        return activationData;
    }

    public RegisterResultStatus getStatus() {
        return status;
    }

}
