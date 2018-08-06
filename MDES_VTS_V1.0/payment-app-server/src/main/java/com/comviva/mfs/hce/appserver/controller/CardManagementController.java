package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.decryptFlow.DecryptFlowStep;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.apache.http.entity.mime.MIME;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/card")
public class CardManagementController {
    private static final Logger LOGGER = LoggerFactory.getLogger(CardManagementController.class);


    @Autowired
    private CardDetailService cardDetailService;
    @Autowired
    private HCEControllerSupport hCEControllerSupport;

    public CardManagementController(CardDetailService cardDetailService ) {
        this.cardDetailService = cardDetailService;
    }

    @ResponseBody
    @RequestMapping(value = "/checkCardEligibility", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> addCard(@RequestBody String addCardParm) {
        Map <String,Object> checkEligibilityResponse= null;
        AddCardParm addCardParmpojo = null;
        try{
            addCardParmpojo =(AddCardParm) hCEControllerSupport.requestFormation(addCardParm,AddCardParm.class);
            checkEligibilityResponse  = cardDetailService.checkDeviceEligibility(addCardParmpojo);
        }catch (HCEActionException addCardHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->checkCardEligibility",addCardHceActionException);
            throw addCardHceActionException;
        }catch (Exception addCardExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->checkCardEligibility", addCardExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return checkEligibilityResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/continueDigitization", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public  Map<String, Object> continueDigitization(@RequestBody String digitizationParam) {
        DigitizationParam digitizationParamPojo = null;
        Map <String,Object>continueDigitizationResponse= null;
        try{
            digitizationParamPojo = (DigitizationParam) hCEControllerSupport.requestFormation(digitizationParam,DigitizationParam.class);
            continueDigitizationResponse = cardDetailService.addCard(digitizationParamPojo);
        }catch (HCEActionException continueDigitizationHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->continueDigitization",continueDigitizationHceActionException);
            throw continueDigitizationHceActionException;
        }catch (Exception continueDigitizationExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->continueDigitization", continueDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return continueDigitizationResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/tokenize", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public  Map<String, Object> tokenize(@RequestBody String tokenizeRequest) {
        TokenizeRequest tokenizeRequestPojo= null;
        Map <String,Object>tokenizeResponse= null;
        try{
            tokenizeRequestPojo = (TokenizeRequest) hCEControllerSupport.requestFormation(tokenizeRequest,TokenizeRequest.class);
            tokenizeResponse = cardDetailService.tokenize(tokenizeRequestPojo);
        }catch (HCEActionException tokenizeHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->tokenize",tokenizeHceActionException);
            throw tokenizeHceActionException;
        }catch (Exception continueDigitizationExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->tokenize", continueDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }

        return tokenizeResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/getAsset", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map getAsset(@RequestBody String getAsset) {
        Map <String,Object> assetResponse= null;
        GetAssetRequest getAssetRequestPojo = null;
        try{
            getAssetRequestPojo =(GetAssetRequest) hCEControllerSupport.requestFormation(getAsset ,GetAssetRequest.class);
            assetResponse = cardDetailService.getAsset(getAssetRequestPojo);
        }catch (HCEActionException assetHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getAsset",assetHceActionException);
            throw assetHceActionException;
        }catch (Exception assetExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getAsset", assetExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return assetResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> activate(@RequestBody String activateReq) {
        Map<String ,Object> activateResponse = null;
        ActivateReq activateReqPojo = null;
        try{
            activateReqPojo =(ActivateReq) hCEControllerSupport.requestFormation(activateReq,ActivateReq.class);
            activateResponse = cardDetailService.activate(activateReqPojo);
        }catch (HCEActionException activateHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->activate",activateHceActionException);
            throw activateHceActionException;
        }catch (Exception activateExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->activate", activateExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return activateResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/enrollPan", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    @DecryptFlowStep("decryptData")
    public Map<String, Object> enrollPan(@RequestBody String enrollPanRequest){
        Map <String,Object> enrollPanResponse= null;
        EnrollPanRequest enrollPanRequestPojo = null;
        try{
            enrollPanRequestPojo =(EnrollPanRequest) hCEControllerSupport.requestFormation(enrollPanRequest,EnrollPanRequest.class);
            enrollPanResponse = cardDetailService.enrollPan(enrollPanRequestPojo);
        }catch (HCEActionException enrollPanHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",enrollPanHceActionException);
            throw enrollPanHceActionException;
        }catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return enrollPanResponse;
    }

    @ResponseBody
    @RequestMapping(value = "/getCardMetadata",method = RequestMethod.POST)
    public Map<String,Object> getCardMetadata(@RequestBody GetCardMetadataRequest getCardMetadataRequest){
        LOGGER.debug("Enter CardManagementController->getCardMetadata");
        Map <String,Object> getCardMetadataResp= null;
        getCardMetadataResp = cardDetailService.getCardMetadata(getCardMetadataRequest);
        LOGGER.debug("Exit CardManagementController->getCardMetadata");
        return  getCardMetadataResp;
    }

    @ResponseBody
    @RequestMapping(value = "/getContent",method = RequestMethod.POST)
    public Map<String,Object>getContent(@RequestBody GetContentRequest getContentRequest){
        LOGGER.debug("Enter CardManagementController->getContent");
        Map<String,Object> getContentResp = null;
        getContentResp = cardDetailService.getContent(getContentRequest);
        LOGGER.debug("Enter CardManagementController->getContent");
      return getContentResp;
    }

    @ResponseBody
    @RequestMapping(value = "/getPANData",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>getPANData(@RequestBody GetPANDataRequest getPANDataRequest){
        return cardDetailService.getPANData(getPANDataRequest);
    }

    @ResponseBody
    @RequestMapping(value = "/lifeCycleManagement",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object>lifeCycleManagement(@RequestBody String lifeCycleManagementReq){
        Map <String, Object> lifeCycleManagementResp = null ;
        LifeCycleManagementReq lifeCycleManagementReqpojo=null ;
        try{
            lifeCycleManagementReqpojo =(LifeCycleManagementReq) hCEControllerSupport.requestFormation(lifeCycleManagementReq ,LifeCycleManagementReq.class);
            lifeCycleManagementResp = cardDetailService.performCardLifeCycleManagement(lifeCycleManagementReqpojo);
        }catch (HCEActionException lifeCycleManagementHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->lifeCycleManagement",lifeCycleManagementHceActionException);
            throw lifeCycleManagementHceActionException;
        }catch (Exception lifeCycleManagementExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->lifeCycleManagement", lifeCycleManagementExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return lifeCycleManagementResp;
    }

    //Transction History APIS
    @ResponseBody
    @RequestMapping(value = "/notifyTransactionDetails", method = RequestMethod.POST)
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
    @RequestMapping(value = "/getRegistrationCode", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map getRegistrationCode(@RequestBody String getRegistrationCodeReq) {
        Map <String, Object> getRegistrationCodeResp ;
        GetRegistrationCodeReq getRegistrationCodeReqPojo ;
        try{
            getRegistrationCodeReqPojo =(GetRegistrationCodeReq) hCEControllerSupport.requestFormation(getRegistrationCodeReq ,GetRegistrationCodeReq.class);
            getRegistrationCodeResp = cardDetailService.getRegistrationCode(getRegistrationCodeReqPojo);
        }catch (HCEActionException getRegistrationCodeHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getRegistrationCode",getRegistrationCodeHceActionException);
            throw getRegistrationCodeHceActionException;
        }catch (Exception getRegistrationCodeExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getRegistrationCode", getRegistrationCodeExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getRegistrationCodeResp;
    }
    @ResponseBody
    @RequestMapping(value = "/registerWithTDS", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map registerWithTDS(@RequestBody String tdsRegistrationReq) {
        Map <String, Object> registerWithTDSResp = null ;
        TDSRegistrationReq tdsRegistrationReqPojo = null ;
        try{
            tdsRegistrationReqPojo =(TDSRegistrationReq) hCEControllerSupport.requestFormation(tdsRegistrationReq ,TDSRegistrationReq.class);
            registerWithTDSResp = cardDetailService.registerWithTDS(tdsRegistrationReqPojo);
        }catch (HCEActionException registerWithTDSHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->registerWithTDS",registerWithTDSHceActionException);
            throw registerWithTDSHceActionException;

        }catch (Exception registerWithTDSExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->registerWithTDS", registerWithTDSExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return registerWithTDSResp;
    }

    @ResponseBody
    @RequestMapping(value = "/getTransactions", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map getTransactions(@RequestBody String getTransactionHistoryReq) {
        Map <String, Object> getTransactionsResp=null ;
        GetTransactionHistoryReq getTransactionHistoryReqPojo = null ;
        try{
            getTransactionHistoryReqPojo =(GetTransactionHistoryReq) hCEControllerSupport.requestFormation(getTransactionHistoryReq ,GetTransactionHistoryReq.class);
            getTransactionsResp = cardDetailService.getTransactionHistory(getTransactionHistoryReqPojo);
        }catch (HCEActionException getTransactionHistoryHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getTransactionHistory",getTransactionHistoryHceActionException);
            throw getTransactionHistoryHceActionException;
        }catch (Exception getTransactionHistoryExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getTransactionHistory", getTransactionHistoryExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getTransactionsResp;
    }

    @ResponseBody
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map getTokens(@RequestBody String getTokensRequest) {
        Map <String, Object> getTokensResp=null ;
        GetTokensRequest getTokensReqpojo=null ;
        try{
            getTokensReqpojo =(GetTokensRequest) hCEControllerSupport.requestFormation(getTokensRequest ,GetTokensRequest.class);
            getTokensResp = cardDetailService.getTokens(getTokensReqpojo);
        }catch (HCEActionException getTokensHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getTokens",getTokensHceActionException);
            throw getTokensHceActionException;
        }catch (Exception getTokensExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getTokens", getTokensExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getTokensResp;
    }
    @ResponseBody
    @RequestMapping(value = "/searchTokens", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map searchTokens(@RequestBody String searchTokensReq) {
        Map <String, Object> searchTokensResp=null ;
        SearchTokensReq searchTokensReqpojo=null ;
        try{
            searchTokensReqpojo =(SearchTokensReq) hCEControllerSupport.requestFormation(searchTokensReq ,SearchTokensReq.class);
            searchTokensResp = cardDetailService.searchTokens(searchTokensReqpojo);
        }catch (HCEActionException searchTokensHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->searchTokens",searchTokensHceActionException);
            throw searchTokensHceActionException;
        }catch (Exception searchTokensExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->searchTokens", searchTokensExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return searchTokensResp;
    }

    @ResponseBody
    @RequestMapping(value = "/getTaskStatus", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map getTaskStatus(@RequestBody String getTaskStatusReq) {
        Map <String, Object> getTaskStatusResp=null ;
        GetTaskStatusReq getTaskStatusReqpojo=null ;
        try{
            getTaskStatusReqpojo =(GetTaskStatusReq) hCEControllerSupport.requestFormation(getTaskStatusReq ,GetTaskStatusReq.class);
            getTaskStatusResp = cardDetailService.getTaskStatus(getTaskStatusReqpojo);
        }catch (HCEActionException getTaskStatusHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getTaskStatus",getTaskStatusHceActionException);
            throw getTaskStatusHceActionException;
        }catch (Exception getTaskStatusExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getTaskStatus", getTaskStatusExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getTaskStatusResp;
    }

    @ResponseBody
    @RequestMapping(value = "/requestActivationCode", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map requestActivationCode(@RequestBody String activationCodeReq) {
        Map <String, Object> activationCodeResp=null ;
        ActivationCodeReq activationCodeReqpojo=null ;
        try{
            activationCodeReqpojo =(ActivationCodeReq) hCEControllerSupport.requestFormation(activationCodeReq ,ActivationCodeReq.class);
            activationCodeResp = cardDetailService.requestActivationCode(activationCodeReqpojo);
        }catch (HCEActionException activationCodeHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->activationCode",activationCodeHceActionException);
            throw activationCodeHceActionException;
        }catch (Exception activationCodeExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->activationCode", activationCodeExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return activationCodeResp ;
    }

    @ResponseBody
    @RequestMapping(value = "/unregisterTds", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map unregisterFromTds(@RequestBody String unregisterTdsReq) {
        Map <String, Object> unregisterTdsResp=null ;
        UnregisterTdsReq unregisterTdsReqPojo = null ;
        try{
            unregisterTdsReqPojo =(UnregisterTdsReq) hCEControllerSupport.requestFormation(unregisterTdsReq ,UnregisterTdsReq.class);
            unregisterTdsResp = cardDetailService.unregisterTds(unregisterTdsReqPojo);
        }catch (HCEActionException unregisterTdsHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->unregisterTds",unregisterTdsHceActionException);
            throw unregisterTdsHceActionException;
        }catch (Exception unregisterTdsExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->unregisterTds", unregisterTdsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return unregisterTdsResp;
    }

    @ResponseBody
    @RequestMapping(value = "/provision", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map provision(@RequestBody String provisionRequest) {
        Map <String, Object> provisionResp=null ;
        ProvisionRequest provisionRequestPojo = null ;
        try{
            provisionRequestPojo =(ProvisionRequest) hCEControllerSupport.requestFormation(provisionRequest ,ProvisionRequest.class);
            provisionResp = cardDetailService.provision(provisionRequestPojo);
        }catch (HCEActionException provisionHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->provision",provisionHceActionException);
            throw provisionHceActionException;
        }catch (Exception provisionExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->provision",provisionExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return provisionResp;
    }


    @ResponseBody
    @RequestMapping(value = "/getSystemHealth", method = RequestMethod.GET)
    public Map getSystemHealth() {
        Map <String, Object> getSystemHealthResp=null ;
        try{
            getSystemHealthResp = cardDetailService.getSystemHealth();
        }catch (HCEActionException getSystemHealthHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getSystemHealth",getSystemHealthHceActionException);
            throw getSystemHealthHceActionException;
        }catch (Exception getSystemHealthExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getSystemHealth", getSystemHealthExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getSystemHealthResp;
    }

    @ResponseBody
    @RequestMapping(value = "/pkCertificate", method = RequestMethod.GET)
    public Object getPublicKeyCertificate() {
        Object getPublicKeyCertificateResp = null ;
        try{
            getPublicKeyCertificateResp = cardDetailService.getPublicKeyCertificate();
        }catch (HCEActionException getPublicKeyCertificateException){
            LOGGER.error("Exception Occured in CardManagementController->getPublicKeyCertificate",getPublicKeyCertificateException);
            throw getPublicKeyCertificateException;
        }catch (Exception getPublicKeyCertificateExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getPublicKeyCertificate", getPublicKeyCertificateExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getPublicKeyCertificateResp;
    }

    @ResponseBody
    @RequestMapping(value = "/getPublicKeyCertWrap", method = RequestMethod.GET)
    public ResponseEntity getPublicKeyCertWrap() {
        ResponseEntity getPublicKeyCertWrapResp = null ;
        try{
            getPublicKeyCertWrapResp = cardDetailService.getPublicKeyCertWrap();
        }catch (HCEActionException getSystemHealthHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getPublicKeyCertWrap",getSystemHealthHceActionException);
            throw getSystemHealthHceActionException;
        }catch (Exception getSystemHealthExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getPublicKeyCertWrap", getSystemHealthExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getPublicKeyCertWrapResp;
    }


}