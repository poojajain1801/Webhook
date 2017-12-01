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
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CmsDResponse {

    @JSON(name = "errorCode")
    private String errorCode;

    @JSON(name = "errorDescription")
    private String errorDescription;

    @JSON(name = "encryptedData")
    private String encryptedData;

    public CmsDResponse() {
        super();
    }

    public CmsDResponse(String errorCode, String errorDescription, String encryptedData) {
        this.errorCode = errorCode;
        this.errorDescription = errorDescription;
        this.encryptedData = encryptedData;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorDescription() {
        return errorDescription;
    }

    public void setErrorDescription(String errorDescription) {
        this.errorDescription = errorDescription;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
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
            return "CmsDResponse [errorCode=" + errorCode + ", errorDescription=" + errorDescription
                   + ", encryptedData=" + encryptedData + "]";
        } else {
            return "CmsDResponse";
        }
    }

    /**
     * Returns equivalent
     * {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDResponse}
     * object from given json string.
     *
     * @param jsonContent Json content to be convert in to CmsDResponse object.
     * @return CmsDResponse object.
     */
    public static CmsDResponse valueOf(String jsonContent) {
        return new JSONDeserializer<CmsDResponse>()
                .deserialize(jsonContent, CmsDResponse.class);
    }

    /**
     * Returns CmsDResponse
     *
     * @param data input json data
     * @return Instance of CmsDResponse
     */
    public static CmsDResponse valueOf(ByteArray data) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(data.getBytes()));
        return new JSONDeserializer<CmsDResponse>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(bfReader, CmsDResponse.class);
    }

    /**
     * Returns equivalent Json string.
     *
     * @return Json string
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(this);
    }

    public boolean isSuccess() {
        return (errorCode == null || errorDescription == null);
    }
}
