package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.*;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface CardDetailService {
    Map<String, Object> checkDeviceEligibility(AddCardParm addCardParm);
    Map<String, Object>  addCard(DigitizationParam digitizationParam);
    Map<String, Object>  tokenize(TokenizeRequest tokenizeRequest);
    Map<String, Object> getAsset(GetAssetPojo assetId);
    Map<String, Object> activate(ActivateReq activateReq);
    Map<String, Object> enrollPan(EnrollPanRequest enrollPanRequest);
    Map<String,Object> getCardMetadata(GetCardMetadataRequest getCardMetadataRequest);
    Map<String,Object>getContent(GetContentRequest getContentRequest);
    Map<String,Object>getPANData(GetPANDataRequest getPANDataRequest);
    Map notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq);
    Map registerWithTDS(TDSRegistrationReq tdsRegistrationReq);
    Map performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq);
    Map getTokens(GetTokensRequest getTokensRequest);
    Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq);
    Map searchTokens(SearchTokensReq searchTokensReq);
    Map unregisterTds(UnregisterTdsReq unregisterTdsReq);
    Map<String,Object> getSystemHealth();
    Object getPublicKeyCertificate();
    Map<String,Object> notifyTokenUpdated(NotifyTokenUpdatedReq notifyTokenUpdatedReqPojo);
}
