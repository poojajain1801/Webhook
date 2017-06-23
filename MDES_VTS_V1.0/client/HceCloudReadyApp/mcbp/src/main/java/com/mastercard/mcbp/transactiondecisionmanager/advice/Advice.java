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

package com.mastercard.mcbp.transactiondecisionmanager.advice;

import java.util.List;

/**
 * Advice. This object is returned by the MPP Lite once the transaction data has been processed
 */
public class Advice {
    /**
     * The authorization advice
     */
    private final Assessment mAssessment;
    /**
     * The list of reasons behind the authorization advice.
     */
    private final List<Reason> mReasons;

    /**
     * Get the advice on authorization
     * */
    public Assessment getAssessment() {
        return mAssessment;
    }

    /**
     * Get the reasons for the authorization decision, if any.
     * The list could be empty.
     * */
    public List<Reason> getReasons() {
        return mReasons;
    }

    /**
     * Constructor, all values must be provided
     * @param assessment The authorization advice
     * @param reasons The list of reasons related to the authorization advice, if any
     */
    public Advice(final Assessment assessment, final List<Reason> reasons) {
        mAssessment = assessment;
        mReasons = reasons;
    }

    /**
     * Format the Advice into human readable input
     * @return The Advice as String
     */
    @Override
    public String toString() {
        final StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Assessment: ");
        stringBuilder.append(mAssessment);
        stringBuilder.append("\nReasons: ");

        for (Reason reason: mReasons) {
            stringBuilder.append(reason);
        }

        stringBuilder.append("\n");

        return stringBuilder.toString();
    }
}
