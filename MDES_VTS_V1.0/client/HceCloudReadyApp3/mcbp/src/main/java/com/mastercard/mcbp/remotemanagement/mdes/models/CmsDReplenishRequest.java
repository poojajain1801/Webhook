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

import java.util.Arrays;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Represents the replenish request for CMS-D.
 */
public class CmsDReplenishRequest extends GenericCmsDRemoteManagementRequest {

    @JSON(name = "tokenUniqueReference")
    String tokenUniqueReference;

    @JSON(name = "transactionCredentialsStatus")
    TransactionCredentialStatus[] transactionCredentialsStatus;

    public CmsDReplenishRequest() {
        super();
    }

    public CmsDReplenishRequest(String tokenUniqueReference, TransactionCredentialStatus[]
            transactionCredentialsStatus) {
        super();
        this.tokenUniqueReference = tokenUniqueReference;
        this.transactionCredentialsStatus = transactionCredentialsStatus;
    }

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    public TransactionCredentialStatus[] getTransactionCredentialsStatus() {
        return transactionCredentialsStatus;
    }

    public void setTransactionCredentialsStatus(TransactionCredentialStatus[]
                                                        transactionCredentialsStatus) {
        this.transactionCredentialsStatus = transactionCredentialsStatus;
    }

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.models
     * .CmsDReplenishRequest}
     * object from given json string.
     *
     * @param jsonString Json string.
     * @return CmsDReplenishRequest object.
     */
    public static CmsDReplenishRequest valueOf(final String jsonString) {
        return new JSONDeserializer<CmsDReplenishRequest>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, CmsDReplenishRequest.class);
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
        return serializer.deepSerialize(this);
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
            return "CmsDReplenishRequest [requestId=" + getRequestId() + ", " +
                   "tokenUniqueReference=" + tokenUniqueReference + ", transactionCredentialsStatus="
                   + Arrays.toString(transactionCredentialsStatus) + "]";
        } else {
            return "CmsDReplenishRequest";
        }
    }

}
