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

package com.mastercard.mcbp.remotemanagement.mdes.profile;

import flexjson.JSON;
/**
 * Represents the icc private key crt components in card profile of mdes.
 */
public class IccPrivateKeyCrtComponents {
    @JSON(name = "p")
    private String p;

    @JSON(name = "q")
    private String q;

    @JSON(name = "dp")
    private String dp;

    @JSON(name = "dq")
    private String dq;

    @JSON(name = "u")
    private String u;

    public String getP() {
        return p;
    }

    public void setP(final String p) {
        this.p = p;
    }

    public String getQ() {
        return q;
    }

    public void setQ(final String q) {
        this.q = q;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(final String dp) {
        this.dp = dp;
    }

    public String getDq() {
        return dq;
    }

    public void setDq(final String dq) {
        this.dq = dq;
    }

    public String getU() {
        return u;
    }

    public void setU(final String u) {
        this.u = u;
    }
}