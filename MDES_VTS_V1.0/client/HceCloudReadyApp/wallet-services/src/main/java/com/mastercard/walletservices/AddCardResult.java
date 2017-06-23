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

package com.mastercard.walletservices;

/**
 * The result that comes back from a request to add a new card.
 */
public enum AddCardResult {
    /**
     * The automatic approval is where the request was sent successfully and a card will be
     * provisioned at some point in the near future.
     */
    AUTOMATIC_APPROVAL,

    /**
     * Require Addition Authentication is where the Issuer has requested an additional approval
     * process, upon receiving this result the user must then choose their preferred
     * approval process.
     */
    REQUIRE_ADDITIONAL_AUTHENTICATION,

    /**
     * Reject is where the card attempting to be added is not allowed.
     */
    REJECT
}
