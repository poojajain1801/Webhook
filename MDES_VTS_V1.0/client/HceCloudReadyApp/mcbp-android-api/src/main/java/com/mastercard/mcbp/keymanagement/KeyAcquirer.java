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

package com.mastercard.mcbp.keymanagement;

import com.mastercard.mcbp.card.McbpCard;
import com.mastercard.mcbp.exceptions.AlreadyInProcessException;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.InvalidCardStateException;
import com.mastercard.mcbp.utils.exceptions.mcbpcard.McbpCardNotFound;

/**
 * Interface that allows developers to create their own method of acquiring new keys.<br>
 * It is recommended to use {@link CmsKeyAcquirer}, which is the
 * default.
 */
public interface KeyAcquirer {
    /**
     * Perform the logic required to acquire new keys, this method may be called on the UI thread so
     * any slow processing should be performed on a thread.
     *
     * @param card Instance of {@link McbpCard} to acquire new
     *             keys form.
     * @return true to raise an event to any wallet listeners; otherwise false.
     * @throws McbpCardNotFound
     */
    boolean acquireKeysForCard(McbpCard card) throws AlreadyInProcessException, InvalidCardStateException;
}
