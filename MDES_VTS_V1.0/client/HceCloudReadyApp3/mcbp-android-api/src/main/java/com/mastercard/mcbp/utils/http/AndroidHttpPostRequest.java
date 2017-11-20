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

package com.mastercard.mcbp.utils.http;

import com.mastercard.mcbp.utils.BuildInfo;

class AndroidHttpPostRequest implements HttpPostRequest {

    /**
     * URL to connect.
     */
    private String mUrl;

    /**
     * Request data to send.
     */
    private String mData;

    /**
     * Request Property
     */
    private String mRequestProperty;

    @Override
    public HttpPostRequest withUrl(String url) {
        this.mUrl = url;
        return this;
    }

    @Override
    public HttpPostRequest withRequestData(String content) {
        this.mData = content;
        return this;
    }

    @Override
    public HttpPostRequest withRequestProperty(final String requestProperty) {
        this.mRequestProperty = requestProperty;
        return this;
    }

    @Override
    public String getRequestProperty() {
        return mRequestProperty;
    }

    @Override
    public String getUrl() {
        return this.mUrl;
    }

    @Override
    public String getRequestData() {
        return this.mData;
    }

    /**
     * Returns a string representation of the object.
     *
     * @return Returns debug information for the class in debug mode.
     * In release mode it returns only the class name, so that sensitive information is never
     * returned by this method.
     */
    @Override
    public String toString() {
        if (BuildInfo.isDebugEnabled()) {
            return "AndroidHttpPostRequest{" +
                   "uri='" + mUrl + '\'' +
                   ", data=" + (mData == null ? "Null" : mData) +
                   '}';
        } else {
            return "AndroidHttpPostRequest";
        }
    }
}
