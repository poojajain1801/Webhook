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
package com.mastercard.mobile_api.utils;

import java.util.Calendar;

import flexjson.JSON;

/**
 * Utility class to manage Date
 */
public class Date {

    private int mYear;

    private int mDay;

    private int mMonth;

    public Date(final int year, final int month, final int day) {
        super();
        this.mYear = year;
        this.mMonth = month;
        this.mDay = day;
    }

    /**
     * Create a Date object based on the actual time
     */
    public Date() {
        // Get
        Calendar currentDate = Calendar.getInstance(); //Get the current date
        mYear = currentDate.get(Calendar.YEAR);
        mMonth = currentDate.get(Calendar.MONTH) + 1;
        mDay = currentDate.get(Calendar.DAY_OF_MONTH);

    }

    @Override
    public String toString() {
        return "Date{" + "Year=" + mYear + ", Day=" + mDay + ", Month=" + mMonth + '}';
    }

    public int getYear() {
        return mYear;
    }

    public int getDay() {
        return mDay;
    }

    public int getMonth() {
        return mMonth;
    }

    @JSON(include = false)
    public boolean isValid() {
        final Calendar calendar = Calendar.getInstance();
        calendar.clear();

        // As per specs a zero date is acceptable when the Merchant provides none
        if (mYear == 0 && mMonth == 0 && mDay == 0) {
            return true;
        }

        if (getYear() > 2000) {
            calendar.set(Calendar.YEAR, getYear());
            if (mMonth >= 1 && mMonth <= 12) {
                calendar.set(Calendar.MONTH, mMonth - 1);  // Months are numbered 0 to 11 (Calendar)
                int actualMaximum = calendar.getActualMaximum(Calendar.DAY_OF_MONTH);
                int actualMinimum = calendar.getActualMinimum(Calendar.DAY_OF_MONTH);
                if (actualMinimum <= mDay && mDay <= actualMaximum) {
                    return true;
                }
            }
        }
        return false;
    }

}
