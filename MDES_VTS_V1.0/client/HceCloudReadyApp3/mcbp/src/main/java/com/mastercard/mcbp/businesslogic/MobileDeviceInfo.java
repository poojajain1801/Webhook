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

package com.mastercard.mcbp.businesslogic;

import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;

import flexjson.JSON;

/**
 * Container for Device Information including OS information, Device
 * capabilities etc.<br>
 * <br>
 */
public abstract class MobileDeviceInfo {
    /**
     * Mobile device Os name
     */
    @JSON(name = "osName")
    private String mOsName;
    /**
     * Mobile device Os version
     */
    @JSON(name = "osVersion")
    private String mOsVersion;
    /**
     * Mobile device Os firmware build
     */
    @JSON(name = "osFirmwareBuild")
    private String mOsFirmwareBuild;
    /**
     * Mobile device supported nfc or not
     */
    @JSON(name = "nfcSupport")
    private String mNfcSupport;
    /**
     * Mobile device mac address
     */
    @JSON(name = "macAddress")
    private String mMacAddress;
    /**
     * Mobile device mManufacturer name
     */
    @JSON(name = "manufacturer")
    private String mManufacturer;
    /**
     * Mobile device mModel name
     */
    @JSON(name = "model")
    private String mModel;
    /**
     * Mobile device mProduct name
     */
    @JSON(name = "product")
    private String mProduct;
    /**
     * Mobile device screen size
     */
    @JSON(name = "screenSize")
    private String mScreenSize;
    /**
     * Mobile device Os Unique Identifier
     */
    @JSON(name = "osUniqueIdentifier")
    private String mOsUniqueIdentifier;
    /**
     * Mobile device mImei
     */
    @JSON(name = "imei")
    private String mImei;

    public String getOsName() {
        return mOsName;
    }

    public void setOsName(String osName) {
        this.mOsName = osName;
    }

    public String getOsVersion() {
        return mOsVersion;
    }

    public void setOsVersion(String osVersion) {
        this.mOsVersion = osVersion;
    }

    public String getOsFirmwareBuild() {
        return mOsFirmwareBuild;
    }

    public void setOsFirmwareBuild(String osFirmwareBuild) {
        this.mOsFirmwareBuild = osFirmwareBuild;
    }

    public String getNfcSupport() {
        return mNfcSupport;
    }

    public void setNfcSupport(String nfcSupport) {
        this.mNfcSupport = nfcSupport;
    }

    public String getMacAddress() {
        return mMacAddress;
    }

    public void setMacAddress(String macAddress) {
        this.mMacAddress = macAddress;
    }

    public String getManufacturer() {
        return mManufacturer;
    }

    public void setManufacturer(String manufacturer) {
        this.mManufacturer = manufacturer;
    }

    public String getModel() {
        return mModel;
    }

    public void setModel(String model) {
        this.mModel = model;
    }

    public String getProduct() {
        return mProduct;
    }

    public void setProduct(String product) {
        this.mProduct = product;
    }

    public String getScreenSize() {
        return mScreenSize;
    }

    public void setScreenSize(String screenSize) {
        this.mScreenSize = screenSize;
    }

    public String getOsUniqueIdentifier() {
        return mOsUniqueIdentifier;
    }

    public void setOsUniqueIdentifier(String osUniqueIdentifier) {
        this.mOsUniqueIdentifier = osUniqueIdentifier;
    }

    public String getImei() {
        return mImei;
    }

    public void setImei(String imei) {
        this.mImei = imei;
    }

    public abstract ByteArray calculateDeviceFingerPrint() throws McbpCryptoException;
}
