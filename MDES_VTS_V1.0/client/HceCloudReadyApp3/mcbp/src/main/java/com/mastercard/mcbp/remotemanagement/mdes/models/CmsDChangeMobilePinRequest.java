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

/**
 * Represents the change mobile pin request for CMS-D.
 */
public class CmsDChangeMobilePinRequest extends GenericCmsDRemoteManagementRequest {

    @JSON(name = "tokenUniqueReference")
    private String tokenUniqueReference;

    @JSON(name = "currentMobilePin")
    private ByteArray currentMobilePin;

    @JSON(name = "newMobilePin")
    private ByteArray newMobilePin;

    @JSON(name = "taskId")
    private String taskId;

    public String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    public void setTokenUniqueReference(String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    public ByteArray getCurrentMobilePin() {
        return currentMobilePin;
    }

    public void setCurrentMobilePin(ByteArray currentMobilePin) {
        this.currentMobilePin = currentMobilePin;
    }

    public ByteArray getNewMobilePin() {
        return newMobilePin;
    }

    public void setNewMobilePin(ByteArray newMobilePin) {
        this.newMobilePin = newMobilePin;
    }

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(final String taskId) {
        this.taskId = taskId;
    }

    /**
     * Returns equivalent
     * {@link CmsDChangeMobilePinRequest} object
     * from given Json string
     *
     * @param jsonString Json string.
     * @return ChangeMobilePin object.
     */
    public static CmsDChangeMobilePinRequest valueOf(String jsonString) {
        return new JSONDeserializer<CmsDChangeMobilePinRequest>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, CmsDChangeMobilePinRequest.class);
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
            return "CmsDChangeMobilePinRequest{" +
                   "tokenUniqueReference='" + tokenUniqueReference + '\'' +
                   ", currentMobilePin='" + currentMobilePin + '\'' +
                   ", newMobilePin='" + newMobilePin + '\'' +
                   ", taskId='" + taskId + '\'' +
                   '}';
        } else {
            return "CmsDChangeMobilePinRequest";
        }

    }
}
