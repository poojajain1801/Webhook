package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;
import static org.junit.Assert.assertEquals;

/**
 * Created by rishikesh.kumar on 19-04-2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CardDetailServiceImplTest {

    @Autowired
    private CardDetailService cardDetailService;

    @Resource
    private WebApplicationContext webApplicationContext;

    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");

    }

    @Test
    public void getTransactionHistory() throws Exception {
        GetTransactionHistoryReq getTransactionHistoryReq = new GetTransactionHistoryReq();
        getTransactionHistoryReq.setAuthenticationCode("sdfghjkl;p;lkjhgfertyu");
        getTransactionHistoryReq.setLastUpdatedTag("jkldfkefkm sdlfkfo");
        getTransactionHistoryReq.setTokenUniqueReference("34567890098765345678");
        Map<String,Object> response  = cardDetailService.getTransactionHistory(getTransactionHistoryReq);
        assertResponse(response, "200");
    }

    @Test
    public void getAsset() throws Exception {
        GetAssetRequest getAssetRequest = new GetAssetRequest();
        getAssetRequest.setAssetId("dfghjklkjdfghjk");
        Map<String,Object> response  = cardDetailService.getAsset(getAssetRequest);
        assertResponse(response, "200");
    }

    @Test
    public void getAssetWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getAssetReq.json");
        request.remove("assetId");
        request.put("dfccs",1234);
        Map getAssetResp = ServiceUtils.servicePOSTResponse("card/getAsset",request);
        assertResponse(getAssetResp, "706");
    }

    @Test
    public void getAssetWithInvalidRequestId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getAssetReq.json");
        Map getAssetResp = ServiceUtils.servicePOSTResponse("card/getAsset",request);
        assertResponse(getAssetResp, "200");
    }

    @Test
    public void activate() throws Exception {
        ActivateReq activateReq = new ActivateReq();
        activateReq.setAuthenticationCode("jkjkmlkeoiokclk3oeckl,ovk");
        activateReq.setPaymentAppInstanceId("kccefiokmodkr9090ikooklckl");
        activateReq.setTokenizationAuthenticationValue("iujieejij930fkolklkvj");
        activateReq.setTokenUniqueReference("ofkor990lmkkrop3-r-ppleifk");
        Map<String,Object> response = cardDetailService.activate(activateReq);
        assertResponse(response, "200");

    }

    @Test
    public void notifyTransactionDetails() throws Exception {
        NotifyTransactionDetailsReq notifyTransactionDetailsReq = new NotifyTransactionDetailsReq();
        notifyTransactionDetailsReq.setPaymentAppInstanceId("ikvokpdlklcdlp[dfjhnm,j");
        notifyTransactionDetailsReq.setRegistrationCode2("568909876556789");
        notifyTransactionDetailsReq.setTdsUrl("fuufificnjfvrfkokovkfovkefv");
        notifyTransactionDetailsReq.setTokenUniqueReference("vjokpel;poplvljkhkvklklfk");
        Map<String,Object> response = cardDetailService.notifyTransactionDetails(notifyTransactionDetailsReq);
        assertResponse(response, "200");
    }

    @Test
    public void getRegistrationCode() throws Exception {
        GetRegistrationCodeReq getRegistrationCodeReq = new GetRegistrationCodeReq();
        getRegistrationCodeReq.setTokenUniqueReference("fuufificnjfvrfkokovkfovkefv");
        Map<String,Object> response = cardDetailService.getRegistrationCode(getRegistrationCodeReq);
        assertResponse(response, "200");
    }

    @Test
    public void registerWithTDS() throws Exception {
        TDSRegistrationReq tdsRegistrationReq = new TDSRegistrationReq();
        tdsRegistrationReq.setRegistrationHash("ikvokpdlklcdlpji");
        tdsRegistrationReq.setTokenUniqueReference("kccefiokmodkr9090ikooklckl");
        Map<String,Object> response = cardDetailService.registerWithTDS(tdsRegistrationReq);
        assertResponse(response, "200");
    }

    @Test
    public void requestActivationCode() throws Exception {
        ActivationCodeReq activationCodeReq = new ActivationCodeReq();
        AuthenticationMethod authenticationMethod = new AuthenticationMethod() ;
        activationCodeReq.setAuthenticationMethod(authenticationMethod);
        activationCodeReq.setPaymentAppInstanceId("hfir239riokldkpfowpklpk");
        activationCodeReq.setTokenUniqueReference("jd237r8ioklsdkdo3ropvofpvk");
        Map<String , Object> response = cardDetailService.requestActivationCode(activationCodeReq);
        assertResponse(response, "200");
    }

    @Test
    public void unregisterTds() throws Exception {
        UnregisterTdsReq unregisterTdsReq = new UnregisterTdsReq();
        unregisterTdsReq.setAuthenticationCode("cjkdkoklkxlasklclkxkdk");
        unregisterTdsReq.setTokenUniqueReference("dhwjdi09e0okclkldioopclk");
        Map<String,Object> response = cardDetailService.unregisterTds(unregisterTdsReq);
        assertResponse(response, "200");
    }

    @Test
    public void getTokens() throws Exception {
        GetTokensRequest getTokensRequest = new GetTokensRequest();
        getTokensRequest.setIncludeTokenDetail(false);
        getTokensRequest.setPaymentAppInstanceId("2345678iuytrewerty");
        getTokensRequest.setTokenUniqueReference("fghjoiuy456789987");
        Map<String,Object> response = cardDetailService.getTokens(getTokensRequest);
        assertResponse(response, "200");
    }

    @Test
    public void searchTokens() throws Exception {
        SearchTokensReq searchTokensReq = new SearchTokensReq();
        CardInfo cardInfo = new CardInfo();
        cardInfo.setEncryptedData("shcjkjD");
        cardInfo.setEncryptedKey("dbujdjhidkooldk");
        cardInfo.setHashingAlgorithm("xjodkokclskc");
        cardInfo.setIv("dgfuhyifjoxckl");
        cardInfo.setPanUniqueReference("jhsiduioepi02e98");
        cardInfo.setPublicKeyFingerPrint("hqsduyui8sujxodko");
        cardInfo.setTokenUniqueReferenceForPanInfo("hdui8798idosjoqweiodik");
        searchTokensReq.setPaymentAppInstanceId("2345678iuytrewerty");
        searchTokensReq.setTokenRequestorId("akjfhijkcjasjeoion");
        searchTokensReq.setCardInfo(cardInfo);
        Map<String,Object> response = cardDetailService.searchTokens(searchTokensReq);
        assertResponse(response, "200");
    }

    @Test
    public void registerUser() throws Exception {
        Map UserRegistrationRequest = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        UserRegistrationRequest.put("userId",userID);
        UserRegistrationRequest.put("clientDeviceID",clientDeviceID);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",UserRegistrationRequest);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void registerUserWithoutClientDeviceId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        userID = DefaultTemplateUtils.randomString(8);
        request.put("userId",userID);
        request.put("clientDeviceID",null);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",request);
        assertResponse(registerUserResp, "500");
    }

    @Test
    public void registerUserWithoutUserId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        request.put("random","abcd");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",request);
        assertResponse(registerUserResp, "706");
    }

    @Test
    public void registerUserWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        request.remove("userId");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",request);
        assertResponse(registerUserResp, "700");
    }

    @Test
    public void registerUserWithNullRequest() throws Exception {
        Map request = null;
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",request);
        assertResponse(registerUserResp, "500");
    }

    @Test
    public void registerDevice() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("userId",userID);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("clientDeviceID",clientDeviceID);
        request.put("mdes",mdes);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "200");
    }

    @Test
    public void registerDeviceWithInvalidUser() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.remove("userId");
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "205");
    }

    @Test
    public void registerDeviceWithInvalidDeviceId() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        request.put("userId",userID);
        request.remove("clientDeviceID");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "703");
    }

    @Test
    public void registerDeviceWithoutVtsRequest() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        Map mdes = (Map) request.get("mdes");
        Map deviceInfo = (Map) mdes.get("deviceInfo");
        deviceInfo.put("imei",DefaultTemplateUtils.randomString(20));
        mdes.put("deviceInfo",deviceInfo);
        mdes.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("mdes",mdes);
        request.put("userId",userID);
        request.put("clientDeviceID",clientDeviceID);
        request.remove("vts");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "500");
    }

    @Test
    public void registerDeviceWithInvalidRequest() throws Exception {
        registerUser();
        Map request = DefaultTemplateUtils.buildRequest("/registerDeviceReq.json");
        request.put("random","");
        Map regDeviceReaponse = ServiceUtils.servicePOSTResponse("device/deviceRegistration",request);
        assertResponse(regDeviceReaponse, "706");
    }

    @Test
    @Ignore
    public void enrollPan() throws Exception {
        Map UserRegistrationRequest = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        UserRegistrationRequest.put("userId",userID);
        UserRegistrationRequest.put("clientDeviceID",clientDeviceID);
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",UserRegistrationRequest);
        registerDevice();
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        request.put("clientDeviceID",clientDeviceID);
        request.put("clientWalletAccountId",registerUserResp.get("clientWalletAccountId"));
        Map enrollPanResp = ServiceUtils.servicePOSTResponse("card/enrollPan",request);
        assertResponse(enrollPanResp, "200");
    }

    @Test
    public void enrollPanWithInvalidRequest() throws Exception {
        registerDevice();
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        request.put("clientDeviceID",clientDeviceID);
        request.put("Random","");
        Map enrollPanResp = ServiceUtils.servicePOSTResponse("card/enrollPan",request);
        assertResponse(enrollPanResp, "706");
    }

    @Test
    public void addCard() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        Map addCardResponse = ServiceUtils.servicePOSTResponse("card/checkCardEligibility",request);
        assertResponse(addCardResponse, "200");
    }

    @Test
    public void addCardWithoutCardInfo() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("cardInfo",null);
        Map addCardResponse = ServiceUtils.servicePOSTResponse("card/checkCardEligibility",request);
        assertResponse(addCardResponse, "200");
    }

    @Test
    public void addCardWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/checkCardEligibilityReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("Random","");
        Map addCardResponse = ServiceUtils.servicePOSTResponse("card/checkCardEligibility",request);
        assertResponse(addCardResponse, "706");
    }

    @Test
    public void addCardWithNullRequest() throws Exception {
        Map request = null;
        Map addCardResponse = ServiceUtils.servicePOSTResponse("card/checkCardEligibility",request);
        assertResponse(addCardResponse, "500");
    }

    @Test
    public void continueDigitization() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        Map continueDegitizationResp = ServiceUtils.servicePOSTResponse("card/continueDigitization",request);
        assertResponse(continueDegitizationResp, "200");
    }

    @Test
    public void continueDigitizationWithoutPaymentAppId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        request.put("paymentAppInstanceId",null);
        Map continueDegitizationResp = ServiceUtils.servicePOSTResponse("card/continueDigitization",request);
        assertResponse(continueDegitizationResp, "200");
    }

    @Test
    public void continueDigitizationWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/ContinueDigitization.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(20);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("Random req","");
        Map continueDegitizationResp = ServiceUtils.servicePOSTResponse("card/continueDigitization",request);
        assertResponse(continueDegitizationResp, "706");
    }

    @Test
    public void continueDigitizationWithNullReq() throws Exception {
        Map request = null;
        Map continueDegitizationResp = ServiceUtils.servicePOSTResponse("card/continueDigitization",request);
        assertResponse(continueDegitizationResp, "500");
    }


    @Test
    public void activateWithoutPaymentId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.remove("paymentAppInstanceId",paymentAppInstanceId);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map activateResp = ServiceUtils.servicePOSTResponse("card/activate",request);
        assertResponse(activateResp, "200");
    }

    @Test
    public void activateWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("Random Req","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map activateResp = ServiceUtils.servicePOSTResponse("card/activate",request);
        assertResponse(activateResp, "706");
    }

    @Test
    public void getCardMetadata() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getCardMetaDataReq.json");
        Map activateResp = ServiceUtils.servicePOSTResponse("card/getCardMetadata",request);
        assertResponse(activateResp, "200");
    }

    @Test
    public void getCardMetadataWithInvalidPanEnrollmentId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getCardMetaDataReq.json");
        String vpanEnrollmentID = DefaultTemplateUtils.randomString(24);
        request.put("vpanEnrollmentID",vpanEnrollmentID);
        Map activateResp = ServiceUtils.servicePOSTResponse("card/getCardMetadata",request);
        assertEquals(activateResp.get("responseCode"), 400);
    }

    @Test
    public void getContent() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getContentReq.json");
        Map getContentResp = ServiceUtils.servicePOSTResponse("card/getContent",request);
        assertResponse(getContentResp, "200");
    }

    @Test
    public void getContentWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getContentReq.json");
        request.put("Random","");
        request.put("guid",null);
        Map getContentResp = ServiceUtils.servicePOSTResponse("card/getContent",request);
        assertResponse(getContentResp, "500");
    }

    @Test
    public void getContentWithInvalidGUId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getContentReq.json");
        String guid = DefaultTemplateUtils.randomString(16);
        request.put("guid",guid);
        Map getContentResp = ServiceUtils.servicePOSTResponse("card/getContent",request);
        assertResponse(getContentResp, "500");
    }

    @Test
    public void provisionWithPanEnrollmentID() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        Map getContentResp = ServiceUtils.servicePOSTResponse("provision/provisionTokenWithPanEnrollmentId",request);
        assertResponse(getContentResp, "707");
    }


    @Test
    public void getPANData() throws Exception {

    }

    @Test
    public void lifeCycleManagement() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/LifeCycleManagementReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        Map lifeCycleManagementResp = ServiceUtils.servicePOSTResponse("card/lifeCycleManagement",request);
        assertResponse(lifeCycleManagementResp, "200");
    }

    @Test
    public void lifeCycleManagementWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/LifeCycleManagementReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        request.put("Random","");
        Map lifeCycleManagementResp = ServiceUtils.servicePOSTResponse("card/lifeCycleManagement",request);
        assertResponse(lifeCycleManagementResp, "706");
    }

    @Test
    public void lifeCycleManagementWithNullReq() throws Exception {
        Map request = null;
        Map lifeCycleManagementResp = ServiceUtils.servicePOSTResponse("card/lifeCycleManagement",request);
        assertResponse(lifeCycleManagementResp, "500");
    }

    @Test
    public void notifyTransactionDetailsWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/notifyTransactionDetailsReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("Random","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map notifyTransactionDetailsResp = ServiceUtils.servicePOSTResponse("card/notifyTransactionDetails",request);
        assertResponse(notifyTransactionDetailsResp, "706");
    }

    @Test
    public void getRegistrationCodeWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getRegistrationCodeReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("Random Req","");
        Map getRegistrationCodeResp = ServiceUtils.servicePOSTResponse("card/getRegistrationCode",request);
        assertResponse(getRegistrationCodeResp, "706");
    }

    @Test
    public void registerWithTDSWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/registerWithTDSReq.json");
        String registrationHash = DefaultTemplateUtils.randomString(64);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("Random req","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map registerWithTDSResp = ServiceUtils.servicePOSTResponse("card/registerWithTDS",request);
        assertResponse(registerWithTDSResp, "706");
    }

    @Test
    public void registerWithTDS1() throws Exception {

    }

    @Test
    public void getTransactions() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        String authenticationCode = DefaultTemplateUtils.randomString(64);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("authenticationCode",authenticationCode);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map getTransactionsResp = ServiceUtils.servicePOSTResponse("card/getTransactions",request);
        assertResponse(getTransactionsResp, "200");
    }

    @Test
    public void getTransactionsWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        String authenticationCode = DefaultTemplateUtils.randomString(64);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("random","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map getTransactionsResp = ServiceUtils.servicePOSTResponse("card/getTransactions",request);
        assertResponse(getTransactionsResp, "706");
    }

    @Test
    public void getTokensWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("Random","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map getTokenResp = ServiceUtils.servicePOSTResponse("card/getToken",request);
        assertResponse(getTokenResp, "706");
    }

    @Test
    public void searchTokensWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/searchTokensReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        request.put("Random","");
        Map searchTokensResp = ServiceUtils.servicePOSTResponse("card/searchTokens",request);
        assertResponse(searchTokensResp, "706");
    }

    @Test
    public void requestActivationCodeWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/requestActivationCodeReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("Random","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map requestActivationCodeResp = ServiceUtils.servicePOSTResponse("card/requestActivationCode",request);
        assertResponse(requestActivationCodeResp, "706");
    }

    @Test
    public void unregisterFromTds() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(48);
        String authenticationCode = DefaultTemplateUtils.randomString(64);
        request.put("authenticationCode",authenticationCode);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map unregisterTdsResp = ServiceUtils.servicePOSTResponse("card/unregisterTds",request);
        assertResponse(unregisterTdsResp, "200");
    }

    @Test
    public void unregisterFromTdsWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(48);
        String authenticationCode = DefaultTemplateUtils.randomString(64);
        request.put("Random","");
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map unregisterTdsResp = ServiceUtils.servicePOSTResponse("card/unregisterTds",request);
        assertResponse(unregisterTdsResp, "706");
    }
}