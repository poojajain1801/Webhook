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

import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by e050052 on 4/19/2015.
 * FIXME: Please remove this, if not needed
 */
public class SampleTest {
    @Before
    public void setup()throws Exception{

    }
    @Test
    public void testCounter()throws Exception{
        int counter = 32;
        //ByteArray result = CmsDRemoteManagementServiceImpl.convertIntToThreeBytes(counter);
        //System.out.println(result.toHexString());
        //

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd'T'hh:mm:ss.sssZ");
        try {
            Date parse = simpleDateFormat.parse("2015-04-28T15:33:00.000+0530");
            if (new Date().compareTo(parse) <= 0) {
                System.out.println("return true");
            } else {
                System.out.println("return false");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

}
