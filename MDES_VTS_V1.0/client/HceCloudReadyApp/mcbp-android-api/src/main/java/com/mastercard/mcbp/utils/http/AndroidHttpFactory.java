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

public class AndroidHttpFactory implements HttpFactory {

    /**
     * Hostname
     */
    private String mHostname;
    /**
     * Byte Array of certificate
     */
    private byte[] certificateBytes;

    @Override
    public HttpPostRequest getHttpPostRequest(final String uri) {
        return new AndroidHttpPostRequest().withUrl(uri);
    }

    @Override
    public HttpResponse execute(HttpPostRequest httpPostRequest) throws HttpException {

        return new HttpsConnection().withRequestMethod(HttpPostRequest.HTTP_METHOD_POST)
                                    .withUrl(httpPostRequest.getUrl())
                                    .withRequestData(
                                            httpPostRequest.getRequestData())
                                    .withHostName(mHostname)
                                    .withCertificate(certificateBytes)
                                    .withRequestProperty(
                                            httpPostRequest.getRequestProperty()).execute();
    }

    @Override
    public void setHostname(String hostName) {
        mHostname = hostName;
    }

    @Override
    public void setCertificateBytes(byte[] certificateBytes) {
        this.certificateBytes = certificateBytes;
    }

    @Override
    public HttpGetRequest getHttpGetRequest(final String uri) {
        return new AndroidHttpGetRequest().withUrl(uri);
    }

    @Override
    public HttpResponse execute(HttpGetRequest httpGetRequest) throws HttpException {
        return new HttpsConnection().withUrl(httpGetRequest.getUrl()).withHostName(mHostname)
                                    .withCertificate(certificateBytes)
                                    .withRequestProperty(httpGetRequest.getRequestProperty())
                                    .execute();
    }
}
