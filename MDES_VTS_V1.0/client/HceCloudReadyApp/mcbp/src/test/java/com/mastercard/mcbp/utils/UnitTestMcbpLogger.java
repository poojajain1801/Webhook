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

import com.mastercard.mcbp.utils.logs.McbpLogger;

public class UnitTestMcbpLogger implements McbpLogger {

    final String mTag;

    public UnitTestMcbpLogger(final Object obj) {
        if (obj == null) {
            this.mTag = "DefaultLog";
        } else {
            this.mTag = obj.getClass().getName();
        }
    }

    @Override
    public void i(final String message) {
        // System.out.println(tag + ": " + message);
    }

    @Override
    public void e(final String message) {
        // System.out.println(tag + ": " + message);
    }

    @Override
    public void d(final String message) {
        // System.out.println(tag + ": " + message);
    }

    @Override
    public boolean isEnabled() {
        return false;
    }
}
