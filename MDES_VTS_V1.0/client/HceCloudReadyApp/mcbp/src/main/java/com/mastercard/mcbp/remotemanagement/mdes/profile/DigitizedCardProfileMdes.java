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

package com.mastercard.mcbp.remotemanagement.mdes.profile;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;

import flexjson.JSON;
import flexjson.JSONDeserializer;

/**
 * Represents the digitized card profile of mdes.
 * It is also providing the conversion of mdes profile structure into core card profile structure.
 */
public class DigitizedCardProfileMdes {

    @JSON(name = "digitizedCardId")
    public String digitizedCardId;

    @JSON(name = "maximumPinTry")
    public int maximumPinTry;

    @JSON(name = "mppLiteModule")
    public MppLiteModule mppLiteModule;

    @JSON(name = "businessLogicModule")
    public BusinessLogicModule businessLogicModule;

    public static DigitizedCardProfileMdes valueOf(String json) {
        return new JSONDeserializer<DigitizedCardProfileMdes>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(json, DigitizedCardProfileMdes.class);
    }

}
