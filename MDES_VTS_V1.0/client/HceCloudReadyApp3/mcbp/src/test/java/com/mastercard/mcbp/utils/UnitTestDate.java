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

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class UnitTestDate {

    @Before
    public void setup() throws Exception {

    }

    @Test
    public void testDate() throws Exception {
        com.mastercard.mobile_api.utils.Date date = new com.mastercard.mobile_api.utils.Date(1920,
                                                                                             13,
                                                                                             30);
        Assert.assertEquals(false, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2000, 17, 30);
        Assert.assertEquals(false, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2000, 12, 30);
        Assert.assertEquals(false, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2001, 11, 31);
        Assert.assertEquals(false, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2004, 11, 30);
        Assert.assertEquals(true, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2010, 11, 33);
        Assert.assertEquals(false, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2010, 1, 28);
        Assert.assertEquals(true, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2004, 1, 29);
        Assert.assertEquals(true, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2004, 1, 30);
        Assert.assertEquals(true, date.isValid());

        date = new com.mastercard.mobile_api.utils.Date(2001, 12, 31);
        Assert.assertEquals(true, date.isValid());
    }
}
