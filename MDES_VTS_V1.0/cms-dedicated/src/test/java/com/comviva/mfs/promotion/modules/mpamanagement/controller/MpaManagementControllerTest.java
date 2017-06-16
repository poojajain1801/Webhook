package com.comviva.mfs.promotion.modules.mpamanagement.controller;

import com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.DefaultTemplateUtils;
import com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.ServiceUtils;
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

import static com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.ServiceUtils.assertResponse;
import static org.junit.Assert.*;

/**
 * Created by tanmay.patel on 4/24/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class MpaManagementControllerTest {
    String paymentAppInstanceId="";
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/mdes/");
    }
    @Test
    public void registerDevice_ShouldSucceed() throws Exception {
        paymentAppInstanceId = DefaultTemplateUtils.randomString(8);
        Map response = postRegisterDevice(paymentAppInstanceId);
        assertResponse(response, "200");

    }
    @Test
    public void registerDevice_should_throwError_Device_AllReadyRegistered(){
        try {
            registerDevice_ShouldSucceed();
            Map response = postRegisterDevice(paymentAppInstanceId);
            assertResponse(response, "210");
        } catch (Exception e) {
            e.printStackTrace();
        }


    }
    public Map postRegisterDevice(String paymentAppInstanceId)
    {
        Map request = DefaultTemplateUtils.buildRequest("/register_device.json");
        request.put("paymentAppInstanceId",paymentAppInstanceId);
        return ServiceUtils.servicePOSTResponse("mpamanagement/1/0/register",request);

    }

}