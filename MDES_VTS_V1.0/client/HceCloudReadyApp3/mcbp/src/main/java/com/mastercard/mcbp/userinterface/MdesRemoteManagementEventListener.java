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

package com.mastercard.mcbp.userinterface;

/**
 * Internal MDES CMS dedicated event listener
 */
public interface MdesRemoteManagementEventListener {
    /**
     * Event raised when provision of card is successful.
     *
     * @param tokenUniqueReference Identifier of the card affected
     * @since 1.0.4
     */
    void onCardAdded(final String tokenUniqueReference);

    /**
     * Event raised when provision of card fails.
     *
     * @param tokenUniqueReference Identifier of the card affected
     * @param retriesRemaining     (> 0)  indicates the number of retries the SDK will
     *                             autonomously perform to complete the task / request.
     *                             (<= 0) Indicates that the SDK will not attempt again to
     *                             complete the task / request.
     * @param errorCode            A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onCardAddedFailure(final String tokenUniqueReference, final int retriesRemaining,
                            final int errorCode);

    /**
     * Event raised when delete of card is successful.
     *
     * @param tokenUniqueReference Identifier of the card affected.
     * @since 1.0.4
     */
    void onCardDelete(final String tokenUniqueReference);

    /**
     * Event raised when delete of card fails.
     *
     * @param tokenUniqueReference Identifier of the card affected.
     * @param retriesRemaining     (> 0)  indicates the number of retries the SDK will
     *                             autonomously perform to complete the task / request.
     *                             (<= 0) Indicates that the SDK will not attempt again to
     *                             complete the task / request.
     * @param errorCode            A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onCardDeleteFailure(final String tokenUniqueReference, final int retriesRemaining,
                             final int errorCode);

    /**
     * Event raised when Set/Change PIN of card is successful.
     *
     * @param tokenUniqueReference Identifier of the card affected
     * @param result               Result of Set/Change PIN.
     * @param pinTriesRemaining    The current Mobile PIN tries remaining.
     * @since 1.0.4
     */
    void onCardPinChanged(final String tokenUniqueReference, final String result,
                          final int pinTriesRemaining);

    /**
     * Event raised when Set/Change PIN of card fails.
     *
     * @param tokenUniqueReference Identifier of the card affected
     * @param retriesRemaining     (> 0)  indicates the number of retries the SDK will
     *                             autonomously perform to complete the task / request.
     *                             (<= 0) Indicates that the SDK will not attempt again to
     *                             complete the task / request.
     * @param errorCode            A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onCardPinChangedFailure(final String tokenUniqueReference, final int retriesRemaining,
                                 final int errorCode);

    /**
     * Event raised when reset of PIN of card is initiated by CMS-D and it is successful.
     *
     * @param tokenUniqueReference Identifier of the card affected
     */
    void onCardPinReset(final String tokenUniqueReference);

    /**
     * Event raised when reset of PIN of card is initiated by CMS-D and it fails.
     *
     * @param tokenUniqueReference Identifier of the card affected.
     * @param retriesRemaining     (> 0)  indicates the number of retries the SDK will
     *                             autonomously perform to complete the task / request.
     *                             (<= 0) Indicates that the SDK will not attempt again to
     *                             complete the task / request.
     * @param errorCode            A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onCardPinResetFailure(final String tokenUniqueReference, final int retriesRemaining,
                               final int errorCode);

    /**
     * Event raised when replenishment of single use key for card is successful.
     *
     * @param tokenUniqueReference       Identifier of the card affected
     * @param numberOfCredentialReceived Number of credential received
     * @since 1.0.4
     */
    void onPaymentTokensReceived(final String tokenUniqueReference,
                                 final int numberOfCredentialReceived);

    /**
     * Event raised when replenishment of single use key for card fails.
     *
     * @param tokenUniqueReference Identifier of the card affected
     * @param retriesRemaining     (> 0)  indicates the number of retries the SDK will
     *                             autonomously perform to complete the task / request.
     *                             (<= 0) Indicates that the SDK will not attempt again to
     *                             complete the task / request.
     * @param errorCode            A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onPaymentTokensReceivedFailure(final String tokenUniqueReference,
                                        final int retriesRemaining,
                                        final int errorCode);

    /**
     * Event raised when the registration with CMS-D is successful.
     *
     * @since 1.0.4
     */
    void onRegistrationCompleted();

    /**
     * Event raised when registration with CMS-D fails.
     *
     * @param retriesRemaining (> 0)  indicates the number of retries the SDK will
     *                         autonomously perform to complete the task / request.
     *                         (<= 0) Indicates that the SDK will not attempt again to
     *                         complete the task / request.
     * @param errorCode        A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onRegistrationFailure(final int retriesRemaining, final int errorCode);

    /**
     * Event raised when retrieval of task status is successful.
     *
     * @param status Status of task
     * @since 1.0.4
     */
    void onTaskStatusReceived(final String status);

    /**
     * Event raised when retrieval of task status fails.
     *
     * @param retriesRemaining (> 0)  indicates the number of retries the SDK will
     *                         autonomously perform to complete the task / request.
     *                         (<= 0) Indicates that the SDK will not attempt again to
     *                         complete the task / request.
     * @param errorCode        A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onTaskStatusReceivedFailure(final int retriesRemaining, final int errorCode);

    /**
     * Event raised when Set/Change PIN of Wallet fails.
     *
     * @param result            Result of Set/Change PIN.
     * @param pinTriesRemaining The current Mobile PIN tries remaining.
     * @since 1.0.4
     */
    void onWalletPinChange(final String result, final int pinTriesRemaining);

    /**
     * Event raised when Set/Change PIN of Wallet fails.
     *
     * @param retriesRemaining (> 0)  indicates the number of retries the SDK will
     *                         autonomously perform to complete the task / request.
     *                         (<= 0) Indicates that the SDK will not attempt again to
     *                         complete the task / request.
     * @param errorCode        A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onWalletPinChangeFailure(final int retriesRemaining, final int errorCode);

    /**
     * Event raised when reset of PIN of Wallet is initiated by CMS-D and it is successful.
     *
     * @since 1.0.4
     */
    void onWalletPinReset();

    /**
     * Event raised when reset of PIN of Wallet is initiated by CMS-D and it fails.
     *
     * @param retriesRemaining (> 0)  indicates the number of retries the SDK will
     *                         autonomously perform to complete the task / request.
     *                         (<= 0) Indicates that the SDK will not attempt again to
     *                         complete the task / request.
     * @param errorCode        A code identifying the error that occurred.
     * @since 1.0.4
     */
    void onWalletPinResetFailure(final int retriesRemaining, final int errorCode);


    /**
     * Event raised when the system health with CMS-D is successful.
     *
     * @since 1.0.6a
     */
    void onSystemHealthCompleted();

    /**
     * Event raised when system health with CMS-D fails.
     *
     * @param errorCode A code identifying the error that occurred.
     * @since 1.0.6a
     */
    void onSystemHealthFailure(final int errorCode);
}
