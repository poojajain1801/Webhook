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
 * Unit test class for {@link com.mastercard.mcbp.remotemanagement.mdes.models.CmsDRegisterResponse}
 */
public class CmsDRegisterResponseTest {
    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws Exception {
        InputStream resourceAsStream = CmsDRegisterResponseTest.class.getClassLoader()
                                                                     .getResourceAsStream(
                                                                             "./file/CmsDRegisterResponseTest.properties");
        properties.load(resourceAsStream);
    }

    private static String getPropertyFormFile(String key) {
        return (properties.getProperty(key));
    }

    @Test
    public void testDeserializationOfCmsDRegistrationResponse() {
        String sampleRegisterResponse = getPropertyFormFile("sampleRegisterResponse");
        CmsDRegisterResponse actualCmsDRegisterResponse =
                CmsDRegisterResponse.valueOf(ByteArray.of(sampleRegisterResponse.getBytes()));

        Assert.assertEquals(actualCmsDRegisterResponse.getMobileKeysetId(),
                            getPropertyFormFile("mobileKeysetId"));
        Assert.assertEquals(
                actualCmsDRegisterResponse.getMobileKeys().getTransportKey().toHexString(),
                getPropertyFormFile("transportKey"));
        Assert.assertEquals(actualCmsDRegisterResponse.getMobileKeys().getMacKey().toHexString(),
                            getPropertyFormFile("macKey"));
        Assert.assertEquals(
                actualCmsDRegisterResponse.getMobileKeys().getDataEncryptionKey().toHexString(),
                getPropertyFormFile("dataEncryptionKey"));
    }

    @Test
    public void testSerializationOfCmsDRegistrationResponse() throws IOException {
        String expectedData = getPropertyFormFile("sampleRegisterResponse");
        CmsDRegisterResponse actualCmsDRegisterResponse = new CmsDRegisterResponse();

        actualCmsDRegisterResponse.setMobileKeysetId(properties.getProperty("mobileKeysetId"));
        actualCmsDRegisterResponse
                .setRemoteManagementUrl(properties.getProperty("remoteManagementUrl"));
        actualCmsDRegisterResponse.setMobileKeys(getMobileKeys());

        String actualData = actualCmsDRegisterResponse.toJsonString();

        Assert.assertEquals(expectedData.toUpperCase(), actualData.toUpperCase());
    }

    private MobileKeys getMobileKeys() {
        MobileKeys mobileKeys = new MobileKeys();
        mobileKeys.setDataEncryptionKey(ByteArray.of(properties.getProperty("dataEncryptionKey")));
        mobileKeys.setMacKey(ByteArray.of(properties.getProperty("macKey")));
        mobileKeys.setTransportKey(ByteArray.of(properties.getProperty("transportKey")));
        return mobileKeys;
    }

}
