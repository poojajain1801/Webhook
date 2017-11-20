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

public class CmsDRequestSessionTest {

    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDRequestSessionTest.class.getClassLoader()
                                                                   .getResourceAsStream(
                                                                           "file/CmsDRequestSessionTest.properties");
        properties.load(resourceAsStream);
    }

    /**
     * Return expected cmsD request session string.
     *
     * @return json string
     */
    private static String getExpectedCmsDRequestSession() {
        return ((String) properties.get("input_data"));
    }

    @Test
    public void testDeserializeCmsDRequestSessionRequest() throws Exception {
        String inputData = getExpectedCmsDRequestSession();
        CmsDRequestSession cmsDRequestSession = CmsDRequestSession.valueOf(inputData);
        Assert.assertEquals(properties.getProperty("paymentAppInstanceId"),
                            cmsDRequestSession.getPaymentAppInstanceId());
        Assert.assertEquals(properties.getProperty("paymentAppProviderId"),
                            cmsDRequestSession.getPaymentAppProviderId());
        Assert.assertEquals(properties.getProperty("mobileKeySetId"),
                            new String(cmsDRequestSession.getMobileKeysetId().getBytes()));
    }

    @Test
    public void testSerializeCmsDRequestSessionRequest() throws Exception {
        String inputData = getExpectedCmsDRequestSession();

        CmsDRequestSession cmsDRequestSession = new CmsDRequestSession();
        cmsDRequestSession.setPaymentAppProviderId(properties.getProperty("paymentAppProviderId"));
        cmsDRequestSession.setPaymentAppInstanceId(properties.getProperty("paymentAppInstanceId"));
        cmsDRequestSession.setMobileKeysetId(properties.getProperty("mobileKeySetId"));

        String actualData = cmsDRequestSession.toJsonString();
        Assert.assertEquals(inputData, actualData);
    }
}
