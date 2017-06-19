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

package com.mastercard.mcbp.card.profile;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.json.ByteArrayObjectFactory;
import com.mastercard.mobile_api.utils.json.ByteArrayTransformer;
import com.mastercard.mobile_api.utils.json.SuppressNullTransformer;

import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import flexjson.JSON;
import flexjson.JSONDeserializer;
import flexjson.JSONSerializer;

/**
 * Digitized Card Profile information
 * <p/>
 * The data structure is used by the Local Database Encrypted as well as by the MPP Lite logic
 * <p/>
 * The remote management interfaces need to provide an object that can be converted to a
 * DigitizedCardProfile. This is achieved by implementing the following interface
 * {@link McbpDigitizedCardProfileWrapper}
 */
@JSON(name = "cardProfile")
public final class DigitizedCardProfile implements McbpDigitizedCardProfileWrapper {
    /**
     * Check whether a mobile PIN has been initially configured for this profile
     *
     * @return True if the profile has a valid PIN configuration, false otherwise
     */
    public boolean isMobilePinInitialConfiguration() {
        return mobilePinInitialConfiguration;
    }

    /**
     * Set whether this card has a valid associated mobile PIN (backend)
     *
     * @param mobilePinInitialConfiguration True if a valid mobile PIN for this card exists, false
     *                                      otherwise
     */
    public void setMobilePinInitialConfiguration(boolean mobilePinInitialConfiguration) {
        this.mobilePinInitialConfiguration = mobilePinInitialConfiguration;
    }

    /**
     * Get the maximum Pin Try (if applicable)
     *
     * @return the Maximum Number of Mobile PIN tries
     */
    public int getMaximumPinTry() {
        return maximumPinTry;
    }

    /**
     * Set the maximum number of PIN tries (if applicable)
     *
     * @param maximumPinTry The maximum number of PIN tries
     */
    public void setMaximumPinTry(int maximumPinTry) {
        this.maximumPinTry = maximumPinTry;
    }

    /**
     * Default constructor
     */
    public DigitizedCardProfile() {
        contactlessSupported = false;
        remotePaymentSupported = false;
    }

    /**
     * Check whether the Remote Payment functionality is supported
     *
     * @return True if the card profile supports remote payment, false otherwise
     */
    public boolean getRemotePaymentSupported() {
        return remotePaymentSupported;
    }

    /**
     * Set whether this card supports Remote Payment or not
     *
     * @param remotePaymentSupported A boolean specifying whether remote payment is supported or not
     */
    public void setRemotePaymentSupported(boolean remotePaymentSupported) {
        this.remotePaymentSupported = remotePaymentSupported;
    }

    /**
     * Check whether the Contactless Payment functionality is supported
     *
     * @return True if the card profile supports contactless payment, false otherwise
     */
    public boolean getContactlessSupported() {
        return contactlessSupported;
    }

    /**
     * Set whether this card supports Contactless Payment or not
     *
     * @param contactlessSupported A boolean specifying whether contactless payment is supported
     */
    public void setContactlessSupported(boolean contactlessSupported) {
        this.contactlessSupported = contactlessSupported;
    }

    /**
     * Get the MPP Lite Module card profile information
     *
     * @return The MPP Lite Module card profile information
     */
    public MppLiteModule getMppLiteModule() {
        return mppLiteModule;
    }

    /**
     * Set the MPP Lite Module card profile information
     *
     * @param mppLiteModule An instance of the MPP Lite Module card profile information
     */
    public void setMppLiteModule(MppLiteModule mppLiteModule) {
        this.mppLiteModule = mppLiteModule;
    }

    /**
     * Get the Business Logic Module card profile information
     *
     * @return Business Logic Module card profile information
     */
    public BusinessLogicModule getBusinessLogicModule() {
        return businessLogicModule;
    }

    /**
     * Set the MPP Business Logic card profile information
     *
     * @param businessLogicModule An instance of the Business Logic Module card profile information
     */
    public void setBusinessLogicModule(BusinessLogicModule businessLogicModule) {
        this.businessLogicModule = businessLogicModule;
    }

