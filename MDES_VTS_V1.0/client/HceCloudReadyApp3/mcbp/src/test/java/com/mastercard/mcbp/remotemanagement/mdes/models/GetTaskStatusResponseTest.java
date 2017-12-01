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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import flexjson.JSONException;

/**
 * Unit test class for {@link GetTaskStatusResponse}.
 */
public class GetTaskStatusResponseTest {
    private final Properties mProperties = new Properties();

    @Before
    public void setUp() throws IOException {
        InputStream resourceAsStream = GetTaskStatusRequestTest.class
                .getClassLoader().getResourceAsStream("file/GetTaskStatusResponseTest.properties");
        mProperties.load(resourceAsStream);
    }


    @Test
    public void testDeserializeGetTaskStatusResponse() {
        GetTaskStatusResponse getTaskStatusResponse = GetTaskStatusResponse.valueOf(
                ByteArray.of(mProperties.getProperty("input_data").getBytes()));

        Assert.assertEquals(getTaskStatusResponse.getResponseId(),
                            mProperties.getProperty("responseId"));
        Assert.assertEquals(getTaskStatusResponse.getStatus(), mProperties.getProperty("status"));
    }


    @Test
    public void testSerializeGetTaskStatusResponse() throws JSONException {
        String expectedData = mProperties.getProperty("input_data");

        GetTaskStatusResponse getTaskStatusResponse = new GetTaskStatusResponse();
        getTaskStatusResponse.setResponseId(mProperties.getProperty("responseId"));
        getTaskStatusResponse.setStatus(mProperties.getProperty("status"));

        String actualData = getTaskStatusResponse.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }
}