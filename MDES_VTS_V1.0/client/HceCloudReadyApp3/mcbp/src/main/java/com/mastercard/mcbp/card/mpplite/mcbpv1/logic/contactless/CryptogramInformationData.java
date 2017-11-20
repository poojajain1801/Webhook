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

package com.mastercard.mcbp.card.mpplite.mcbpv1.logic.contactless;

/**
 * Indicates the type of cryptogram for EMV mode transactions.
 */
public class CryptogramInformationData {
    /**
     * The Cryptogram Information Data
     */
    private byte mCryptogramInformationData;

    /**
     * Create an empty Cryptogram Information data object
     */
    public CryptogramInformationData() {
        mCryptogramInformationData = 0x00;
    }

    /**
     * Indicate Online decision (ARQC to be returned)
     */
    public void indicateOnlineDecision() {
        mCryptogramInformationData = (byte)0x80;
    }

    /**
     * Indicate that an AAC is to be returned
     */
    public void indicateDecline() {
        mCryptogramInformationData = (byte)0x00;
    }

    /**
     * Get the value of the Cid
     * @return The Cid value as byte
     */
    public byte getValue() {
        return mCryptogramInformationData;
    }
}
