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

import com.mastercard.mcbp.remotemanagement.mdes.profile.DigitizedCardProfileMdes;
import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

public class CmsDProvisionResponse extends GenericCmsDRemoteManagementResponse {

    @JSON(name = "cardProfile")
    private DigitizedCardProfileMdes cardProfile;
    @JSON(name = "iccKek")
    private String iccKek;

    public CmsDProvisionResponse() {
        super();
    }

    public CmsDProvisionResponse(String responseId, DigitizedCardProfileMdes cardProfile,
                                 String iccKek, String responseHost) {
        setResponseId(responseId);
        this.cardProfile = cardProfile;
        this.iccKek = iccKek;
        setResponseHost(responseHost);
    }

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.models
     * .CmsDProvisionResponse}
     * object from given json string.
     *
     * @param content Json string as Byte Array
     * @return CmsDProvisionResponse object.
     */
    public static CmsDProvisionResponse valueOf(ByteArray content) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
        return new JSONDeserializer<CmsDProvisionResponse>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .use(Integer.class, new ObjectFactory() {
                    @Override
                    public Object instantiate(final ObjectBinder context, final Object value,
                                              final Type targetType, final Class targetClass) {
                        if (value instanceof Number) {
                            return (int) ((Number) value).doubleValue();
                        } else {
                            try {
                                Double parseDouble = Double.parseDouble(value.toString());
                                return parseDouble.intValue();
                            } catch (Exception e) {
                                throw context.cannotConvertValueToTargetType(value,
                                                                             Integer.class);
                            }
                        }
                    }
                })
                .deserialize(bfReader, CmsDProvisionResponse.class);
    }

    public DigitizedCardProfileMdes getCardProfile() {
        return cardProfile;
    }

    public void setCardProfile(DigitizedCardProfileMdes cardProfile) {
        this.cardProfile = cardProfile;
    }

    public String getIccKek() {
        return iccKek;
    }

    public void setIccKek(String iccKek) {
        this.iccKek = iccKek;
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
            return "CmsDProvisionResponse{" +
                   "responseId='" + getResponseId() + '\'' +
                   ", cardProfile=" + cardProfile +
                   ", iccKek='" + iccKek + '\'' +
                   ", responseHost='" + getResponseHost() + '\'' +
                   '}';
        } else {
            return "CmsDProvisionResponse";
        }
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
