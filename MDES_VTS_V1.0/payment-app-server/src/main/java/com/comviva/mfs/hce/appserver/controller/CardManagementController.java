package com.comviva.mfs.hce.appserver.controller;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
@RestController
@RequestMapping("/api/card")
public class CardManagementController {

    @Autowired
    private CardDetailService cardDetailService;

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
    public Map<String, Object> enrollPan(@RequestBody EnrollPanRequest enrollPanRequest){
        return cardDetailService.enrollPan(enrollPanRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/getCardMetadata",method = RequestMethod.POST)
    public Map<String,Object> getCardMetadata(@RequestBody GetCardMetadataRequest getCardMetadataRequest){
        return cardDetailService.getCardMetadata(getCardMetadataRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/getContent",method = RequestMethod.POST)
    public Map<String,Object>getContent(@RequestBody GetContentRequest getContentRequest){
      return cardDetailService.getContent(getContentRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/getPANData",method = RequestMethod.POST)
    public Map<String,Object>getPANData(@RequestBody GetPANDataRequest getPANDataRequest){
        return cardDetailService.getPANData(getPANDataRequest);
    }
    @ResponseBody
    @RequestMapping(value = "/delete",method = RequestMethod.POST)
    public Map<String,Object>delete(@RequestBody LifeCycleManagementReq lifeCycleManagementReq){
        return cardDetailService.deleteCard(lifeCycleManagementReq);
    }
    //Transction History APIS
    @ResponseBody
    @RequestMapping(value = "/notifyTransactionDetails", method = RequestMethod.POST)
    public Map notifyTransactionDetails(@RequestBody NotifyTransactionDetailsReq notifyTransactionDetailsReq) {
        return cardDetailService.notifyTransactionDetails(notifyTransactionDetailsReq);
    }
    @ResponseBody
    @RequestMapping(value = "/getRegistrationCode", method = RequestMethod.POST)
    public Map getRegistrationCode(@RequestBody GetregCodeReq getregCodeReq) {
        return cardDetailService.getRegistrationCode(getregCodeReq);
    }
    @ResponseBody
    @RequestMapping(value = "/registerWithTDS", method = RequestMethod.POST)
    public Map registerWithTDS(@RequestBody TDSRegistration tdsRegistration) {
        return cardDetailService.registerWithTDS(tdsRegistration);
    }
    @ResponseBody
    @RequestMapping(value = "/getTransactions", method = RequestMethod.POST)
    public Map registerWithTDS(@RequestBody GetTransactionHistoryReq getTransactionHistoryReq) {
        return cardDetailService.getTransctionHistory(getTransactionHistoryReq);
    }

    @ResponseBody
    @RequestMapping(value = "/requestActivationCode", method = RequestMethod.POST)
    public Map requestActivationCode(@RequestBody ActivationCodeReq activationCodeReq) {
        return cardDetailService.requestActivationCode(activationCodeReq);
    }
}