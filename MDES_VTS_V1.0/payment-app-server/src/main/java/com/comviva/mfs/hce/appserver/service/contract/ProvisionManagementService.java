package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by amgoth.madan on 4/27/2017.
 */
public interface ProvisionManagementService {
    Map<String, Object> ProvisionTokenGivenPanEnrollmentId(ProvisionTokenGivenPanEnrollmentIdRequest panEnrollmentIdRequest);
    Map<String, Object>ConfirmProvisioning(ConfirmProvisioningRequest confirmProvisioningRequest);
    Map<String ,Object>ActiveAccountManagementReplenish(ActiveAccountManagementReplenishRequest activeAccountManagementReplenishRequest);
    Map<String,Object>ActiveAccountManagementConfirmReplenishment(ConfirmReplenishmenRequest activeAccountManagementConfirmReplenishmentRequest);
    Map<String ,Object>submitIDandVStepupMethod(SubmitIDandVStepupMethodRequest submitIDandVStepupMethodRequest);
    Map<String,Object>validateOTP(ValidateOTPRequest validateOTPRequest);
    Map<String,Object>getStepUpOptions(GetStepUpOptionsRequest getStepUpOptionsRequest);
    Map<String, Object> replenishODAData(ReplenishODADataRequest replenishODADataRequest);
}