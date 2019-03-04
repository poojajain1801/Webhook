package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.controller.HCEControllerSupport;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Assert;
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
public class TransactionManagementServiceTest {
    @Resource
    private WebApplicationContext webApplicationContext;
    private String paymentAppInstanceId = "";
    private String userID = DefaultTemplateUtils.randomString(8);
    private String clientDeviceID = DefaultTemplateUtils.randomString(24);

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/transaction");
    }

    @Test
    public void getTransactionsMasterCard() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        Map getTransactionResp = ServiceUtils.servicePOSTResponse("/getTransactions",request);
        assertResponse(getTransactionResp, "200");
    }

    @Test
    public void getTransactionsMasterCardNoTokenUniqueReference() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        request.put("tokenUniqueReference",null);
        Map getTransactionResp = ServiceUtils.servicePOSTResponse("/getTransactions",request);
        assertResponse(getTransactionResp, "200");
    }

    @Test
    public void getTransactionsMasterCardNoPaymentAppInstanceId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        request.put("paymentAppInstanceId",null);
        Map getTransactionResp = ServiceUtils.servicePOSTResponse("/getTransactions",request);
        assertResponse(getTransactionResp, "200");
    }

    @Test
    public void getTransactionsWithNullRequest() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsReq.json");
        request.put("paymentAppInstanceId",null);
        request.put("tokenUniqueReference",null);
        Map getTransactionResp = ServiceUtils.servicePOSTResponse("/getTransactions",request);
        assertResponse(getTransactionResp, "200");
    }

    @Test
    public void registerWithTDS() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterWithTDSReq.json");
        Map RegisterWithTDSResp = ServiceUtils.servicePOSTResponse("/registerWithTDS",request);
        assertResponse(RegisterWithTDSResp, "200");
    }

    @Test
    public void registerWithTDSNotokenUniqueReference() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterWithTDSReq.json");
        request.put("tokenUniqueReference",null);
        Map getTransactionResp = ServiceUtils.servicePOSTResponse("/registerWithTDS",request);
        assertResponse(getTransactionResp, "737");
    }

    @Test
    public void registerWithTDSNopaymentAppInstanceId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/RegisterWithTDSReq.json");
        request.put("paymentAppInstanceId",null);
        Map getTransactionResp = ServiceUtils.servicePOSTResponse("/registerWithTDS",request);
        assertResponse(getTransactionResp, "750");
    }

    @Test
    public void registerWithTDSNullReq() throws Exception {
        Map request = null;
        Map RegisterWithTDSResp = ServiceUtils.servicePOSTResponse("/registerWithTDS",request);
        assertResponse(RegisterWithTDSResp, "500");
    }

    @Test
    public void getRegistrationCode() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getRegistrationCodeReq.json");
        Map getRegistrationCodeResp = ServiceUtils.servicePOSTResponse("registerWithTDS",request);
        assertResponse(getRegistrationCodeResp, "200");
    }

    @Test
    public void getRegistrationCodeWithInvalidToken() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getRegistrationCodeReq.json");
        String paymentAppInstanceId = DefaultTemplateUtils.randomString(32);
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        Map getRegistrationCodeResp = ServiceUtils.servicePOSTResponse("registerWithTDS",request);
        assertResponse(getRegistrationCodeResp, "750");
    }

    @Test
    public void registerWithTDSNullTokenUniqueRef() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/registerWithTDSReq.json");
        request.put("tokenUniqueReference",null);
        Map registerWithTDSResp = ServiceUtils.servicePOSTResponse("registerWithTDS",request);
        assertResponse(registerWithTDSResp, "737");
    }

    @Test
    public void getTransactionHistoryVisa() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/getTransactionsHistoryReq.json");
        Map getTransactionsHistoryResp = ServiceUtils.servicePOSTResponse("/getTransactionHistory",request);
        assertResponse(getTransactionsHistoryResp, "500");
    }

    @Test
    public void utcToLocalTime() throws Exception {
        HCEControllerSupport hceControllerSupport = new HCEControllerSupport();
        TransactionManagementServiceImpl transactionManagementService = new TransactionManagementServiceImpl(hceControllerSupport);
        String getTransactionsHistoryResp = transactionManagementService.utcToLocalTime("2011-01-01 15:00:00");
        Assert.assertNotNull(getTransactionsHistoryResp);
    }
}
