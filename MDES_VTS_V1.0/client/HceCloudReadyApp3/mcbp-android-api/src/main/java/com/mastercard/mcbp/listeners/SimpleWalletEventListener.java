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

package com.mastercard.mcbp.listeners;

import com.mastercard.mcbp.remotemanagement.mdes.ChangePinStatus;

/**
 * Implementer of all wallet events that can be used to extend so you only need to implement the
 * events you care about
 */
public class SimpleWalletEventListener implements WalletEventListener {
    @Override
    public boolean applicationReset() {
        return false;
    }

    @Override
    public boolean cardDeleted(String digitizedCardId) {
        return false;
    }

    @Override
    public boolean cardAdded(String digitizedCardId) {
        return false;
    }

    @Override
    public boolean paymentTokensAdded(String digitizedCardId) {
        return false;
    }

    @Override
    public boolean cardSuspended(String digitizedCardId) {
        return false;
    }

    @Override
    public boolean cardResumed(String digitizedCardId) {
        return false;
    }

    @Override
    public boolean pinChanged(String digitizedCardId) {
        return false;
    }

    @Override
    public boolean remoteWipe() {
        return false;
    }

    @Override
    public boolean changePinStatusReceived(final ChangePinStatus changePinStatus) {
        return false;
    }

}
