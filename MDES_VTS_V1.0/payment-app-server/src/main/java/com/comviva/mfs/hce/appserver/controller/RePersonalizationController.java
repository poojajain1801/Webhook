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

package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.decryptFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoFlowRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.RePersoTokenRequest;
import com.comviva.mfs.hce.appserver.service.contract.RePersoService;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import java.util.Date;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;


@CrossOrigin(maxAge = 3600)
@RestController
@RequestMapping("/api/device/")
public class RePersonalizationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(RePersonalizationController.class);

    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    @Autowired
    private RePersoService rePersoService;


    @ResponseBody
    @RequestMapping(value="/rePersoFlow", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> rePersoFlow(@RequestBody String rePersoFlowReq) {
        LOGGER.info("re-personaliztion flow request lands --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        Map<String, Object> rePersoFlowResponse = null;
        RePersoFlowRequest  rePersoFlowRequestPojo = null;
        try {
            rePersoFlowRequestPojo = (RePersoFlowRequest) hCEControllerSupport.requestFormation(rePersoFlowReq,
                    RePersoFlowRequest.class);
            rePersoFlowResponse = rePersoService.rePersoFlow(rePersoFlowRequestPojo);
        } catch (HCEActionException rePersoFlowException){
            LOGGER.error("Exception Occured in Enter DeviceRegistrationController->rePersoFlow",rePersoFlowException);
            throw rePersoFlowException;
        }catch (Exception rePersoFlow) {
            LOGGER.error(" Exception Occured in Enter DeviceRegistrationController->rePersoFlow", rePersoFlow);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.info("re-personaliztion flow request Ends at --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        return rePersoFlowResponse;
    }

    @ResponseBody
    @RequestMapping(value="/rePerso", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> rePersoToken(@RequestBody String rePersoReq) {
        LOGGER.info("re-personaliztion request lands --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        Map<String, Object> rePersoResponse = null;
        RePersoTokenRequest  rePersoTokenRequestPojo = null;
        try {
            rePersoTokenRequestPojo = (RePersoTokenRequest) hCEControllerSupport.requestFormation(rePersoReq,
                    RePersoTokenRequest.class);
            rePersoResponse = rePersoService.rePersoTokenDataRequest(rePersoTokenRequestPojo);
        } catch (HCEActionException rePersoTokenException){
            LOGGER.error("Exception Occured in Enter DeviceRegistrationController->rePersoToken",rePersoTokenException);
            throw rePersoTokenException;
        }catch (Exception rePersoToken) {
            LOGGER.error(" Exception Occured in Enter DeviceRegistrationController->rePersoToken", rePersoToken);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.info("re-personaliztion request Ends at --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        return rePersoResponse;
    }
}
