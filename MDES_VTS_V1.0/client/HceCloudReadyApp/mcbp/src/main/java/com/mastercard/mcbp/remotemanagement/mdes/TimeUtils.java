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

package com.mastercard.mcbp.remotemanagement.mdes;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Utility class for session related APIs
 */
public class TimeUtils {

    private static final String DATE_TIME_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ";

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat(DATE_TIME_FORMAT);

    /**
     * Validate if current timestamp is before what is provided
     *
     * @param timeStamp Timestamp
     * @return true if given timestamp is less than or equals to current timestamp. False
     * otherwise
     */
    public static boolean isBefore(String timeStamp) {
        if (timeStamp == null || timeStamp.length() == 0) {
            return false;
        }
        try {
            Date parse = parseDate(timeStamp);
            Date currentDate = new Date(System.currentTimeMillis());
            int value = currentDate.compareTo(parse);
            if (value <= 0) {
                return true;
            }
        } catch (ParseException e) {
        }
        return false;
    }

    /**
     * formatted given {@link Date} object according to ISO 8601 Specification.
     *
     * @param date {@link Date} instance need to format
     * @return Formatted date as yyyy-MM-dd'T'HH:mm:ss.SSSZZZZZ.
     */
    public static String getFormattedDate(Date date) {
        if (date == null) {
            return null;
        }
        return DATE_FORMAT.format(date);
    }

    /**
     * Retrieve date from ISO 8601 formatted timestamp.
     *
     * @param timeStamp Time stamp to be convert in date object.
     * @return {@link Date}
     * @throws ParseException
     */
    public static Date parseDate(String timeStamp) throws ParseException {
        return DATE_FORMAT.parse(timeStamp);
    }


}
