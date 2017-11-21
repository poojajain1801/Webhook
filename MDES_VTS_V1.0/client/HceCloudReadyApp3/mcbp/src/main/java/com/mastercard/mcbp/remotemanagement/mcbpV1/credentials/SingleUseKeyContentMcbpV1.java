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

import com.mastercard.mcbp.utils.BuildInfo;
import com.mastercard.mobile_api.bytes.ByteArray;

import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated

public class SingleUseKeyContentMcbpV1 {

    @JSON(name = "hash")
    private ByteArray hash;

    @JSON(name = "ATC")
    private ByteArray atc;

    @JSON(name = "SUK_CL_UMD")
    private ByteArray sukContactlessUmd;

    @JSON(name = "SK_CL_MD")
    private ByteArray sessionKeyContactlessMd;

    @JSON(name = "SUK_RP_UMD")
    private ByteArray sukRemotePaymentUmd;

    @JSON(name = "SK_RP_MD")
    private ByteArray sessionKeyRemotePaymentMd;

    @JSON(name = "SUKInfo")
    private ByteArray sukInfo;

    @JSON(name = "IDN")
    private ByteArray idn;

    public static final int BYTE_VALUE_FOR_CL_RP = 56; // "00111000"

    public static final int BYTE_VALUE_FOR_CL = 48; //"00110000"

    public static final int BYTE_VALUE_FOR_RP = 40; //"00101000"

    public SingleUseKeyContentMcbpV1() {
    }

    public ByteArray getHash() {
        return hash;
    }

    public void setHash(ByteArray hash) {
        this.hash = hash;
    }

    public ByteArray getAtc() {
        return atc;
    }

    public void setAtc(ByteArray atc) {
        this.atc = atc;
    }

    public ByteArray getSukContactlessUmd() {
        return sukContactlessUmd;
    }

    public void setSukContactlessUmd(ByteArray sukContactlessUmd) {
        this.sukContactlessUmd = sukContactlessUmd;
    }

    public ByteArray getSessionKeyContactlessMd() {
        return sessionKeyContactlessMd;
    }

    public void setSessionKeyContactlessMd(ByteArray sessionKeyContactlessMd) {
        this.sessionKeyContactlessMd = sessionKeyContactlessMd;
    }

    public ByteArray getSukRemotePaymentUmd() {
        return sukRemotePaymentUmd;
    }

    public void setSukRemotePaymentUmd(ByteArray sukRemotePaymentUmd) {
        this.sukRemotePaymentUmd = sukRemotePaymentUmd;
    }

    public ByteArray getSessionKeyRemotePaymentMd() {
        return sessionKeyRemotePaymentMd;
    }

    public void setSessionKeyRemotePaymentMd(ByteArray sessionKeyRemotePaymentMd) {
        this.sessionKeyRemotePaymentMd = sessionKeyRemotePaymentMd;
    }

    public ByteArray getSukInfo() {
        return sukInfo;
    }

    public void setSukInfo(ByteArray sukInfo) {
        this.sukInfo = sukInfo;
    }

    public ByteArray getIdn() {
        return idn;
    }

    public void setIdn(ByteArray idn) {
        this.idn = idn;
    }

    public boolean isValid() {
        // check size of each elements
        int sukInfo = Integer.parseInt(this.sukInfo.toHexString(), 16);
        if (sukInfo == BYTE_VALUE_FOR_CL_RP) {
            if (sukContactlessUmd == null || sukContactlessUmd.getLength() != 16) {
                return false;
            }
            if (sessionKeyContactlessMd == null || sessionKeyContactlessMd.getLength() != 16) {
                return false;
            }
            if (sukRemotePaymentUmd == null || sukRemotePaymentUmd.getLength() != 16) {
                return false;
            }
            if (sessionKeyRemotePaymentMd == null || sessionKeyRemotePaymentMd.getLength() != 16) {
                return false;
            }
        } else if (sukInfo == BYTE_VALUE_FOR_CL) {
            if (sukContactlessUmd == null || sukContactlessUmd.getLength() != 16) {
                return false;
            }
            if (sessionKeyContactlessMd == null || sessionKeyContactlessMd.getLength() != 16) {
                return false;
            }
        } else if (sukInfo == BYTE_VALUE_FOR_RP) {
            if (sukRemotePaymentUmd == null || sukRemotePaymentUmd.getLength() != 16) {
                return false;
            }
            if (sessionKeyRemotePaymentMd == null || sessionKeyRemotePaymentMd.getLength() != 16) {
                return false;
            }
        }
        return !(idn == null || idn.getLength() != 8) && !(atc == null || atc.getLength() != 2);
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
            return "SingleUseKeyContentMcbpV1 [hash=" + hash + ", atc="
                   + atc + ", sukContactlessUmd=" + sukContactlessUmd
                   + ", sessionKeyContactlessMd=" + sessionKeyContactlessMd
                   + ", sukRemotePaymentUmd=" + sukRemotePaymentUmd
                   + ", sessionKeyRemotePaymentMd=" + sessionKeyRemotePaymentMd
                   + ", sukInfo=" + sukInfo
                   + ", idn=" + idn + "]";
        } else {
            return "SingleUseKeyContentMcbpV1";
        }
    }

}