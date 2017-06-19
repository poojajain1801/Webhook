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

import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.utils.exceptions.McbpUncheckedException;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.McbpCardException;
import com.mastercard.mcbp_android.BuildConfig;
import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * API class used for interacting with the wallet (MDES Mode only)
 */
public class MdesMcbpWalletApi {

    /**
     * maximum allowed numeric digits for Mobile PIN
     */
    protected static final int MAXIMUM_MOBILE_PIN_LENGTH = 8;

    /**
     * minimum allowed numeric digits for Mobile PIN
     */
    protected static final int MINIMUM_MOBILE_PIN_LENGTH = 4;

    /**
     * Change the wallet PIN.Format of the pin is numeric.
     * MINIMUM LENGTH ALLOWED : 4
     * MAXIMUM LENGTH ALLOWED : 8
     * This length enforcement is according to
     * MasterCardCloudBasedPayments_IssuerCryptographicAlgorithms_v1-1 (Section 3.4)
     * Note: Results will be notified using ChangeWalletPinListeners
     *
     * @param oldPin The entered old PIN in byte[] form.
     * @param newPin The entered new PIN in byte[] form.
     * @throws McbpCardException         An exception if the wallet developer has opted for card
     *                                   level pin in the BuildConfig
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void changeWalletPin(final byte[] oldPin, final byte[] newPin)
            throws McbpCardException, AlreadyInProcessException {
        if (!BuildConfig.WALLET_PIN) {
            throw new McbpUncheckedException("This method is available only with BuildConfig"
                                             + ".WALLET_PIN");
        }

        if (oldPin != null) {
            // The old PIN is not null, thus let's check that it is of the right format
            if (oldPin.length < MINIMUM_MOBILE_PIN_LENGTH
                || oldPin.length > MAXIMUM_MOBILE_PIN_LENGTH) {
                throw new IllegalArgumentException("Invalid old PIN Length: " + oldPin.length);
            }
        }

        // Finally let's check that new PIN is of the right format
        if (newPin == null || newPin.length < MINIMUM_MOBILE_PIN_LENGTH
            || newPin.length > MAXIMUM_MOBILE_PIN_LENGTH) {
            throw new IllegalArgumentException("Invalid new PIN Length: "
                                               + ((newPin != null) ? newPin.length : "null"));
        }

        RemoteManagementServices.changePin(null,
                                           (oldPin == null) ? null : ByteArray.of(oldPin),
                                           ByteArray.of(newPin));
    }

    /**
     * Set the wallet PIN for the first time.Format of the pin is numeric.
     * MINIMUM LENGTH ALLOWED : 4
     * MAXIMUM LENGTH ALLOWED : 8
     * This length enforcement is according to
     * MasterCardCloudBasedPayments_IssuerCryptographicAlgorithms_v1-1 (Section 3.4)
     * Note: Results will be notified using ChangeWalletPinListeners
     *
     * @param newPin The entered new PIN in byte[] form.
     * @throws McbpCardException         An exception if the wallet developer has opted for card
     *                                   level pin in the BuildConfig
     * @throws AlreadyInProcessException Exception thrown if another request is already in process.
     * @since 1.0.4
     */
    public static void setWalletPin(final byte[] newPin)
            throws McbpCardException, AlreadyInProcessException {
        changeWalletPin(null, newPin);
    }

    /**
     * This enables the wallet to cancel a SDK pending request which is previously requested and
     * because of invalid session waiting for a fresh and valid session to arrive. This is
     * required in cases where a request of higher priority than previous request needs to made.
     *
     * @return false if there is no SDK pending request, otherwise cancel the request and return
     * true.
     */
    public static boolean cancelPendingRequest() {

        return RemoteManagementServices.cancelPendingRequest();
    }
}
