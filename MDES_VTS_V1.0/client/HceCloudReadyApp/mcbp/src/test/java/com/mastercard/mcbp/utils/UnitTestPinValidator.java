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

package com.mastercard.mcbp.utils;

import com.mastercard.mcbp.card.cvm.ChValidator;
import com.mastercard.mcbp.card.cvm.ChValidatorListener;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * Simple PIN Validator for unit test.
 */
public class UnitTestPinValidator implements ChValidator {
    private final ByteArray mMobilePin;

    private final boolean mIsAuthenticated;

    public UnitTestPinValidator(final ByteArray mobilePin, final boolean isAuthenticated) {
        this.mMobilePin = mobilePin;
        this.mIsAuthenticated = isAuthenticated;
    }

    @Override
    public String getDescription() {
        return "UNIT_TEST_PIN";
    }

    @Override
    public void authenticate(final ByteArray key, final ChValidatorListener listener) {
        throw new RuntimeException("Async call not supported");
    }

    public ByteArray authenticate(final ByteArray key) throws McbpCryptoException {
        final CryptoService crypto = CryptoServiceFactory.getDefaultCryptoService();
        return crypto.deriveSessionKey(key, mMobilePin);
    }

    @Override
    public void notifyTransactionCompleted() {
        // Do nothing
    }

    @Override
    public boolean isAuthenticated() {
        return mIsAuthenticated;
    }
}
