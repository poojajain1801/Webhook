package com.comviva.mfs.promotion.util.cpadapter;

import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import com.mastercard.mcbp.remotemanagement.mdes.profile.*;
import lombok.Getter;
import lombok.Setter;

import java.security.GeneralSecurityException;

/**
 * Generates Card profile in MPA format.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Setter
@Getter
public class CardProfile {
    private String digitizedCardId;

    private Records getRecords(int recordNumber, String sfi, String recordValue) {
        Records records = new Records();
        records.setSfi(sfi);
        records.setRecordNumber(recordNumber);
        records.setRecordValue(recordValue);
        return records;
    }

    private MagstripeCvmIssuerOptions getMagstripeCvmIssuerOptions() {
        MagstripeCvmIssuerOptions magstripeCvmIssuerOptions = new MagstripeCvmIssuerOptions();
        magstripeCvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(false);
        magstripeCvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(false);
        magstripeCvmIssuerOptions.setAckAutomaticallyResetByApplication(false);
        magstripeCvmIssuerOptions.setAckPreEntryAllowed(false);
        magstripeCvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(true);
        magstripeCvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(true);
        magstripeCvmIssuerOptions.setPinAutomaticallyResetByApplication(false);
        magstripeCvmIssuerOptions.setPinPreEntryAllowed(true);
        return magstripeCvmIssuerOptions;
    }

    private MChipCvmIssuerOptions getmChipCvmIssuerOptions() {
        MChipCvmIssuerOptions mChipCvmIssuerOptions = new MChipCvmIssuerOptions();
        mChipCvmIssuerOptions.setAckAlwaysRequiredIfCurrencyNotProvided(false);
        mChipCvmIssuerOptions.setAckAlwaysRequiredIfCurrencyProvided(false);
        mChipCvmIssuerOptions.setAckAutomaticallyResetByApplication(false);
        mChipCvmIssuerOptions.setAckPreEntryAllowed(false);
        mChipCvmIssuerOptions.setPinAlwaysRequiredIfCurrencyNotProvided(true);
        mChipCvmIssuerOptions.setPinAlwaysRequiredIfCurrencyProvided(true);
        mChipCvmIssuerOptions.setPinAutomaticallyResetByApplication(false);
        mChipCvmIssuerOptions.setPinPreEntryAllowed(true);
        return mChipCvmIssuerOptions;
    }

    private RemotePaymentData getRemotePaymentData(String pan) {
        RemotePaymentData remotePaymentData = new RemotePaymentData();
        remotePaymentData.setAip("1A00");
        remotePaymentData.setApplicationExpiryDate("181130");
        remotePaymentData.setCiacDecline("010008");
        remotePaymentData.setCvrMaskAnd("FF0000000000");
        remotePaymentData.setIssuerApplicationData("0314000000000000000000000000000000FF");
        remotePaymentData.setPan(pan);
        remotePaymentData.setPanSequenceNumber("01");
        remotePaymentData.setTrack2Equivalent("5480981500100002D18112011000000000000F");
        return null;
    }

    private IccPrivateKeyCrtComponents getIccPrivateKeyCrtComponents(byte[] iccKek) {
        byte[] p = ArrayUtil.getByteArray("101565610013301240713207239558950144682174355406589305284428666903702505233009");
        byte[] q = ArrayUtil.getByteArray("89468719188754548893545560595594841381237600305314352142924213312069293984003");
        byte[] dp = ArrayUtil.getByteArray("93508487983621011980308809077436163233486980736420426663592427234014400426465");
        byte[] dq = ArrayUtil.getByteArray("39924206061844862938366722914051164017185614552526332124140845908593107749243");
        byte[] u = ArrayUtil.getByteArray("82979745043413288095388081210478420482729140505221184605143714377105157807297");

        byte[] encP = null, encQ = null, encDp = null, encDq = null, encU = null;
        try {
            encP = AESUtil.cipherECB(p, iccKek, AESUtil.Padding.ISO7816_4, true);
            encQ = AESUtil.cipherECB(q, iccKek, AESUtil.Padding.ISO7816_4, true);
            encDp = AESUtil.cipherECB(dp, iccKek, AESUtil.Padding.ISO7816_4, true);
            encDq = AESUtil.cipherECB(dq, iccKek, AESUtil.Padding.ISO7816_4, true);
            encU = AESUtil.cipherECB(u, iccKek, AESUtil.Padding.ISO7816_4, true);
        } catch (GeneralSecurityException e) {
            e.printStackTrace();
        }

        IccPrivateKeyCrtComponents iccPrivateKeyCrtComponents = new IccPrivateKeyCrtComponents();
        iccPrivateKeyCrtComponents.setP(ArrayUtil.getHexString(encP));
        iccPrivateKeyCrtComponents.setQ(ArrayUtil.getHexString(encQ));
        iccPrivateKeyCrtComponents.setDp(ArrayUtil.getHexString(encDp));
        iccPrivateKeyCrtComponents.setDq(ArrayUtil.getHexString(encDq));
        iccPrivateKeyCrtComponents.setU(ArrayUtil.getHexString(encU));
        return iccPrivateKeyCrtComponents;
    }

    public DigitizedCardProfileMdes getDigitizedCardProfileMdes(byte[] iccKek, String pan, String digitizedCardId) {
        DigitizedCardProfileMdes cardProfile = new DigitizedCardProfileMdes();

        MppLiteModule mppLiteModule = new MppLiteModule();
        // Card Risk Management Data
        CardRiskManagementData cardRiskManagementData = new CardRiskManagementData();
        cardRiskManagementData.setAdditionalCheckTable("000000000000000000000000000000000000");
        cardRiskManagementData.setCrmCountryCode("0840");
        mppLiteModule.setCardRiskManagementData(cardRiskManagementData);
        // Contactless Payment Data
        ContactlessPaymentData contactlessPaymentData = new ContactlessPaymentData();
        contactlessPaymentData.setAid("A0000000041010");
        contactlessPaymentData.setAlternateContactlessPaymentData(null);
        contactlessPaymentData.setCdol1RelatedDataLength("2D");
        contactlessPaymentData.setCiacDecline("010008");
        contactlessPaymentData.setCiacDeclineOnPpms("4100");
        contactlessPaymentData.setCvrMaskAnd("FFFFFFFFFFFF");
        contactlessPaymentData.setGpoResponse("770E82021B8094080801010010010301");
        contactlessPaymentData.setIccPrivateKeyCrtComponents(getIccPrivateKeyCrtComponents(iccKek));
        contactlessPaymentData.setIssuerApplicationData("0314000100000000000000000000000000FF");
        contactlessPaymentData.setPaymentFci("8407A0000000041010A52D500A4D4153544552434152448701015F2D02656E9F38099F1D089F1A029F3501BF0C0A9F6E0708400000313400");
        contactlessPaymentData.setPinIvCvc3Track2("56DE");
        contactlessPaymentData.setPpseFci("6F39840E325041592E5359532E4444463031A527BF0C2461224F07A0000000041010500A4D4153544552434152448701015F550255534203545501");
        // SFI-1, Record-1
        Records[] records = new Records[5];
        records[0] = getRecords(1, "0C", "7081919F6C0200019F62060000000700009F630600000078F0009F640104563442353438303938313530303130303030325E202F5E313831313230313130303030303030303030303030303030303030303030309F650200E09F66020F1E9F6B135480981500100002D18112011000000000000F9F6701049F69199F6A049F7E019F02065F2A029F1A029C019A039F15029F3501");
        // SFI-2, Record-1
        records[1] = getRecords(1, "14", "70819157135480981500100002D18112011000000000000F5A0854809815001000025F24031811305F25034912315F280208405F3401018C249F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F15028E0A00000000000000001F039F070200009F080200029F0D0500600000009F0E0500000000009F0F0500600000009F420208409F4A0182");
        // SFI-2, Record-2
        records[2] = getRecords(2, "14", "70138F01029F320101920312345690051234567890");
        // SFI-2, Record-3
        records[3] = getRecords(3, "14", "7081BB9F4701039F48009F4681B072492120CAEFC82D7AA8EAA175ADDEC1699A064A24C71D2F8FE68899F4E1AE8422E4EAE5B9E55B7A18502B9DCDFF7CDDE6DCD7C725706E0276B87A62FF03C4D902632852C02184BB9AB5D6AC75358AC0180FA3BDFCE46EB27DE98573DE161DC7A8D0C04F8032B10B291A3CE383AD136691874EEA53F4366878CE8A6AB08A22BE0E53EF57E3AE38C465054B1187CBEBF49B6CBBAF315AA51741DA6D176569CF433E21350E22833C5C975E77ADF0BF3B44");
        // SFI-3, Record-1
        records[4] = getRecords(1, "1C", "70295A0854809815001000025F24031811305F34010157135480981500100002D18112011000000000000F");
        contactlessPaymentData.setRecords(records);
        mppLiteModule.setContactlessPaymentData(contactlessPaymentData);
        // Remote Payment Data
        mppLiteModule.setRemotePaymentData(getRemotePaymentData(pan));

        // Business Logic
        BusinessLogicModule businessLogicModule = new BusinessLogicModule();
        businessLogicModule.setCvmResetTimeout(30);
        businessLogicModule.setDualTapResetTimeout(30);
        businessLogicModule.setApplicationLifeCycleData(null);
        businessLogicModule.setCardLayoutDescription("11018000");
        businessLogicModule.setSecurityWord("00000000000000000000000000000000");
        String[] cardholderValidators = new String[1];
        cardholderValidators[0] = "DEVICE_MOBILE_PIN";
        businessLogicModule.setCardholderValidators(cardholderValidators);
        businessLogicModule.setChipCvmIssuerOptions(getmChipCvmIssuerOptions());
        businessLogicModule.setMagstripeCvmIssuerOptions(getMagstripeCvmIssuerOptions());

        cardProfile.mppLiteModule = mppLiteModule;
        cardProfile.businessLogicModule = businessLogicModule;
        cardProfile.digitizedCardId = digitizedCardId;
        cardProfile.maximumPinTry = 3;
        return cardProfile;
    }

}
