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

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Unit test class for {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterRequest}
 */
public class CmsDRegisterRequestTest {

    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDRequestSessionTest.class.getClassLoader()
                                                                   .getResourceAsStream(
                                                                           "file/CmsDRegisterRequestTest.properties");
        properties.load(resourceAsStream);
    }

    @Test
    public void testDeserializeCmsDRegisterRequest() {
        String inputData = getDataFromPropertiesFile("input_data");

        CmsDRegisterRequest cmsDRegisterRequest = CmsDRegisterRequest.valueOf(inputData);

        Assert.assertEquals(properties.getProperty("paymentAppProviderId"),
                            cmsDRegisterRequest.getPaymentAppProviderId());
        Assert.assertEquals(properties.getProperty("rgk"), cmsDRegisterRequest.getRgk().toString());
        Assert.assertEquals(properties.getProperty("paymentAppInstanceId"),
                            cmsDRegisterRequest.getPaymentAppInstanceId());
        Assert.assertEquals(properties.getProperty("deviceFingerprint").toUpperCase(),
                            cmsDRegisterRequest.getDeviceFingerprint().toString());
        Assert.assertEquals(properties.getProperty("registrationCode"),
                            cmsDRegisterRequest.getRegistrationCode());
    }

    /**
     * Return cmsD register request string.
     *
     * @param key The property key
     * @return json string
     */
    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }

    @Test
    public void testSerializeCmsDRegisterRequest() throws Exception {
        String expectedData = getDataFromPropertiesFile("input_data");

        CmsDRegisterRequest cmsDRegisterRequest = new CmsDRegisterRequest();
        cmsDRegisterRequest.setDeviceFingerprint(ByteArray.of(properties.getProperty
                ("deviceFingerprint")));
        cmsDRegisterRequest.setPaymentAppInstanceId((properties.getProperty
                ("paymentAppInstanceId")));
        cmsDRegisterRequest.setPaymentAppProviderId(properties.getProperty("paymentAppProviderId"));
        cmsDRegisterRequest.setRegistrationCode(properties.getProperty("registrationCode"));
        cmsDRegisterRequest.setRgk(ByteArray.of(properties.getProperty("rgk")));
        String actualData = cmsDRegisterRequest.toJsonString();

        Assert.assertEquals(expectedData.toUpperCase(), actualData.toUpperCase());
    }

}
