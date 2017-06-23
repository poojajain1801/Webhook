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

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Encapsulate the data which will be needed to process request to delete a Token Credential.
 */
public class CmsDDeleteRequest extends GenericCmsDRemoteManagementRequest {

    @JSON(name = "tokenUniqueReference")
    private String tokenUniqueReference;

    @JSON(name = "transactionCredentialsStatus")
    private TransactionCredentialStatus[] transactionCredentialsStatus;

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
     * Returns equivalent
     * {@link CmsDDeleteRequest} object
     * from given Json string
     *
     * @param jsonString Json string.
     * @return CmsDDeleteRequest object.
     */
    public static CmsDDeleteRequest valueOf(String jsonString) {
        return new JSONDeserializer<CmsDDeleteRequest>()
                .deserialize(jsonString, CmsDDeleteRequest.class);
    }

    /**
     * Returns json string.
     *
     * @return Json string.
     */
    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(this);
    }

}
