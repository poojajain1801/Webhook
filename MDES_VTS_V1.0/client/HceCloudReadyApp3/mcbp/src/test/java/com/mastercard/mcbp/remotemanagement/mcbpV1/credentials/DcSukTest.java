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

package com.mastercard.mcbp.remotemanagement.mcbpV1.credentials;

import com.mastercard.mcbp.card.credentials.SingleUseKey;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.nio.charset.Charset;

import static org.junit.Assert.assertEquals;

/**
 * DcSuk to SingleUseKey class conversion
 */
public class DcSukTest {
    public final static String INPUT =
            "{\"DC_SUK_ID\":\"5413339000001513FFFF001501220000070001150202\"," +
            "\"DC_SUK_CONTENT\":{\"RFU\":\"00\",\"SUKInfo\":\"38\",\"ATC\":\"0001\"," +
            "\"IDN\":\"DA98438667C8FDC2\"," +
            "\"SK_CL_MD\":\"225BC8E86ED81A00F9CF9C74A6653BD5\"," +
            "\"SK_RP_MD\":\"E8E486F384C8F1F8D5ED020E035391D8\"," +
            "\"SUK_CL_UMD\":\"FC24AF40DA0D2E8F5A83D7933CF521B6\"," +
            "\"SUK_RP_UMD\":\"4F982FBF2186B4A7F82C45C25C0E216D\"," +
            "\"hash\":\"81A1D404354789F4A0710341AA884F560916BB2E376802A4292D4D54FEC81582\"}}";

    public final static String expectedOutput = "{\"suk\":{\"content\":{\"atc\":\"0001\"," +
                                                "\"hash\":\"81A1D404354789F4A0710341AA884F560916BB2E376802A4292D4D54FEC81582\"," +
                                                "\"idn\":\"DA98438667C8FDC2\",\"info\":\"38\"," +
                                                "\"contactlessMdSessionKey\":\"225BC8E86ED81A00F9CF9C74A6653BD5\"," +
                                                "\"dsrpMdSessionKey\":\"E8E486F384C8F1F8D5ED020E035391D8\"," +
                                                "\"contactlessUmdSingleUseKey\":\"FC24AF40DA0D2E8F5A83D7933CF521B6\"," +
                                                "\"dsrpUmdSingleUseKey\":\"4F982FBF2186B4A7F82C45C25C0E216D\"}," +
                                                "\"digitizedCardId\":\"5413339000001513FFFF00150122000007\"," +
                                                "\"id\":\"5413339000001513FFFF001501220000070001150202\"}}";

    private SingleUseKeyMcbpV1 singleUseKeyMcbpV1;

    @Before
    public void setUp() throws Exception {
        singleUseKeyMcbpV1 = SingleUseKeyMcbpV1.valueOf(INPUT.getBytes(Charset.defaultCharset()));
    }

    @Test
    public void testGetSingleUseKeyId() throws Exception {
        assertEquals("5413339000001513FFFF00150122000007", singleUseKeyMcbpV1.getDcId());
    }

    @Test
    public void testToSingleUseKey() throws Exception {
        SingleUseKey singleUseKey = singleUseKeyMcbpV1.toSingleUseKey();
        String singleUseKeyJson = JsonUtils.serializeObjectWithByteArray(singleUseKey, "suk");

        //      Flexible to compare different orders of JSON fields
        Assert.assertEquals(expectedOutput, singleUseKeyJson);
    }
}