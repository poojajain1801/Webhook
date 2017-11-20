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
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Utils;

public class DigitizedCardProfileMdesContainer implements McbpDigitizedCardProfileWrapper {

    private final DigitizedCardProfileMdes mDigitizedCardProfileMdes;
    private final ByteArray mIccKek;

    /**
     * Logger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);

    /**
     * Default constructor. The Digitized Card Profile must be provided
     *
     * @param digitizedCardProfileMdes The Digitized Card Profile in MDES CMS-D format
     */
    public DigitizedCardProfileMdesContainer(
            final DigitizedCardProfileMdes digitizedCardProfileMdes,
            final ByteArray iccKek) {
        this.mDigitizedCardProfileMdes = digitizedCardProfileMdes;
        this.mIccKek = iccKek;
    }

    // Check if the input string is null. If it is null returns a ByteArray that is equivalent to
    // an empty string
    static private ByteArray prepareValue(String input) {
        if (input == null) return ByteArray.of("");
        return ByteArray.of(input);
    }

    @Override
    public String getCardId() {
        return mDigitizedCardProfileMdes.digitizedCardId;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DigitizedCardProfile toDigitizedCardProfile() {
        final DigitizedCardProfile digitizedCardProfile = new DigitizedCardProfile();

        digitizedCardProfile
                .setDigitizedCardId(ByteArray.of(mDigitizedCardProfileMdes.digitizedCardId));

        final boolean clSupported =
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData() != null;

        boolean rpSupported = false;

        final boolean isRemotePaymentDataNull =
                mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData() == null;

        if (!isRemotePaymentDataNull) {
            final boolean isRemotePaymentDataEmpty =
                    mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData()
                                                           .getPan()
                                                           .isEmpty();
            if (!isRemotePaymentDataEmpty) {
                rpSupported = true;
            }
        }

        digitizedCardProfile.setMaximumPinTry(mDigitizedCardProfileMdes.maximumPinTry);

        digitizedCardProfile.setContactlessSupported(clSupported);
        digitizedCardProfile.setRemotePaymentSupported(rpSupported);

        digitizedCardProfile.setBusinessLogicModule(buildBusinessLogicModule());
        digitizedCardProfile.setMppLiteModule(buildMppLiteModule());

        digitizedCardProfile.setCardMetadata("");

        return digitizedCardProfile;
    }

    /**
     * Utility function to map the Business Logic Module data into the SDK internal data structure
     * from the CMS data structure
     */
    private BusinessLogicModule buildBusinessLogicModule() {
        BusinessLogicModule
                businessLogicModule = new com.mastercard.mcbp.card.profile.BusinessLogicModule();

        businessLogicModule.setApplicationLifeCycleData(prepareValue(
                mDigitizedCardProfileMdes.businessLogicModule.getApplicationLifeCycleData()));
        businessLogicModule.setCvmResetTimeout(
                mDigitizedCardProfileMdes.businessLogicModule.getCvmResetTimeout());
        businessLogicModule.setDualTapResetTimeout(
                mDigitizedCardProfileMdes.businessLogicModule.getDualTapResetTimeout());

        // We ignore the CLD received by the CMS-D and replace with our own as we know it is
        // not valid. Next versions of the SDK may simply ignore it. It is our sample app that needs
        // it.
        businessLogicModule.setCardLayoutDescription(ByteArray.of(
                mDigitizedCardProfileMdes.businessLogicModule.getCardLayoutDescription()));

        businessLogicModule.setCardholderValidators(buildCardholderValidators());
        businessLogicModule.setSecurityWord(
                ByteArray.of(mDigitizedCardProfileMdes.businessLogicModule.getSecurityWord()));
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
     * Utility function to map the Cardholder Validators data into the SDK internal data structure
     * from the CMS data structure
     */
    private CardholderValidators buildCardholderValidators() {
        CardholderValidators cardholderValidators = new CardholderValidators();
        cardholderValidators.setCardholderValidators(
                mDigitizedCardProfileMdes.businessLogicModule.getCardholderValidators()[0]);
        return cardholderValidators;
    }

    /**
     * Utility function to map the Magstripe CVM Issuer Options data into the SDK internal data
     * structure from the CMS data structure
     */
    private CvmIssuerOptions buildMagstripeCvmIssuerOptions() {
        CvmIssuerOptions cvmIssuerOptions = new CvmIssuerOptions();

        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isAckAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isAckAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setAckAutomaticallyResetByApplication(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isAckAutomaticallyResetByApplication());
        cvmIssuerOptions.setAckPreEntryAllowed(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isAckPreEntryAllowed());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isPinAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isPinAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setPinAutomaticallyResetByApplication(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isPinAutomaticallyResetByApplication());
        cvmIssuerOptions.setPinPreEntryAllowed(
                mDigitizedCardProfileMdes.businessLogicModule.getMagstripeCvmIssuerOptions()
                                                             .isPinPreEntryAllowed());

        return cvmIssuerOptions;
    }

    /**
     * Utility function to map the MChip CVM Issuer Options data into the SDK internal data
     * structure from the CMS data structure
     */
    private CvmIssuerOptions buildMChipCvmIssuerOptions() {
        CvmIssuerOptions cvmIssuerOptions = new CvmIssuerOptions();

        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isAckAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isAckAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setAckAutomaticallyResetByApplication(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isAckAutomaticallyResetByApplication());
        cvmIssuerOptions.setAckPreEntryAllowed(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isAckPreEntryAllowed());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isPinAlwaysRequiredIfCurrencyNotProvided());
        cvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isPinAlwaysRequiredIfCurrencyProvided());
        cvmIssuerOptions.setPinAutomaticallyResetByApplication(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isPinAutomaticallyResetByApplication());
        cvmIssuerOptions.setPinPreEntryAllowed(
                mDigitizedCardProfileMdes.businessLogicModule.getMChipCvmIssuerOptions()
                                                             .isPinPreEntryAllowed());

        return cvmIssuerOptions;
    }

