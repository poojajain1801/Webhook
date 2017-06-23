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

import java.io.InputStream;
import java.util.Properties;


/**
 * Simple test class for cms-d replenish request.
 */
public class CmsDReplenishRequestTest {

    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws Exception {
        InputStream resourceAsStream = CmsDRegisterResponseTest.class.getClassLoader()
                                                                     .getResourceAsStream(
                                                                             "./file/CmsDReplenishRequestTest.properties");
        properties.load(resourceAsStream);
    }

    private static String getPropertyFormFile(String key) {
        return (properties.getProperty(key));
    }

    @Test
    public void testSerializeReplenishRequest() throws Exception {
        String expectedData = getPropertyFormFile("expected_replenish_request");

        CmsDReplenishRequest cmsDReplenishRequest = new CmsDReplenishRequest();
        cmsDReplenishRequest.setRequestId(properties.getProperty("requestId"));
        cmsDReplenishRequest.setTransactionCredentialsStatus(getTransactionCredentialStatus());
        cmsDReplenishRequest
                .setTokenUniqueReference(properties.getProperty("tokenUniqueReference"));

        String actualData = cmsDReplenishRequest.toJsonString();

        Assert.assertEquals(expectedData.toUpperCase(), actualData.toUpperCase());
    }


    @Test
    public void testDeserializeReplenishRequest() throws Exception {

        String expectedReplenishRequest = getPropertyFormFile("expected_replenish_request");

        CmsDReplenishRequest cmsDReplenishRequest = CmsDReplenishRequest.valueOf
                (expectedReplenishRequest);

        Assert.assertEquals(properties.getProperty("requestId"),
                            cmsDReplenishRequest.getRequestId());
        Assert.assertEquals(properties.getProperty("tokenUniqueReference"),
                            cmsDReplenishRequest.getTokenUniqueReference());
        Assert.assertEquals(properties.getProperty("atc1"),
                            String.valueOf(cmsDReplenishRequest.getTransactionCredentialsStatus()[0]
                                                   .getAtc()));
        Assert.assertEquals(properties.getProperty("status1"),
                            cmsDReplenishRequest.getTransactionCredentialsStatus()[0].getStatus());
        Assert.assertEquals(properties.getProperty("timestamp1"),
                            cmsDReplenishRequest.getTransactionCredentialsStatus()[0]
                                    .getTimestamp());
    }

    private TransactionCredentialStatus[] getTransactionCredentialStatus() {
        TransactionCredentialStatus[] transactionCredentialStatusArray =
                new TransactionCredentialStatus[1];

        TransactionCredentialStatus transactionCredentialStatus = new TransactionCredentialStatus();

        transactionCredentialStatus.setStatus(properties.getProperty("status1"));
        transactionCredentialStatus.setAtc(Integer.parseInt(properties.getProperty("atc1")));
        transactionCredentialStatus.setTimestamp(properties.getProperty("timestamp1"));

        transactionCredentialStatusArray[0] = transactionCredentialStatus;
        return transactionCredentialStatusArray;
    }
}
