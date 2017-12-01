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

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Unit test class for {@link com.mastercard.mcbp.remotemanagement
 * .mdes.models.CmsDNotifyProvisioningRequest
 */
public class CmsDNotifyProvisioningRequestTest {
    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDRequestSessionTest.class.getClassLoader()
                                                                   .getResourceAsStream(
                                                                           "file/CmsDNotifyProvisioningRequestTest.properties");
        properties.load(resourceAsStream);
    }


    @Test
    public void testDeserializeCmsDNotifyProvisioningRequest() {

        String inputData = getDataFromPropertiesFile("input_data");

        CmsDNotifyProvisioningRequest cmsDNotifyProvisioningRequest =
                CmsDNotifyProvisioningRequest.valueOf(inputData);

        Assert.assertEquals(properties.getProperty("requestId"),
                            cmsDNotifyProvisioningRequest.getRequestId());
        Assert.assertEquals(properties.getProperty("tokenUniqueReference"),
                            cmsDNotifyProvisioningRequest.getTokenUniqueReference());
        Assert.assertEquals(properties.getProperty("resultSuccess"),
                            cmsDNotifyProvisioningRequest.getResult());
    }

    @Test
    public void testDeserializeCmsDNotifyProvisioningRequestWithError() {

        String inputData = getDataFromPropertiesFile("input_data_with_error");

        CmsDNotifyProvisioningRequest cmsDNotifyProvisioningRequest =
                CmsDNotifyProvisioningRequest.valueOf(inputData);

        Assert.assertEquals(properties.getProperty("requestId"),
                            cmsDNotifyProvisioningRequest.getRequestId());
        Assert.assertEquals(properties.getProperty("resultError"),
                            cmsDNotifyProvisioningRequest.getResult());
        Assert.assertEquals(properties.getProperty("errorCode"),
                            cmsDNotifyProvisioningRequest.getErrorCode());
        Assert.assertEquals(properties.getProperty("errorDescription"),
                            cmsDNotifyProvisioningRequest.getErrorDescription());
    }

    @Test
    public void testSerializeCmsDNotifyProvisioningRequest() throws Exception {
        String expectedData = getDataFromPropertiesFile("input_data");

        CmsDNotifyProvisioningRequest cmsDNotifyProvisioningRequest = new
                CmsDNotifyProvisioningRequest();
        cmsDNotifyProvisioningRequest.setRequestId(properties.getProperty("requestId"));
        cmsDNotifyProvisioningRequest.setTokenUniqueReference(properties.getProperty
                ("tokenUniqueReference"));
        cmsDNotifyProvisioningRequest.setResult(properties.getProperty("resultSuccess"));

        String actualData = cmsDNotifyProvisioningRequest.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    @Test
    public void testSerializeCmsDNotifyProvisioningRequestWithError() throws Exception {
        String expectedData = getDataFromPropertiesFile("input_data_with_error");

        CmsDNotifyProvisioningRequest cmsDNotifyProvisioningRequest = new
                CmsDNotifyProvisioningRequest();
        cmsDNotifyProvisioningRequest.setRequestId(properties.getProperty("requestId"));
        cmsDNotifyProvisioningRequest.setResult(properties.getProperty("resultError"));
        cmsDNotifyProvisioningRequest.setErrorCode(properties.getProperty("errorCode"));
        cmsDNotifyProvisioningRequest.setErrorDescription(properties.getProperty
                ("errorDescription"));

        String actualData = cmsDNotifyProvisioningRequest.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    /**
     * Return cmsD notify provisioning request string.
     *
     * @param key Key
     * @return json string
     */
    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }
}
