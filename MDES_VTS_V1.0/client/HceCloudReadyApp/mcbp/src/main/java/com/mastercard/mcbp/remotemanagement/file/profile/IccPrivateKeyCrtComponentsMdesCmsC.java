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

package com.mastercard.mcbp.remotemanagement.file.profile;

import flexjson.JSON;

class IccPrivateKeyCrtComponentsMdesCmsC {
    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getqValue() {
        return qValue;
    }

    public void setqValue(String qValue) {
        this.qValue = qValue;
    }

    public String getDpValue() {
        return dpValue;
    }

    public void setDpValue(String dpValue) {
        this.dpValue = dpValue;
    }

    public String getDqValue() {
        return dqValue;
    }

    public void setDqValue(String dqValue) {
        this.dqValue = dqValue;
    }

    public String getuValue() {
        return uValue;
    }

    public void setuValue(String uValue) {
        this.uValue = uValue;
    }

    @JSON(name = "p")
    private String pValue;

    @JSON(name = "q")
    private String qValue;


    @JSON(name = "dp")
    private String dpValue;

    @JSON(name = "dq")
    private String dqValue;

    @JSON(name = "u")
    private String uValue;

}