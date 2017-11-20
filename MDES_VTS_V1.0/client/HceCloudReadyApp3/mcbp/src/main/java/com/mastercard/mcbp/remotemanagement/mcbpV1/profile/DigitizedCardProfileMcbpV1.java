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

package com.mastercard.mcbp.remotemanagement.mcbpV1.profile;

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
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import flexjson.JSON;

/**
 * @deprecated Use MDES build flavour instead
 * */
@Deprecated
public class DigitizedCardProfileMcbpV1 implements McbpDigitizedCardProfileWrapper {
    @JSON(name = "DC_CP_MK")
    public ByteArray digitizedCardProfileMk;

    @JSON(name = "RP_Supported")
    public boolean rpSupported;

    @JSON(name = "DC_CP_LDE")
    public ByteArray digitizedCardProfileLde;

    @JSON(name = "DC_CP_MPP")
    public DigitizedCardProfileMpp digitizedCardProfileMpp;

    @JSON(name = "CL_Supported")
    public boolean clSupported;

    @JSON(name = "DC_CP_BL")
    public DigitizedCardProfileBusinessLogic digitizedCardProfileBusinessLogic;

    @JSON(name = "DC_ID")
    public String digitizedCardId;

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

        digitizedCardProfile.setContactlessSupported(this.clSupported);
        digitizedCardProfile.setRemotePaymentSupported(this.rpSupported);

        digitizedCardProfile.setMppLiteModule(buildMppLiteModule());
        digitizedCardProfile.setBusinessLogicModule(buildBusinessLogicModule());

