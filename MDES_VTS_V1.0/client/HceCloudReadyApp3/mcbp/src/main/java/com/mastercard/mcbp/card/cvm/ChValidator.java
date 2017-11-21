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

package com.mastercard.mcbp.card.cvm;

import com.mastercard.mobile_api.bytes.ByteArray;

/**
 * ChValidator is a helper object to which a McbpCard can delegate the
 * cardholder validation for a Digitized Card.<br>
 * <br>
 */

public interface ChValidator {

    /**
     * Method used by the Business Logic to get a human readable description of
     * the ChValidator. This description will be used to present the different
     * CHValidators to the User in case multiple CVM options are available.
     *
     * @return description string.
     */
    String getDescription();

    /**
     * Authenticates a key Method used by the McbpCard before a successful
     * transaction can take place: - during McbpCard.startContactless() for a
     * Contactless transaction - during McbpCard.activateRemote() for a remote
     * transaction
     *
     * @param key to be unlocked
     * @param listener instance of ChValidatorListener
     */
    void authenticate(final ByteArray key, final ChValidatorListener listener);

    /**
     * Check whether the authentication has been performed
     * @return True if the authentication has been performed, false otherwise
     */
    boolean isAuthenticated();

    /**
     * Notify Transaction completed. This may allow the ChValidator to reset variables related to
     * user authentication
     * TODO: Need to discuss this APIs with the team
     */
    void notifyTransactionCompleted();
}
