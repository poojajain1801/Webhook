package com.comviva.mfs.hce.appserver.service;

import com.comviva.mfs.Utils.DefaultTemplateUtils;
import com.comviva.mfs.Utils.ServiceUtils;
import com.comviva.mfs.hce.appserver.service.contract.ReportsService;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.Before;
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

/**
 * Created by rishikesh.kumar on 30-01-2019.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class ReportServiceImplTest {

    @Autowired
    ReportsService reportsService;
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/api/");
    }

    @Test
    public void consumerReport() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/consumerReportReq.json");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/consumerReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void consumerReportWithoutUserId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/consumerReportReq.json");
        request.remove("userId");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/consumerReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void consumerReportWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/consumerReportReq.json");
        request.remove("fromDate");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/consumerReport",request);
        assertResponse(registerUserResp, "500");
    }

    @Test
    public void deviceReport() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/deviceReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void deviceReportWithoutUserId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        request.remove("userId");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/deviceReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void deviceReportWithoutDeviceId() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        request.remove("deviceId");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/deviceReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void deviceReportWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/deviceReportReq.json");
        request.remove("toDate");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/deviceReport",request);
        assertResponse(registerUserResp, "500");
    }

    @Test
    public void userDeviceCardMappingReport() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userDeviceCardReportReq.json");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/userDeviceCardMappingReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void userDeviceCardMappingReportWithoutUserStatus() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userDeviceCardReportReq.json");
        request.remove("userStatus");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/userDeviceCardMappingReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void userDeviceCardMappingReportWithoutDeviceStatus() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userDeviceCardReportReq.json");
        request.remove("deviceStatus");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/userDeviceCardMappingReport",request);
        assertResponse(registerUserResp, "200");
    }

    @Test
    public void userDeviceCardMappingReportWithException() throws Exception {
        Map request = DefaultTemplateUtils.buildRequest("/userDeviceCardReportReq.json");
        request.remove("fromDate");
        Map registerUserResp = ServiceUtils.servicePOSTResponse("reports/userDeviceCardMappingReport",request);
        assertResponse(registerUserResp, "500");
    }
}
