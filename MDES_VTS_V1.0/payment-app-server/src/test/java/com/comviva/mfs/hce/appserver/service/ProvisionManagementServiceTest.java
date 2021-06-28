package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import java.util.Map;

import static com.comviva.mfs.Utils.ServiceUtils.assertResponse;

/**
 * Created by rishikesh.kumar on 29-11-2018.
 */

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ProvisionManagementServiceTest {
    @Resource
    private WebApplicationContext webApplicationContext;
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);


    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/provisionCard");
    }

    @Test
    public void provisionTokenWithPanEnrollmentId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        Map provisionTokenResp = ServiceUtils.servicePOSTResponse("/provisionTokenWithPanEnrollmentId",request);
        assertResponse(provisionTokenResp, "707");
    }

    @Test
    public void provisionTokenWithPanEnrollmentIdNoclientDeviceID() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        request.put("clientDeviceID",null);
        Map provisionTokenResp = ServiceUtils.servicePOSTResponse("/provisionTokenWithPanEnrollmentId",request);
        assertResponse(provisionTokenResp, "707");
    }

    @Test
    public void provisionTokenWithPanEnrollmentIdNopanEnrollmentID() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        request.put("panEnrollmentID",null);
        Map provisionTokenResp = ServiceUtils.servicePOSTResponse("/provisionTokenWithPanEnrollmentId",request);
        assertResponse(provisionTokenResp, "707");
    }

    @Test
    public void provisionTokenWithPanEnrollmentIdNoemailAddress() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/provisionWithPanEnrollmentID.json");
        request.put("emailAddress",null);
        Map provisionTokenResp = ServiceUtils.servicePOSTResponse("/provisionTokenWithPanEnrollmentId",request);
        assertResponse(provisionTokenResp, "500");
    }

    @Test
    public void confirmProvisioning() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/confirmProvisioningReq.json");
        Map provisionTokenResp = ServiceUtils.servicePOSTResponse("/confirmProvisioning",request);
        assertResponse(provisionTokenResp, "500");
    }

    @Test
    public void confirmProvisioningNovprovisionedTokenID() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/confirmProvisioningReq.json");
//        request.put("vprovisionedTokenId",null);
        Map provisionTokenResp = ServiceUtils.servicePOSTResponse("/confirmProvisioning",request);
        assertResponse(provisionTokenResp, "500");
    }


    @Test
    public void getStepUpOptions() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getStepUpOptionsReq.json");
        Map stepupoptResp = ServiceUtils.servicePOSTResponse("/getStepUpOptions",request);
        assertResponse(stepupoptResp, "409");
    }

    @Test
    public void validateOTP() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/validateOTPReq.json");
        Map validateOTPResp = ServiceUtils.servicePOSTResponse("/validateOTP",request);
        assertResponse(validateOTPResp, "400");
    }

    @Test
    public void activeAccountManagementReplenish() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activateAccountManagementReplenishReq.json");
        Map ReplenishResp = ServiceUtils.servicePOSTResponse("/activeAccountManagementReplenish",request);
        assertResponse(ReplenishResp, "500");
    }

    @Test
    public void activeAccountManagementConfirmReplenishment() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/activeAccountManagementConfirmReplenishmentRequest.json");
        Map ReplenishResp = ServiceUtils.servicePOSTResponse("/activeAccountManagementConfirmReplenishment",request);
        assertResponse(ReplenishResp, "500");
    }

    @Test
    public void submitIDandVStepupMethodRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/submitIDandVStepupMethodReq.json");
        Map stepupResp = ServiceUtils.servicePOSTResponse("/submitIDandVStepupMethodRequest",request);
        assertResponse(stepupResp, "400");
    }
}



