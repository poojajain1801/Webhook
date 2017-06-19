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

package com.mastercard.mcbp.transactiondecisionmanager.input;


import com.mastercard.mcbp.card.mpplite.apdu.emv.DolValues;

/**
 * The Mobile Support Indicator (MSI) is modeled as object to provide easy access to utility
 * functions
 */
public final class MobileSupportIndicator {
    public static final String MOBILE_SUPPORT_INDICATOR_TAG = "9F7E";

    /**
     * The Mobile Support Indicator byte as received in the C-APDU
     */
    final byte mMobileSupportIndicator;

    /**
     * Build the Mobile Support Indicator from a byte value
     * @param mobileSupportIndicator The Mobile Support Indicator
     */
    public static MobileSupportIndicator of(final byte mobileSupportIndicator) {
        return new MobileSupportIndicator(mobileSupportIndicator);
    }

    public static MobileSupportIndicator of(final DolValues pdolValues) {

        final byte[] msiTlv = pdolValues.getValueByTag(MOBILE_SUPPORT_INDICATOR_TAG);

        return MobileSupportIndicator.of(msiTlv == null || msiTlv.length < 1? 0x00: msiTlv[0]);
    }

    /**
     * Constructor is not available. Please use the static factory method instead.
     */
    private MobileSupportIndicator(final byte mobileSupportIndicator) {
        mMobileSupportIndicator = mobileSupportIndicator;
    }

    /**
     * Check whether 'Mobile' is supported by the Terminal
     * @return True if the Terminal supports mobile, false otherwise
     */
    public final boolean isMobileSupported() {
        return (mMobileSupportIndicator & 0x01) == 0x01;
    }

    /**
     * Check whether the Terminal requires CD CVM
     * @return True, if CD CVM is Required, false otherwise
     */
    public final boolean isCdCvmRequired() {
        return (mMobileSupportIndicator & 0x02) == 0x02;
    }

    /**
     * Check whether the Mobile Support Indicator is supported by the Terminal (i.e. we got back
     * from the Terminal a valid element).
     * @return True if the terminal supports 'Mobile Support Indicator' field. False otherwise.
     */
    public final boolean isSupportedByTheTerminal() {
        return (mMobileSupportIndicator & 0x03) != 0x00;
    }
}
