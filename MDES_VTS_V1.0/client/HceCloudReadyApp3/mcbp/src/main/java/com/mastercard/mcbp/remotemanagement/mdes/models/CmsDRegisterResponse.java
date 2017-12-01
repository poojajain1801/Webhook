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

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class CmsDRegisterResponse extends GenericCmsDRemoteManagementResponse {

    @JSON(name = "mobileKeysetId")
    String mobileKeysetId;

    @JSON(name = "mobileKeys")
    MobileKeys mobileKeys;

    @JSON(name = "remoteManagementUrl")
    String remoteManagementUrl;

    public CmsDRegisterResponse() {
        super();
    }

    public CmsDRegisterResponse(String mobileKeySetId, MobileKeys mobileKeys,
                                String remoteManagementUrl) {
        super();
        this.mobileKeysetId = mobileKeySetId;
        this.mobileKeys = mobileKeys;
        this.remoteManagementUrl = remoteManagementUrl;
    }

    /**
     * Returns equivalent
     * {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterResponse} object
     * from given Json string
     *
     * @return CmsDRegisterResponse object.
     */
    public static CmsDRegisterResponse valueOf(ByteArray content) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
        return new JSONDeserializer<CmsDRegisterResponse>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(bfReader, CmsDRegisterResponse.class);
    }

    public String getMobileKeysetId() {
        return mobileKeysetId;
    }

    public void setMobileKeysetId(String mobileKeysetId) {
        this.mobileKeysetId = mobileKeysetId;
    }

    public MobileKeys getMobileKeys() {
        return mobileKeys;
    }

    public void setMobileKeys(MobileKeys mobileKeys) {
        this.mobileKeys = mobileKeys;
    }

    public String getRemoteManagementUrl() {
        return remoteManagementUrl;
    }

    public void setRemoteManagementUrl(String remoteManagementUrl) {
        this.remoteManagementUrl = remoteManagementUrl;
    }

    /**
     * Returns equivalent json string.
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
            return "CmsDRegisterResponse{" +
                   "mobileKeysetId='" + mobileKeysetId + '\'' +
                   ", mobileKeys=" + mobileKeys +
                   ", remoteManagementUrl='" + remoteManagementUrl + '\'' +
                   '}';
        } else {
            return "CmsDRegisterResponse";
        }
    }
}
