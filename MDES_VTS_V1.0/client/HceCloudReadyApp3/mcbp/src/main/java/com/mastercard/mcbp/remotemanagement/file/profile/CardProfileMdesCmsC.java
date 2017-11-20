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

import com.mastercard.mcbp.card.profile.AlternateContactlessPaymentData;
import com.mastercard.mcbp.card.profile.BusinessLogicModule;
import com.mastercard.mcbp.card.profile.CardRiskManagementData;
import com.mastercard.mcbp.card.profile.CardholderValidators;
import com.mastercard.mcbp.card.profile.ContactlessPaymentData;
import com.mastercard.mcbp.card.profile.CvmIssuerOptions;
import com.mastercard.mcbp.card.profile.DigitizedCardProfile;
import com.mastercard.mcbp.card.profile.IccPrivateKeyCrtComponents;
import com.mastercard.mcbp.card.profile.McbpDigitizedCardProfileWrapper;
import com.mastercard.mcbp.card.profile.MppLiteModule;
import com.mastercard.mcbp.card.profile.Record;
import com.mastercard.mcbp.card.profile.RemotePaymentData;
import com.mastercard.mcbp.utils.crypto.CryptoService;
import com.mastercard.mcbp.utils.crypto.CryptoServiceFactory;
import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

import static com.mastercard.mcbp.utils.crypto.CryptoService.Mode.*;

import flexjson.JSON;

/**
 *
 * */
public class CardProfileMdesCmsC implements McbpDigitizedCardProfileWrapper {
    @JSON(name = "digitizedCardId")
    private String digitizedCardId;
    @JSON(name = "maximumPinTry")
    private int maximumPinTry;
    @JSON(name = "mppLiteModule")
    private MppLiteModuleMdesCmsC mppLiteModule;
    @JSON(name = "businessLogicModule")
    private BusinessLogicModuleMdesCmsC businessLogicModule;
    @JSON(include = false)
    private ByteArray iccKek = null;

    private CardProfileMdesCmsC() {
        // Intentionally No-Op
    }

    public String getDigitizedCardId() {
        return digitizedCardId;
    }

    public void setDigitizedCardId(String digitizedCardId) {
        this.digitizedCardId = digitizedCardId;
    }

    public int getMaximumPinTry() {
        return maximumPinTry;
    }

    public void setMaximumPinTry(int maximumPinTry) {
        this.maximumPinTry = maximumPinTry;
    }

