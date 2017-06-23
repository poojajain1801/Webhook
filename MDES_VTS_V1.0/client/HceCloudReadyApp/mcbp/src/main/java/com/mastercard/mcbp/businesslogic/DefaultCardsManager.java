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

package com.mastercard.mcbp.businesslogic;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.userinterface.MakeDefaultListener;

/**
 * Interface to handle default cards for this payment application.<br>
 */
public interface DefaultCardsManager {

    /**
     * Checks whether the card with the Digitized card Id is the default payment card for
     * contactless payments
     *
     * @return  true if the card is the default payment card for contactless payments;
     *          false otherwise
     */
    boolean isDefaultCardForContactlessPayment(McbpCard digitizedCardId);

    /**
     * Checks whether the card with the Digitized card Id is the default payment card for
     * remote payments
     *
     * @return  true if the card is the default payment card for remote payments;
     *          false otherwise
     */
    boolean isDefaultCardForRemotePayment(McbpCard digitizedCardId);

    /**
     * Sets application as the default payment application
     */
    void setAsDefaultCardForContactlessPayment(McbpCard mcbpCard,
                                               boolean applicationDefault,
                                               MakeDefaultListener listener);

    /**
     * Sets application as the default payment application
     */
    void setAsDefaultCardForContactlessPayment(McbpCard mcbpCard, MakeDefaultListener listener);

    /**
     * Unset application as the default payment application
     */
    void unsetAsDefaultCardForContactlessPayment(McbpCard mcbpCard, MakeDefaultListener listener);

    /**
     * Set card as default for remote payments.
     *
     * @return  true if the card is successfully set as default;
     *          false otherwise
     */
    boolean setAsDefaultCardForRemotePayment(McbpCard mcbpCard);

    /**
     * Unset the default card for remote payment
     *
     * @return  true if the card is successfully unset as default;
     *          false otherwise
     */
    boolean unsetAsDefaultCardForRemotePayment(McbpCard mcbpCard);

    /**
     * Returns the default card for contactless payment if set
     *
     * @return  McbpCard instance for the default card if set;
     *          null otherwise
     */
    McbpCard getDefaultCardForContactlessPayment();

    /**
     * Returns the default card for remote payment if set
     *
     * @return  McbpCard instance for the default card if set;
     *          null otherwise
     */
    McbpCard getDefaultCardForRemotePayment();
}
