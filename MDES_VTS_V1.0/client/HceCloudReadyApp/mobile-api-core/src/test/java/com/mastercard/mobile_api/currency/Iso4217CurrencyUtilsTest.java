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

package com.mastercard.mobile_api.currency;

import com.mastercard.mobile_api.bytes.ByteArray;

import org.junit.Test;

import java.util.Currency;

import static org.junit.Assert.*;

/**
 * ISO 4217 Test for utility functions
 */
public class Iso4217CurrencyUtilsTest {

    @Test
    public void testConvertBcdAmountToDouble() throws Exception {
        final String amount = "235499";
        final Currency currency = Currency.getInstance("EUR");

        final double expectedAmount = 2354.99;

        assertEquals(expectedAmount,
                     Iso4217CurrencyUtils.convertBcdAmountToDouble(ByteArray.of(amount).getBytes(),
                                                                   currency),
                     0.1);
    }

    @Test
    public void testConvertBinaryAmountToDouble() throws Exception {
        final String amount = "FFFF";  // This should be 65536
        final Currency currency = Currency.getInstance("EUR");

        final double expectedAmount = 655.36;

        assertEquals(expectedAmount,
                     Iso4217CurrencyUtils.convertBinaryAmountToDouble(
                             ByteArray.of(amount).getBytes(), currency),
                     0.1);
    }

}