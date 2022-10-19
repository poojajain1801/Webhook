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
package com.comviva.mfs.hce.appserver.service.contract;

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
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTokenUpdatedReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.NotifyTransactionDetailsReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.RedigitizeReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.SearchTokensReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.TDSRegistrationReq;
import com.comviva.mfs.hce.appserver.mapper.pojo.TokenizeRequest;
import com.comviva.mfs.hce.appserver.mapper.pojo.UnregisterTdsReq;

import java.util.Map;

/**
 * Created by Tanmay.Patel on 1/8/2017.
 */
public interface CardDetailService {
    /**
     * CheckDevice Eligibility
     * @param addCardParm card details
     * @return Map
     * */
    Map<String, Object> checkDeviceEligibility(AddCardParm addCardParm);

    /**
     * Add Card
     * @param digitizationParam digitazation details
     * @return Map
     * */
    Map<String, Object>  addCard(DigitizationParam digitizationParam);

    /**
     * tokenize
     * @param tokenizeRequest token details
     * @return Map
     * */
    Map<String, Object>  tokenize(TokenizeRequest tokenizeRequest);

    /**
     * GetAssetPojo
     * @param assetId asset id
     * @return Map
     * */
    Map<String, Object> getAsset(GetAssetPojo assetId);

    /**
     * Activate
     * */
    Map<String, Object> activate(ActivateReq activateReq);

    /**
     * enrollPan
     * @param enrollPanRequest enroll pan details
     * @return Map
     * */
    Map<String, Object> enrollPan(EnrollPanRequest enrollPanRequest);

    /**
     * getCardMetaData
     * @param getCardMetadataRequest card metadata
     * @return Map
     * */
    Map<String,Object> getCardMetadata(GetCardMetadataRequest getCardMetadataRequest);

    /**
     * getContent
     * @param getContentRequest contentRequest
     * @return Map
     * */
    Map<String,Object>getContent(GetContentRequest getContentRequest);

    /**
     * notifyTransactionDetails
     * @param notifyTransactionDetailsReq notification
     * @return Map
     * */
    Map<String, Object> notifyTransactionDetails(NotifyTransactionDetailsReq notifyTransactionDetailsReq);

    /**
     * registerWithTDS
     * @param tdsRegistrationReq tdsRequest
     * @return Map
     * */
    Map<String, Object> registerWithTDS(TDSRegistrationReq tdsRegistrationReq);

    /**
     * performCardLifeCycleManagement
     * @param lifeCycleManagementReq lifeCycleManagement details
     * @return Map
     * */
    Map<String, Object> performCardLifeCycleManagement(LifeCycleManagementReq lifeCycleManagementReq);

    /**
     * getTokens
     * @param getTokensRequest tokenRequest
     * @return Map
     * */
    Map<String, Object> getTokens(GetTokensRequest getTokensRequest);

    /**
     * requestActivationCode
     * @param activationCodeReq activatationCoderequest
     * @return Map
     * */
    Map<String, Object> requestActivationCode(ActivationCodeReq activationCodeReq);

    /**
     * searchTokens
     * @param searchTokensReq searchTokenReq
     * @return Map
     * */
    Map<String, Object> searchTokens(SearchTokensReq searchTokensReq);

    /**
     * unregisterTds
     * @param unregisterTdsReq unregisterTdsRequest
     * @return Map
     * */
    Map<String, Object> unregisterTds(UnregisterTdsReq unregisterTdsReq);

    /**
     * getSystemHealth
     * @return Map
     * */
    Map<String,Object> getSystemHealth();

    /**
     * getPublicKeyCertificate
     * @return object
     * */
    Object getPublicKeyCertificate();

    /**
     * notifyTokenUpdated
     * @param notifyTokenUpdatedReqPojo notifyTokenUpdatedPojo
     * @return Map
     * */
    Map<String,Object> notifyTokenUpdated(NotifyTokenUpdatedReq notifyTokenUpdatedReqPojo);

    /**
     * getCustomerCareContact
     * @return Map
     * */
    Map<String,Object> getCustomerCareContact();

    /**
     * reDigitize
     * @return Map
     * */
    Map<String, Object> reDigitize(RedigitizeReq reDigitizationRequestPojo);
}
