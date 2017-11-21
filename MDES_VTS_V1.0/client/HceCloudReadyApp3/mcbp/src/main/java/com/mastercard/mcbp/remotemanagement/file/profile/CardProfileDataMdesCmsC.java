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

package com.mastercard.mcbp.remotemanagement.file.profile;

import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.remotemanagement.file.TestKeyStore;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;

import static com.mastercard.mcbp.utils.crypto.CryptoService.Mode.*;

import flexjson.JSON;
import flexjson.JSONDeserializer;

/**
 * Top level Json structure for MDES CMS-C card profile message
 */
public class CardProfileDataMdesCmsC {
    @JSON(name = "cardProfile")
    private CardProfileMdesCmsC cardProfile;
    @JSON(name = "iccKek")
    private String iccKek;
    @JSON(name = "kekId")
    private String kekId;

    /**
     * Create a new Card Profile object using an appropriate JSON String
     * */
    public static CardProfileDataMdesCmsC valueOf(String jsonCardProfile) {
        JSONDeserializer<CardProfileDataMdesCmsC> deserializer = new JSONDeserializer<>();
        CardProfileDataMdesCmsC result =
                deserializer.deserialize(jsonCardProfile, CardProfileDataMdesCmsC.class);
        String iccKekDecryptionKey = TestKeyStore.getKey(result.kekId);
        CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        ByteArray decryptedIccKey;
        try {
            decryptedIccKey = cryptoService.decryptIccKey(ByteArray.of(result.iccKek),
                                                          ByteArray.of(iccKekDecryptionKey));
        } catch (final NullPointerException | McbpCryptoException e) {
            return null;
        }
        result.cardProfile.setIccKek(decryptedIccKey);
        return result;
    }

    public String getKekId() {
        return kekId;
    }

    public void setKekId(String kekId) {
        this.kekId = kekId;
    }

    public String getIccKek() {
        return iccKek;
    }

    public void setIccKek(String iccKek) {
        this.iccKek = iccKek;
    }

    public CardProfileMdesCmsC getCardProfile() {
        return cardProfile;
    }

    public void setCardProfile(CardProfileMdesCmsC cardProfile) {
        this.cardProfile = cardProfile;
    }
}

