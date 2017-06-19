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

package com.mastercard.mcbp.remotemanagement.file.credentials;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mcbp.card.credentials.SingleUseKeyContent;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.remotemanagement.file.TestKeyStore;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.apache.commons.codec.binary.Hex;

import flexjson.JSON;
import flexjson.JSONDeserializer;

/**
 * Top level Json structure for MDES CMS-C card profile message
 */
public class CredentialsDataMdesCmsC {
    @JSON(name = "rawTransactionCredentials")
    private SingleUseKeyContentMdesCmsC[] rawTransactionCredentials;
    @JSON(name = "kekId")
    private String kekId;
    @JSON(include = false)
    private String digitizedCardId = "";

    /**
     * Create a new Card Profile object using an appropriate JSON String
     * */
    public static CredentialsDataMdesCmsC valueOf(String jsonSuks) {
        return new JSONDeserializer<CredentialsDataMdesCmsC>().
                deserialize(jsonSuks, CredentialsDataMdesCmsC.class);
    }

    public void setDigitizedCardId(String digitizedCardId) {
        this.digitizedCardId = digitizedCardId;
    }

    public SingleUseKey[] getAllSingleUseKeys() {
        SingleUseKey[] keys = new SingleUseKey[rawTransactionCredentials.length];

        for (int i = 0; i < rawTransactionCredentials.length; i++) {
            // Create the DC_SUK_ID for this particular SUK as it will be used by the internal
            // SDK Database
            SingleUseKeyContentMdesCmsC currentSuk = rawTransactionCredentials[i];

            ByteArray atc = ByteArray.of((short) currentSuk.getAtc());
            ByteArray clMd = decryptValue(currentSuk.getSessionKeyContactlessMd());
            ByteArray clUmd = decryptValue(currentSuk.getSessionKeyContactlessUmd());
            ByteArray dsrpMd = decryptValue(currentSuk.getSessionKeyDsrpMd());
            ByteArray dsrpUmd = decryptValue(currentSuk.getSessionKeyDsrpUmd());
            ByteArray idn = decryptValue(currentSuk.getIdn());

            String pin = TestKeyStore.getKey(digitizedCardId.substring(0, 16) + "-pin");
            if (pin == null) pin = TestKeyStore.getKey("default-pin");
            String mobilePin = new String(Hex.encodeHex(pin.getBytes())); // Convert PIN to HEX

            ByteArray defaultPin = ByteArray.of(mobilePin);

            String digitizedSingleUseKeyId = this.digitizedCardId +
                    atc.toHexString() +
                    "000000";
            SingleUseKey singleUseKey = new SingleUseKey();

            // Generate the content
            SingleUseKeyContent content = new SingleUseKeyContent();
            content.setHash(ByteArray.of("0000"));
            content.setInfo(ByteArray.of("56"));
            content.setAtc(atc);
            content.setSessionKeyContactlessMd(clMd);
            content.setSessionKeyRemotePaymentMd(dsrpMd);
            // Copy only the 8 most right bytes of IDN
            content.setIdn(idn != null ? idn.copyOfRange(8, 16): null);
            content.setSukRemotePaymentUmd(authenticate(dsrpUmd, defaultPin));
            content.setSukContactlessUmd(authenticate(clUmd, defaultPin));

            singleUseKey.setContent(content);
            singleUseKey.setDigitizedCardId(ByteArray.of(this.digitizedCardId));
            singleUseKey.setId(ByteArray.of(digitizedSingleUseKeyId));

            keys[i] = singleUseKey;
        }
        return keys;
    }

    /**
     * Decrypt the SUK given the current KeK
     * */
     private ByteArray decryptValue(String suk) {
        ByteArray key = ByteArray.of(TestKeyStore.getKey(this.kekId));
        ByteArray inputData = ByteArray.of(suk);

        CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        try {
            return cryptoService.decryptDataEncryptedField(inputData, key);
        } catch (McbpCryptoException e) {
            return null;
        }
    }

    private ByteArray authenticate(final ByteArray key, final ByteArray pin) {
        // Unlock key
        return fnXor(key, shiftPin(pin));
    }

    /**
     * Performs fnXor function
     */
    private ByteArray fnXor(ByteArray sessionKey, ByteArray pin) {
        // We can only handle up to 8 digit PIN, if more then just loop 8 times
        ByteArray result = ByteArray.of(sessionKey);
        int len = pin.getLength() < 8 ? pin.getLength() : 8;

        for (int i = 0; i < len; i++) {
            result.setByte(i, (byte) (sessionKey.getByte(i) ^ pin.getByte(i)));
            result.setByte(i + 8, (byte) (sessionKey.getByte(i + 8) ^ pin.getByte(i)));
        }
        return result;
    }

    /**
     * Shifts the pin
     */
    private ByteArray shiftPin(ByteArray spinPin) {
        ByteArray result = ByteArray.get(spinPin.getLength());
        byte b;
        for (int i = 0; i < spinPin.getLength(); i++) {
            b = spinPin.getByte(i);
            b <<= 1;
            result.setByte(i, b);
        }
        b = 0x00;
        return result;
    }

    public String getKekId() {
        return kekId;
    }

    public void setKekId(String kekId) {
        this.kekId = kekId;
    }

    public SingleUseKeyContentMdesCmsC[] getRawTransactionCredentials() {
        return rawTransactionCredentials;
    }

    public void setRawTransactionCredentials(SingleUseKeyContentMdesCmsC[] suks) {
        this.rawTransactionCredentials = suks;
    }
}

