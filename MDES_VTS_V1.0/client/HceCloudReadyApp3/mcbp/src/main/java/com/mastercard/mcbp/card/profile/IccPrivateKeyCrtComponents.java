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

package com.mastercard.mcbp.card.profile;

import com.mastercard.mobile_api.bytes.ByteArray;

import flexjson.JSON;

/**
 * The ICC Private Key Crt Component stores the key parameters for the ICC Private Key generation
 */
public final class IccPrivateKeyCrtComponents {
    public ByteArray getP() {
        return p;
    }

    public void setP(ByteArray p) {
        this.p = p;
    }

    public ByteArray getQ() {
        return q;
    }

    public void setQ(ByteArray q) {
        this.q = q;
    }

    public ByteArray getDp() {
        return dp;
    }

    public void setDp(ByteArray dp) {
        this.dp = dp;
    }

    public ByteArray getDq() {
        return dq;
    }

    public void setDq(ByteArray dq) {
        this.dq = dq;
    }

    public ByteArray getU() {
        return u;
    }

    public void setU(ByteArray u) {
        this.u = u;
    }

    @JSON(name = "p")
    private ByteArray p;

    @JSON(name = "q")
    private ByteArray q;

    @JSON(name = "dp")
    private ByteArray dp;

    @JSON(name = "dq")
    private ByteArray dq;

    @JSON(name = "u")
    private ByteArray u;
}
