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

/**
 * Implementation of {@link HttpGetRequest}
 */
public class AndroidHttpGetRequest implements HttpGetRequest {

    /**
     * URL to connect.
     */
    private String mUrl;

    /**
     * Request Property
     */
    private String mRequestProperty;

    /**
     * Set Http URL.
     *
     * @param uri URL
     */
    @Override
    public HttpGetRequest withUrl(final String uri) {
        this.mUrl = uri;
        return this;
    }

    @Override
    public HttpGetRequest withRequestProperty(final String requestProperty) {
        this.mRequestProperty = requestProperty;
        return this;
    }


    /**
     * Get URL
     *
     * @return URL String
     */
    @Override
    public String getUrl() {
        return mUrl;
    }

    @Override
    public String getRequestProperty() {
        return mRequestProperty;
    }

}
