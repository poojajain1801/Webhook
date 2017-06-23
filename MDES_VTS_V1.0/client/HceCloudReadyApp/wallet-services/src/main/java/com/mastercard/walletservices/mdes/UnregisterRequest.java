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

package com.mastercard.walletservices.mdes;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;
/**
  * Encapsulate the data which will be needed to process unregister request.
  */
public class UnregisterRequest {
    @JSON(name = "paymentAppInstanceId")
    private String paymentAppInstanceId;

    public UnregisterRequest() {
    }

    public String getPaymentAppInstanceId() {
        return paymentAppInstanceId;
    }

    public void setPaymentAppInstanceId(String paymentAppInstanceId) {
        this.paymentAppInstanceId = paymentAppInstanceId;
    }

    public static UnregisterRequest deserialize(String json) {
        return new JSONDeserializer<UnregisterRequest>()
                .deserialize(json, UnregisterRequest.class);
    }

    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        return serializer.serialize(this);
    }

}
