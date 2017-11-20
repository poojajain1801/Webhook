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

package com.mastercard.mcbp.remotemanagement.mdes.profile;

import com.mastercard.mcbp.remotemanagement.mdes.models.CmsDProvisionResponse;
import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.util.Properties;

/**
 * Simple test class for file profile wrapper
 */
public class DigitizedCardProfileMdesTest {
    private String expectedCardProfile;
    private CmsDProvisionResponse cmsDProvisionResponse;

    @Before
    public void setUp() throws Exception {

        Properties properties = new Properties();
        InputStream inputStream = this.getClass().getClassLoader()
                                      .getResourceAsStream(
                                              "./file/DigitizedCardProfileMdesTest.properties");
        properties.load(inputStream);

        String inputCardProfile = properties.getProperty("input_card_profile_mdes");

        expectedCardProfile = properties.getProperty("expected_card_profile_mdes");

        cmsDProvisionResponse =
                CmsDProvisionResponse.valueOf(ByteArray.of(inputCardProfile.getBytes()));
    }

    @Test
    public void testToDigitizedCardProfile() throws Exception {
        String actual = cmsDProvisionResponse.toJsonString();

        Assert.assertEquals(expectedCardProfile, actual);
    }
}