    /**
     * Utility function to map the Card Risk Management data into the SDK internal data structure
     * from the CMS data structure
     */
    private CardRiskManagementData buildCardRiskManagementData() {
        // Card Risk Management Data
        CardRiskManagementData cardRiskManagementData = new CardRiskManagementData();
        cardRiskManagementData.setAdditionalCheckTable(ByteArray.of(
                mDigitizedCardProfileMdes.mppLiteModule.getCardRiskManagementData()
                                                       .getAdditionalCheckTable()));
        cardRiskManagementData.setCrmCountryCode(
                ByteArray.of(mDigitizedCardProfileMdes.mppLiteModule.getCardRiskManagementData()
                                                                    .getCrmCountryCode()));

        return cardRiskManagementData;
    }

    /**
     * Utility function to map the Contactless Payment data into the SDK internal data structure
     * from the CMS data structure
     */
    private ContactlessPaymentData buildContactlessPaymentData() {
        // Contactless Payment Data
        ContactlessPaymentData contactlessPaymentData = new ContactlessPaymentData();

        contactlessPaymentData.setAid(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData().getAid()));
        contactlessPaymentData.setPpseFci(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData().getPpseFci()));
        contactlessPaymentData.setPaymentFci(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getContactlessPaymentData().getPaymentFci()));
        contactlessPaymentData.setGpoResponse(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getContactlessPaymentData().getGpoResponse()));
        String cdol1RelatedDataLength = mDigitizedCardProfileMdes
                .mppLiteModule
                .getContactlessPaymentData()
                .getCdol1RelatedDataLength();
        contactlessPaymentData.setCdol1RelatedDataLength(Integer.parseInt(
                cdol1RelatedDataLength == null || cdol1RelatedDataLength.isEmpty() ?
                "00" : cdol1RelatedDataLength, 16));
        contactlessPaymentData.setCiacDecline(ByteArray.of(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getContactlessPaymentData().getCiacDecline()));
        contactlessPaymentData.setCvrMaskAnd(ByteArray.of(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getContactlessPaymentData().getCvrMaskAnd()));
        contactlessPaymentData.setIssuerApplicationData(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getIssuerApplicationData()));

        contactlessPaymentData.setIccPrivateKeyCrtComponents(buildIccPrivateKeyCrtComponents());

        contactlessPaymentData.setPinIvCvc3Track2(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getPinIvCvc3Track2()));
        contactlessPaymentData.setCiacDeclineOnPpms(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getCiacDeclineOnPpms()));

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

        if (mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData() == null) {
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

        remotePaymentData.setTrack2EquivalentData(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getRemotePaymentData().getTrack2Equivalent()));

        remotePaymentData.setPan(prepareValue(Utils.padPan(
                mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData().getPan())));


        remotePaymentData.setPanSequenceNumber(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getRemotePaymentData().getPanSequenceNumber()));
        remotePaymentData.setApplicationExpiryDate(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getRemotePaymentData().getApplicationExpiryDate()));
        remotePaymentData.setAip(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData().getAip()));
        remotePaymentData.setCiacDecline(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData().getCiacDecline()));
        remotePaymentData.setCvrMaskAnd(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getRemotePaymentData().getCvrMaskAnd()));
        remotePaymentData.setIssuerApplicationData(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule
                        .getRemotePaymentData().getIssuerApplicationData()));

        return remotePaymentData;
    }

    /**
     * Utility function to map the Icc Private Key data into the SDK internal data structure
     * from the CMS data structure
     */
    private IccPrivateKeyCrtComponents
    buildIccPrivateKeyCrtComponents() {
        final IccPrivateKeyCrtComponents icc = new IccPrivateKeyCrtComponents();

        // Decrypt IccPrivateKeyCrtComponents values by decrypted IccKek.
        final ByteArray iccU = decryptIccComponent(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getIccPrivateKeyCrtComponents().getU()));
        final ByteArray iccP = decryptIccComponent(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getIccPrivateKeyCrtComponents().getP()));
        final ByteArray iccQ = decryptIccComponent(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getIccPrivateKeyCrtComponents().getQ()));
        final ByteArray iccDp = decryptIccComponent(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getIccPrivateKeyCrtComponents().getDp()));
        final ByteArray iccDq = decryptIccComponent(prepareValue(
                mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                       .getIccPrivateKeyCrtComponents().getDq()));

        if (iccU == null || iccP == null || iccQ == null || iccDp == null || iccDq == null) {
            // No Icc PrivateKeyCrt Components data
            icc.setU(prepareValue(""));
            icc.setP(prepareValue(""));
            icc.setQ(prepareValue(""));
            icc.setDp(prepareValue(""));
            icc.setDq(prepareValue(""));
            return icc;
        }

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
        AlternateContactlessPaymentData alternateContactlessPaymentData =
                new AlternateContactlessPaymentData();

        if (mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                   .getAlternateContactlessPaymentData() == null) {
            // No Alternate data
            alternateContactlessPaymentData.setPaymentFci(prepareValue(""));
            alternateContactlessPaymentData.setAid(prepareValue(""));
            alternateContactlessPaymentData.setCiacDecline(prepareValue(""));
            alternateContactlessPaymentData.setCvrMaskAnd(prepareValue(""));
            alternateContactlessPaymentData.setGpoResponse(prepareValue(""));
            return alternateContactlessPaymentData;
        }

        alternateContactlessPaymentData.setAid(
                prepareValue(mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                                    .getAlternateContactlessPaymentData()
                                                                    .getAid()));

        alternateContactlessPaymentData.setPaymentFci(
                prepareValue(mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                                    .getAlternateContactlessPaymentData()
                                                                    .getPaymentFci()));

        alternateContactlessPaymentData.setGpoResponse(
                prepareValue(mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                                    .getAlternateContactlessPaymentData()
                                                                    .getGpoResponse()));

        alternateContactlessPaymentData.setCiacDecline(
                prepareValue(mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                                    .getAlternateContactlessPaymentData()
                                                                    .getCiacDecline()));

        alternateContactlessPaymentData.setCvrMaskAnd(
                prepareValue(mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                                    .getAlternateContactlessPaymentData()
                                                                    .getCvrMaskAnd()));

        return alternateContactlessPaymentData;
    }

    /**
     * Utility function to map the Records data into the SDK internal data structure
     * from the CMS data structure
     */
    private Record[] buildRecords() {
        final int numberOfRecords = mDigitizedCardProfileMdes.mppLiteModule
                .getContactlessPaymentData().getRecords().length;

        final Record[] records = new Record[numberOfRecords];

        for (int i = 0; i < numberOfRecords; i++) {
            Record record = new Record();

            final byte number =
                    (byte) mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                                  .getRecords()[i]
                            .getRecordNumber();
            byte sfi = (byte) Integer.parseInt(
                    mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                           .getRecords()[i].getSfi(), 16);
            record.setRecordNumber(number);

            // Need to convert the record as there is a different format between MDES and the
            // internal representation used by the SDK
            sfi = (byte) (sfi >> 3);
            record.setSfi(sfi);

            record.setRecordValue(prepareValue(
                    mDigitizedCardProfileMdes.mppLiteModule.getContactlessPaymentData()
                                                           .getRecords()[i].getRecordValue()));
            records[i] = record;
        }
        return records;
    }

    /**
     * @param inputData The ICC Component to be decrypted
     * @return The Decrypted ICC component
     */
    private ByteArray decryptIccComponent(ByteArray inputData) {
        CryptoService cryptoService = CryptoServiceFactory.getDefaultCryptoService();
        ByteArray result;
        try {
            result = cryptoService.decryptIccComponent(inputData, mIccKek);
        } catch (McbpCryptoException e) {
            mLogger.d(e.getMessage());
            return null;
        }
        return result;
    }
}
