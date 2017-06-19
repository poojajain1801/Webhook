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

package com.mastercard.mcbp.remotemanagement.mdes.credentials;

import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.json.JsonUtils;

import org.junit.Before;
import org.junit.Test;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Properties;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;

public class TransactionCredentialTest {
    private TransactionCredential singleUseKeyMdes;
    private Properties properties = new Properties();

    @Before
    public void setUp() throws Exception {
        singleUseKeyMdes = null;
        ClassLoader classLoader = TransactionCredentialTest.class.getClassLoader();
        InputStream resourceAsStream =
                classLoader.getResourceAsStream("file/TransactionCredentialTest.properties");
        properties.clear();
        properties.load(resourceAsStream);
    }

    @Test
    public void testGetSingleUseKeyId() throws Exception {
        singleUseKeyMdes = TransactionCredential
                .valueOf(getDataFromPropertiesFile("singlesuk").getBytes(Charset.defaultCharset()));
        assertEquals(5, singleUseKeyMdes.atc);
    }

    @Test
    public void testsinglesukwithOutKey() throws Exception {
        singleUseKeyMdes = TransactionCredential
                .valueOf(getDataFromPropertiesFile("singlesukwithOutKey")
                                 .getBytes(Charset.defaultCharset()));
        assertEquals(5, singleUseKeyMdes.atc);
        assertEquals(null, singleUseKeyMdes.contactlessMdSessionKey);
    }

    @Test
    public void testsinglesukwithEmptyKey() throws Exception {
        singleUseKeyMdes = TransactionCredential
                .valueOf(getDataFromPropertiesFile("singlesukwithEmptyKey")
                                 .getBytes(Charset.defaultCharset()));
        assertArrayEquals(ByteArray.of("").getBytes(),
                          singleUseKeyMdes.contactlessMdSessionKey.getBytes());
    }

    @Test
    public void testGetSingleUseKeys() throws Exception {
        String[] strings = JsonUtils.deserializeStringArray(getDataFromPropertiesFile("sukarray"));
        for (final String string : strings) {
            TransactionCredential singleUseKeyMdes1 = TransactionCredential
                    .valueOf(string.getBytes(Charset.defaultCharset()));
        }
        assertEquals(2, strings.length);
    }

    private String getDataFromPropertiesFile(String key) {
        return (String) properties.get(key);
    }

}
