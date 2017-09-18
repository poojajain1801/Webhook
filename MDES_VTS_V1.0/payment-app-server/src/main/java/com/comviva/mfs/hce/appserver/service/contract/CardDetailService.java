package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface CardDetailService {
    AddCardResponse checkDeviceEligibility(AddCardParm addCardParm);
    AddCardResponse addCard(DigitizationParam digitizationParam);
    Asset getAsset(String assetId);
    ActivateResp activate(ActivateReq activateReq);
    Map<String, Object> enrollPan(EnrollPanRequest enrollPanRequest);
    Map<String,Object> getCardMetadata(GetCardMetadataRequest getCardMetadataRequest);
    Map<String,Object>getContent(GetContentRequest getContentRequest);
    Map<String,Object>getPANData(GetPANDataRequest getPANDataRequest);
    Map notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq);
    Map getRegistrationCode(GetRegCodeReq getRegCodeReq);
    Map registerWithTDS(TDSRegistration tdsRegistration);
    Map getTransactionHistory(GetTransactionHistoryReq getTransactionHistoryRea);
    Map performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq);
    Map getTokens(GetTokensRequest getTokensRequest);
    Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq);
    Map searchTokens(SearchTokensReq searchTokensReq);
    Map unregisterTds(Map<String, String> unregisterTdsReq);
}
