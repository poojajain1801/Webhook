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

public class CmsDChangeMobilePinRequestTest {

    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDRequestSessionTest.class.getClassLoader()
                                                                   .getResourceAsStream(
                                                                           "file/CmsDChangeMobilePinRequestTest.properties");
        properties.load(resourceAsStream);
    }


    @Test
    public void testDeserializeCmsDChangeMobilePinRequest() throws Exception {
        String inputData = getDataFromPropertiesFile("input_data");
        CmsDChangeMobilePinRequest cmsDChangeMobilePinRequest = CmsDChangeMobilePinRequest
                .valueOf(inputData);

        Assert.assertEquals(properties.getProperty("requestId"), cmsDChangeMobilePinRequest
                .getRequestId());
        Assert.assertEquals(properties.getProperty("tokenUniqueReference"),
                            cmsDChangeMobilePinRequest.getTokenUniqueReference());
        Assert.assertEquals(properties.getProperty("currentMobilePin"),
                            cmsDChangeMobilePinRequest.getCurrentMobilePin().toString());
        Assert.assertEquals(properties.getProperty("newMobilePin"),
                            cmsDChangeMobilePinRequest.getNewMobilePin().toString());
        Assert.assertEquals(properties.getProperty("taskId"),
                            cmsDChangeMobilePinRequest.getTaskId());
    }

    @Test
    public void testSerializeCmsDChangeMobilePinRequest() throws Exception {
        String expectedData = getDataFromPropertiesFile("input_data");

        CmsDChangeMobilePinRequest cmsDChangeMobilePinRequest = new CmsDChangeMobilePinRequest();
        cmsDChangeMobilePinRequest.setRequestId(properties.getProperty("requestId"));
        cmsDChangeMobilePinRequest.setTokenUniqueReference(properties.getProperty
                ("tokenUniqueReference"));
        cmsDChangeMobilePinRequest.setCurrentMobilePin(ByteArray.of(properties.getProperty
                ("currentMobilePin")));
        cmsDChangeMobilePinRequest.setNewMobilePin(ByteArray.of(properties.getProperty
                ("newMobilePin")));
        cmsDChangeMobilePinRequest.setTaskId(properties.getProperty("taskId"));

        String actualData = cmsDChangeMobilePinRequest.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    @Test
    public void testDeserializeCmsDChangeMobilePinRequestWithNullCurrentPin() {
        String inputData = getDataFromPropertiesFile("input_data_with_null_current_pin");
        CmsDChangeMobilePinRequest actualData = CmsDChangeMobilePinRequest.valueOf(inputData);
        ByteArray actualCurrentMobilePin = actualData.getCurrentMobilePin();
        Assert.assertEquals(null, actualCurrentMobilePin);
    }

    /**
     * Return cmsD change mobile pin string.
     *
     * @param key Key
     * @return json string
     */
    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }
}


