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

package com.mastercard.mcbp.card.mpplite.mcbpv1.output;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

public final class CryptogramOutput {

    /**
     * Application Transaction Counter
     */
    private final ByteArray mAtc;
    /**
     * Issuer Application Data
     */
    private final ByteArray mIssuerApplicationData;
    /**
     * Cryptogram
     */
    private final ByteArray mCryptogram;
    /**
     * Cryptogram Information Data
     */
    private final byte mCid;

    public CryptogramOutput(final ByteArray atc,
                            final ByteArray issuerApplicationData,
                            final ByteArray cryptogram,
                            final byte cid) {
        this.mAtc = atc;
        this.mIssuerApplicationData = issuerApplicationData;
        this.mCryptogram = cryptogram;
        this.mCid = cid;
    }

    public final ByteArray getCryptogram() {
        return mCryptogram;
    }

    public final byte getCid() {
        return mCid;
    }

    public final ByteArray getAtc() {
        return mAtc;
    }

    public final ByteArray getIssuerApplicationData() {
        return mIssuerApplicationData;
    }

    /**
     * wipe all sensitive data.
     */
    public final void wipe() {
        Utils.clearByteArray(mAtc);
        Utils.clearByteArray(mCryptogram);
        Utils.clearByteArray(mIssuerApplicationData);
    }

}
