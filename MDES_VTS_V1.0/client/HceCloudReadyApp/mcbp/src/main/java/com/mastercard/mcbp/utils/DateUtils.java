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

import com.mastercard.mcbp.utils.exceptions.McbpUncheckedException;
import com.mastercard.mobile_api.bytes.ByteArray;

import java.util.Calendar;

public class DateUtils {

    /**
     * Return Today Transaction Date
     *
     * @return byte array of today date.
     */
    public static ByteArray getTodayTransactionDate() {
        Calendar calendar = Calendar.getInstance();
        int year = calendar.get(Calendar.YEAR) - 2000;

        // Phones with Date not set will return year as 1971 - throw exception in this case
        if (year < 0) {
            throw new McbpUncheckedException("Unable to retrieve the current year");
        }

        int month = calendar.get(Calendar.MONTH) + 1;
        int dayOfMonth = calendar.get(Calendar.DAY_OF_MONTH);
        return ByteArray.of(
                padZero(String.valueOf(year)) + padZero(String.valueOf(month))
                        + padZero(String.valueOf(dayOfMonth)));
    }

    private static String padZero(String str) {
        if (str.length() == 1) {
            str = "0" + str;
            return str;
        }
        return str;
    }
}
