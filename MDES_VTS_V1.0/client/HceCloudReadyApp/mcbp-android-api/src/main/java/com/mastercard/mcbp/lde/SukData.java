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

package com.mastercard.mcbp.lde;

class SukData {

    final private byte[] mSuksInfo;
    final private String mSukId;
    final private byte[] mContactlessUmd;
    final private byte[] mContactlessMd;
    final private byte[] mRemoteUmd;
    final private byte[] mRemoteMd;
    final private byte[] mIdn;
    final private byte[] mAtc;
    final private String mHash;
    final private String mCardId;

    public SukData(final byte[] suksInfo, final String sukId, final byte[] contactlessUmd,
                   final byte[] contactlessMd, final byte[] remoteUmd, final byte[] remoteMd,
                   final byte[] idn, final byte[] atc, final String hash, final String cardId) {
        mSuksInfo = suksInfo;
        mSukId = sukId;
        mContactlessUmd = contactlessUmd;
        mContactlessMd = contactlessMd;
        mRemoteUmd = remoteUmd;
        mRemoteMd = remoteMd;
        mIdn = idn;
        mAtc = atc;
        mHash = hash;
        mCardId = cardId;
    }

    public byte[] getSuksInfo() {
        return mSuksInfo;
    }

    public String getSukId() {
        return mSukId;
    }

    public byte[] getContactlessUmd() {
        return mContactlessUmd;
    }

    public byte[] getContactlessMd() {
        return mContactlessMd;
    }

    public byte[] getRemoteUmd() {
        return mRemoteUmd;
    }

    public byte[] getRemoteMd() {
        return mRemoteMd;
    }

    public byte[] getIdn() {
        return mIdn;
    }

    public byte[] getAtc() {
        return mAtc;
    }

    public String getHash() {
        return mHash;
    }

    public String getCardId() {
        return mCardId;
    }

}
