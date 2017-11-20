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

package com.mastercard.mcbp.utils.crypto;

import com.mastercard.mcbp.utils.exceptions.crypto.McbpCryptoException;
import com.mastercard.mcbp.utils.http.HttpFactory;
import com.mastercard.mcbp.utils.http.HttpGetRequest;
import com.mastercard.mcbp.utils.http.HttpResponse;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;

/**
 * Utility class to extract the public key and the Certificate Finger Print from a Certificate URL
 */
public class CertificateHandler {
    /**
     * Request property of Http Get Request
     */
    private static final String HTTP_GET_REQUEST_PROPERTY =
            "text/plain, application/octet-stream, application/pkix-cert";

    final String mCertificateUrl;

    /**
     * Http Factory to execute HttpGet & HttpPost request.
     */
    private final HttpFactory mHttpFactory;

    public CertificateHandler(String certificateUrl, HttpFactory httpFactory) {
        mCertificateUrl = certificateUrl;
        mHttpFactory = httpFactory;
    }

    /**
     * Retrieve certificate.
     *
     * @return Instance of {@link CertificateMetaData}
     * @throws HttpException
     * @throws CertificateException
     * @throws McbpCryptoException
     */
    public CertificateMetaData getCertificateMetaData()
            throws HttpException, CertificateException, McbpCryptoException {
        return retrieveCertificateMetaData(downloadCertificate());
    }

    /**
     * Download certificate form given URL.
     *
     * @return Certificate contents.
     */
    private ByteArray downloadCertificate() throws HttpException {
        //Get HttpGet Request
        HttpGetRequest httpGetRequest = mHttpFactory.getHttpGetRequest(mCertificateUrl);
        httpGetRequest.withRequestProperty(HTTP_GET_REQUEST_PROPERTY);

        //Execute HttpGetRequest
        HttpResponse httpResponse = mHttpFactory.execute(httpGetRequest);
        return httpResponse.getContent();
    }

    /**
     * Retrieve public certificate and its finger print.
     *
     * @param content MDES certificate
     * @return Instance of {@link CertificateMetaData}
     * @throws CertificateException
     * @throws McbpCryptoException
     */
    private CertificateMetaData retrieveCertificateMetaData(ByteArray content)
            throws CertificateException, McbpCryptoException {
        //Create certificate
        CertificateFactory cf = CertificateFactory.getInstance("X.509");
        InputStream mInputStream = new ByteArrayInputStream(content.getBytes());
        final Certificate certificate = cf.generateCertificate(mInputStream);
        final ByteArray publicKey = ByteArray.of(certificate.getPublicKey().getEncoded());
        return new CertificateMetaData() {
            @Override
            public ByteArray getPublicKey() {
                return publicKey;
            }
        };
    }
}
