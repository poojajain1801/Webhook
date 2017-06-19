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

package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.remotemanagement.mdes.models.RemoteManagementSessionData;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;

import org.apache.commons.codec.binary.Base64;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Represents the remote notification message from CMS-D.
 */
public class RnsMessage {

    @JSON(name = "responseHost")
    private String responseHost;

    @JSON(name = "registrationData")
    private PaymentAppRegistrationData registrationData;

    @JSON(name = "mobileKeysetId")
    private String mobileKeysetId;

    @JSON(name = "encryptedData")
    private String encryptedData;

    /**
     * Non Parameterize constructor
     */
    public RnsMessage() {
    }

    /**
     * Parameterize constructor
     *
     * @param responseHost Remote Host.
     * @param registrationData Registration data.
     * @param mobileKeysetId Mobile key set id.
     * @param encryptedData encrypted data.
     */
    public RnsMessage(String responseHost, PaymentAppRegistrationData registrationData,
                      String mobileKeysetId, String encryptedData) {
        this.responseHost = responseHost;
        this.registrationData = registrationData;
        this.mobileKeysetId = mobileKeysetId;
        this.encryptedData = encryptedData;
    }

    public final String getResponseHost() {
        return responseHost;
    }

    public final void setResponseHost(String responseHost) {
        this.responseHost = responseHost;
    }

    public final PaymentAppRegistrationData getRegistrationData() {
        return registrationData;
    }

    public final void setRegistrationData(PaymentAppRegistrationData registrationData) {
        this.registrationData = registrationData;
    }

    public final String getMobileKeysetId() {
        return mobileKeysetId;
    }

    public final void setMobileKeysetId(String mobileKeysetId) {
        this.mobileKeysetId = mobileKeysetId;
    }

    public final String getEncryptedData() {
        return encryptedData;
    }

    public final ByteArray getEncryptedDataInByteArray() {
        final byte[] decodeBase64EncryptedData =
                Base64.decodeBase64(encryptedData.getBytes(Charset.defaultCharset()));

        final ByteArray decodedData = ByteArray.of(decodeBase64EncryptedData);
        Utils.clearByteArray(decodeBase64EncryptedData);

        return decodedData;
    }

    public final boolean hasRegistrationData() {
        return registrationData != null;
    }

    public final void setEncryptedData(String encryptedData) {
        this.encryptedData = encryptedData;
    }

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.RnsMessage}
     * object from given json string.
     *
     * @param jsonString Json string.
     * @return RnsMessage object.
     */
    public static RnsMessage valueOf(final String jsonString) {
        return new JSONDeserializer<RnsMessage>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(jsonString, RnsMessage.class);
    }

    /**
     * Returns equivalent {@link com.mastercard.mcbp.remotemanagement.mdes.RnsMessage}
     * object from given json string.
     *
     * @param data Json string.
     * @return RnsMessage object.
     */
    public static RnsMessage valueOf(ByteArray data) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(data.getBytes()));
        return new JSONDeserializer<RnsMessage>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(bfReader, RnsMessage.class);
    }

    /**
     * Returns json string.
     *
     * @return Json string.
     */

    public final String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class").transform(new ByteArrayTransformer(), ByteArray.class);
        return serializer.serialize(this);
    }

    @Override
    public final String toString() {
        return "NotificationMessageData{" +
                "responseHost='" + responseHost + '\'' +
                ", registrationData=" + registrationData +
                ", mobileKeysetId='" + mobileKeysetId + '\'' +
                ", encryptedData='" + encryptedData + '\'' +
                '}';
    }

    /**
     *
     * @param macKey The MAC Key used to verify the data
     * @param transportKey The Transport key used to decrypt the data
     * @param cryptoService The Crypto Service to be used for decryption
     * @return the RemoteManagementSession data if successful, null otherwise (e.g. the data
     * could not be decrypted).
     */
    public final
    RemoteManagementSessionData getRemoteManagementSessionData(final ByteArray macKey,
                                                               final ByteArray transportKey,
                                                               final CryptoService cryptoService) {

        final ByteArray encryptedData = getEncryptedDataInByteArray();

        final ByteArray decryptedServiceResponse;
        try {
            decryptedServiceResponse =
                    cryptoService.decryptNotificationData(encryptedData, macKey, transportKey);
        } catch (final McbpCryptoException e) {
            // Something went wrong... we return null
            return null;
        }
        Utils.clearByteArray(transportKey);
        Utils.clearByteArray(macKey);

        // Remove 16 bytes random data
        final int length = decryptedServiceResponse.getLength();
        final ByteArray serviceResponse = decryptedServiceResponse.copyOfRange(16, length);
        Utils.clearByteArray(decryptedServiceResponse);

        // Create a JSON string

        final String remoteManagementSessionDataJson = new String(serviceResponse.getBytes());
        Utils.clearByteArray(serviceResponse);

        // de-serialization
        return RemoteManagementSessionData.valueOf(remoteManagementSessionDataJson);
    }
}
