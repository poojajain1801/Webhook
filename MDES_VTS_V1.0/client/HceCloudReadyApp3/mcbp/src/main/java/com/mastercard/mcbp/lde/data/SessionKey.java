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

package com.mastercard.mcbp.lde.data;

import com.mastercard.mobile_api.bytes.ByteArray;

public class SessionKey {

    private final String mId;
    private final ByteArray mSessionKeyUmd;
    private final ByteArray mSessionKeyMd;
    private final byte mInfo;
    private final ByteArray mAtc;
    private final ByteArray mIdn;

    /**
     * Default constructor.
     */
    public SessionKey(String id, ByteArray sukUmd, ByteArray sukMd, byte info, ByteArray atc,
                      ByteArray idn) {

        this.mId = id;
        this.mSessionKeyUmd = sukUmd;
        this.mSessionKeyMd = sukMd;
        this.mInfo = info;
        this.mAtc = atc;
        this.mIdn = idn;
    }

    public String getId() {
        return mId;
    }

    public ByteArray getIdn() {
        return mIdn;
    }

    public ByteArray getSessionKeyUmd() {
        return mSessionKeyUmd;
    }

    public ByteArray getSessionKeyMd() {
        return mSessionKeyMd;
    }

    public byte getInfo() {
        return mInfo;
    }

    public ByteArray getAtc() {
        return mAtc;
    }

}
