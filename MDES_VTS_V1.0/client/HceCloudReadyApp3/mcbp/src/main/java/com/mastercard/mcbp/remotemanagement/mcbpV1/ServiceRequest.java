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

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import com.mastercard.mobile_api.utils.json.ByteObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteTransformer;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class ServiceRequest implements Serializable {

    private static final long serialVersionUID = 52084021652089521L;

    @JSON(name = "serviceRequestID")
    private String serviceRequestId;

    @JSON(name = "serviceID")
    private String serviceId;

    @JSON(name = "serviceData")
    private String serviceData;

    public ServiceRequest() {
    }

    public String getServiceRequestId() {
        return serviceRequestId;
    }

    public void setServiceRequestId(String serviceRequestId) {
        this.serviceRequestId = serviceRequestId;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public String getServiceData() {
        return serviceData;
    }

    public void setServiceData(String serviceData) {
        this.serviceData = serviceData;
    }

    public static ServiceRequest valueOf(byte[] content) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(content));
        return new JSONDeserializer<ServiceRequest>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .use(byte.class, new ByteObjectFactory())
                .deserialize(bfReader, ServiceRequest.class);
    }

    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        serializer.transform(new ByteArrayTransformer(), ByteArray.class);
        serializer.transform(new ByteTransformer(), byte.class);
        // Skip null values
        serializer.transform(new SuppressNullTransformer(), void.class);
        return serializer.serialize(null);
    }


}
