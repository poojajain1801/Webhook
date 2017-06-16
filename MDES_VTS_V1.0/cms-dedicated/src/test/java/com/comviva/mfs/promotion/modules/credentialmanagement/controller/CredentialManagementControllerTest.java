package com.comviva.mfs.promotion.modules.credentialmanagement.controller;

import com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.*;
import com.jayway.restassured.module.mockmvc.RestAssuredMockMvc;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;

import java.security.GeneralSecurityException;
import java.util.Map;

import static com.comviva.mfs.promotion.modules.mobilepaymentapi.utils.ServiceUtils.assertResponse;
import static org.junit.Assert.*;

/**
 * Created by tanmay.patel on 4/25/2017.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
public class CredentialManagementControllerTest {
    @Resource
    private WebApplicationContext webApplicationContext;

    @Before
    public void Setup(){
        MockMvc mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();
        RestAssuredMockMvc.mockMvc(mockMvc);
        ServiceUtils.serviceInit("/mdes/");
    }

    @Test
    public void provisionMdes_ShouldSucceed() throws Exception {
        Map response = postProvisionMdes();
        assertResponse(response, "200");
    }


    public Map postProvisionMdes()
    {
        Map request = DefaultTemplateUtils.buildRequest("/provision_for_mdes_req.json");
        request.put("encryptedData",AESUtil.encTokenCredentials());
        String tokenUniqueReference = DefaultTemplateUtils.randomString(48);
        request.put("tokenUniqueReference",tokenUniqueReference);
        return ServiceUtils.servicePOSTResponse("credentials/1/0/provision",request);

    }



}