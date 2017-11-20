package com.mastercard.mcbp.remotemanagement.mdes;

import com.mastercard.mcbp.remotemanagement.mdes.MdesCommunicatorTest.RetryType;
import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.http.HttpFactory;
import com.mastercard.mcbp.utils.http.HttpGetRequest;
import com.mastercard.mcbp.utils.http.HttpPostRequest;
import com.mastercard.mcbp.utils.http.HttpResponse;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;

/**
 * ****************************************************************************
 * Copyright (c) 2016, MasterCard International Incorporated and/or its
 * affiliates. All rights reserved.
 * <p/>
 * The contents of this file may only be used subject to the MasterCard
 * Mobile Payment SDK for MCBP and/or MasterCard Mobile MPP UI SDK
 * Materials License.
 * <p/>
 * Please refer to the file LICENSE.TXT for full details.
 * <p/>
 * TO THE EXTENT PERMITTED BY LAW, THE SOFTWARE IS PROVIDED "AS IS", WITHOUT
 * WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NON INFRINGEMENT. TO THE EXTENT PERMITTED BY LAW, IN NO EVENT SHALL
 * MASTERCARD OR ITS AFFILIATES BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS
 * IN THE SOFTWARE.
 * *****************************************************************************
 */
public class MockHttpFactory implements HttpFactory {


    public static final int RETRY_AFTER_VALUE_IN_SECONDS = 2;
    public static final String COMMUNICATION_ERROR = "communication_error";
    public static int expectedRemainRetryCount;

    private RetryType mRetryType = null;

    public MockHttpFactory(RetryType retryType) {
        this.mRetryType = retryType;
    }

    @Override
    public HttpPostRequest getHttpPostRequest(String uri) {
        return new HttpPostRequest() {
            public String requestProperty;
            public String content;
            public String uri;

            @Override
            public HttpPostRequest withUrl(String uri) {
                this.uri = uri;
                return this;
            }

            @Override
            public HttpPostRequest withRequestData(String content) {

                this.content = content;
                return this;
            }

            @Override
            public HttpPostRequest withRequestProperty(String requestProperty) {

                this.requestProperty = requestProperty;
                return this;
            }

            @Override
            public String getRequestProperty() {
                return requestProperty;
            }

            @Override
            public String getUrl() {
                return uri;
            }

            @Override
            public String getRequestData() {
                return content;
            }
        };
    }

    @Override
    public HttpResponse execute(HttpPostRequest httpPostRequest) throws HttpException {


        switch (mRetryType) {

            case RETRY_FOR_SC_INTERNAL_SERVER_ERROR:

                // To test if valid response come from CMS-D then retry remain count should reset to zero
                if (expectedRemainRetryCount == 1) {
                    ByteArray httpContent = ByteArray.of("ABAB");
                    HttpResponse httpResponse = new HttpResponse(200);
                    httpResponse.setContent(httpContent);
                    return httpResponse;
                }
                // To test if CMS-D send response with error code 500
                throw new HttpException(500, COMMUNICATION_ERROR);


            case RETRY_FOR_SSL_ERROR:

                throw new HttpException(McbpErrorCode.SSL_ERROR_CODE, COMMUNICATION_ERROR);

            case RETRY_FOR_SC_MOVED_TEMPORARILY:

                throw new HttpException(302, COMMUNICATION_ERROR);

            case RETRY_FOR_SC_SERVICE_UNAVAILABLE:
                throw new HttpException(503, COMMUNICATION_ERROR, RETRY_AFTER_VALUE_IN_SECONDS);


            case RETRY_FOR_REQUEST_TIMEOUT:

                throw new HttpException(408, COMMUNICATION_ERROR);

            case RETRY_FOR_GATEWAY_TIMEOUT:

                throw new HttpException(504, COMMUNICATION_ERROR);

            case NO_RETRY:
                throw new HttpException(0, COMMUNICATION_ERROR);

        }

        return null;
    }

    @Override
    public void setHostname(String hostName) {

    }

    @Override
    public void setCertificateBytes(byte[] certificateBytes) {

    }

    @Override
    public HttpGetRequest getHttpGetRequest(String uri) {
        return null;
    }

    @Override
    public HttpResponse execute(HttpGetRequest httpGetRequest) throws HttpException {
        return null;
    }
}
