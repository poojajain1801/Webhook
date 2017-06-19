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

package com.mastercard.mcbp.card.mpplite.mcbpv1.credentials;

import com.mastercard.mcbp.transactiondecisionmanager.transaction.TransactionRange;

/**
 * Callback interface for the MPP Lite to ask for Credentials when needed
 */
public interface TransactionCredentialsManager {
    /**
     * Credentials are grouped based on their scope of applicability (either Contactless or Remote
     * Payment).
     */
    enum Scope {
        CONTACTLESS,
        REMOTE_PAYMENT
    }

    /**
     * Ask for valid contactless credentials as the decision was to approved the transaction
     *
     * @param scope The scope for which the credentials will be used (Contactless vs Remote
     *              Payment)
     * @return A valid set of credentials to be used for the cryptogram generation
     */
    TransactionCredentials getValidUmdAndMdCredentialsFor(final Scope scope);

    /**
     * Ask for valid remote payment credentials as the decision was to approved the transaction
     *
     * @param scope The scope for which the credentials will be used (Contactless vs Remote
     *              Payment)
     * @return A valid set of credentials to be used for the cryptogram generation
     */
    TransactionCredentials getValidMdCredentialsFor(final Scope scope);

    /**
     * Ask for a random credentials.
     *
     * @return A random set of credentials that will be used for the cryptogram generation
     */
    TransactionCredentials getRandomCredentials();

    /**
     * Check whether access to the credentials is subject to Cardholder Verification for any type
     * of transaction
     *
     * @param scope The scope for which the credentials will be used (Contactless vs Remote
     *              Payment)
     * @return True, if access to credentials requires card holder verification, false otherwise
     */
    boolean areUmdCredentialsSubjectToCvmFor(final TransactionRange transactionRange,
                                             final Scope scope);

    /**
     * Check whether at least one set of credentials is available for the MPP lite to use in a
     * Contactless transaction
     *
     * @param scope The scope for which the credentials will be used (Contactless vs Remote Payment)
     * @return True if credentials are available on the device, false otherwise
     */
    boolean hasValidCredentialsFor(final Scope scope);

    /**
     * Get Act to use in logs generated for cancel payment.  The transaction manager will return a
     * actual Atc or a random one based on it's evaluation of the situation.
     *
     * @param scope The scope for which the credentials will be used (Contactless vs Remote
     *              Payment)
     * @return a random or actual Atc based on state of transaction manager
     */
    byte[] getAtcForCancelPayment(final Scope scope);
}
