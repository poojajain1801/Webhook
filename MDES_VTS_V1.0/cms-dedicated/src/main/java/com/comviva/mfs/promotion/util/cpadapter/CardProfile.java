package com.comviva.mfs.promotion.util.cpadapter;

import com.comviva.mfs.promotion.util.ArrayUtil;
import com.comviva.mfs.promotion.util.aes.AESUtil;
import com.mastercard.mcbp.remotemanagement.mdes.profile.*;
import flexjson.JSONDeserializer;
import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

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
        byte[] p = ArrayUtil.getByteArray("f90ca5ac46dc22667b7a58f1ca048beae65af5ee3856783922aa91fb27fd4a10a0c17b1ea8fd10e92dfe1bc41bf77d409639414feb7cbdf9a4e3dcedeef0961b8b70943a45e6a7482b5d012db349ca5d556dba753fd1755fa7e56be58e9da62d96b093c92e7dd30936e6942f1f90db243da83fb96d939c975db4c617d04df83f");
        byte[] q = ArrayUtil.getByteArray("b413b2a68aa7f1b9fddc64f355d3108bea3bbf8e8eca61be01fecad9bb9b8247a011ad27d62b2b8b790cb82d81dddf62485d168d5be3f0bbca43aa7a5d7b614305ed2265f0faa74142fd58c169ca8dbe32c8fb554bab4836d12bbc02371279641816f7a6e12cc88ec2abfe6910fc0e876f7624b2b0aac6a47ba3a0e24bd8bf51");
        byte[] dp = ArrayUtil.getByteArray("c6b774a9d5906852654be6146720e193786233c0f46aea5a8c729530556420e8cb9c15e50ce97fc8cecde6d1bf8ee5fdd6dfb06158809c04c9b096e3b2b77cce55a06a6564c8f9c8f7f0a9b4114e5b8dc5505c6a99954396239d474e4f5132fb453577761b068290e3a16fb379f85722bf9fb3d8fcb9bf44779c7a431e0e5879");
        byte[] dq = ArrayUtil.getByteArray("a4f3663227d0af1c2fee328ab393231506fab3fd61cf00b98c1a58b619a3d932c2ed6e2f8f7efbe4467de037cf3dede19967abc0d0eb7b1889ae71faa4a6dff104fcb305ae37ebfd5bdfb5ed757a955c2428f610abbfb9e67cb41303f46e77c3b84d518d0bb67a004b524138fcd5e67929208f7d1f574f9e5ba3073c433dc801");
        byte[] qInv = ArrayUtil.getByteArray("627578ddc9466db48adc8fffb70f05f476b6717c09aa20fdd00ed84b776f6c9d23b3f9e21ffe84ceb906b8dc6220dca1ca0814b374f37b09a065b82ff04f3261013daf803cb658badde6edd157b43fe5b098526b69b3697303dcb3ce9504ab85cad85f137d64ba024bcb99af2907ac28594111ad1e02d8c8a0022cab08659004");

        byte[] encP = null, encQ = null, encDp = null, encDq = null, encU = null;
        try {
            encP = AESUtil.cipherECB(p, iccKek, AESUtil.Padding.ISO7816_4, true);
            encQ = AESUtil.cipherECB(q, iccKek, AESUtil.Padding.ISO7816_4, true);
            encDp = AESUtil.cipherECB(dp, iccKek, AESUtil.Padding.ISO7816_4, true);
            encDq = AESUtil.cipherECB(dq, iccKek, AESUtil.Padding.ISO7816_4, true);
            encU = AESUtil.cipherECB(qInv, iccKek, AESUtil.Padding.ISO7816_4, true);
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
        contactlessPaymentData.setPaymentFci("6F388407A0000000041010A52D500A4D4153544552434152448701015F2D02656E9F38099F1D089F1A029F3501BF0C0A9F6E0708400000313400");
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

    public DigitizedCardProfileMdes getDigitizedCardProfileMdes(JSONObject jsCardProfile) {
        DigitizedCardProfileMdes digitizedCardProfileMdes = new DigitizedCardProfileMdes();

        digitizedCardProfileMdes.maximumPinTry = jsCardProfile.getInt("maximumPinTry");
        digitizedCardProfileMdes.digitizedCardId = jsCardProfile.getString("digitizedCardId");

        // BusinessLogicModule
        digitizedCardProfileMdes.businessLogicModule = (BusinessLogicModule) new JSONDeserializer()
                .deserialize(jsCardProfile.getJSONObject("businessLogicModule").toString(), BusinessLogicModule.class);

        // MppLiteModule
        JSONObject jsMppLiteModule = jsCardProfile.getJSONObject("mppLiteModule");
        digitizedCardProfileMdes.mppLiteModule = new MppLiteModule();

        // CardRiskManagementData
        CardRiskManagementData cardRiskManagementData = (CardRiskManagementData) new JSONDeserializer()
                .deserialize(jsMppLiteModule.getJSONObject("cardRiskManagementData").toString(), CardRiskManagementData.class);
        digitizedCardProfileMdes.mppLiteModule.setCardRiskManagementData(cardRiskManagementData);

        // ContactlessPaymentData
        JSONObject jsContactlessPaymentData = jsMppLiteModule.getJSONObject("contactlessPaymentData");
        ContactlessPaymentData contactlessPaymentData = new ContactlessPaymentData();
        contactlessPaymentData.setAid(jsContactlessPaymentData.getString("aid"));
        contactlessPaymentData.setPpseFci(jsContactlessPaymentData.getString("ppseFci"));
        contactlessPaymentData.setPaymentFci(jsContactlessPaymentData.getString("paymentFci"));
        contactlessPaymentData.setGpoResponse(jsContactlessPaymentData.getString("gpoResponse"));
        contactlessPaymentData.setCdol1RelatedDataLength(jsContactlessPaymentData.getString("cdol1RelatedDataLength"));
        contactlessPaymentData.setCiacDecline(jsContactlessPaymentData.getString("ciacDecline"));
        contactlessPaymentData.setCvrMaskAnd(jsContactlessPaymentData.getString("cvrMaskAnd"));
        contactlessPaymentData.setIssuerApplicationData(jsContactlessPaymentData.getString("issuerApplicationData"));
        contactlessPaymentData.setPinIvCvc3Track2(jsContactlessPaymentData.getString("pinIvCvc3Track2"));
        contactlessPaymentData.setCiacDeclineOnPpms(jsContactlessPaymentData.getString("ciacDeclineOnPpms"));

        IccPrivateKeyCrtComponents iccPrivateKeyCrtComponents = (IccPrivateKeyCrtComponents) new JSONDeserializer()
                .deserialize(jsContactlessPaymentData.getJSONObject("iccPrivateKeyCrtComponents").toString(), IccPrivateKeyCrtComponents.class);
        contactlessPaymentData.setIccPrivateKeyCrtComponents(iccPrivateKeyCrtComponents);

        try {
            AlternateContactlessPaymentData alternateContactlessPaymentData = (AlternateContactlessPaymentData) new JSONDeserializer()
                    .deserialize(jsContactlessPaymentData.getJSONObject("alternateContactlessPaymentData").toString(), AlternateContactlessPaymentData.class);
            contactlessPaymentData.setAlternateContactlessPaymentData(alternateContactlessPaymentData);
        } catch (Exception e) {
            contactlessPaymentData.setAlternateContactlessPaymentData(null);
        }

        JSONArray jsArrRecords = jsContactlessPaymentData.getJSONArray("records");
        int noOfRecords = jsArrRecords.length();
        Records[] records = new Records[noOfRecords];
        JSONObject tempObject;
        for (int i = 0; i < noOfRecords; i++) {
            tempObject = jsArrRecords.getJSONObject(i);
            records[i] = new Records();
            records[i].setRecordNumber(tempObject.getInt("recordNumber"));
            records[i].setSfi(tempObject.getString("sfi"));
            records[i].setRecordValue(tempObject.getString("recordValue"));
        }
        contactlessPaymentData.setRecords(records);
        digitizedCardProfileMdes.mppLiteModule.setContactlessPaymentData(contactlessPaymentData);

        // RemotePaymentData
        RemotePaymentData remotePaymentData = (RemotePaymentData) new JSONDeserializer()
                .deserialize(jsMppLiteModule.getJSONObject("remotePaymentData").toString(), RemotePaymentData.class);
        digitizedCardProfileMdes.mppLiteModule.setRemotePaymentData(remotePaymentData);

        return digitizedCardProfileMdes;
    }
}
