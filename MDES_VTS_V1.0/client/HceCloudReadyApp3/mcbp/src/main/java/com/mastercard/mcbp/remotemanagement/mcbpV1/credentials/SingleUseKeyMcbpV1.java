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

package com.mastercard.mcbp.remotemanagement.mcbpV1.credentials;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mcbp.card.credentials.SingleUseKeyWrapper;
import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class SingleUseKeyMcbpV1 implements SingleUseKeyWrapper {

    @JSON(include = false)
    public static final int DC_ID_LENGTH = 34;
    @JSON(include = false)
    public static final int DC_ID_OFFSET = 0;

    @JSON(name = "DC_SUK_ID")
    private ByteArray digitizedCardSingleUseKeyId;

    @JSON(name = "DC_SUK_CONTENT")
    private SingleUseKeyContentMcbpV1 singleUseKeyContentMcbpV1;

    /**
     * Used by flex json
     */
    public SingleUseKeyMcbpV1() {
        singleUseKeyContentMcbpV1 = new SingleUseKeyContentMcbpV1();
    }

    public ByteArray getDigitizedCardSingleUseKeyId() {
        return digitizedCardSingleUseKeyId;
    }

    public void setDigitizedCardSingleUseKeyId(ByteArray digitizedCardSingleUseKeyId) {
        this.digitizedCardSingleUseKeyId = digitizedCardSingleUseKeyId;
    }

    public SingleUseKeyContentMcbpV1 getSingleUseKeyContentMcbpV1() {
        return singleUseKeyContentMcbpV1;
    }

    public void setSingleUseKeyContentMcbpV1(SingleUseKeyContentMcbpV1 singleUseKeyContentMcbpV1) {
        this.singleUseKeyContentMcbpV1 = singleUseKeyContentMcbpV1;
    }

    public String getDcId() {
        return this.digitizedCardSingleUseKeyId.toHexString().substring(DC_ID_OFFSET, DC_ID_LENGTH);
    }

    @Override
    public String getCardId() {
        return getDcId();
    }

    @Override
    public SingleUseKey toSingleUseKey() {
        assert this.singleUseKeyContentMcbpV1.isValid();

        SingleUseKey singleUseKey = new SingleUseKey();
        singleUseKey.setId(this.digitizedCardSingleUseKeyId);

        byte[] digitizedCardId = new byte[DC_ID_LENGTH / 2];
        System.arraycopy(this.digitizedCardSingleUseKeyId.getBytes(), DC_ID_OFFSET,
                         digitizedCardId, 0, DC_ID_LENGTH / 2);
        singleUseKey.setDigitizedCardId(ByteArray.of(digitizedCardId));

        SingleUseKeyContent singleUseKeyContent = new SingleUseKeyContent();

        singleUseKeyContent.setAtc(this.singleUseKeyContentMcbpV1.getAtc());
        singleUseKeyContent.setIdn(this.singleUseKeyContentMcbpV1.getIdn());
        singleUseKeyContent.setSessionKeyContactlessMd(
                this.singleUseKeyContentMcbpV1.getSessionKeyContactlessMd());
        singleUseKeyContent.setSessionKeyRemotePaymentMd(
                this.singleUseKeyContentMcbpV1.getSessionKeyRemotePaymentMd());
        singleUseKeyContent
                .setSukContactlessUmd(this.singleUseKeyContentMcbpV1.getSukContactlessUmd());
        singleUseKeyContent
                .setSukRemotePaymentUmd(this.singleUseKeyContentMcbpV1.getSukRemotePaymentUmd());
        singleUseKeyContent.setInfo(this.singleUseKeyContentMcbpV1.getSukInfo());
        singleUseKeyContent.setHash(this.singleUseKeyContentMcbpV1.getHash());

        singleUseKey.setContent(singleUseKeyContent);

        return singleUseKey;
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
            return "SingleUseKeyMcbpV1{" +
                   "digitizedCardSingleUseKeyId=" + digitizedCardSingleUseKeyId +
                   ", singleUseKeyContentMcbpV1=" + singleUseKeyContentMcbpV1 +
                   '}';
        } else {
            return "SingleUseKeyMcbpV1";
        }
    }

    public static SingleUseKeyMcbpV1 valueOf(byte[] content) {
        return new JsonUtils<SingleUseKeyMcbpV1>(SingleUseKeyMcbpV1.class).valueOf(content);
    }

}
