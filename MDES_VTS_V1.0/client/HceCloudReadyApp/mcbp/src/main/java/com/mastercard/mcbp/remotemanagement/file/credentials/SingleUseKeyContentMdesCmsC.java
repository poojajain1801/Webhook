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
package com.mastercard.mcbp.remotemanagement.file.credentials;

import flexjson.JSON;

public class SingleUseKeyContentMdesCmsC {

    @JSON(name = "atc")
    private int atc;

    @JSON(name = "contactlessUmdSessionKey")
    private String sessionKeyContactlessUmd;

    @JSON(name = "contactlessMdSessionKey")
    private String sessionKeyContactlessMd;

    @JSON(name = "dsrpUmdSessionKey")
    private String sessionKeyDsrpUmd;

    @JSON(name = "dsrpMdSessionKey")
    private String sessionKeyDsrpMd;

    @JSON(name = "idn")
    private String idn;

    public int getAtc() {
        return atc;
    }

    public void setAtc(int atc) {
        this.atc = atc;
    }

    public String getSessionKeyContactlessUmd() {
        return sessionKeyContactlessUmd;
    }

    public void setSessionKeyContactlessUmd(String sessionKeyContactlessUmd) {
        this.sessionKeyContactlessUmd = sessionKeyContactlessUmd;
    }

    public String getSessionKeyContactlessMd() {
        return sessionKeyContactlessMd;
    }

    public void setSessionKeyContactlessMd(String sessionKeyContactlessMd) {
        this.sessionKeyContactlessMd = sessionKeyContactlessMd;
    }

    public String getSessionKeyDsrpUmd() {
        return sessionKeyDsrpUmd;
    }

    public void setSessionKeyDsrpUmd(String sessionKeyDsrpUmd) {
        this.sessionKeyDsrpUmd = sessionKeyDsrpUmd;
    }

    public String getSessionKeyDsrpMd() {
        return sessionKeyDsrpMd;
    }

    public void setSessionKeyDsrpMd(String sessionKeyDsrpMd) {
        this.sessionKeyDsrpMd = sessionKeyDsrpMd;
    }

    public String getIdn() {
        return idn;
    }

    public void setIdn(String idn) {
        this.idn = idn;
    }
}