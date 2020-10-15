package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.ProvisionTokenGivenPanEnrollmentIdRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmProvisioningRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActiveAccountManagementReplenishRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ConfirmReplenishmenRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.SubmitIDandVStepupMethodRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ValidateOTPRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetStepUpOptionsRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ReplenishODADataRequest;

import java.util.Map;

/**
 * Created by amgoth.madan on 4/27/2017.
 */
public interface ProvisionManagementService {
    /**
     * ProvisionTokenGivenPanEnrollmentId
     * @param panEnrollmentIdRequest panEnrollmentId Request
     * @return Map
     * */
    Map<String, Object> ProvisionTokenGivenPanEnrollmentId(ProvisionTokenGivenPanEnrollmentIdRequest panEnrollmentIdRequest);

    /**
     * ConfirmProvisioning
     * @param confirmProvisioningRequest confirmProvisioningRequest
     * @return Map
     * */
    Map<String, Object>ConfirmProvisioning(ConfirmProvisioningRequest confirmProvisioningRequest);

    /**
     * ActiveAccountManagementReplenish
     * @param activeAccountManagementReplenishRequest replenish request
     * @return Map
     * */
    Map<String ,Object>ActiveAccountManagementReplenish(ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest);

    /**
     * ActiveAccountManagementConfirmReplenishment
     * @param activeAccountManagementConfirmReplenishmentRequest activeAccntMgmtConfirmReplenishment
     * @return Map
     * */
    Map<String,Object>ActiveAccountManagementConfirmReplenishment(ConfirmReplenishmenRequest activeAccountManagementConfirmReplenishmentRequest);

    /**
     * submitIdandStepupMethod
     * @param submitIDandVStepupMethodRequest SubmitIdandStepupMehodRequest
     * @return Map
     * */
    Map<String ,Object>submitIDandVStepupMethod(SubmitIDandVStepupMethodRequest submitIDandVStepupMethodRequest);

    /**
     * validateOTP
     * @param validateOTPRequest validateOTPRequest
     * @return Map
     * */
    Map<String,Object>validateOTP(ValidateOTPRequest validateOTPRequest);

    /**
     * getStepUpOptions
     * @param getStepUpOptionsRequest stepUpoptionsRequest
     * @return Map
     * */
    Map<String,Object>getStepUpOptions(GetStepUpOptionsRequest getStepUpOptionsRequest);

    /**
     * replenishODAData
     * @param replenishODADataRequest replenishODADataRequest
     * @return Map
     * */
    Map<String, Object> replenishODAData(ReplenishODADataRequest replenishODADataRequest);
}