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

public class CmsDDeleteRequestTest {

    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDRequestSessionTest.class.getClassLoader()
                                                                   .getResourceAsStream(
                                                                           "file/CmsDDeleteRequestTest.properties");
        properties.load(resourceAsStream);
    }

    @Test
    public void testDeserializeCmsDDeleteRequest() throws Exception {
        String inputData = getDataFromPropertiesFile("input_data");
        CmsDDeleteRequest cmsDDeleteRequest = CmsDDeleteRequest.valueOf(inputData);

        Assert.assertEquals(properties.getProperty("requestId"), cmsDDeleteRequest.getRequestId());
        Assert.assertEquals(properties.getProperty("tokenUniqueReference"),
                            cmsDDeleteRequest.getTokenUniqueReference());
        Assert.assertEquals(properties.getProperty("transactionCredentialsStatusLength"),
                            String.valueOf(
                                    cmsDDeleteRequest.getTransactionCredentialsStatus().length));

        Assert.assertEquals(properties.getProperty("atc1"),
                            String.valueOf(cmsDDeleteRequest.getTransactionCredentialsStatus()[0]
                                                   .getAtc()));
        Assert.assertEquals(properties.getProperty("status1"),
                            cmsDDeleteRequest.getTransactionCredentialsStatus()[0].getStatus());
        Assert.assertEquals(properties.getProperty("timestamp1"),
                            cmsDDeleteRequest.getTransactionCredentialsStatus()[0].getTimestamp());

    }

    @Test
    public void testSerializeCmsDDeleteRequest() throws Exception {
        String expectedData = getDataFromPropertiesFile("input_data");
        CmsDDeleteRequest cmsDDeleteRequest = new CmsDDeleteRequest();

        cmsDDeleteRequest.setRequestId(properties.getProperty("requestId"));
        cmsDDeleteRequest.setTokenUniqueReference(properties.getProperty("tokenUniqueReference"));
        cmsDDeleteRequest.setTransactionCredentialsStatus(generateDummyCredentialData());

        String actualData = cmsDDeleteRequest.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }

    private TransactionCredentialStatus[] generateDummyCredentialData() {
        TransactionCredentialStatus[] credentialStatus = new TransactionCredentialStatus[4];

        TransactionCredentialStatus transactionCredentialStatus1 =
                new TransactionCredentialStatus();
        transactionCredentialStatus1.setAtc(Integer.parseInt(properties.getProperty("atc1")));
        transactionCredentialStatus1.setStatus(properties.getProperty("status1"));
        transactionCredentialStatus1.setTimestamp(properties.getProperty("timestamp1"));

        TransactionCredentialStatus transactionCredentialStatus2 =
                new TransactionCredentialStatus();
        transactionCredentialStatus2.setAtc(Integer.parseInt(properties.getProperty("atc2")));
        transactionCredentialStatus2.setStatus(properties.getProperty("status2"));
        transactionCredentialStatus2.setTimestamp(properties.getProperty("timestamp2"));

        TransactionCredentialStatus transactionCredentialStatus3 =
                new TransactionCredentialStatus();
        transactionCredentialStatus3.setAtc(Integer.parseInt(properties.getProperty("atc3")));
        transactionCredentialStatus3.setStatus(properties.getProperty("status3"));
        transactionCredentialStatus3.setTimestamp(properties.getProperty("timestamp3"));

        TransactionCredentialStatus transactionCredentialStatus4 =
                new TransactionCredentialStatus();
        transactionCredentialStatus4.setAtc(Integer.parseInt(properties.getProperty("atc4")));
        transactionCredentialStatus4.setStatus(properties.getProperty("status4"));
        transactionCredentialStatus4.setTimestamp(properties.getProperty("timestamp4"));

        credentialStatus[0] = transactionCredentialStatus1;
        credentialStatus[1] = transactionCredentialStatus2;
        credentialStatus[2] = transactionCredentialStatus3;
        credentialStatus[3] = transactionCredentialStatus4;

        return credentialStatus;
    }


    /**
     * Return cmsD delete request string.
     *
     * @param key Key
     * @return json string
     */
    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }
}
