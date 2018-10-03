package com.comviva.hceservice.util;

import com.comviva.hceservice.common.CardType;
import com.comviva.hceservice.common.CommonUtil;

/**
 * Get URL of all APIs.
 */
public class UrlUtil {

    private static String PAYMENT_APP_SERVER_ADDRESS;


    public static void initialize(final String payAppServerIp,
                                  final String payAppServerPort) {

        if (payAppServerPort == null || payAppServerPort.isEmpty() || payAppServerPort.equalsIgnoreCase("")) {
            PAYMENT_APP_SERVER_ADDRESS = CommonUtil.decrypt(payAppServerIp) + Constants.CONTEXT_ROOT;
        } else {
            PAYMENT_APP_SERVER_ADDRESS = CommonUtil.decrypt(payAppServerIp) + ":" + CommonUtil.decrypt(payAppServerPort) + Constants.CONTEXT_ROOT;
        }
    }


    public static String getRegisterUserUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/user/userRegistration";
    }


    public static String getRegisterDeviceUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/device/deviceRegistration";
    }


    public static String getUnRegisterDeviceUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/device/deRegister";
    }


    public static String getCheckCardEligibilityUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/checkCardEligibility";
    }


    public static String getAssetUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/mdes/asset";
    }


    public static String getContinueDigitizationUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/continueDigitization";
    }


    public static String getCardLifeCycleManagementMdesUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/lifeCycleManagement";
    }


    public static String getMDESTransactionHistory() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/transaction/getTransactions";
    }


    public static String getRegisterForTransactionHistoryMdes() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/transaction/registerWithTDS";
    }


    public static String getVTSTransactionHistory() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/transaction/getTransactionHistory";
    }


    public static String getVTSEnrollPanUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/enrollPan";
    }


    public static String getVTSProvisionTokenUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/provisionTokenWithPanEnrollmentId";
    }


    public static String getVTSReplenishTokenUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/activeAccountManagementReplenish";
    }


    public static String getVTSConfirmReplenishTokenUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/activeAccountManagementConfirmReplenishment";
    }


    public static String getVTSReplenishODADataTokenUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/replenishODAData";
    }


    public static String getVTSContentUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/getContent";
    }


    public static String getVTSConfirmProvisioningUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/confirmProvisioning";
    }


    public static String getCardLifeCycleManagementVtsUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/token/lifeCycleManagementVisa";
    }


    public static String getVTSCardMetaDataUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/getCardMetadata";
    }


    public static String getVTSPanData() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/card/getPANData";
    }


    public static String getVTSTokenStatus() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/token/getTokenStatus";
    }


    public static String getRequestOtpUrl(CardType cardType) {

        if (cardType.equals(CardType.VTS)) {
            return PAYMENT_APP_SERVER_ADDRESS + "api/provision/submitIDandVStepupMethodRequest";
        } else {
            return PAYMENT_APP_SERVER_ADDRESS + "api/card/requestActivationCode";
        }
    }


    public static String getVerifyOtpUrl(CardType cardType) {

        if (cardType.equals(CardType.VTS)) {
            return PAYMENT_APP_SERVER_ADDRESS + "api/provision/validateOTP";
        } else {
            return PAYMENT_APP_SERVER_ADDRESS + "api/card/activate";
        }
    }


    public static String getStepUpUrl() {

        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/getStepUpOptions";
    }
}
