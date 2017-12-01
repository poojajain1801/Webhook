package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
    public AddCardResponse addCard(@RequestBody AddCardParm addCardParm) {
        return cardDetailService.checkDeviceEligibility(addCardParm);
    }

    @ResponseBody
    @RequestMapping(value = "/continueDigitization", method = RequestMethod.POST)
    public AddCardResponse continueDigitization(@RequestBody DigitizationParam digitizationParam) {
        return cardDetailService.addCard(digitizationParam);
    }

    @RequestMapping(value = "/mdes/digitization/1/0/asset", method = RequestMethod.GET)
    public Asset getAsset(@RequestParam(value="assetId") String assetId) {
        return cardDetailService.getAsset(assetId);
    }

    @ResponseBody
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    public ActivateResp activate(@RequestBody ActivateReq activateReq) {
        return cardDetailService.activate(activateReq);
    }

    @ResponseBody
    @RequestMapping(value = "/enrollPan", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> enrollPan(@RequestBody String enrollPanRequest){
        Map <String,Object> enrollPanResponse= null;
        EnrollPanRequest enrollPanRequestPojo = null;
        try{
            LOGGER.debug("Enter CardManagementController->enrollPan");
           // enrollPanRequest = hCEControllerSupport.decryptRequest(enrollPanRequest);
            enrollPanRequestPojo =(EnrollPanRequest) hCEControllerSupport.requestFormation(enrollPanRequest,EnrollPanRequest.class);
            enrollPanResponse = cardDetailService.enrollPan(enrollPanRequestPojo);
        }catch (HCEValidationException enrollPanRequestValidation){
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",enrollPanRequestValidation);
            throw enrollPanRequestValidation;
        }catch (HCEActionException enrollPanHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",enrollPanHceActionException);
            throw enrollPanHceActionException;
        }catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.SERVICE_FAILED);
        }
        LOGGER.debug("Exit CardManagementController->enrollPan");

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
        LOGGER.debug("Enter CardManagementController->getCardMetadata");
        Map<String,Object> getContentResp = null;
        getContentResp = cardDetailService.getContent(getContentRequest);
        LOGGER.debug("Enter CardManagementController->getCardMetadata");
      return getContentResp;
    }
    @ResponseBody
    @RequestMapping(value = "/getPANData",method = RequestMethod.POST)
    public Map<String,Object>getPANData(@RequestBody GetPANDataRequest getPANDataRequest){
        return cardDetailService.getPANData(getPANDataRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public Map<String,Object>delete(@RequestBody LifeCycleManagementReq lifeCycleManagementReq){
        return cardDetailService.performCardLifeCycleManagement(lifeCycleManagementReq);
    }
    //Transction History APIS
    @ResponseBody
    @RequestMapping(value = "/notifyTransactionDetails", method = RequestMethod.POST)
    public Map notifyTransactionDetails(@RequestBody NotifyTransactionDetailsReq notifyTransactionDetailsReq) {
        return cardDetailService.notifyTransactionDetails(notifyTransactionDetailsReq);
    }
    @ResponseBody
    @RequestMapping(value = "/getRegistrationCode", method = RequestMethod.POST)
    public Map getRegistrationCode(@RequestBody GetRegCodeReq getRegCodeReq) {
        return cardDetailService.getRegistrationCode(getRegCodeReq);
    }
    @ResponseBody
    @RequestMapping(value = "/registerWithTDS", method = RequestMethod.POST)
    public Map registerWithTDS(@RequestBody TDSRegistration tdsRegistration) {
        return cardDetailService.registerWithTDS(tdsRegistration);
    }
    @ResponseBody
    @RequestMapping(value = "/getTransactions", method = RequestMethod.POST)
    public Map registerWithTDS(@RequestBody GetTransactionHistoryReq getTransactionHistoryReq) {
        return cardDetailService.getTransactionHistory(getTransactionHistoryReq);
    }

    @ResponseBody
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    public Map getToekns(@RequestBody GetTokensRequest getTokensRequest) {
        return cardDetailService.getTokens(getTokensRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/searchTokens", method = RequestMethod.POST)
    public Map searchTokens(@RequestBody SearchTokensReq getTokensRequest) {
        return cardDetailService.searchTokens(getTokensRequest);
    }
	    @ResponseBody
    @RequestMapping(value = "/requestActivationCode", method = RequestMethod.POST)
    public Map requestActivationCode(@RequestBody ActivationCodeReq activationCodeReq) {
        return cardDetailService.requestActivationCode(activationCodeReq);
    }

    @ResponseBody
    @RequestMapping(value = "/unregisterTds", method = RequestMethod.POST)
    public Map unregisterFromTds(@RequestBody Map unregisterTdsReq) {
        return cardDetailService.unregisterTds(unregisterTdsReq);
    }



}