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

import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredential;
import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.lang.reflect.Type;
import java.util.Arrays;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
import flexjson.ObjectBinder;
import flexjson.ObjectFactory;

/**
 * Represents the replenish response for CMS-D.
 */
public class CmsDReplenishResponse extends GenericCmsDRemoteManagementResponse {

    @JSON(name = "transactionCredentials")
    private TransactionCredential[] transactionCredentials;

    public CmsDReplenishResponse() {
        super();
    }

    public TransactionCredential[] getTransactionCredentials() {
        return transactionCredentials;
    }

    public void setTransactionCredentials(TransactionCredential[] transactionCredentials) {
        this.transactionCredentials = transactionCredentials;
    }

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.models
     * .CmsDReplenishResponse}
     * object from given json string.
     *
     * @param content Json.
     * @return CmsDReplenishResponse object.
     */
    public static CmsDReplenishResponse valueOf(ByteArray content) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(content.getBytes()));
        return new JSONDeserializer<CmsDReplenishResponse>()
                .use(ByteArray.class, new ObjectFactory() {

                    public Object instantiate(ObjectBinder objectBinder,
                                              Object o, Type type,
                                              Class aClass) {
                        try {
                            return ByteArray
                                    .of(o.toString());
                        } catch (Exception e) {
                            return null;
                        }
                    }
                }).deserialize(bfReader,
                               CmsDReplenishResponse.class);
    }

    /**
     * Returns json string.
     *
     * @return Json string.
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
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
            return "CmsDReplenishResponse{" +
                   "transactionCredentials=" + Arrays.toString(transactionCredentials) +
                   ", responseId='" + getResponseId() + '\'' +
                   ", responseHost='" + getResponseHost() + '\'' +
                   '}';

        } else {
            return "CmsDReplenishResponse";
        }
    }
}
