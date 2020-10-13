package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetDeviceInfoRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTokenUpdatedReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTransactionDetailsReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.PushTransctionDetailsReq;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.service.contract.RemoteNotificationService;
import com.comviva.mfs.hce.appserver.service.contract.TransactionManagementService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * Created by rishikesh.kumar on 06-08-2018.
 */

@RestController
public class NotificationController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CardManagementController.class);

    @Autowired
    private CardDetailService cardDetailService;
    @Autowired
    private HCEControllerSupport hCEControllerSupport;
    @Autowired
    private TransactionManagementService transactionManagementService;
    @Autowired
    private RemoteNotificationService remoteNotificationService;

    public NotificationController(CardDetailService cardDetailService ) {
        this.cardDetailService = cardDetailService;
    }

    @ResponseBody
    @RequestMapping(value = "/digitization/1/0/notifyTokenUpdated", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map notifyTokenUpdated(@RequestBody String notifyTokenUpdatedReq) {
        Map <String, Object> notifyTokenUpdatedResp = null ;
        NotifyTokenUpdatedReq notifyTokenUpdatedReqPojo = null ;
        try{
            notifyTokenUpdatedReqPojo =(NotifyTokenUpdatedReq) hCEControllerSupport.requestFormation(notifyTokenUpdatedReq ,NotifyTokenUpdatedReq.class);
            notifyTokenUpdatedResp = cardDetailService.notifyTokenUpdated(notifyTokenUpdatedReqPojo);
        }catch (HCEActionException notifyTokenUpdatedHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->notifyTokenUpdated",notifyTokenUpdatedHceActionException);
            throw notifyTokenUpdatedHceActionException;

        }catch (Exception notifyTokenUpdatedExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->registerWithTDS", notifyTokenUpdatedExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return notifyTokenUpdatedResp;
    }

    @ResponseBody
    @RequestMapping(value = "/digitization/1/0/notifyTransactionDetails", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map notifyTransactionDetails(@RequestBody String notifyTransactionDetailsReq) {
        Map <String, Object> notifyTransactionDetailsResp = null ;
        NotifyTransactionDetailsReq notifyTransactionDetailsReqPojo = null ;
        try{
            notifyTransactionDetailsReqPojo =(NotifyTransactionDetailsReq) hCEControllerSupport.requestFormation(notifyTransactionDetailsReq ,NotifyTransactionDetailsReq.class);
            notifyTransactionDetailsResp = cardDetailService.notifyTransactionDetails(notifyTransactionDetailsReqPojo);
        }catch (HCEActionException notifyTransactionDetailsHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->notifyTransactionDetails",notifyTransactionDetailsHceActionException);
            throw notifyTransactionDetailsHceActionException;
        }catch (Exception notifyTransactionDetailsExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->notifyTransactionHistory",notifyTransactionDetailsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return notifyTransactionDetailsResp;
    }

    @ResponseBody
    @RequestMapping(value = "/digitization/1/0/pushTransactionDetails",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>pushTransctionDetails(@RequestBody String pushTxnDetailsReq ) {
        LOGGER.debug("Enter TransactionManagementController->pushTransctionDetails");
        Map<String, Object> pushTransctionDetailsResponse = null;
        PushTransctionDetailsReq pushTransctionDetailsReq = null;
        try {
            pushTransctionDetailsReq = (PushTransctionDetailsReq) hCEControllerSupport.requestFormation(pushTxnDetailsReq, PushTransctionDetailsReq.class);
            pushTransctionDetailsResponse = transactionManagementService.pushTransctionDetails(pushTransctionDetailsReq);
        } catch (HCEValidationException pushTransactionDetailsRequestValidation) {
            LOGGER.error("Exception Occured in TransactionManagementController->pushTransactionDetails", pushTransactionDetailsRequestValidation);
            throw pushTransactionDetailsRequestValidation;
        }catch (HCEActionException pushTransctionDetailsActionException){
            LOGGER.error("Exception Occured in TransactionManagementController->pushTransactionDetails",pushTransctionDetailsActionException);
            throw pushTransctionDetailsActionException;
        } catch (Exception pushTransactionDetailsExcetption) {
            LOGGER.error(" Exception Occured in TransactionManagementController->pushTransactionDetails", pushTransactionDetailsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return pushTransctionDetailsResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/digitization/1/0/getDeviceInfo",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>getDeviceInfo(@RequestBody String getDeviceInfoReq ) {
        LOGGER.debug("Enter notificationController->getDeviceInfo");
        Map<String, Object> getDeviceInfoResponse = null;
        GetDeviceInfoRequest getDeviceInfoRequest = null;
        try {
            getDeviceInfoRequest = (GetDeviceInfoRequest) hCEControllerSupport.requestFormation(getDeviceInfoReq, GetDeviceInfoRequest.class);
            getDeviceInfoResponse = remoteNotificationService.getDeviceInfo(getDeviceInfoRequest);
        } catch (HCEValidationException getDeviceInfoValidation) {
            LOGGER.error("Exception Occured in TransactionManagementController->getDeviceInfo", getDeviceInfoValidation);
            throw getDeviceInfoValidation;
        }catch (HCEActionException getDeviceInfoActionException){
            LOGGER.error("Exception Occured in TransactionManagementController->getDeviceInfo",getDeviceInfoActionException);
            throw getDeviceInfoActionException;
        } catch (Exception getDeviceInfoExcetption) {
            LOGGER.error(" Exception Occured in TransactionManagementController->getDeviceInfo", getDeviceInfoExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getDeviceInfoResponse;
    }


}

