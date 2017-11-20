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
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

import flexjson.JSONException;

/**
 * Unit test class for {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterRequest}
 */
public class CmsDRequestTest {
    private final Properties properties = new Properties();

    @Before
    public void setUp() throws Exception {
        ClassLoader classLoader = CmsDRequestTest.class.getClassLoader();
        InputStream resourceAsStream =
                classLoader.getResourceAsStream("file/CmsDRequestTest.properties");
        properties.clear();
        properties.load(resourceAsStream);
    }

    @Test
    public void testCmsDRequestDeserialization() {
        CmsDRequest actualData = CmsDRequest.valueOf(getDataFromPropertiesFile("expectedData"));
        String actualEncryptedData = actualData.getEncryptedData();
        Assert.assertEquals(getDataFromPropertiesFile("encryptedData"), actualEncryptedData);
    }

    @Test
    public void testCmsDRequestSerialization() throws JSONException {
        String expectedData = getDataFromPropertiesFile("expectedData");

        CmsDRequest cmsDRequest = new CmsDRequest();
        cmsDRequest.setMobileKeysetId(properties.getProperty("mobileKeysetId"));
        cmsDRequest.setAuthenticationCode(ByteArray.of(
                properties.getProperty("authenticationCode")));
        cmsDRequest.setEncryptedData(properties.getProperty("encryptedData"));

        String actualData = cmsDRequest.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    @Test
    public void testCmsDRequestWithNullEncryptedData() {
        CmsDRequest actualData =
                CmsDRequest.valueOf(getDataFromPropertiesFile("expectedDataWithNullEncData"));
        String actualEncryptedData = actualData.getEncryptedData();
        Assert.assertEquals(null, actualEncryptedData);
    }

    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }

}
