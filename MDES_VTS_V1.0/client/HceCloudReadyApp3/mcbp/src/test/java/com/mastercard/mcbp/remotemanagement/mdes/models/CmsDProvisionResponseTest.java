/*
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 *
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 *
 * Please refer to the file LICENSE.TXT for full details.
 *
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 */

package com.mastercard.mcbp.remotemanagement.mdes.models;

import com.mastercard.mcbp.remotemanagement.mdes.profile.DigitizedCardProfileMdes;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import flexjson.JSONException;

/**
 * Unit test class for {@link com.mastercard.mcbp.remotemanagement.mdes.models
 * .CmsDProvisionResponse}
 */
public class CmsDProvisionResponseTest {
    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDRequestSessionTest.class.getClassLoader()
                                                                   .getResourceAsStream(
                                                                           "file/CmsDProvisionResponseTest.properties");
        properties.load(resourceAsStream);
    }

    @Test
    public void testDeserializeCmsDProvisionResponse() {

        String inputData = getDataFromPropertiesFile("input_data");
        CmsDProvisionResponse cmsDProvisionResponse = CmsDProvisionResponse
                .valueOf(ByteArray.of(inputData.getBytes()));

        Assert.assertEquals(properties.getProperty("iccKek"), cmsDProvisionResponse.getIccKek());
        Assert.assertEquals(properties.getProperty("responseId"),
                            cmsDProvisionResponse.getResponseId());
        Assert.assertEquals(properties.getProperty("responseHost"),
                            cmsDProvisionResponse.getResponseHost());
    }

    @Test
    public void testDeserializeCmsDProvisionResponseWithError() {

        String inputData = getDataFromPropertiesFile("input_data_error_response");
        CmsDProvisionResponse cmsDProvisionResponse = CmsDProvisionResponse
                .valueOf(ByteArray.of(inputData.getBytes()));

        Assert.assertEquals(properties.getProperty("responseId"),
                            cmsDProvisionResponse.getResponseId());
        Assert.assertEquals(properties.getProperty("responseHost"),
                            cmsDProvisionResponse.getResponseHost());
        Assert.assertEquals(properties.getProperty("errorCode"),
                            cmsDProvisionResponse.getErrorCode());
        Assert.assertEquals(properties.getProperty("errorDescription"),
                            cmsDProvisionResponse.getErrorDescription());
    }

    @Test
    public void testSerializeCmsDProvisionResponse() throws JSONException {

        String expectedData = getDataFromPropertiesFile("input_data");
        CmsDProvisionResponse cmsDProvisionResponse = new CmsDProvisionResponse();

        cmsDProvisionResponse.setIccKek(properties.getProperty("iccKek"));
        cmsDProvisionResponse.setResponseId(properties.getProperty("responseId"));
        cmsDProvisionResponse.setResponseHost(properties.getProperty("responseHost"));
        cmsDProvisionResponse.setCardProfile(getCardProfile());

        String actualData = cmsDProvisionResponse.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    @Test
    public void testSerializeCmsDProvisionResponseWithError() throws JSONException {

        String expectedData = getDataFromPropertiesFile("input_data_error_response");
        CmsDProvisionResponse cmsDProvisionResponse = new CmsDProvisionResponse();

        cmsDProvisionResponse.setResponseId(properties.getProperty("responseId"));
        cmsDProvisionResponse.setResponseHost(properties.getProperty("responseHost"));
        cmsDProvisionResponse.setErrorCode(properties.getProperty("errorCode"));
        cmsDProvisionResponse.setErrorDescription(properties.getProperty("errorDescription"));

        String actualData = cmsDProvisionResponse.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    /**
     * Return cmsD provision response string.
     *
     * @param key Key
     * @return json string
     */
    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }


    private DigitizedCardProfileMdes getCardProfile() {
        String cardProfile = properties.getProperty("cardProfile");
        return DigitizedCardProfileMdes.valueOf(cardProfile);
    }
}
