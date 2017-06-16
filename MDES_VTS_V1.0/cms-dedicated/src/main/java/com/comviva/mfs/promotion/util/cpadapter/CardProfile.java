package com.comviva.mfs.promotion.util.cpadapter;

import lombok.Getter;
import lombok.Setter;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Generates Card profile in MPA format.
 * Created by tarkeshwar.v on 2/1/2017.
 */
@Setter
@Getter
public class CardProfile {
    private String digitizedCardId;

    private JSONObject getRecordJson(String recordNumber, String sfi, String recordValue) {
        JSONObject record = new JSONObject();
        record.put("recordNumber", recordNumber);
        record.put("sfi", sfi);
        record.put("recordValue", recordValue);
        return record;
    }

    private JSONObject getMagstripeCvmIssuerOptions() {
        JSONObject magstripeCvmIssuerOptions = new JSONObject();
        magstripeCvmIssuerOptions.put("ackAlwaysRequiredIfCurrencyNotProvided", false);
        magstripeCvmIssuerOptions.put("ackAlwaysRequiredIfCurrencyProvided", false);
        magstripeCvmIssuerOptions.put("ackAutomaticallyResetByApplication", false);
        magstripeCvmIssuerOptions.put("ackPreEntryAllowed", false);
        magstripeCvmIssuerOptions.put("pinAlwaysRequiredIfCurrencyNotProvided", true);
        magstripeCvmIssuerOptions.put("pinAlwaysRequiredIfCurrencyProvided", true);
        magstripeCvmIssuerOptions.put("pinAutomaticallyResetByApplication", false);
        magstripeCvmIssuerOptions.put("pinPreEntryAllowed", true);
        return magstripeCvmIssuerOptions;
    }

    private JSONObject getmChipCvmIssuerOptions() {
        JSONObject mChipCvmIssuerOptions = new JSONObject();
        mChipCvmIssuerOptions.put("ackAlwaysRequiredIfCurrencyNotProvided", false);
        mChipCvmIssuerOptions.put("ackAlwaysRequiredIfCurrencyProvided", false);
        mChipCvmIssuerOptions.put("ackAutomaticallyResetByApplication", false);
        mChipCvmIssuerOptions.put("ackPreEntryAllowed", false);
        mChipCvmIssuerOptions.put("pinAlwaysRequiredIfCurrencyNotProvided", true);
        mChipCvmIssuerOptions.put("pinAlwaysRequiredIfCurrencyProvided", true);
        mChipCvmIssuerOptions.put("pinAutomaticallyResetByApplication", false);
        mChipCvmIssuerOptions.put("pinPreEntryAllowed", true);
        return mChipCvmIssuerOptions;
    }

    private JSONObject getRemotePaymentData() {
        JSONObject remotePaymentData = new JSONObject();
        remotePaymentData.put("track2Equivalent", "5480981500100002D18112011000000000000F");
        remotePaymentData.put("pan", "5480981500100002");
        remotePaymentData.put("panSequenceNumber", "01");
        remotePaymentData.put("applicationExpiryDate", "181130");
        remotePaymentData.put("aip", "1A00");
        remotePaymentData.put("ciacDecline", "010008");
        remotePaymentData.put("cvrMaskAnd", "FF0000000000");
        remotePaymentData.put("issuerApplicationData", "0314000000000000000000000000000000FF");
        return remotePaymentData;
    }

    private JSONObject getIccPrivateKeyCrtComponents() {
        JSONObject iccPrivateKeyCrtComponents = new JSONObject();
        iccPrivateKeyCrtComponents.put("p", "4E4F54205245414C2044415441");
        iccPrivateKeyCrtComponents.put("q", "4E4F54205245414C2044415441");
        iccPrivateKeyCrtComponents.put("dp", "4E4F54205245414C2044415441");
        iccPrivateKeyCrtComponents.put("dq", "4E4F54205245414C2044415441");
        iccPrivateKeyCrtComponents.put("u", "4E4F54205245414C2044415441");
        return iccPrivateKeyCrtComponents;
    }