    public void setIccKek(ByteArray iccKek) {
        this.iccKek = iccKek;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getCardId() {
        return digitizedCardId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitizedCardProfile toDigitizedCardProfile() {
        DigitizedCardProfile digitizedCardProfile = new DigitizedCardProfile();

        digitizedCardProfile.setDigitizedCardId(ByteArray.of(this.digitizedCardId));

        boolean clSupported = mppLiteModule.getContactlessPaymentData() != null;
        boolean rpSupported = mppLiteModule.getRemotePaymentData() != null;

        digitizedCardProfile.setMaximumPinTry(this.maximumPinTry);

        digitizedCardProfile.setContactlessSupported(clSupported);
        digitizedCardProfile.setRemotePaymentSupported(rpSupported);

        digitizedCardProfile.setBusinessLogicModule(buildBusinessLogicModule());
        digitizedCardProfile.setMppLiteModule(buildMppLiteModule());

        digitizedCardProfile.setCardMetadata("");

        return digitizedCardProfile;
    }

    private BusinessLogicModule buildBusinessLogicModule() {
        BusinessLogicModule businessLogicModule = new BusinessLogicModule();

        if (this.getBusinessLogicModule().getApplicationLifeCycleData() == null) {
            this.getBusinessLogicModule().setApplicationLifeCycleData("");
        }
        if (this.getBusinessLogicModule().getSecurityWord() == null) {
            this.getBusinessLogicModule().setSecurityWord("");
        }
        if (this.getBusinessLogicModule().getCardLayoutDescription() == null) {
            this.getBusinessLogicModule().setCardLayoutDescription("");
        }

        businessLogicModule.setApplicationLifeCycleData(
                ByteArray.of(this.getBusinessLogicModule().getApplicationLifeCycleData()));
        businessLogicModule.setCvmResetTimeout(this.getBusinessLogicModule().getCvmResetTimeout());
        businessLogicModule.setDualTapResetTimeout(
                this.getBusinessLogicModule().getDualTapResetTimeout());

        // Override any CLD you may have got from the file
        businessLogicModule.setCardLayoutDescription(
                ByteArray.of(this.getBusinessLogicModule().getCardLayoutDescription()));
        //businessLogicModule.setCardLayoutDescription(ByteArray.of(DEFAULT_CLD));

        businessLogicModule.setCardholderValidators(buildCardholderValidators());
        businessLogicModule.setSecurityWord(
                ByteArray.of(this.getBusinessLogicModule().getSecurityWord()));
        businessLogicModule.setMagstripeCvmIssuerOptions(buildMagstripeCvmIssuerOptions());
        businessLogicModule.setMChipCvmIssuerOptions(buildMChipCvmIssuerOptions());

        return businessLogicModule;
    }

    /**
     * Utility function to map the MPP Lite Module data into the SDK internal data structure
     * from the CMS data structure
     */
    private MppLiteModule buildMppLiteModule() {

        MppLiteModule mppProfile = new MppLiteModule();
        mppProfile.setCardRiskManagementData(buildCardRiskManagementData());
        mppProfile.setContactlessPaymentData(buildContactlessPaymentData());
        mppProfile.setRemotePaymentData(buildRemotePaymentData());

        return mppProfile;
    }

    public BusinessLogicModuleMdesCmsC getBusinessLogicModule() {
        return businessLogicModule;
    }

    public void setBusinessLogicModule(BusinessLogicModuleMdesCmsC businessLogicModule) {
        this.businessLogicModule = businessLogicModule;
    }

    /**
     * Utility function to map the Cardholder Validators data into the SDK internal data structure
     * from the CMS data structure
     */
    private CardholderValidators buildCardholderValidators() {
        CardholderValidators cardholderValidators = new CardholderValidators();
        cardholderValidators.setCardholderValidators(
                this.getBusinessLogicModule().getCardholderValidators()[0]);
        return cardholderValidators;
    }

    /**
     * Utility function to map the Magstripe CVM Issuer Options data into the SDK internal data
     * structure from the CMS data structure
     */
    private CvmIssuerOptions buildMagstripeCvmIssuerOptions() {
        CvmIssuerOptions cvmIssuerOptions = new CvmIssuerOptions();

        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(
                businessLogicModule.getMagstripeCvmIssuerOptions()
                        .isAckAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(
                businessLogicModule.getMagstripeCvmIssuerOptions()
                        .isAckAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setAckAutomaticallyResetByApplication(
                businessLogicModule.getMagstripeCvmIssuerOptions()
                        .isAckAutomaticallyResetByApplication());
        cvmIssuerOptions.setAckPreEntryAllowed(
                businessLogicModule.getMagstripeCvmIssuerOptions().isAckPreEntryAllowed());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(
                businessLogicModule.getMagstripeCvmIssuerOptions()
                        .isPinAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(
                businessLogicModule.getMagstripeCvmIssuerOptions()
                        .isPinAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setPinAutomaticallyResetByApplication(
                businessLogicModule.getMagstripeCvmIssuerOptions()
                        .isPinAutomaticallyResetByApplication());
        cvmIssuerOptions.setPinPreEntryAllowed(
                businessLogicModule.getMagstripeCvmIssuerOptions().isPinPreEntryAllowed());
        return cvmIssuerOptions;
    }

    /**
     * Utility function to map the MChip CVM Issuer Options data into the SDK internal data
     * structure from the CMS data structure
     */
    private CvmIssuerOptions buildMChipCvmIssuerOptions() {
        CvmIssuerOptions cvmIssuerOptions = new CvmIssuerOptions();

        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(
                businessLogicModule.getmChipCvmIssuerOptions()
                        .isAckAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(
                businessLogicModule.getmChipCvmIssuerOptions()
                        .isAckAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setAckAutomaticallyResetByApplication(
                businessLogicModule.getmChipCvmIssuerOptions()
                        .isAckAutomaticallyResetByApplication());
        cvmIssuerOptions.setAckPreEntryAllowed(
                businessLogicModule.getmChipCvmIssuerOptions().isAckPreEntryAllowed());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(
                businessLogicModule.getmChipCvmIssuerOptions()
                        .isPinAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(
                businessLogicModule.getmChipCvmIssuerOptions()
                        .isPinAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setPinAutomaticallyResetByApplication(
                businessLogicModule.getmChipCvmIssuerOptions()
                        .isPinAutomaticallyResetByApplication());
        cvmIssuerOptions.setPinPreEntryAllowed(
                businessLogicModule.getmChipCvmIssuerOptions().isPinPreEntryAllowed());
        return cvmIssuerOptions;
    }

    /**
     * Utility function to map the Card Risk Management data into the SDK internal data structure
     * from the CMS data structure
     */
    private CardRiskManagementData buildCardRiskManagementData() {
        // Card Risk Management Data
        CardRiskManagementData cardRiskManagementData = new CardRiskManagementData();
        cardRiskManagementData.setAdditionalCheckTable(
                ByteArray.of(mppLiteModule.getCardRiskManagementData().getAdditionalCheckTable()));
        cardRiskManagementData.setCrmCountryCode(
                ByteArray.of(mppLiteModule.getCardRiskManagementData().getCrmCountryCode()));
        return cardRiskManagementData;
    }

    /**
     * Utility function to map the Contactless Payment data into the SDK internal data structure
     * from the CMS data structure
     */
    private ContactlessPaymentData buildContactlessPaymentData() {
        // Contactless Payment Data
        ContactlessPaymentData contactlessPaymentData = new ContactlessPaymentData();

        contactlessPaymentData.setAid(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getAid()));
        contactlessPaymentData.setPpseFci(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getPpseFci()));
        contactlessPaymentData.setPaymentFci(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getPaymentFci()));
        contactlessPaymentData.setGpoResponse(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getGpoResponse()));
        String cdol1RelatedDataLength =
                mppLiteModule.getContactlessPaymentData().getCdol1RelatedDataLength();
        contactlessPaymentData.setCdol1RelatedDataLength(Integer.valueOf(
                cdol1RelatedDataLength == null || cdol1RelatedDataLength.isEmpty() ?
                        "00" : cdol1RelatedDataLength, 16));
        contactlessPaymentData.setCiacDecline(ByteArray.of(
                mppLiteModule.getContactlessPaymentData().getCiacDecline()));
        contactlessPaymentData.setCvrMaskAnd(ByteArray.of(
                mppLiteModule.getContactlessPaymentData().getCvrMaskAnd()));
        contactlessPaymentData.setIssuerApplicationData(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getIssuerApplicationData()));

        contactlessPaymentData.setIccPrivateKeyCrtComponents(buildIccPrivateKeyCrtComponents());

        contactlessPaymentData.setPinIvCvc3Track2(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getPinIvCvc3Track2()));
        contactlessPaymentData.setCiacDeclineOnPpms(
                ByteArray.of(mppLiteModule.getContactlessPaymentData().getCiacDeclineOnPpms()));

        // Get the Alternate Contactless Payment Data
        contactlessPaymentData.setAlternateContactlessPaymentData(
                buildAlternateContactlessPaymentData());

        contactlessPaymentData.setRecords(buildRecords());

        return contactlessPaymentData;
    }

    /**
     * Utility function to map the Remote Payment data into the SDK internal data structure
     * from the CMS data structure
     */
    private RemotePaymentData buildRemotePaymentData() {
        // Remote Payment Data
        RemotePaymentData remotePaymentData = new RemotePaymentData();
        if (mppLiteModule.getRemotePaymentData() == null) {
            // No Remote Payment data
            remotePaymentData.setPan(prepareValue(""));
            remotePaymentData.setAip(prepareValue(""));
            remotePaymentData.setApplicationExpiryDate(prepareValue(""));
            remotePaymentData.setCiacDecline(prepareValue(""));
            remotePaymentData.setCvrMaskAnd(prepareValue(""));
            remotePaymentData.setIssuerApplicationData(prepareValue(""));
            remotePaymentData.setPanSequenceNumber(prepareValue(""));
            remotePaymentData.setTrack2EquivalentData(prepareValue(""));
            return remotePaymentData;
        }

        remotePaymentData.setTrack2EquivalentData(
                ByteArray.of(mppLiteModule.getRemotePaymentData().getTrack2Equivalent()));
        remotePaymentData.setPan(ByteArray.of(
                Utils.padPan(mppLiteModule.getRemotePaymentData().getPan())));
        remotePaymentData.setPanSequenceNumber(
                ByteArray.of(mppLiteModule.getRemotePaymentData().getPanSequenceNumber()));
        remotePaymentData.setApplicationExpiryDate(
                ByteArray.of(mppLiteModule.getRemotePaymentData().getApplicationExpiryDate()));
        remotePaymentData.setAip(ByteArray.of(mppLiteModule.getRemotePaymentData().getAip()));
        remotePaymentData.setCiacDecline(
                ByteArray.of(mppLiteModule.getRemotePaymentData().getCiacDecline()));
        remotePaymentData.setCvrMaskAnd(
                ByteArray.of(mppLiteModule.getRemotePaymentData().getCvrMaskAnd()));
        remotePaymentData.setIssuerApplicationData(
                ByteArray.of(mppLiteModule.getRemotePaymentData().getIssuerApplicationData()));

        return remotePaymentData;
    }

    /**
     * Utility function to map the Icc Private Key data into the SDK internal data structure
     * from the CMS data structure
     */
    private IccPrivateKeyCrtComponents buildIccPrivateKeyCrtComponents() {
        IccPrivateKeyCrtComponents icc = new IccPrivateKeyCrtComponents();
        ByteArray iccU = decryptIccComponent(mppLiteModule.getContactlessPaymentData().
                getIccPrivateKeyCrtComponents().getuValue());
        ByteArray iccP = decryptIccComponent(mppLiteModule.getContactlessPaymentData().
                getIccPrivateKeyCrtComponents().getpValue());
        ByteArray iccQ = decryptIccComponent(mppLiteModule.getContactlessPaymentData().
                getIccPrivateKeyCrtComponents().getqValue());
        ByteArray iccDp = decryptIccComponent(mppLiteModule.getContactlessPaymentData().
                getIccPrivateKeyCrtComponents().getDpValue());
        ByteArray iccDq = decryptIccComponent(mppLiteModule.getContactlessPaymentData().
                getIccPrivateKeyCrtComponents().getDqValue());

        icc.setU(iccU);
        icc.setP(iccP);
        icc.setQ(iccQ);
        icc.setDp(iccDp);
        icc.setDq(iccDq);

        return icc;
    }

    /**
     * Utility function to map the Alternate Contactless Payment data into the SDK internal data
     * structure from the CMS data structure
     */
    private AlternateContactlessPaymentData buildAlternateContactlessPaymentData() {
        final AlternateContactlessPaymentData alternateContactlessPaymentData =
                new AlternateContactlessPaymentData();

        if (this.getMppLiteModule().getContactlessPaymentData().
                getAlternateContactlessPaymentData() == null) {
            // No Alternate data
            alternateContactlessPaymentData.setPaymentFci(ByteArray.of(""));
            alternateContactlessPaymentData.setAid(ByteArray.of(""));
            alternateContactlessPaymentData.setCiacDecline(ByteArray.of(""));
            alternateContactlessPaymentData.setCvrMaskAnd(ByteArray.of(""));
            alternateContactlessPaymentData.setGpoResponse(ByteArray.of(""));
            return alternateContactlessPaymentData;
        }

        String alternateAid = mppLiteModule.getContactlessPaymentData()
                .getAlternateContactlessPaymentData().getAid();
        if (alternateAid != null && alternateAid.length() != 0) {
            alternateContactlessPaymentData.setAid(
                    ByteArray.of(mppLiteModule.getContactlessPaymentData().
                            getAlternateContactlessPaymentData().getAid()));
        } else {
            alternateContactlessPaymentData.setAid(ByteArray.of(""));
        }

        String alternatePaymentFci = mppLiteModule.getContactlessPaymentData().
                getAlternateContactlessPaymentData().getPaymentFci();

        if (alternatePaymentFci != null && alternatePaymentFci.length() != 0) {
            alternateContactlessPaymentData.setPaymentFci(
                    ByteArray.of(mppLiteModule.getContactlessPaymentData().
                            getAlternateContactlessPaymentData().getPaymentFci()));
        } else {
            alternateContactlessPaymentData.setPaymentFci(ByteArray.of(""));
        }

        String alternateGpoResponse = mppLiteModule.getContactlessPaymentData().
                getAlternateContactlessPaymentData().getGpoResponse();


        if (alternateGpoResponse != null && alternateGpoResponse.length() != 0) {
            alternateContactlessPaymentData.setGpoResponse(
                    ByteArray.of(mppLiteModule.getContactlessPaymentData().
                            getAlternateContactlessPaymentData().getGpoResponse()));
        } else {
            alternateContactlessPaymentData.setGpoResponse(ByteArray.of(""));
        }

        String alternateCiacDecline = mppLiteModule.getContactlessPaymentData().
                getAlternateContactlessPaymentData().getCiacDecline();

        if (alternateCiacDecline != null && alternateCiacDecline.length() != 0) {
            alternateContactlessPaymentData.setCiacDecline(
                    ByteArray.of(mppLiteModule.getContactlessPaymentData().
                            getAlternateContactlessPaymentData().getCiacDecline()));
        } else {
            alternateContactlessPaymentData.setCiacDecline(ByteArray.of(""));
        }

        String alternateCvrMaskAnd = mppLiteModule.getContactlessPaymentData().
                getAlternateContactlessPaymentData().getCvrMaskAnd();

        if (alternateCvrMaskAnd != null && alternateCvrMaskAnd.length() != 0) {
            alternateContactlessPaymentData.setCvrMaskAnd(
                    ByteArray.of(mppLiteModule.getContactlessPaymentData().
                            getAlternateContactlessPaymentData().getCvrMaskAnd()));
        } else {
            alternateContactlessPaymentData.setCvrMaskAnd(ByteArray.of(""));
        }

        return alternateContactlessPaymentData;
    }

    /**
     * Utility function to map the Records data into the SDK internal data structure
     * from the CMS data structure
     */
    private Record[] buildRecords() {
        int numberOfRecords = mppLiteModule.getContactlessPaymentData().getRecords().length;

        Record[] records = new Record[numberOfRecords];

        for (int i = 0; i < mppLiteModule.getContactlessPaymentData().getRecords().length;
             i++) {
            Record record = new Record();
            byte number = (byte) mppLiteModule.getContactlessPaymentData().
                    getRecords()[i].getRecordNumber();
            byte sfi = ByteArray.of(mppLiteModule.getContactlessPaymentData().
                    getRecords()[i].getSfi()).getByte(0);
            record.setRecordNumber(number);
            // Need to convert the record as there is a different format between MCBP v1 and the
            // internal representation used by the SDK
            record.setSfi((byte) (sfi >> 3));
            record.setRecordValue(
                    ByteArray.of(mppLiteModule.getContactlessPaymentData().
                            getRecords()[i].getRecordValue()));
            records[i] = record;
        }
        return records;
    }

    private ByteArray decryptIccComponent(String component) {
        ByteArray inputData = ByteArray.of(component);
        CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        ByteArray result;
        try {
            result = cryptoService.decryptIccComponent(inputData, iccKek);
        } catch (McbpCryptoException e) {
            return null;
        }
        return result;
    }

    static private ByteArray prepareValue(String input) {
        if (input == null) return ByteArray.of("");
        return ByteArray.of(input);
    }

    public MppLiteModuleMdesCmsC getMppLiteModule() {
        return mppLiteModule;
    }

    public void setMppLiteModule(MppLiteModuleMdesCmsC mppLiteModule) {
        this.mppLiteModule = mppLiteModule;
    }
}
