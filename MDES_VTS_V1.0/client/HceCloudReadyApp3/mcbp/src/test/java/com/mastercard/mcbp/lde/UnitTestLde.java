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

package com.mastercard.mcbp.lde;

import com.mastercard.mcbp.utils.lde.Utils;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnitTestLde {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void testWith15DigitPanNumber() throws Exception {
        String ACTUAL_PAN = "548098150010000FFFFF01150305163347";
        String EXPECTED_VALUE = "0000";
        String lastPanDigits = Utils.getLastFourDigitOfPAN(ACTUAL_PAN);
        Assert.assertEquals(EXPECTED_VALUE, lastPanDigits);
    }

    @Test
    public void testWith16DigitPanNumber() throws Exception {
        String ACTUAL_PAN = "5480981500100002FFFF01150305163347";
        String EXPECTED_VALUE = "0002";
        String lastPanDigits = Utils.getLastFourDigitOfPAN(ACTUAL_PAN);
        Assert.assertEquals(EXPECTED_VALUE, lastPanDigits);
    }

    @Test
    public void testWith17DigitPanNumber() throws Exception {
        String ACTUAL_PAN = "54809815001000023FFF01150305163347";
        String EXPECTED_VALUE = "0023";
        String lastPanDigits = Utils.getLastFourDigitOfPAN(ACTUAL_PAN);
        Assert.assertEquals(EXPECTED_VALUE, lastPanDigits);
    }

    @Test
    public void testWith18DigitPanNumber() throws Exception {
        String ACTUAL_PAN = "548098150010000225FF01150305163347";
        String EXPECTED_VALUE = "0225";
        String lastPanDigits = Utils.getLastFourDigitOfPAN(ACTUAL_PAN);
        Assert.assertEquals(EXPECTED_VALUE, lastPanDigits);
    }

    @Test
    public void testWith19DigitPanNumber() throws Exception {
        String ACTUAL_PAN = "5480981500100002599F01150305163347";
        String EXPECTED_VALUE = "2599";
        String lastPanDigits = Utils.getLastFourDigitOfPAN(ACTUAL_PAN);
        Assert.assertEquals(EXPECTED_VALUE, lastPanDigits);
    }

}
