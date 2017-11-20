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

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;

import flexjson.JSON;

public class SingleUseKeyContent {
    /**
     * The hash of the Single Use Key. Currently used for legacy reasons only
     */
    @JSON(name = "hash")
    private ByteArray hash;

    /**
     * The application transaction counter as Byte Array
     */
    @JSON(name = "atc")
    private ByteArray atc;

    /**
     * The Contactless User and Mobile Device (UMD) Single Use Key
     */
    @JSON(name = "contactlessUmdSingleUseKey")
    private ByteArray sukContactlessUmd;

    /**
     * The Contactless Mobile Device (MD) Session Key
     */
    @JSON(name = "contactlessMdSessionKey")
    private ByteArray sessionKeyContactlessMd;

    /**
     * The Digital Secure Remote Payment User and Mobile Device (UMD) Single Use Key
     */
    @JSON(name = "dsrpUmdSingleUseKey")
    private ByteArray sukRemotePaymentUmd;

    /**
     * The Digital Secure Remote Payment Mobile Device (MD) Session Key
     */
    @JSON(name = "dsrpMdSessionKey")
    private ByteArray sessionKeyRemotePaymentMd;

    /**
     * Information associated with this single use key
     */
    @JSON(name = "info")
    private ByteArray info;

    /**
     * The ICC Dynamic Number
     */
    @JSON(name = "idn")
    private ByteArray idn;

    /**
     * Get the hash of this single use key
     *
     * @return The Sha-256 hash of the single use key as Byte Array
     */
    public ByteArray getHash() {
        return hash;
    }

    /**
     * Set the hash of this single use key
     *
     * @param hash the SHA-256 hash of this use key
     */
    public void setHash(ByteArray hash) {
        this.hash = hash;
    }

    /**
     * Get the Application Transaction Counter (ATC) of this single use key
     *
     * @return The ATC of the single use key as Byte Array
     */
    public ByteArray getAtc() {
        return atc;
    }

    /**
     * Set the Application Transaction Counter (ATC)  of this single use key
     *
     * @param atc the ATC of this set of keys
     */
    public void setAtc(ByteArray atc) {
        this.atc = atc;
    }

    /**
     * Get the Single Use Key for Contactless (User and Mobile Device)
     *
     * @return The Single Use Key UMD for Contactless as Byte Array
     */
    public ByteArray getSukContactlessUmd() {
        return sukContactlessUmd;
    }

    /**
     * Set the Single Use Key for Contactless (User and Mobile Device)
     *
     * @param sukContactlessUmd The Single Use Key for Contactless (User and Mobile Device)
     */
    public void setSukContactlessUmd(ByteArray sukContactlessUmd) {
        this.sukContactlessUmd = sukContactlessUmd;
    }

    /**
     * Set the Single Use Key for Contactless (User and Mobile Device)
     *
     * @param sukContactlessUmd The Single Use Key for Contactless (User and Mobile Device)
     */
    public void setSukContactlessUmd(byte[] sukContactlessUmd) {
        this.sukContactlessUmd = ByteArray.of(sukContactlessUmd);
    }

    /**
     * Get the Session Key for Contactless (Mobile Device)
     *
     * @return The Single Use Key MD for Contactless as Byte Array
     */
    public ByteArray getSessionKeyContactlessMd() {
        return sessionKeyContactlessMd;
    }

    /**
     * Set the Session Key for Contactless (Mobile Device)
     *
     * @param sessionKeyContactlessMd The Session Key for Contactless (Mobile Device)
     */
    public void setSessionKeyContactlessMd(ByteArray sessionKeyContactlessMd) {
        this.sessionKeyContactlessMd = sessionKeyContactlessMd;
    }

    /**
     * Set the Session Key for Contactless (Mobile Device)
     *
     * @param sessionKeyContactlessMd The Session Key for Contactless (Mobile Device)
     */
    public void setSessionKeyContactlessMd(byte[] sessionKeyContactlessMd) {
        this.sessionKeyContactlessMd = ByteArray.of(sessionKeyContactlessMd);
    }

    /**
     * Get the Single Use Key for Remote Payment (User and Mobile Device)
     *
     * @return The Single Use Key UMD for Remote Payment as Byte Array
     */
    public ByteArray getSukRemotePaymentUmd() {
        return sukRemotePaymentUmd;
    }

    /**
     * Set the Single Use Key for Remote Payment (User and Mobile Device)
     *
     * @param sukRemotePaymentUmd The Single Use Key for Remote Payment (User and Mobile Device)
     */
    public void setSukRemotePaymentUmd(ByteArray sukRemotePaymentUmd) {
        this.sukRemotePaymentUmd = sukRemotePaymentUmd;
    }

    /**
     * Set the Single Use Key for Remote Payment (User and Mobile Device)
     *
     * @param sukRemotePaymentUmd The Single Use Key for Remote Payment (User and Mobile Device)
     */
    public void setSukRemotePaymentUmd(byte[] sukRemotePaymentUmd) {
        this.sukRemotePaymentUmd = ByteArray.of(sukRemotePaymentUmd);
    }

    /**
     * Get the Session Key for Remote Payment (Mobile Device)
     *
     * @return The Single Use Key MD for Remote Payment as Byte Array
     */
    public ByteArray getSessionKeyRemotePaymentMd() {
        return sessionKeyRemotePaymentMd;
    }

    /**
     * Set the Session Key for Remote Payment (Mobile Device)
     *
     * @param sessionKeyRemotePaymentMd The Session Key for Remote Payment (Mobile Device)
     */
    public void setSessionKeyRemotePaymentMd(ByteArray sessionKeyRemotePaymentMd) {
        this.sessionKeyRemotePaymentMd = sessionKeyRemotePaymentMd;
    }

    /**
     * Set the Session Key for Remote Payment (Mobile Device)
     *
     * @param sessionKeyRemotePaymentMd The Session Key for Remote Payment (Mobile Device)
     */
    public void setSessionKeyRemotePaymentMd(byte[] sessionKeyRemotePaymentMd) {
        this.sessionKeyRemotePaymentMd = ByteArray.of(sessionKeyRemotePaymentMd);
    }

    /**
     * Get the ICC Dynamic Number for this set of keys
     *
     * @return the ICC Dynamic Number
     */
    public ByteArray getIdn() {
        return idn;
    }

    /**
     * Set the ICC Dynamic Number for this set of keys
     *
     * @param idn the ICC Dynamic Number
     */
    public void setIdn(ByteArray idn) {
        this.idn = idn;
    }

    /**
     * Get the info byte associated to this set of keys
     *
     * @return The info byte as ByteArray
     */
    public ByteArray getInfo() {
        return info;
    }

    /**
     * Set the info byte associated to this set of keys
     *
     * @param info The info as ByteArray
     */
    public void setInfo(ByteArray info) {
        this.info = info;
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
            return "SingleUseKeyContent [hash=" + hash + ", atc=" + atc +
                   ", sukContactlessUmd=" + sukContactlessUmd
                   + ", sessionKeyContactlessMd=" + sessionKeyContactlessMd
                   + ", sukRemotePaymentUmd=" + sukRemotePaymentUmd
                   + ", sessionKeyRemotePaymentMd=" + sessionKeyRemotePaymentMd
                   + ", info=" + info
                   + ", idn=" + idn + "]";
        } else {
            return "SingleUseKeyContent";
        }
    }
}