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

import com.mastercard.mobile_api.utils.json.JsonUtils;

import java.io.Serializable;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class ServiceResponse implements Serializable {

    private static final long serialVersionUID = 1255454743594073774L;

    /**
     * Service Request Id
     */
    private String serviceRequestId;
    /**
     * Service Response code
     */
    private String serviceResponseCode;
    /**
     * Service Data
     */
    private String serviceData;

    public ServiceResponse() {
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getServiceResponseCode() {
        return serviceResponseCode;
    }

    public void setServiceResponseCode(String serviceResponseCode) {
        this.serviceResponseCode = serviceResponseCode;
    }

    public String getServiceData() {
        return serviceData;
    }

    public void setServiceData(String serviceData) {
        this.serviceData = serviceData;
    }

    public String toJsonString() {
        return new JsonUtils<ServiceResponse>(ServiceResponse.class).toJsonString(this);
    }
}
