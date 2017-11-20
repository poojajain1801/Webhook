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

import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class CmsToken implements Serializable {

    private static final long serialVersionUID = 320807141541105487L;

    @JSON(name = "serviceRequest")
    private ServiceRequest serviceRequest;

    @JSON(name = "refC2m")
    private int refCmsToMpa = 0;

    @JSON(name = "m2cCounter")
    private int mpaToCmsCounter = 0;

    public CmsToken() {
    }

    public int getRefCmsToMpa() {
        return refCmsToMpa;
    }

    public void setRefCmsToMpa(int refCmsToMpa) {
        this.refCmsToMpa = refCmsToMpa;
    }

    public int getMpaToCmsCounter() {
        return mpaToCmsCounter;
    }

    public void setMpaToCmsCounter(int mpaToCmsCounter) {
        this.mpaToCmsCounter = mpaToCmsCounter;
    }

    public void incrementRefCmsToMpa() {
        this.refCmsToMpa++;
    }

    public void incrementMpaToCms() {
        this.mpaToCmsCounter++;
    }

    public ServiceRequest getServiceRequest() {
        return serviceRequest;
    }

    public void setServiceRequest(ServiceRequest serviceRequest) {
        this.serviceRequest = serviceRequest;
    }

    public void wipe() {
        setServiceRequest(null);
    }

    public static CmsToken valueOf(byte[] content) {
        return new JsonUtils<CmsToken>(CmsToken.class).valueOf(content);
    }

    public String toJsonString() {
        return new JsonUtils<CmsToken>(CmsToken.class).toJsonString(this);
    }

}
