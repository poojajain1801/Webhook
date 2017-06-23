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

package com.mastercard.mcbp.utils.logs;

public interface McbpLogger {
    /**
     * Log in Info
     *
     * @param message input message
     *
     * @since 1.0.0
     */
    void i(final String message);

    /**
     * Log in Error
     *
     * @param message input message
     *
     * @since 1.0.0
     */
    void e(final String message);

    /**
     * Log in Debug
     *
     * @param message input message
     *
     * @since 1.0.0
     */
    void d(final String message);

    /**
     * Return true if the logging has been enabled.
     *
     * Typically indicates that the library has been built in Debug Mode.
     *
     * @since 1.0.4
     * */
    boolean isEnabled();
}
