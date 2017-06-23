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

package com.mastercard.mcbp.remotemanagement.mdes.models;


import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Represents the change mobile pin response for CMS-D.
 */
public class CmsDChangeMobilePinResponse extends GenericCmsDRemoteManagementResponse {

    @JSON(name = "result")
    private String result;

    @JSON(name = "mobilePinTriesRemaining")
    private int mobilePinTriesRemaining;

    public CmsDChangeMobilePinResponse() {
        super();
    }

    public CmsDChangeMobilePinResponse(String responseId, String responseHost, String result,
                                       int mobilePinTriesRemaining) {
        setResponseId(responseId);
        setResponseHost(responseHost);
        this.result = result;
        this.mobilePinTriesRemaining = mobilePinTriesRemaining;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getMobilePinTriesRemaining() {
        return mobilePinTriesRemaining;
    }

    public void setMobilePinTriesRemaining(int mobilePinTriesRemaining) {
        this.mobilePinTriesRemaining = mobilePinTriesRemaining;
    }

    /**
     * Returns equivalent
     * {@link CmsDChangeMobilePinResponse} object
     * from given Json string
     *
     * @param content The Response as Byte Array of the JSON String
     * @return ChangePinResponse object.
     */
    public static CmsDChangeMobilePinResponse valueOf(ByteArray content) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
        return new JSONDeserializer<CmsDChangeMobilePinResponse>()
                .deserialize(bfReader, CmsDChangeMobilePinResponse.class);
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
        return serializer.serialize(this);
    }

    /**
     * Returns a string representation of the object.
     *
     * @return Returns debug information for the class in debug mode.
     * In release mode it returns only the class name, so that sensitive information is never
     * returned by this method.
     */
    @Override
    public String toString() {
        if (BuildInfo.isDebugEnabled()) {
            return "CmsDChangeMobilePinResponse{" +
                   "result='" + result + '\'' +
                   ", mobilePinTriesRemaining=" + mobilePinTriesRemaining +
                   '}';
        } else {
            return "CmsDChangeMobilePinResponse";
        }

    }
}
