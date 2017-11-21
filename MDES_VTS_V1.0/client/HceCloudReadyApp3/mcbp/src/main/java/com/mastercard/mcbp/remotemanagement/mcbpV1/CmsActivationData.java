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
package com.mastercard.mcbp.remotemanagement.mcbpV1;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class CmsActivationData {

    @JSON(name = "cmsMPAId")
    private String cmsMpaId;

    @JSON(name = "mConfKey")
    private ByteArray confidentialityKey;

    @JSON(name = "mMacKey")
    private ByteArray macKey;

    @JSON(name = "notificationUrl")
    private String notificationUrl;

    @JSON(name = "issuerConfig")
    private CmsValueName[] issuerConfig;

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public void setNotificationUrl(String notificationUrl) {
        this.notificationUrl = notificationUrl;
    }

    public CmsValueName[] getIssuerConfig() {
        return issuerConfig;
    }

    public void setIssuerConfig(CmsValueName[] issuerConfig) {
        this.issuerConfig = issuerConfig;
    }

    public ByteArray getConfidentialityKey() {
        return confidentialityKey;
    }

    public void setConfidentialityKey(ByteArray confidentialityKey) {
        this.confidentialityKey = confidentialityKey;
    }

    public ByteArray getMacKey() {
        return macKey;
    }

    public void setMacKey(ByteArray macKey) {
        this.macKey = macKey;
    }

    public void wipe() {
        Utils.clearByteArray(confidentialityKey);
        Utils.clearByteArray(macKey);
        if (notificationUrl != null) {
            notificationUrl = "";
        }
        if (getCmsMpaId() != null) {
            setCmsMpaId(null);
        }
        if (issuerConfig != null && issuerConfig.length > 0) {
            for (CmsValueName anIssuerConfig : issuerConfig) {
                anIssuerConfig.wipe();
            }
        }
    }

    public String getCmsMpaId() {
        return cmsMpaId;
    }

    public void setCmsMpaId(String cmsMpaId) {
        this.cmsMpaId = cmsMpaId;
    }

    public static CmsActivationData valueOf(final byte[] content) {
        return new JsonUtils<CmsActivationData>(CmsActivationData.class).valueOf(content);
    }
}
