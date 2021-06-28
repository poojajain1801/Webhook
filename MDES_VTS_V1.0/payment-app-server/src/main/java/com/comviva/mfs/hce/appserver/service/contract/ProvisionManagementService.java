/*
 * COPYRIGHT(c) 2015: Comviva Technologies Pvt. Ltd.
 *
 * This software is the sole property of Comviva and is protected by copyright
 * law and international treaty provisions. Unauthorized reproduction or
 * redistribution of this program, or any portion of it may result in severe
 * civil and criminal penalties and will be prosecuted to the maximum extent
 * possible under the law. Comviva reserves all rights not expressly granted.
 * You may not reverse engineer, decompile, or disassemble the software, except
 * and only to the extent that such activity is expressly permitted by
 * applicable law notwithstanding this limitation.
 *
 * THIS SOFTWARE IS PROVIDED TO YOU "AS IS" WITHOUT WARRANTY OF ANY KIND,
 * EITHER EXPRESS OR IMPLIED,INCLUDING BUT NOT LIMITED TO THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A PARTICULAR PURPOSE.
 * YOU ASSUME THE ENTIRE RISK AS TO THE ACCURACY AND THE USE OF THIS SOFTWARE.
 * Comviva SHALL NOT BE LIABLE FOR ANY DAMAGES WHATSOEVER ARISING OUT OF THE
 * USE OF OR INABILITY TO USE THIS SOFTWARE, EVEN IF Comviva HAS BEEN ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
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
