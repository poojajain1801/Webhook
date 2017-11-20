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

package com.mastercard.mcbp.remotemanagement.mcbpV1.models;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class SendInformationRequest {

    @JSON(name = "userId")
    public String userId;

    @JSON(name = "mobileId")
    public String mobileId;

    @JSON(name = "osName")
    public String osName;

    @JSON(name = "osVersion")
    public String osVersion;

    @JSON(name = "osFirmwarebuild")
    public String osFirmwarebuild;

    @JSON(name = "manufacturer")
    public String manufacturer;

    @JSON(name = "model")
    public String model;

    @JSON(name = "product")
    public String product;

    @JSON(name = "osUniqueIdentifier")
    public String osUniqueIdentifier;

    @JSON(name = "imei")
    public String imei;

    @JSON(name = "macAddress")
    public String macAddress;

    @JSON(name = "nfcSupport")
    public String nfcSupport;

    @JSON(name = "screenSize")
    public String screenSize;

    @JSON(name = "mobilePin")
    public String mobilePin;


    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mcbpV1.models
     * .SendInformationRequest}
     * object from given json string.
     *
     * @param jsonString Json string.
     * @return SendInformationRequest object.
     */
    public static SendInformationRequest valueOf(final String jsonString) {
        return new JSONDeserializer<SendInformationRequest>().deserialize(jsonString,
                SendInformationRequest.class);
    }

    /**
     * Returns json string.
     *
     * @return Json string.
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        serializer.transform(new ByteArrayTransformer(), ByteArray.class);
        // Skip null values
        serializer.transform(new SuppressNullTransformer(), void.class);
        return serializer.serialize(this);
    }
}
