package com.comviva.mfs.hce.appserver.service.contract;

import com.comviva.mfs.hce.appserver.mapper.pojo.AddCardParm;
import com.comviva.mfs.hce.appserver.mapper.pojo.DigitizationParam;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetAssetPojo;
import com.comviva.mfs.hce.appserver.mapper.pojo.TokenizeRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActivateReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.EnrollPanRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetCardMetadataRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetContentRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTransactionDetailsReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.TDSRegistrationReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.LifeCycleManagementReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.GetTokensRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.ActivationCodeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.SearchTokensReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnregisterTdsReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTokenUpdatedReq;

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
    Map<String, Object> notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq);
    Map<String, Object> registerWithTDS(TDSRegistrationReq tdsRegistrationReq);
    Map<String, Object> performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq);
    Map<String, Object> getTokens(GetTokensRequest getTokensRequest);
    Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq);
    Map<String, Object> searchTokens(SearchTokensReq searchTokensReq);
    Map<String, Object> unregisterTds(UnregisterTdsReq unregisterTdsReq);
    Map<String,Object> getSystemHealth();
    Object getPublicKeyCertificate();
    Map<String,Object> notifyTokenUpdated(NotifyTokenUpdatedReq notifyTokenUpdatedReqPojo);
    Map<String,Object> getCustomerCareContact();
}
