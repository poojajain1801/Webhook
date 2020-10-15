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
    /**
     * CheckDevice Eligibility
     * */
    Map<String, Object> checkDeviceEligibility(AddCardParm addCardParm);

    /**
     * Add Card
     * */
    Map<String, Object>  addCard(DigitizationParam digitizationParam);

    /**
     * tokenize
     * */
    Map<String, Object>  tokenize(TokenizeRequest tokenizeRequest);

    /**
     * GetAssetPojo
     * */
    Map<String, Object> getAsset(GetAssetPojo assetId);

    /**
     * Activate
     * */
    Map<String, Object> activate(ActivateReq activateReq);

    /**
     * enrollPan
     * */
    Map<String, Object> enrollPan(EnrollPanRequest enrollPanRequest);

    /**
     * getCardMetaData
     * */
    Map<String,Object> getCardMetadata(GetCardMetadataRequest getCardMetadataRequest);

    /**
     * getContent
     * */
    Map<String,Object>getContent(GetContentRequest getContentRequest);

    /**
     * notifyTransactionDetails
     * */
    Map<String, Object> notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq);

    /**
     * registerWithTDS
     * */
    Map<String, Object> registerWithTDS(TDSRegistrationReq tdsRegistrationReq);

    /**
     * performCardLifeCycleManagement
     * */
    Map<String, Object> performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq);

    /**
     * getTokens
     * */
    Map<String, Object> getTokens(GetTokensRequest getTokensRequest);

    /**
     * requestActivationCode
     * */
    Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq);

    /**
     * searchTokens
     * */
    Map<String, Object> searchTokens(SearchTokensReq searchTokensReq);

    /**
     * unregisterTds
     * */
    Map<String, Object> unregisterTds(UnregisterTdsReq unregisterTdsReq);

    /**
     * getSystemHealth
     * */
    Map<String,Object> getSystemHealth();

    /**
     * getPublicKeyCertificate
     * */
    Object getPublicKeyCertificate();

    /**
     * notifyTokenUpdated
     * */
    Map<String,Object> notifyTokenUpdated(NotifyTokenUpdatedReq notifyTokenUpdatedReqPojo);

    /**
     * getCustomerCareContact
     * */
    Map<String,Object> getCustomerCareContact();
}
