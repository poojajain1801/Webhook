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

import com.comviva.mfs.hce.appserver.decryptFlow.DecryptFlowStep;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.exception.HCEValidationException;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActivateReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActivationCodeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.AddCardParm;
import com.comviva.mfs.hce.appserver.mapper.pojo.DigitizationParam;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollPanRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetAssetPojo;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetCardMetadataRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetContentRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokensRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.RedigitizeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.SearchTokensReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.TokenizeRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnregisterTdsReq;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.comviva.mfs.hce.appserver.serviceFlow.ServiceFlowStep;
import com.comviva.mfs.hce.appserver.util.common.HCEMessageCodes;
import com.comviva.mfs.hce.appserver.util.common.HCEUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;
import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@CrossOrigin(maxAge = 3600)
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

    /**
     * addCard
     * @param addCardParm addCardRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/checkCardEligibility", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> addCard(@RequestBody String addCardParm) {
        LOGGER.info("Check card eligibility request lands  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        Map <String,Object> checkEligibilityResponse= null;
        AddCardParm addCardParmpojo = null;
        try{
            addCardParmpojo =(AddCardParm) hCEControllerSupport.requestFormation(addCardParm,AddCardParm.class);
            checkEligibilityResponse  = cardDetailService.checkDeviceEligibility(addCardParmpojo);
        }catch (HCEActionException addCardHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->addCard",addCardHceActionException);
            throw addCardHceActionException;
        }catch (Exception addCardExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->addCard", addCardExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        LOGGER.info("Check card eligibility response goes  --> TIME " + HCEUtil.convertDateToTimestamp(new Date()));
        return checkEligibilityResponse;
    }


    /**
     * continueDigitization
     * @param digitizationParam digitizationReq
     * @return map
     * */
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
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",continueDigitizationHceActionException);
            throw continueDigitizationHceActionException;
        }catch (Exception continueDigitizationExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", continueDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return continueDigitizationResponse;
    }


    /**
     * tokenize
     * @param tokenizeRequest tokenizeRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/tokenize", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public  Map<String, Object> tokenize(@RequestBody String tokenizeRequest) {
        TokenizeRequest tokenizeRequestPojo= null;
        Map <String,Object>tokenizeResponse= null;
        try{
            tokenizeRequestPojo = (TokenizeRequest) hCEControllerSupport.requestFormation(tokenizeRequest,TokenizeRequest.class);
            tokenizeResponse = cardDetailService.tokenize(tokenizeRequestPojo);
        }catch (HCEActionException continueDigitizationHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",continueDigitizationHceActionException);
            throw continueDigitizationHceActionException;
        }catch (Exception continueDigitizationExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", continueDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return tokenizeResponse;
    }

    /**
     * getAsset
     * @param assetId assertId
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/mdes/asset", method = RequestMethod.POST)
    public Map<String,Object> getAsset(@RequestBody String assetId) {
        GetAssetPojo getAssetPojo= null;
        Map <String,Object>getAssetResponse= null;
        try{
            getAssetPojo = (GetAssetPojo) hCEControllerSupport.requestFormation(assetId,GetAssetPojo.class);
            getAssetResponse = cardDetailService.getAsset(getAssetPojo);
        }catch (HCEActionException continueDigitizationHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->getAsset",continueDigitizationHceActionException);
            throw continueDigitizationHceActionException;
        }catch (Exception continueDigitizationExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getAsset", continueDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getAssetResponse;
    }


    /**
     * activate
     * @param activateReq activeRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/activate", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> activate(@RequestBody String activateReq) {
        ActivateReq activateReqPojo = null;
        Map <String,Object>activateResponse= null;
        try{
            activateReqPojo = (ActivateReq) hCEControllerSupport.requestFormation(activateReq,ActivateReq.class);
            activateResponse =cardDetailService.activate(activateReqPojo);
        }catch (HCEActionException continueDigitizationHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->activate",continueDigitizationHceActionException);
            throw continueDigitizationHceActionException;
        }catch (Exception continueDigitizationExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->activate", continueDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return activateResponse;
    }


    /**
     * requestActivationCode
     * @param activationCodeReq activationCodeRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/requestActivationCode", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> requestActivationCode(@RequestBody String activationCodeReq) {
        ActivationCodeReq activationCodeReqPojo = null;
        Map <String,Object>requestActivationCodeResponse= null;
        try {
            activationCodeReqPojo = (ActivationCodeReq)hCEControllerSupport.requestFormation(activationCodeReq,ActivationCodeReq.class);
            requestActivationCodeResponse =  cardDetailService.requestActivationCode(activationCodeReqPojo);
        }catch (HCEActionException requestActivationCodeHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",requestActivationCodeHceActionException);
            throw requestActivationCodeHceActionException;
        }catch (Exception requestActivationCodeExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", requestActivationCodeExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return requestActivationCodeResponse;
    }


    /**
     * enrollPan
     * @param enrollPanRequest enrollPanRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/enrollPan", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    @DecryptFlowStep("decryptData")
    public Map<String, Object> enrollPan(@RequestBody String enrollPanRequest){
        Map <String,Object> enrollPanResponse = null;
        EnrollPanRequest enrollPanRequestPojo = null;
        try{
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
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return enrollPanResponse;
    }

    /**
     * getCardMetaData
     * @param getCardMetadataRequest getCardMetadataRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/getCardMetadata",method = RequestMethod.POST)
    public Map<String,Object> getCardMetadata(@RequestBody GetCardMetadataRequest getCardMetadataRequest){
        LOGGER.debug("Enter CardManagementController->getCardMetadata");
        Map <String,Object> getCardMetadataResp= null;
        getCardMetadataResp = cardDetailService.getCardMetadata(getCardMetadataRequest);
        LOGGER.debug("Exit CardManagementController->getCardMetadata");
        return  getCardMetadataResp;
    }
    /**
     * getContent
     * @param getContentRequest getContentRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/getContent",method = RequestMethod.POST)
    public Map<String,Object> getContent(@RequestBody GetContentRequest getContentRequest){
        LOGGER.debug("Enter CardManagementController->getContent");
        Map<String,Object> getContentResp = null;
        getContentResp = cardDetailService.getContent(getContentRequest);
        LOGGER.debug("Exit CardManagementController->getContent");
      return getContentResp;
    }

    /**
     * delete
     * @param lifeCycleManagementReq lifeCycleMgmtReq
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/lifeCycleManagement",method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    @DecryptFlowStep("decryptData")
    public Map<String,Object>delete(@RequestBody String lifeCycleManagementReq){
        LifeCycleManagementReq lifeCycleManagementReqPojo = null;
        Map<String,Object> lifeCycleManagementResp = null;
        try {
            lifeCycleManagementReqPojo = (LifeCycleManagementReq)hCEControllerSupport.requestFormation(lifeCycleManagementReq,LifeCycleManagementReq.class);
            lifeCycleManagementResp =  cardDetailService.performCardLifeCycleManagement(lifeCycleManagementReqPojo);
        }catch (HCEActionException enrollPanHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->lifeCycleManagement",enrollPanHceActionException);
            throw enrollPanHceActionException;
        }catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->lifeCycleManagement", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return lifeCycleManagementResp;
    }


    /**
     * getToken
     * @param getTokensRequest getTokenRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/getToken", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String, Object> getToekns(@RequestBody String getTokensRequest) {
        Map<String,Object> getToeknsTokensResp = null;
        GetTokensRequest getTokensRequestPojo = null;
        try {
            getTokensRequestPojo = (GetTokensRequest)hCEControllerSupport.requestFormation(getTokensRequest,GetTokensRequest.class);
            getToeknsTokensResp = cardDetailService.getTokens(getTokensRequestPojo);
        }catch (HCEActionException getToeknsHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->enrollPan",getToeknsHceActionException);
            throw getToeknsHceActionException;
        }catch (Exception getToeknsExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", getToeknsExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return  getToeknsTokensResp;
    }

    /**
     * searchTokens
     * @param getTokensRequest getTokenRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/searchTokens", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> searchTokens(@RequestBody String getTokensRequest) {
        Map<String,Object> searchTokensResp = null;
        SearchTokensReq searchTokensReqPojo = null;
        try {
            searchTokensReqPojo = (SearchTokensReq)hCEControllerSupport.requestFormation(getTokensRequest,SearchTokensReq.class);
            searchTokensResp =  cardDetailService.searchTokens(searchTokensReqPojo);
        }catch (HCEActionException searchTokensHceActionException){
            LOGGER.error("Exception Occured in CardManagementController->searchTokens",searchTokensHceActionException);
            throw searchTokensHceActionException;
        }catch (Exception enrollPanExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->enrollPan", enrollPanExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return searchTokensResp;
    }

    /**
     * unregisterFromTds
     * @param unregisterTdsReq unregisterTdsReq
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/unregisterTds", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public Map<String,Object> unregisterFromTds(@RequestBody String unregisterTdsReq) {
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
    @RequestMapping(value = "/getSystemHealth", method = RequestMethod.GET)
    public Map<String,Object> getSystemHealth() {
        Map <String, Object> getSystemHealthResp=null;
        try{
            //Check master card is accessable or not.
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
    @RequestMapping(value = "/pkCertificate", method = RequestMethod.POST)
    public Map getPublicKeyCertificate() {
        Map getPublicKeyCertificateResp = null;
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
    @RequestMapping(value = "/customerCareContact", method = RequestMethod.GET)
    public Map<String,Object> getCustomerCareContact() {
        Map <String, Object> getCustomerCareContactResp = null;
        try{
            getCustomerCareContactResp = cardDetailService.getCustomerCareContact();
        }catch (HCEActionException getCustomerCareContactException){
            LOGGER.error("Exception Occured in CardManagementController->getCustomerCareContact",getCustomerCareContactException);
            throw getCustomerCareContactException;
        }catch (Exception getCustomerCareContactExcetption) {
            LOGGER.error(" Exception Occured in CardManagementController->getCustomerCareContact", getCustomerCareContactExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return getCustomerCareContactResp;
    }


    /**
     * reDigitization
     * @param reDigitizationRequest reDigitizationRequest
     * @return map
     * */
    @ResponseBody
    @RequestMapping(value = "/reDigitization", method = RequestMethod.POST)
    @ServiceFlowStep("paymentApp")
    public  Map<String, Object> reDigitization(@RequestBody String reDigitizationRequest) {
        RedigitizeReq reDigitizationRequestPojo = null;
        Map <String,Object> reDigitizationResponse= null;
        try{
            reDigitizationRequestPojo = (RedigitizeReq) hCEControllerSupport.requestFormation(reDigitizationRequest,
                    RedigitizeReq.class);
            reDigitizationResponse = cardDetailService.reDigitize(reDigitizationRequestPojo);
        }catch (HCEActionException reDigitizationHceActionException){
            LOGGER.error("Exception Occurred in CardManagementController->reDigitize",reDigitizationHceActionException);
            throw reDigitizationHceActionException;
        }catch (Exception reDigitizationExcetption) {
            LOGGER.error(" Exception Occurred in CardManagementController->reDigitize", reDigitizationExcetption);
            throw new HCEActionException(HCEMessageCodes.getServiceFailed());
        }
        return reDigitizationResponse;
    }
}
