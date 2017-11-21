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

import com.mastercard.mobile_api.utils.exceptions.http.HttpException;

/**
 * Interface to implement HTTP client.
 */
public interface HttpFactory {
    /**
     * Default retry count
     */
    int DEFAULT_RETRY_COUNT = 3;

    /**
     * Get the Http Post Request
     *
     * @param uri Url to connect cms
     * @return Instance of HttpPostRequest
     */
    HttpPostRequest getHttpPostRequest(String uri);

    /**
     * Executes the HTTP post request provided.
     *
     * @param httpPostRequest Instance of HttpPostRequest
     * @return Instance of HttpResponse
     */
    HttpResponse execute(HttpPostRequest httpPostRequest) throws HttpException;

    /**
     * Set the host name
     *
     * @param hostName host name
     */
    void setHostname(String hostName);

    /**
     * Set the certificate as byte array
     *
     * @param certificateBytes byte array of certificate
     */
    void setCertificateBytes(byte[] certificateBytes);

    /**
     * Get the Http Get Request
     *
     * @param uri Url to connect.
     * @return Instance of HttpGetRequest
     */
    HttpGetRequest getHttpGetRequest(String uri);
    /**
     * Executes the HTTP Get request provided.
     *
     * @param httpGetRequest Instance of HttpGetRequest
     * @return Instance of HttpResponse
     */
    HttpResponse execute(HttpGetRequest httpGetRequest) throws HttpException;
}
