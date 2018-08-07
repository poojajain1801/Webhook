package com.comviva.hceservice.util;

import com.comviva.hceservice.common.CardLcmOperation;

/**
 * Get URL of all APIs.
 */
public class UrlUtil {
    private static String PAYMENT_APP_SERVER_ADDRESS;
    private static String CMS_D_SERVER_ADDRESS;

    public static void initialize(final String payAppServerIp,
                                  final String payAppServerPort,
                                  final String cmsDIp,
                                  final String cmsDPort) {
        if (payAppServerPort == null || payAppServerPort.isEmpty() || payAppServerPort.equalsIgnoreCase("-1")) {
            PAYMENT_APP_SERVER_ADDRESS = payAppServerIp + "/payment-app/";
        } else {
            PAYMENT_APP_SERVER_ADDRESS = payAppServerIp + ":" + payAppServerPort + "/payment-app/";
        }

        //http://localhost:9099/mdes/paymentapp/1/0/requestSession
        CMS_D_SERVER_ADDRESS = "http://" + cmsDIp + ":" + cmsDPort + "/mdes/paymentapp/1/0/";
    }

    public static String getRegisterUserUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/user/userRegistration";
    }

    public static String getActivateUserUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/user/activateUser";
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
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/mdes/digitization/1/0/asset";
    }

    public static String getContinueDigitizationUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/continueDigitization";
    }

    public static String getRequestSessionUrl() {
        return CMS_D_SERVER_ADDRESS + "requestSession";
    }


    public static String getCardLifeCycleManagementMdesUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/lifeCycleManagement";
    }

    public static String getRequestActivationCodeUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/requestActivationCode";
    }

    public static String getActivateUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/activate";
    }

    public static String getRegCodeTdsUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/getRegistrationCode";
    }

    public static String getRegisterTdsUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/registerWithTDS";
    }

    public static String getTransactionDetailsUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/getTransactions";
    }

    public static String getUnregisterTdsUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/card/unregisterTds";
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

    public static String getCardLifeCycleManagementVtsUrl(CardLcmOperation operation) {
        switch (operation) {
            case SUSPEND:
            case RESUME:
            case DELETE:
                return PAYMENT_APP_SERVER_ADDRESS + "api/token/lifeCycleManagementVisa";
            default:
                return null;
        }
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

    public static String getRequestOtpUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/submitIDandVStepupMethodRequest";
    }

    public static String getVerifyOtpUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/validateOTP";
    }


    public static String getStepUpUrl() {
        return PAYMENT_APP_SERVER_ADDRESS + "api/provision/getStepUpOptions";
    }
}
