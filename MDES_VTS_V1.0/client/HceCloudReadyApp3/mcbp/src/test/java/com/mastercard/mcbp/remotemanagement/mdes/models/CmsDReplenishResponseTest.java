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

import com.mastercard.mcbp.remotemanagement.mdes.credentials.TransactionCredential;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Simple test class for cms-d replenish response.
 */
public class CmsDReplenishResponseTest {

    private static final Properties properties = new Properties();

    @BeforeClass
    public static void setUp() throws IOException {
        InputStream resourceAsStream = CmsDReplenishResponseTest.class.getClassLoader()
                                                                      .getResourceAsStream(
                                                                              "./file/CmsDReplenishResponseTest.properties");
        properties.load(resourceAsStream);
    }

    @Test
    public void testDeserializationOfCmsDReplenishResponse() throws Exception {
        String expectedReplenishResponse = properties.getProperty("expected_replenish_response");

        CmsDReplenishResponse cmsDReplenishResponse = CmsDReplenishResponse.valueOf(
                ByteArray.of(expectedReplenishResponse.getBytes()));

        Assert.assertEquals(cmsDReplenishResponse.getResponseId(),
                            properties.getProperty("responseId"));

        Assert.assertEquals(cmsDReplenishResponse.getResponseHost(),
                            properties.getProperty("responseHost"));
    }

    @Test
    public void testSerializationOfCmsDReplenishResponse() throws Exception {
        String expectedReplenishResponse = properties.getProperty("expected_replenish_response");

        CmsDReplenishResponse cmsDReplenishResponse = new CmsDReplenishResponse();
        cmsDReplenishResponse.setTransactionCredentials(createDummyCredentialStatusArray());
        cmsDReplenishResponse.setResponseId(properties.getProperty("responseId"));
        cmsDReplenishResponse.setResponseHost(properties.getProperty("responseHost"));

        String actualData = cmsDReplenishResponse.toJsonString();

        Assert.assertEquals(expectedReplenishResponse, actualData);
    }

    private TransactionCredential[] createDummyCredentialStatusArray() {
        TransactionCredential[] result = new TransactionCredential[3];
        result[0] = new TransactionCredential();
        result[0].atc = 5;
        result[0].contactlessMdSessionKey = ByteArray.of("4E4F54205245414C204E4643204D4420534B");
        result[0].contactlessUmdSingleUseKey =
                ByteArray.of("4E4F54205245414C204E464320554D442053554B");
        result[0].dsrpMdSessionKey = ByteArray.of("4E4F54205245414C2044535250204D4420534B");
        result[0].dsrpUmdSingleUseKey = ByteArray.of("4E4F54205245414C204453525020554D442053554B");
        result[0].idn = ByteArray.of("4E4F54205245414C2049444E");

        result[1] = new TransactionCredential();
        result[1].atc = 6;
        result[1].contactlessMdSessionKey = ByteArray.of("4E4F54205245414C204E4643204D4420534B");
        result[1].contactlessUmdSingleUseKey =
                ByteArray.of("4E4F54205245414C204E464320554D442053554B");
        result[1].dsrpMdSessionKey = ByteArray.of("4E4F54205245414C2044535250204D4420534B");
        result[1].dsrpUmdSingleUseKey = ByteArray.of("4E4F54205245414C204453525020554D442053554B");
        result[1].idn = ByteArray.of("4E4F54205245414C2049444E");

        result[2] = new TransactionCredential();
        result[2].atc = 7;
        result[2].contactlessMdSessionKey = ByteArray.of("4E4F54205245414C204E4643204D4420534B");
        result[2].contactlessUmdSingleUseKey =
                ByteArray.of("4E4F54205245414C204E464320554D442053554B");
        result[2].dsrpMdSessionKey = ByteArray.of("4E4F54205245414C2044535250204D4420534B");
        result[2].dsrpUmdSingleUseKey = ByteArray.of("4E4F54205245414C204453525020554D442053554B");
        result[2].idn = ByteArray.of("4E4F54205245414C2049444E");

        return result;
    }

}
