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
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CmsDRequest {

    @JSON(name = "mobileKeysetId")
    private String mobileKeysetId;

    @JSON(name = "authenticationCode")
    private ByteArray authenticationCode;

    @JSON(name = "encryptedData")
    private String encryptedData;

    public CmsDRequest(String mobileKeysetId, ByteArray authenticationCode, String encryptedData) {
        super();
        this.mobileKeysetId = mobileKeysetId;
        this.authenticationCode = authenticationCode;
        this.encryptedData = encryptedData;
    }

    public CmsDRequest() {
        super();
    }

    public String getMobileKeysetId() {
        return mobileKeysetId;
    }

    public void setMobileKeysetId(String mobileKeysetId) {
        this.mobileKeysetId = mobileKeysetId;
    }

    public ByteArray getAuthenticationCode() {
        return authenticationCode;
    }

    public void setAuthenticationCode(ByteArray authenticationCode) {
        this.authenticationCode = authenticationCode;
    }

    public String getEncryptedData() {
        return encryptedData;
    }

    public void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRequest}
     * object from given json string.
     * @param jsonString Json string.
     * @return CmsDRequest object.
     */
    public static CmsDRequest valueOf(final String jsonString) {
        return new JSONDeserializer<CmsDRequest>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, CmsDRequest.class);
    }

    /**
     * Returns json string.
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
            return "CmsDRequest [mobileKeysetId=" + mobileKeysetId + ", " +
                   "authenticationCode=" + authenticationCode + ", encryptedData="
                   + encryptedData + "]";
        } else {
            return "CmsDRequest";
        }
    }

}