        return digitizedCardProfile;
    }

    private BusinessLogicModule buildBusinessLogicModule() {
        BusinessLogicModule businessLogicModule = new BusinessLogicModule();

        businessLogicModule.setApplicationLifeCycleData(
                ByteArray.of(this.digitizedCardProfileBusinessLogic.applicationLifeCycleData));
        businessLogicModule.setCvmResetTimeout(this.digitizedCardProfileBusinessLogic
                                                       .cvmResetTimeout);
        businessLogicModule.setDualTapResetTimeout(this.digitizedCardProfileBusinessLogic
                                                           .dualTapResetTimeout);
        businessLogicModule
                .setCardLayoutDescription(
                        ByteArray.of(this.digitizedCardProfileBusinessLogic.cardLayoutDescription));
        businessLogicModule.setCardholderValidators(buildCardholderValidators());
        businessLogicModule.setSecurityWord(
                ByteArray.of(this.digitizedCardProfileBusinessLogic.securityWord));
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

    /**
     * Utility function to map the Card Risk Management data into the SDK internal data structure
     * from the CMS data structure
     */
    private CardRiskManagementData buildCardRiskManagementData() {
        // Card Risk Management Data
        CardRiskManagementData cardRiskManagementData = new CardRiskManagementData();
        cardRiskManagementData.setAdditionalCheckTable(
                ByteArray.of(this.digitizedCardProfileMpp.
                                     cardRiskManagementData.additionalCheckTable));
        cardRiskManagementData.setCrmCountryCode(
                ByteArray.of(this.digitizedCardProfileMpp.cardRiskManagementData.crmCountryCode));
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
                prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.aid));
        contactlessPaymentData.setPpseFci(
                prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.ppseFci));
        contactlessPaymentData.setPaymentFci(
                prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.paymentFci));
        contactlessPaymentData.setGpoResponse(
                prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.gpoResponse));
        contactlessPaymentData.setCdol1RelatedDataLength(
                this.digitizedCardProfileMpp.contactlessPaymentData.cdol1RelatedDataLength);
        contactlessPaymentData.setCiacDecline(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.ciacDecline));
        contactlessPaymentData.setCvrMaskAnd(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.cvrMaskAnd));
        contactlessPaymentData.setIssuerApplicationData(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.issuerApplicationData));

        contactlessPaymentData.setIccPrivateKeyCrtComponents(buildIccPrivateKeyCrtComponents());

        contactlessPaymentData.setPinIvCvc3Track2(
                prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.pinIvCvc3Track2));
        contactlessPaymentData.setCiacDeclineOnPpms(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.ciacDeclineOnPpms));

        // Get the Alternate Contactless Payment Data
        contactlessPaymentData.setAlternateContactlessPaymentData(
                buildAlternateContactlessPaymentData());

        contactlessPaymentData.setRecords(buildRecords());

        return contactlessPaymentData;
    }

    /**
     * Utility function to map the Icc Private Key data into the SDK internal data structure
     * from the CMS data structure
     */
    private IccPrivateKeyCrtComponents buildIccPrivateKeyCrtComponents() {
        IccPrivateKeyCrtComponents icc = new IccPrivateKeyCrtComponents();
        icc.setU(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.iccPrivateKeyA));
        icc.setP(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.iccPrivateKeyP));
        icc.setQ(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.iccPrivateKeyQ));
        icc.setDp(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.iccPrivateKeyDp));
        icc.setDq(prepareValue(
                this.digitizedCardProfileMpp.contactlessPaymentData.iccPrivateKeyDq));
        return icc;
    }

    /**
     * Utility function to map the Alternate Contactless Payment data into the SDK internal data
     * structure from the CMS data structure
     */
    private AlternateContactlessPaymentData buildAlternateContactlessPaymentData() {
        AlternateContactlessPaymentData alternateContactlessPaymentData =
                new AlternateContactlessPaymentData();

        String alternateAid = this.digitizedCardProfileMpp.contactlessPaymentData
                .alternateContactlessPaymentData.aid;

        alternateContactlessPaymentData.setAid(prepareValue(alternateAid));

        String alternatePaymentFci = this.digitizedCardProfileMpp.contactlessPaymentData
                .alternateContactlessPaymentData.paymentFci;

        alternateContactlessPaymentData.setPaymentFci(prepareValue(alternatePaymentFci));

        String alternateGpoResponse = this.digitizedCardProfileMpp.contactlessPaymentData
                .alternateContactlessPaymentData.gpoResponse;

        alternateContactlessPaymentData.setGpoResponse(prepareValue(alternateGpoResponse));

        String alternateCiacDecline = this.digitizedCardProfileMpp.contactlessPaymentData
                .alternateContactlessPaymentData.ciacDecline;

        alternateContactlessPaymentData.setCiacDecline(prepareValue(alternateCiacDecline));

        String alternateCvrMaskAnd = this.digitizedCardProfileMpp.contactlessPaymentData
                .alternateContactlessPaymentData.cvrMaskAnd;

        alternateContactlessPaymentData.setCvrMaskAnd(prepareValue(alternateCvrMaskAnd));

        return alternateContactlessPaymentData;
    }

    /**
     * Utility function to map the Records data into the SDK internal data structure
     * from the CMS data structure
     */
    private Record[] buildRecords() {
        int numberOfRecords = this.digitizedCardProfileMpp.contactlessPaymentData.records.length;

        Record[] records = new Record[numberOfRecords];

        for (int i = 0; i < numberOfRecords; i++) {
            Record record = new Record();
            byte number =
                    prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.records[i]
                                         .recordNumber).getByte(0);
            byte sfi = prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.records[i]
                                            .sfi).getByte(0);
            record.setRecordNumber(number);
            // Need to convert the record as there is a different format between MCBP v1 and the
            // internal representation used by the SDK
            record.setSfi((byte) (sfi >> 3));
            record.setRecordValue(
                    prepareValue(this.digitizedCardProfileMpp.contactlessPaymentData.records[i]
                                         .recordValue));
            records[i] = record;
        }
        return records;
    }

    /**
     * Utility function to map the Remote Payment data into the SDK internal data structure
     * from the CMS data structure
     */
    private RemotePaymentData buildRemotePaymentData() {
        // Remote Payment Data
        RemotePaymentData remotePaymentData = new RemotePaymentData();

        remotePaymentData.setTrack2EquivalentData(
                prepareValue(this.digitizedCardProfileMpp.remotePaymentData.track2EquivalentData));
        remotePaymentData.setPan(prepareValue(
                Utils.padPan(this.digitizedCardProfileMpp.remotePaymentData.pan)));
        remotePaymentData.setPanSequenceNumber(
                prepareValue(this.digitizedCardProfileMpp.remotePaymentData.panSequenceNumber));
        remotePaymentData.setApplicationExpiryDate(
                prepareValue(this.digitizedCardProfileMpp.remotePaymentData.applicationExpiryDate));
        remotePaymentData.setAip(prepareValue(this.digitizedCardProfileMpp.remotePaymentData.aip));
        remotePaymentData.setCiacDecline(
                prepareValue(this.digitizedCardProfileMpp.remotePaymentData.ciacDecline));
        remotePaymentData.setCvrMaskAnd(
                prepareValue(this.digitizedCardProfileMpp.remotePaymentData.cvrMaskAnd));
        remotePaymentData.setIssuerApplicationData(
                prepareValue(this.digitizedCardProfileMpp.remotePaymentData.issuerApplicationData));

        return remotePaymentData;
    }

    /**
     * Utility function to map the Cardholder Validators data into the SDK internal data structure
     * from the CMS data structure
     */
    private CardholderValidators buildCardholderValidators() {
        CardholderValidators cardholderValidators = new CardholderValidators();
        cardholderValidators.setCardholderValidators(this.digitizedCardProfileBusinessLogic
                                                             .cardholderValidators.cvm);
        return cardholderValidators;
    }

    /**
     * Utility function to map the Magstripe CVM Issuer Options data into the SDK internal data
     * structure from the CMS data structure
     */
    private CvmIssuerOptions buildMagstripeCvmIssuerOptions() {
        CvmIssuerOptions cvmIssuerOptions = new CvmIssuerOptions();

        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .ackAlwaysRequiredIfCurrencyNotProvided);
        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .ackAlwaysRequiredIfCurrencyProvided);
        cvmIssuerOptions.setAckAutomaticallyResetByApplication(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .ackAutomaticallyResetByApplication);
        cvmIssuerOptions.setAckPreEntryAllowed(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .ackPreEntryAllowed);
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .pinAlwaysRequiredIfCurrencyNotProvided);
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .pinAlwaysRequiredIfCurrencyProvided);
        cvmIssuerOptions.setPinAutomaticallyResetByApplication(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .pinAutomaticallyResetByApplication);
        cvmIssuerOptions.setPinPreEntryAllowed(
                this.digitizedCardProfileBusinessLogic.magstripeCvmIssuerOptions
                        .pinPreEntryAllowed);
        return cvmIssuerOptions;
    }

    /**
     * Utility function to map the MChip CVM Issuer Options data into the SDK internal data
     * structure from the CMS data structure
     */
    private CvmIssuerOptions buildMChipCvmIssuerOptions() {
        CvmIssuerOptions cvmIssuerOptions = new CvmIssuerOptions();

        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions
                        .ackAlwaysRequiredIfCurrencyNotProvided);
        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions
                        .ackAlwaysRequiredIfCurrencyProvided);
        cvmIssuerOptions.setAckAutomaticallyResetByApplication(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions
                        .ackAutomaticallyResetByApplication);
        cvmIssuerOptions.setAckPreEntryAllowed(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions.ackPreEntryAllowed);
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions
                        .pinAlwaysRequiredIfCurrencyNotProvided);
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions
                        .pinAlwaysRequiredIfCurrencyProvided);
        cvmIssuerOptions.setPinAutomaticallyResetByApplication(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions
                        .pinAutomaticallyResetByApplication);
        cvmIssuerOptions.setPinPreEntryAllowed(
                this.digitizedCardProfileBusinessLogic.mChipCVM_IssuerOptions.pinPreEntryAllowed);
        return cvmIssuerOptions;
    }

    // Check if the input string is null. If it is null returns a ByteArray that is equivalent to
    // an empty string
    static private ByteArray prepareValue(String input) {
        if (input == null) return ByteArray.of("");
        return ByteArray.of(input);
    }

    public static DigitizedCardProfileMcbpV1 valueOf(byte[] content) {
        return new JsonUtils<DigitizedCardProfileMcbpV1>(DigitizedCardProfileMcbpV1.class).valueOf(
                content);
    }

    public String toJsonString() {
        return new JsonUtils<DigitizedCardProfileMcbpV1>(DigitizedCardProfileMcbpV1.class).toJsonString(this);
    }
}
