package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.comviva.mfs.hce.appserver.exception.HCEActionException;
import com.comviva.mfs.hce.appserver.mapper.pojo.*;
import com.comviva.mfs.hce.appserver.service.contract.CardDetailService;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Assert;
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
import java.security.PrivateKey;
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
    public void registerUserWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterUserReq.json");
        request.put("random","");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("user/userRegistration",request);
        assertResponse(registerUserResp, "706");
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

    /*@Test
    public void enrollPan() throws Exception {
        String enrollPanRespCode = null;
        EnrollPanRequest enrollPanRequest = new EnrollPanRequest();
        EncPaymentInstrument encPaymentInstrument = new EncPaymentInstrument();
        encPaymentInstrument.setAccountNumber("4260838210001459");
        enrollPanRequest.setClientAppId("NBKewallet");
        enrollPanRequest.setClientDeviceID("B4D7F2161537854926231FFF");
        enrollPanRequest.setClientWalletAccountId("PT1809251125ELBH1KHRN7WD");
        enrollPanRequest.setLocale("en-US");
        enrollPanRequest.setPanSource("MANUALLYENTERED");
        enrollPanRequest.setEncPaymentInstrument(encPaymentInstrument);
        try {
            Map enrollPanResp = cardDetailService.enrollPan(enrollPanRequest);
        }catch(HCEActionException enrollPanHCEactionException){
            enrollPanRespCode =  enrollPanHCEactionException.getMessageCode();
        }
        Assert.assertEquals(enrollPanRespCode ,"704");
    }*/

    @Test
    public void enrollPanWithInvalidRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/enrollPanReq.json");
        Map enrollPanResp = ServiceUtils.servicePOSTResponse("card/enrollPan",request);
        assertResponse(enrollPanResp, "704");
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
        assertResponse(addCardResponse, "500");
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
        Map continueDegitizationResp = ServiceUtils.servicePOSTResponse("card/continueDigitization",request);
        assertResponse(continueDegitizationResp, "500");
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
    public void tokenize() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/tokenize.json");
        Map tokenizeResp = ServiceUtils.servicePOSTResponse("card/tokenize",request);
        assertResponse(tokenizeResp,"500");
    }

    @Test
    public void getAsset() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getAssetReq.json");
        Map getAssetResp = ServiceUtils.servicePOSTResponse("card/mdes/asset",request);
        assertResponse(getAssetResp, "200");
    }


    @Test
    public void activate() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map activateResp = ServiceUtils.servicePOSTResponse("card/activate",request);
        assertResponse(activateResp, "750");
    }

    @Test
    public void activateWithoutTkenUniqueReference() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("tokenUniqueReference",null);
        Map activateResp = ServiceUtils.servicePOSTResponse("card/activate",request);
        assertResponse(activateResp, "737");
    }

    @Test
    public void activateWithNullRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request = null;
        Map activateResp = ServiceUtils.servicePOSTResponse("card/activate",request);
        assertResponse(activateResp, "500");
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
        assertResponse(lifeCycleManagementResp, "500");
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
    public void getTokens() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        Map getTokenResp = ServiceUtils.servicePOSTResponse("card/getToken",request);
        assertResponse(getTokenResp, "707");
    }

    @Test
    public void getTokensWithIncorrectParameters() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTokenReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map getTokenResp = ServiceUtils.servicePOSTResponse("card/getToken",request);
        assertResponse(getTokenResp, "707");
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
    public void searchTokens() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/searchTokensReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        Map searchTokensResp = ServiceUtils.servicePOSTResponse("card/searchTokens",request);
        assertResponse(searchTokensResp, "704");
    }

    @Test
    public void searchTokensWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/searchTokensReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        request.put("paymentAppInstanceId",null);
        Map searchTokensResp = ServiceUtils.servicePOSTResponse("card/searchTokens",request);
        assertResponse(searchTokensResp, "500");
    }

    @Test
    public void requestActivationCode() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/requestActivationCodeReq.json");
        Map requestActivationCodeResp = ServiceUtils.servicePOSTResponse("card/requestActivationCode",request);
        assertResponse(requestActivationCodeResp, "757");
    }

    @Test
    public void requestActivationCodeWithInvalidReq() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/requestActivationCodeReq.json");
        paymentAppInstanceId = DefaultTemplateUtils.randomString(48);
        String tokenUniqueReference = DefaultTemplateUtils.randomString(64);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map requestActivationCodeResp = ServiceUtils.servicePOSTResponse("card/requestActivationCode",request);
        assertResponse(requestActivationCodeResp, "750");
    }

    @Test
    public void unregisterFromTds() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        Map unregisterTdsResp = ServiceUtils.servicePOSTResponse("card/unregisterTds",request);
        assertResponse(unregisterTdsResp, "500");
    }

    @Test
    public void unregisterFromTdsWithInvalidToken() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/unregisterTdsReq.json");
        String tokenUniqueReference = DefaultTemplateUtils.randomString(48);
        request.put("tokenUniqueReference",tokenUniqueReference);
        Map unregisterTdsResp = ServiceUtils.servicePOSTResponse("card/unregisterTds",request);
        assertResponse(unregisterTdsResp, "707");
    }

    @Test
    public void getSystemHealth() throws Exception {
        Map systemHealthResponse = ServiceUtils.serviceGETResponse("card/getSystemHealth",null,null);
        assertResponse(systemHealthResponse,"200");
    }

    @Test
    public void getPublicKeyCertificate() throws Exception {
        Object response = cardDetailService.getPublicKeyCertificate();
        Assert.assertNotNull(response);
    }
}
