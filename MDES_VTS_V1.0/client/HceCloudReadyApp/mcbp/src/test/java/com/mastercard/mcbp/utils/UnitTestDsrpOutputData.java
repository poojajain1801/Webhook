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

package com.mastercard.mcbp.utils;

import com.mastercard.mcbp.card.mobilekernel.CryptogramType;
import com.mastercard.mcbp.card.mobilekernel.DsrpOutputData;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

public class UnitTestDsrpOutputData {
    private static Properties properties = new Properties();

    @Before
    public void setUp() throws Exception {
        final InputStream resourceAsStream =
                UnitTestDsrpOutputData
                        .class
                        .getClassLoader()
                        .getResourceAsStream("file/UnitTestDsrpOutputData.properties");
        properties.load(resourceAsStream);
    }

    @Test
    public void testToJsonStr() throws Exception {
        final String expectedJson = (String) properties.get("input_data");
        final DsrpOutputData sampleDsrpOutPutData = createSampleDsrpOutPutData();
        final String actualJson = sampleDsrpOutPutData.toJsonString();

        Assert.assertEquals(expectedJson, actualJson);
    }

    private DsrpOutputData createSampleDsrpOutPutData() {
        return new DsrpOutputData("1234567890123456",
                                  1,
                                  new Date(2015, 12, 31),
                                  ByteArray.of(new byte[]{1, 2, 3, 4, 5, 6, 7, 8}),
                                  ByteArray.of(new byte[]{1, 2, 3, 4, 5, 6, 7, 8}),
                                  0,
                                  999,
                                  250,
                                  1,
                                  1234567,
                                  CryptogramType.DE55,
                                  "1234567890123456"
        );
    }

}
