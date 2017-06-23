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

package com.mastercard.walletservices.utils.http;

import android.os.Build;
import android.util.Log;

import com.mastercard.walletservices.utils.exceptions.CmsCommunicationException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * A concrete connection implementation that encapsulates the sending /
 * receiving of data to / from the server via the HTTPS protocols.
 */
public class HttpsConnection {
    /**
     * Connection time out.
     */
    public static final int TIMEOUT = 120 * 1000;
    /**
     * URL
     */
    private String mUrl;
    /**
     * Input Data
     */
    private String mData;
    /**
     *
     */
    private String requestType = HTTP_METHOD_POST;
    /**
     * HTTP Post method
     */
    public static final String HTTP_METHOD_POST = "POST";
    /**
     * HTTP Get method
     */
    public static final String HTTP_METHOD_GET = "GET";

    /**
     * This method takes request url
     *
     * @param url url of cms
     * @return Instance of HttpsConnection
     */
    public HttpsConnection withUrl(String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * This method takes HTTP method
     *
     * @param method HTTP method
     * @return Instance of HttpsConnection
     */
    public HttpsConnection withMethod(String method) {
        this.requestType = method;
        return this;
    }

    /**
     * This method takes request data to send
     *
     * @param content data to send in request.
     * @return Instance of HttpsConnection
     */
    public HttpsConnection withRequestData(String content) {
        this.mData = content;
        return this;
    }

    /**
     * Creates URL object out of String url.
     *
     * @return URL Url
     */
    private URL getServerUrl() throws MalformedURLException {
        return new URL(mUrl);
    }

    /**
     * Return All Trusted Certificates
     *
     * @return TrustManager Array
     */
    private TrustManager[] getTrustAllCertificates() {
        return new TrustManager[]{new X509TrustManager() {
            public java.security.cert.X509Certificate[] getAcceptedIssuers() {
                return null;
            }

            public void checkClientTrusted(
                    java.security.cert.X509Certificate[] certs,
                    String authType) {
            }

            public void checkServerTrusted(
                    java.security.cert.X509Certificate[] certs,
                    String authType) {
            }
        }};
    }

    /**
     * Read received input stream
     *
     * @param stream input data stream
     * @return byte[] of received data
     * @throws IOException
     */
    protected byte[] readAll(InputStream stream) throws IOException {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        byte[] chunk = new byte[2048];
        int length;
        while ((length = stream.read(chunk)) != -1) {
            if (length == chunk.length) {
                outputStream.write(chunk);
            } else {
                byte[] lastChunk = new byte[length];
                System.arraycopy(chunk, 0, lastChunk, 0, length);
                outputStream.write(lastChunk);
            }
        }
        return outputStream.toByteArray();
    }

    /**
     * Initialize ssl context as TLS.
     *
     * @return SSLContext Instance of SSLContext
     */
    private SSLContext initializePermissiveSslContext() throws NoSuchAlgorithmException,
            KeyManagementException {
        TrustManager[] trustAllCerts = getTrustAllCertificates();

        SSLContext sc = SSLContext.getInstance("TLS");
        sc.init(null, trustAllCerts, null);

        return sc;
    }

    /**
     * Set HTTPs url connection.
     *
     * @return HttpsURLConnection Instance of HttpsURLConnection
     */
    private HttpsURLConnection setupHttpsUrlConnection(URL serverUrl) throws
            KeyManagementException, NoSuchAlgorithmException,
            IllegalArgumentException, IOException {
        SSLContext sslContext = initializePermissiveSslContext();

        HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());

        HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String s, final SSLSession sslSession) {
                return true;
            }
        });

        HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) serverUrl.openConnection();
        httpsUrlConnection.setDoInput(true);
        httpsUrlConnection.setConnectTimeout(TIMEOUT);
        httpsUrlConnection.setReadTimeout(TIMEOUT);

        if (requestType.equalsIgnoreCase(HTTP_METHOD_POST)) {
            httpsUrlConnection.setRequestMethod(HTTP_METHOD_POST);
            httpsUrlConnection.setDoOutput(true);
            httpsUrlConnection.setRequestProperty("Content-Type", "application/json");
            httpsUrlConnection.setRequestProperty("Accept", "application/json");
        } else if (requestType.equalsIgnoreCase(HTTP_METHOD_GET)) {
            httpsUrlConnection.setRequestMethod(HTTP_METHOD_GET);
            httpsUrlConnection.setRequestProperty("Accept", "text/plain, application/octet-stream, application/pkix-cert");
        }

        if (Build.VERSION.SDK_INT > 13) {
            httpsUrlConnection.setRequestProperty("Connection", "close");
        }
        return httpsUrlConnection;
    }


    /**
     * Set HTTP url connection.
     *
     * @return HttpURLConnection Instance of HttpURLConnection
     */
    private HttpURLConnection setupHttpUrlConnection(URL serverUrl) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) serverUrl.openConnection();
        httpURLConnection.setDoInput(true);
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);

        if (requestType.equalsIgnoreCase(HTTP_METHOD_POST)) {
            httpURLConnection.setRequestMethod(HTTP_METHOD_POST);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setRequestProperty("Content-Type", "application/json");
            httpURLConnection.setRequestProperty("Accept", "application/json");
        } else if (requestType.equalsIgnoreCase(HTTP_METHOD_GET)) {
            httpURLConnection.setRequestMethod(HTTP_METHOD_GET);
            httpURLConnection.setRequestProperty("Accept", "text/plain, application/octet-stream, application/pkix-cert");
        }

        if (Build.VERSION.SDK_INT > 13) {
            httpURLConnection.setRequestProperty("Connection", "close");
        }
        return httpURLConnection;
    }

    /**
     * This method create HTTPs connection send request and returns response.
     *
     * @return HTTP Response.
     */
    public byte[] execute() throws CmsCommunicationException {
        // TODO: Add Retry Strategy
        HttpURLConnection httpUrlConnection = null;
        InputStream inputStream;
        try {

            URL serverUrl = getServerUrl();
            int responseCode;

            if (serverUrl.getProtocol().equalsIgnoreCase("https")) {
                httpUrlConnection = setupHttpsUrlConnection(serverUrl);
            } else {
                httpUrlConnection = setupHttpUrlConnection(serverUrl);
            }
            if (this.mData != null) {
                httpUrlConnection.getOutputStream().write(this.mData.getBytes());
            }
            httpUrlConnection.connect();
            responseCode = httpUrlConnection.getResponseCode();
            Log.d("HTTP", "HTTP POST RESPONSE CODE:" + responseCode);

            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new CmsCommunicationException(responseCode, "Http Error");
            }

            // Try to build a response
            inputStream = httpUrlConnection.getInputStream();

            try {
                return readAll(inputStream);
            } catch (IOException e) {
                throw new CmsCommunicationException(e.getMessage(), e);
            }
        } catch (Exception e) {
            throw new CmsCommunicationException(e.getMessage(), e);
        } finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
        }

    }
}