    public JSONObject getCardProfile() {
        // 1 mppLiteModule
        // 1.1 cardRiskManagementData
        // 1.2 contactlessPaymentData
        //      1.2.1 iccPrivateKeyCrtComponents
        //      1.2.2 records
        // 1.3 remotePaymentData

        // 2 businessLogicModule
        // 2.1 mChipCvmIssuerOptions
        // 2.2 magstripeCvmIssuerOptions

        JSONObject mppLiteModule = new JSONObject();
        JSONObject cardRiskManagementData = new JSONObject();
        cardRiskManagementData.put("additionalCheckTable", "000000000000000000000000000000000000");
        cardRiskManagementData.put("crmCountryCode", "0840");

        JSONObject contactlessPaymentData = new JSONObject();
        contactlessPaymentData.put("aid", "A0000000041010");
        contactlessPaymentData.put("ppseFci", "6F39840E325041592E5359532E4444463031A527BF0C2461224F07A0000000041010500A4D4153544552434152448701015F550255534203545501");
        contactlessPaymentData.put("paymentFci", "8407A0000000041010A52D500A4D4153544552434152448701015F2D02656E9F38099F1D089F1A029F3501BF0C0A9F6E0708400000313400");
        contactlessPaymentData.put("gpoResponse", "770E82021B8094080801010010010301");
        contactlessPaymentData.put("cdol1RelatedDataLength", "2D");
        contactlessPaymentData.put("ciacDecline", "010008");
        contactlessPaymentData.put("cvrMaskAnd", "FFFFFFFFFFFF");
        contactlessPaymentData.put("issuerApplicationData", "0314000100000000000000000000000000FF");
        contactlessPaymentData.put("pinIvCvc3Track2", "56DE");
        contactlessPaymentData.put("ciacDeclineOnPpms", "4100");
        contactlessPaymentData.put("alternateContactlessPaymentData", "null");
        contactlessPaymentData.put("iccPrivateKeyCrtComponents", getIccPrivateKeyCrtComponents());
        JSONArray records = new JSONArray();
        records.put(getRecordJson("1", "0C", "7081919F6C0200019F62060000000700009F630600000078F0009F640104563442353438303938313530303130303030325E202F5E313831313230313130303030303030303030303030303030303030303030309F650200E09F66020F1E9F6B135480981500100002D18112011000000000000F9F6701049F69199F6A049F7E019F02065F2A029F1A029C019A039F15029F3501"));
        records.put(getRecordJson("1", "14", "70819157135480981500100002D18112011000000000000F5A0854809815001000025F24031811305F25034912315F280208405F3401018C249F02069F03069F1A0295055F2A029A039C019F37049F35019F45029F4C089F34039F15028E0A00000000000000001F039F070200009F080200029F0D0500600000009F0E0500000000009F0F0500600000009F420208409F4A0182"));
        records.put(getRecordJson("1", "14", "70138F01029F320101920312345690051234567890"));
        records.put(getRecordJson("1", "14", "7081BB9F4701039F48009F4681B072492120CAEFC82D7AA8EAA175ADDEC1699A064A24C71D2F8FE68899F4E1AE8422E4EAE5B9E55B7A18502B9DCDFF7CDDE6DCD7C725706E0276B87A62FF03C4D902632852C02184BB9AB5D6AC75358AC0180FA3BDFCE46EB27DE98573DE161DC7A8D0C04F8032B10B291A3CE383AD136691874EEA53F4366878CE8A6AB08A22BE0E53EF57E3AE38C465054B1187CBEBF49B6CBBAF315AA51741DA6D176569CF433E21350E22833C5C975E77ADF0BF3B44"));
        records.put(getRecordJson("1", "1C", "70295A0854809815001000025F24031811305F34010157135480981500100002D18112011000000000000F"));
        contactlessPaymentData.put("records", records);

        mppLiteModule.put("cardRiskManagementData", cardRiskManagementData);
        mppLiteModule.put("contactlessPaymentData", contactlessPaymentData);
        mppLiteModule.put("remotePaymentData", getRemotePaymentData());

        JSONObject businessLogicModule = new JSONObject();
        businessLogicModule.put("cvmResetTimeout", 30);
        businessLogicModule.put("dualTapResetTimeout", 30);
        businessLogicModule.put("applicationLifeCycleData", "null");
        businessLogicModule.put("cardLayoutDescription", "11018000");
        businessLogicModule.put("securityWord", "00000000000000000000000000000000");
        JSONArray cardholderValidators = new JSONArray();
        cardholderValidators.put("DEVICE_MOBILE_PIN");
        businessLogicModule.put("cardholderValidators", cardholderValidators);

        businessLogicModule.put("mChipCvmIssuerOptions", getmChipCvmIssuerOptions());
        businessLogicModule.put("magstripeCvmIssuerOptions", getMagstripeCvmIssuerOptions());

        JSONObject cardProfile = new JSONObject();
        cardProfile.put("digitizedCardId", "5480981500100002FFFF01150305163347");
        cardProfile.put("maximumPinTry", "3");
        cardProfile.put("mppLiteModule", mppLiteModule);
        cardProfile.put("businessLogicModule", businessLogicModule);

        return cardProfile;
    }

}
