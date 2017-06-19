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
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import flexjson.JSONException;

/**
 * Unit test class for {@link GetTaskStatusRequest}.
 */
public class GetTaskStatusRequestTest {
    private final Properties mProperties = new Properties();


    @Before
    public void setUp() throws IOException {
        InputStream resourceAsStream = GetTaskStatusRequestTest.class
                .getClassLoader().getResourceAsStream("file/GetTaskStatusRequestTest.properties");
        mProperties.load(resourceAsStream);
    }


    @Test
    public void testDeserializeGetTaskStatusRequest() {
        GetTaskStatusRequest getTaskStatusRequest = GetTaskStatusRequest
                .valueOf(mProperties.getProperty("input_data"));

        Assert.assertEquals(getTaskStatusRequest.getRequestId(),
                            mProperties.getProperty("requestId"));
        Assert.assertEquals(getTaskStatusRequest.getTaskId(), mProperties.getProperty("taskId"));
    }


    @Test
    public void testSerializeGetTaskStatusRequest() throws JSONException {
        String expectedData = mProperties.getProperty("input_data");

        GetTaskStatusRequest getTaskStatusRequest = new GetTaskStatusRequest();
        getTaskStatusRequest.setRequestId(mProperties.getProperty("requestId"));
        getTaskStatusRequest.setTaskId(mProperties.getProperty("taskId"));

        String actualData = getTaskStatusRequest.toJsonString();

        Assert.assertEquals(expectedData, actualData);
    }
}