    /**
     * Get the card digitized Id for this card
     *
     * @return The Digitized Card Id as byte array
     */
    public ByteArray getDigitizedCardId() {
        return digitizedCardId;
    }

    /**
     * Set the Digitized Card Id
     *
     * @param digitizedCardId The Digitized Card Id as byte array
     */
    public void setDigitizedCardId(ByteArray digitizedCardId) {
        this.digitizedCardId = digitizedCardId;
    }

    /**
     * Get the Card cardMetadata information
     * <p/>
     * (deserialization is responsibility of the application level)
     *
     * @return The Card cardMetadata information as String
     */
    public String getCardMetadata() {
        return cardMetadata;
    }

    /**
     * Set the Card Metadata that can be set by the application layer
     * <p/>
     * (serialization is responsibility of the application level)
     *
     * @param cardMetadata The Card Metadata as String
     */
    public void setCardMetadata(String cardMetadata) {
        this.cardMetadata = cardMetadata;
    }

    /**
     * Implements the Card Wrapper interface, so that it can be used as object in the remote
     * management functions as well.
     */
    @Override
    public DigitizedCardProfile toDigitizedCardProfile() {
        return this;
    }

    /**
     * Get the card digitized Id for this card
     *
     * @return The Digitized Card Id as String
     */
    public String getCardId() {
        return digitizedCardId.toHexString();
    }

    /**
     * Specify whether the Contactless Mode is supported or not
     */
    @JSON(name = "contactlessSupported")
    private boolean contactlessSupported;

    /**
     * Specify whether the Remote Payment is supported or not
     */
    @JSON(name = "remoteSupported")
    private boolean remotePaymentSupported;

    /**
     * MPP Lite Module - Specific profile information
     */
    @JSON(name = "mppLiteModule")
    private MppLiteModule mppLiteModule;

    /**
     * Business Logic Module - Specific profile information
     */
    @JSON(name = "businessLogicModule")
    private BusinessLogicModule businessLogicModule;

    /**
     * The Digitized Card Id as per MCBP specs
     */
    @JSON(name = "digitizedCardId")
    private ByteArray digitizedCardId;

    /**
     * Number of maximum Pin Re-Try attempts (valid only for MDES)
     */
    @JSON(name = "maximumPinTry")
    private int maximumPinTry = 0;

    /**
     * Flag to specify whether this card has a valid PIN associated with it.
     * The flag is always true for MCBPv1 since the PIN is always configured before the card
     * is provisioned
     */
    @JSON(name = "mobilePinInitialConfiguration")
    private boolean mobilePinInitialConfiguration = true;

    /**
     * Issuer specific cardMetadata related to the card. The format is implementation specific a
     * nd it is not processed by the MCBP SDK.
     */
    @JSON(name = "cardMetadata")
    private String cardMetadata;

    public void wipe() {
        if (mppLiteModule != null) {
            mppLiteModule.wipe();
        }
        if (businessLogicModule != null) {
            businessLogicModule.wipe();
        }
        if (cardMetadata != null) {
            cardMetadata = "";
        }
        Utils.clearByteArray(digitizedCardId);
        maximumPinTry = 0;
    }

    public static DigitizedCardProfile valueOf(final byte[] bytes) {
        Reader bfReader = new InputStreamReader(new ByteArrayInputStream(bytes));
        return new JSONDeserializer<DigitizedCardProfile>()
                .use(ByteArray.class, new ByteArrayObjectFactory())
                .deserialize(bfReader, DigitizedCardProfile.class);
    }

    public String toJsonString() {
        JSONSerializer serializer = new JSONSerializer();
        serializer.exclude("*.class");
        // ByteArray serialization
        serializer.transform(new ByteArrayTransformer(), ByteArray.class);
        // Skip null values
        serializer.transform(new SuppressNullTransformer(), void.class);
        return serializer.serialize(this);
    }
}
