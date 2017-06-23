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

package com.mastercard.mcbp.api;

import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Utility data structure to return the content of a registration request parameter
 */
public class RegistrationRequestParameters {
    final ByteArray mRandomGeneratedKey;
    final ByteArray mNewMobilePin;

    /***
     * Default constructor
     * @param randomGeneratedKey The Random Generated Key encrypted with the public key of the CMS-D
     * @param newMobilePin The encrypted pin block
     */
    public RegistrationRequestParameters(final ByteArray randomGeneratedKey,
                                         final ByteArray newMobilePin) {
        mRandomGeneratedKey = randomGeneratedKey;
        mNewMobilePin = newMobilePin;
    }

    /**
     * Get the Mobile Random Generated Key encrypted with the public key of the CMS-D
     *
     * @return It can be null in case the mobile PIN was not given as an input
     * */
    public ByteArray getRandomGeneratedKey() {
        return mRandomGeneratedKey;
    }

    /***
     * If not null, it contains the mobile pin block encrypted with the RGK
     * @return It can be null in case the mobile PIN was not given as an input
     */
    public ByteArray getNewMobilePin() {
        return mNewMobilePin;
    }
}
