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

public class RemoteManagementSessionData {

    @JSON(name = "version")
    private String version;

    @JSON(name = "sessionCode")
    private ByteArray sessionCode;

    @JSON(name = "expiryTimestamp")
    private String expiryTimestamp;

    @JSON(name = "validForSeconds")
    private int validForSeconds;

    @JSON(name = "pendingAction")
    private String pendingAction;

    @JSON(name = "tokenUniqueReference")
    private String tokenUniqueReference;

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.models
     * .RemoteManagementSessionData}
     * object from given json string.
     *
     * @param jsonString Json string.
     * @return RemoteManagementSessionData object.
     */
    public static RemoteManagementSessionData valueOf(final String jsonString) {
        return new JSONDeserializer<RemoteManagementSessionData>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, RemoteManagementSessionData.class);
    }

    public final String getVersion() {
        return version;
    }

    /**
     * @param version Version number of the Mobile Payment APIs. This is not
     *                related to the version of this document.Max Length: 16
     */
    public final void setVersion(String version) {
        this.version = version;
    }

    public final ByteArray getSessionCode() {
        return sessionCode;
    }

    /**
     * @param sessionCode The remote management session code used by the Mobile
     *                    Payment App to generate an authentication code when communicating
     *                    with the Credentials Management (Dedicated).Max Length: 64
     */
    public final void setSessionCode(ByteArray sessionCode) {
        this.sessionCode = sessionCode;
    }

    public final String getExpiryTimestamp() {
        return expiryTimestamp;
    }

    /**
     * @param expiryTimestamp The date/time when the remote management session code
     *                        will expire. In ISO 8601 format: yyyy-MM-dd'T'HH:mm:ssZ. Max Length:
     *                        28
     */
    public final void setExpiryTimestamp(String expiryTimestamp) {
        this.expiryTimestamp = expiryTimestamp;
    }

    public final int getValidForSeconds() {
        return validForSeconds;
    }

    /**
     * @param validForSeconds The number of seconds after which the remote management
     *                        session code will expire after first use. Max Length: 16
     */
    public final void setValidForSeconds(int validForSeconds) {
        this.validForSeconds = validForSeconds;
    }

    public final String getPendingAction() {
        return pendingAction;
    }

    /**
     * @param pendingAction The pending action requested by the Credentials
     *                      Management (Dedicated) for a Token on the Mobile Payment App. Max
     *                      Length:64
     */
    public final void setPendingAction(String pendingAction) {
        this.pendingAction = pendingAction;
    }

    public final String getTokenUniqueReference() {
        return tokenUniqueReference;
    }

    /**
     * @param tokenUniqueReference The Token Credential on which the action is requested.
     *                             Must be a valid reference as assigned by the Digitization
     *                             Service.
     *                             Max Length: 64
     */
    public final void setTokenUniqueReference(final String tokenUniqueReference) {
        this.tokenUniqueReference = tokenUniqueReference;
    }

    /**
     * Returns json string.
     *
     * @return Json string.
     */
    public final String toJsonString() {
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
        return "RemoteManagementSessionData [version=" + version
               + ", sessionCode=" + sessionCode + ", expiryTimestamp="
               + expiryTimestamp + ", validForSeconds=" + validForSeconds
               + ", pendingAction=" + pendingAction
               + ", tokenUniqueReference=" + tokenUniqueReference + "]";
        } else {
            return "RemoteManagementSessionData";
        }
    }

    @JSON(include = false)
    public final boolean isValid() {
        //In case of wallet change PIN/Reset Pin tokenUniqueReference will be null.
        return (pendingAction != null);

    }
}
