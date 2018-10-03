/*
 *  Copyright (c) 2017, MasterCard International Incorporated and/or its
 *  affiliates. All rights reserved.
 *
 *  The contents of this file may only be used subject to the MasterCard
 *  Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 *  Materials License.
 *
 *  Please refer to the file LICENSE.TXT for full details.
 *
 *  TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 *  WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 *  WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 *  NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 *  MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 *  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 *  FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 *  IN THE SOFTWARE.
 */
package com.comviva.hceservice.mdes;

import android.util.Log;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.PaymentCard;
import com.comviva.hceservice.common.SDKData;
import com.comviva.hceservice.common.Tags;
import com.mastercard.mpsdk.componentinterface.ActiveCardProvider;
import com.mastercard.mpsdk.componentinterface.Card;
import com.mastercard.mpsdk.componentinterface.CardManager;
import com.mastercard.mpsdk.componentinterface.PaymentContext;
import com.mastercard.mpsdk.componentinterface.RolloverInProgressException;
import com.visa.cbp.sdk.facade.VisaPaymentSDKImpl;
import com.visa.cbp.sdk.facade.data.TokenData;

public class CardSelectionManagerForTransaction implements ActiveCardProvider {

    private Card card;
    private SDKData sdkData;


    public void setPaymentCardForTransaction(CardType cardType, PaymentCard paymentCard) throws RolloverInProgressException {

        Log.d(Tags.DEBUG_METHOD.getTag(), "setPaymentCardForTransaction");
        sdkData = SDKData.getInstance();
        if (cardType.equals(CardType.VTS)) {
            VisaPaymentSDKImpl.getInstance().selectCard(((TokenData) paymentCard.getCurrentCard()).getTokenKey());
        } else if (cardType.equals(CardType.MDES)) {
            card = sdkData.getMcbp().getCardManager().getCardById(paymentCard.getCardUniqueId());
        } else {
            card = null;
        }
        sdkData.setSelectedCard(paymentCard);
    }

    public void unSetPaymentCardForTransaction() {

        VisaPaymentSDKImpl.getInstance().deselectCard();
        sdkData.setSelectedCard(null);
        card = null;
    }


    @Override
    public Card getActiveCard(PaymentContext paymentContext, CardManager cardManager) {

        return card;
    }
}
