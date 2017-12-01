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

package com.mastercard.mcbp.card.credentials;

import com.mastercard.mcbp.lde.data.SessionKey;
import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import flexjson.JSON;

/**
 * Single Use Key container
 */
public class SingleUseKey implements SingleUseKeyWrapper {
    /**
     * Used to request the type of session keys
     */
    public enum Type {
        CONTACTLESS,
        REMOTE_PAYMENT
    }

    /**
     * Single Use Key id
     */
    @JSON(name = "id")
    private ByteArray id;
    /**
     * Digitized Card Id
     */
    @JSON(name = "digitizedCardId")
    private ByteArray digitizedCardId;
    /**
     * Content of the Single Use Key
     */
    @JSON(name = "content")
    private com.mastercard.mcbp.card.credentials.SingleUseKeyContent content;

    /**
     * Used by flex json
     */
    public SingleUseKey() {
        content = new com.mastercard.mcbp.card.credentials.SingleUseKeyContent();
    }

    /**
     * Get the id of the Single Use Key
     *
     * @return the Single Use Key Id as Byte Array
     */
    public ByteArray getId() {
        return id;
    }

    /**
     * Set the id of this Single Use Key.
     * <p/>
     * The method is mainly used by flexjson to deserialize the object
     *
     * @param id The Single Use Key id
     */
    public void setId(ByteArray id) {
        this.id = id;
    }

    /**
     * Return the Digitized Card Id for which this key has been generated
     *
     * @return The Digitized Card Id as Byte Array
     */
    public ByteArray getDigitizedCardId() {
        return this.digitizedCardId;
    }

    /**
     * Set the Digitized Card Id for which this key has been generated
     *
     * @param digitizedCardId The Digitized Card Id
     */
    public void setDigitizedCardId(ByteArray digitizedCardId) {
        this.digitizedCardId = digitizedCardId;
    }

    /**
     * Get the content associated with the Single Use Key
     *
     * @return the Single Use Key content object
     */
    public com.mastercard.mcbp.card.credentials.SingleUseKeyContent getContent() {
        return content;
    }

    /**
     * Set the content of this Single Use Key.
     * <p/>
     * The method is mainly used by flexjson to deserialize the object
     *
     * @param content The Single Use Key id
     */
    public void setContent(com.mastercard.mcbp.card.credentials.SingleUseKeyContent content) {
        this.content = content;
    }

    /**
     * Get the next available single use key as session key
     *
     * @param type specify whether the contactless or remote payment single use key should
     *             be returned.
     * @return The next contactless single use key is the parameter is true, the next remote
     * payment single use key otherwise. The key is returned as session key.
     */
    public SessionKey getSessionKey(final Type type) {
        ByteArray idn = content.getIdn();
        SessionKey sessionKey;
        if (type == Type.CONTACTLESS) {
            ByteArray sukContactlessUmd = content.getSukContactlessUmd();
            ByteArray sessionKeyContactlessMd = content.getSessionKeyContactlessMd();
            // Get a copy of each credentials value
            sessionKey = new SessionKey(
                    id.toHexString(),
                    ByteArray.of(sukContactlessUmd),
                    ByteArray.of(sessionKeyContactlessMd),
                    content.getInfo().getByte(0),
                    ByteArray.of(content.getAtc()),
                    ByteArray.of(idn));
        } else {
            ByteArray sukRemotePaymentUmd = content.getSukRemotePaymentUmd();
            ByteArray sessionKeyRemotePaymentMd = content.getSessionKeyRemotePaymentMd();
            // Get a copy of each credentials value
            sessionKey = new SessionKey(
                    id.toHexString(),
                    ByteArray.of(sukRemotePaymentUmd),
                    ByteArray.of(sessionKeyRemotePaymentMd),
                    content.getInfo().getByte(0),
                    ByteArray.of(content.getAtc()),
                    ByteArray.of(idn));
        }

        Utils.clearByteArray(content.getSukContactlessUmd());
        Utils.clearByteArray(content.getSessionKeyContactlessMd());
        Utils.clearByteArray(content.getSukRemotePaymentUmd());
        Utils.clearByteArray(content.getSessionKeyRemotePaymentMd());
        Utils.clearByteArray(idn);

        return sessionKey;
    }

    /**
     * Implements the Wrapper function so that this class can be used by other components
     */
    @Override
    @JSON(include = false)
    public String getCardId() {
        return digitizedCardId.toHexString();
    }

    /**
     * Implements the Wrapper function so that this class can be used by other components
     */
    @Override
    public SingleUseKey toSingleUseKey() {
        return this;
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
            return "SingleUseKey{" + "id=" + id + ", digitizedCardId=" + digitizedCardId +
                   ", content=" + content + '}';
        } else {
            return "SingleUseKey";
        }
    }

    /**
     * Equals implementation of the object.
     */
    @Override
    public boolean equals(final Object obj) {
        SingleUseKey singleUseKey = (SingleUseKey) obj;
        if (singleUseKey.getContent().getAtc().toHexString()
                        .equalsIgnoreCase(this.getContent().getAtc().toHexString()) &&
            singleUseKey.getCardId().equalsIgnoreCase(this.getCardId()) &&
            singleUseKey.getId().toHexString().equalsIgnoreCase(this.getId().toHexString())) {
            return true;
        }
        return false;
    }

}
