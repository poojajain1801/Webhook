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

package com.mastercard.mcbp.transactiondecisionmanager.terminal;

import com.mastercard.mcbp.card.mobilekernel.CryptogramType;

/**
 * Specify the Terminal Technology
 */
public enum TerminalTechnology {
    CONTACTLESS_EMV,
    CONTACTLESS_MAGSTRIPE,
    REMOTE_DSRP_EMV,
    REMOTE_DSRP_UCAF;

    /**
     * Utility function to get the Terminal Technology for Remote Payment
     *
     * @param cryptogramType The Cryptogram Type
     * @return The Terminal Technology
     */
    public static TerminalTechnology forRemotePayment(final CryptogramType cryptogramType) {
        return cryptogramType == CryptogramType.DE55 ?
               TerminalTechnology.REMOTE_DSRP_EMV : TerminalTechnology.REMOTE_DSRP_UCAF;
    }
}
