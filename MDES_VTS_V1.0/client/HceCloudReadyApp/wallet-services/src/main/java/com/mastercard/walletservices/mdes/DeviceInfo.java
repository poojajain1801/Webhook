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

package com.mastercard.walletservices.mdes;

import org.apache.commons.codec.binary.Hex;

import java.nio.ByteBuffer;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

public class DeviceInfo {

    @JSON(name = "deviceName")
    private String deviceName;

    @JSON(name = "serialNumber")
    private String serialNumber;

    @JSON(name = "deviceType")
    private String deviceType;

    @JSON(name = "osName")
    private String osName;

    @JSON(name = "imei")
    private String imei;

    @JSON(name = "msisdn")
    private String msisdn;

    @JSON(name = "nfcCapable")
    private String nfcCapable;

    public DeviceInfo() {
        super();
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getSerialNumber() {
        return serialNumber;
    }

    public void setSerialNumber(String serialNumber) {
        this.serialNumber = serialNumber;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getImei() {
        return imei;
    }

    public void setImei(String imei) {
        this.imei = imei;
    }

    public String getMsisdn() {
        return msisdn;
    }

    public void setMsisdn(String msisdn) {
        this.msisdn = msisdn;
    }

    public String getNfcCapable() {
        return nfcCapable;
    }

    public void setNfcCapable(String nfcCapable) {
        this.nfcCapable = nfcCapable;
    }

    /**
     * Return ByteArray of device finger print.
     *
     * @return The Device Finger Pring as String
     */
    public String getDeviceFingerprint() {


        byte[] dataBytes = (this.deviceName +
                            this.deviceType +
                            this.imei +
                            this.msisdn +
                            this.nfcCapable +
                            this.osName).getBytes();

        ByteBuffer byteBuffer = ByteBuffer.wrap(new byte[dataBytes.length]);
        byteBuffer.put(dataBytes);

        // Create MessageDigest using SHA-256 algorithm Added required
        MessageDigest messageDigest;

        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }

        // Hash the result
        byte[] hash = messageDigest.digest(byteBuffer.array());

        // Return Hex
        return new String(Hex.encodeHex(hash));
    }

    /**
     * Returns equivalent
     * {@link DeviceInfo}
     * object from given json string.
     *
     * @param jsonContent The Device Info as JSON String
     * @return DeviceInfo object
     */
    public static DeviceInfo valueOf(String jsonContent) {
        return new JSONDeserializer<DeviceInfo>()
                .deserialize(jsonContent, DeviceInfo.class);
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

}
