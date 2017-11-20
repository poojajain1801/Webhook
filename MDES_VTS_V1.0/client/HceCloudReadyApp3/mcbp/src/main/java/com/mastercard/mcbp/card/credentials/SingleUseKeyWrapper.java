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

package com.mastercard.mcbp.card.credentials;

/**
 * Interface that must be implemented by each remote management module in order to provide
 * an MCBP compatible Single Use Key
 */
public interface SingleUseKeyWrapper {
    /**
     * Return the Digitized Card Id that this Single Use Key belongs to
     *
     * @return the Digitized Card Id the Single Use Key belong to
     *
     * */
    String getCardId();

    /**
     * Convert the object into a valid Single Use Key object that can be used by the other mcbp
     * modules such as LDE and MPP Lite
     *
     * @return The an object of {@link SingleUseKey}
     * */
    SingleUseKey toSingleUseKey();
}
