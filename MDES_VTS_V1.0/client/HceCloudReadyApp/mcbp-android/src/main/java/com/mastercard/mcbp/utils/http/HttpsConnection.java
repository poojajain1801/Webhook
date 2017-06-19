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

import com.mastercard.mcbp.utils.exceptions.McbpErrorCode;
import com.mastercard.mcbp.utils.logs.McbpLogger;
import com.mastercard.mcbp.utils.logs.McbpLoggerFactory;
import com.mastercard.mcbp_android.BuildConfig;
import com.mastercard.mobile_api.bytes.ByteArray;
import com.mastercard.mobile_api.utils.exceptions.http.HttpException;

import org.jetbrains.annotations.NotNull;

import android.util.Log;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateExpiredException;
import java.security.cert.CertificateFactory;
import java.security.cert.CertificateNotYetValidException;
import java.security.cert.X509Certificate;
import java.text.SimpleDateFormat;
import java.util.List;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.KeyManager;
import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLPeerUnverifiedException;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * A concrete connection implementation that encapsulates the sending /
 * receiving of data to / from the server via the HTTPS protocols.
 */
class HttpsConnection {
    /**
     * Connection time out.
     */
    public static final int TIMEOUT = 30 * 1000;

    /**
     * HTTP request method
     */
    private String mRequestMethod = HttpGetRequest.HTTP_METHOD_GET;

    /**
     * McbpLogger
     */
    private final McbpLogger mLogger = McbpLoggerFactory.getInstance().getLogger(this);
    /**
     * URL
     */
    private String mUrl;
    /**
     * Input Data
     */
    private String mData;

    /**
     * The host name or IP address of the URL.
     */
    private String mHostname;

    /**
     * Certificate bytes
     */
    private byte[] mCertificateBytes;
    /**
     * Retry After Header
     */
    private static final String RETRY_AFTER_HEADER = "Retry-After";
    /**
     * Request Property
     */
    private String mRequestProperty;

    /**
     * This method takes request url
     *
     * @param url url of cms
     * @return Instance of HttpsGetPostConnection
     */
    public HttpsConnection withUrl(String url) {
        this.mUrl = url;
        return this;
    }

    /**
     * This method takes request data to send
     *
     * @param content data to send in request.
     * @return Instance of HttpsGetPostConnection
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
    @NotNull
    private URL getServerUrl() throws MalformedURLException {
        return new URL(mUrl);
    }

    /**
     * This method takes host name to verify
     *
     * @param hostName Host Name
     * @return Instance of HttpsGetPostConnection
     */
    public HttpsConnection withHostName(final String hostName) {
        this.mHostname = hostName;
        return this;
    }

    /**
     * This method takes certificate
     *
     * @param certificateBytes Byte Array of certificate
     * @return Instance of HttpsGetPostConnection
     */
    public HttpsConnection withCertificate(final byte[] certificateBytes) {
        this.mCertificateBytes = certificateBytes;
        return this;
    }

    public HttpsConnection withRequestMethod(final String requestMethod) {
        mRequestMethod = requestMethod;
        return this;
    }

    /**
     * This method takes request property
     *
     * @param requestProperty requestProperty
     * @return Instance of HttpsGetPostConnection
     */
    public HttpsConnection withRequestProperty(final String requestProperty) {
        mRequestProperty = requestProperty;
        return this;
    }

    /**
     * Read received input stream
     *
     * @param stream input data stream
     * @return byte[] of received data
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
    @NotNull
    private SSLContext initializePermissiveSslContext() throws NoSuchAlgorithmException,
            KeyManagementException, IOException, HttpException {
        SSLContext sslContext = SSLContext.getInstance("TLS");
        try {
            KeyManager[] keyManagers = null;
            TrustManager[] customTrustManager = null;
            if (mCertificateBytes != null && mCertificateBytes.length > 0) {
                KeyManagerFactory kmf = KeyManagerFactory.getInstance("X509");
                CertificateFactory cf = CertificateFactory.getInstance("X.509");
                InputStream mInputStream = new ByteArrayInputStream(mCertificateBytes);
                Certificate certificate = cf.generateCertificate(mInputStream);

                // Create a KeyStore containing our trusted CAs
                String keyStoreType = KeyStore.getDefaultType();
                KeyStore keyStore = KeyStore.getInstance(keyStoreType);
                keyStore.load(null, null);
                keyStore.setCertificateEntry("ca", certificate);
                customTrustManager = new TrustManager[]{new CustomTrustManager(keyStore)};
                kmf.init(keyStore, null);
                keyManagers = kmf.getKeyManagers();
            }

            sslContext.init(keyManagers, customTrustManager, new java.security.SecureRandom());

        } catch (CertificateException e) {
            mLogger.d(Log.getStackTraceString(e));
            throw new HttpException("Error in Certificate");
        } catch (Exception e) {
            if (e.getCause() != null) {
                mLogger.d(Log.getStackTraceString(e.getCause()));
            } else {
                mLogger.d(Log.getStackTraceString(e));
            }
            throw new HttpException("Error in ssl context preparation");
        }
        return sslContext;
    }

    /**
     * Set HTTPs url connection.
     *
     * @param serverUrl url of server
     * @return HttpsURLConnection Instance of HttpsURLConnection
     */
    private HttpsURLConnection setupHttpsUrlConnection(URL serverUrl) throws
            KeyManagementException, NoSuchAlgorithmException, IllegalArgumentException,
            IOException, HttpException {
        SSLContext sslContext = initializePermissiveSslContext();
        HttpsURLConnection httpsUrlConnection = (HttpsURLConnection) serverUrl.openConnection();

        if (BuildConfig.FORCE_TLS_PROTOCOL != null
            && BuildConfig.FORCE_TLS_PROTOCOL.length != 0) {
            //Only protocols supported mentioned in Build config.
            SSLSocketFactory tslOnlySocketFactory =
                    new CustomSSLSocketFactory(sslContext.getSocketFactory());
            httpsUrlConnection.setSSLSocketFactory(tslOnlySocketFactory);
        } else {
            // #MCBP_LOG_BEGIN
            mLogger.d("No protocol found in Build config");
            // #MCBP_LOG_END

            // Note that this requires at least API level 16
            httpsUrlConnection.setSSLSocketFactory(sslContext.getSocketFactory());
        }

        if (mHostname == null || mHostname.isEmpty()) {
            throw new HttpException("No host name found");
        }

        httpsUrlConnection.setHostnameVerifier(new HostnameVerifier() {
            @Override
            public boolean verify(final String hostname, final SSLSession session) {
                return verifyHost(session);
            }
        });
        httpsUrlConnection = (HttpsURLConnection) configureCommonHttpAttributes(httpsUrlConnection);
        return httpsUrlConnection;
    }

    /**
     * Configure the Http connection request attributes
     *
     * @param urlConnection Instance of HttpURLConnection
     * @return HttpURLConnection with configured attributes
     */
    private HttpURLConnection configureCommonHttpAttributes(final HttpURLConnection urlConnection)
            throws ProtocolException {
        urlConnection.setRequestMethod(mRequestMethod);
        urlConnection.setDoInput(true);

        if (mRequestMethod.equalsIgnoreCase(HttpPostRequest.HTTP_METHOD_POST)) {
            urlConnection.setDoOutput(true);
            urlConnection.setRequestProperty("Content-Type", mRequestProperty);
        }
        urlConnection.setRequestProperty("Accept", mRequestProperty);
        urlConnection.setConnectTimeout(TIMEOUT);
        urlConnection.setReadTimeout(TIMEOUT);
        return urlConnection;
    }

    /**
     * Set HTTP url connection.
     *
     * @param serverUrl Url of server
     * @return HttpURLConnection Instance of HttpURLConnection
     */
    private HttpURLConnection setupHttpUrlConnection(URL serverUrl) throws IOException {

        HttpURLConnection httpURLConnection = (HttpURLConnection) serverUrl.openConnection();
        httpURLConnection = configureCommonHttpAttributes(httpURLConnection);
        return httpURLConnection;
    }

    /**
     * This method create HTTPs connection send request and returns response.
     *
     * @return HTTP Response.
     */
    public HttpResponse execute() throws HttpException {
        mLogger.d("----------HTTP " + mRequestMethod + " START------------");
        HttpResponse httpResponse;
        HttpURLConnection httpUrlConnection = null;
        String errorMessage;
        int responseCode;
        int retryAfterValue;
        try {
            InputStream inputStream = null;
            URL serverUrl = getServerUrl();

            if (serverUrl.getProtocol().equalsIgnoreCase("https")) {
                httpUrlConnection = setupHttpsUrlConnection(serverUrl);
            } else {
                httpUrlConnection = setupHttpUrlConnection(serverUrl);
            }
            mLogger.d("HTTP REQUEST METHOD:->" + httpUrlConnection.getRequestMethod());
            //In case of HttpGetRequest, data will not be there.
            if (this.mData != null) {
                OutputStream outputStream = null;

                try {
                    outputStream = httpUrlConnection.getOutputStream();
                    outputStream.write(this.mData.getBytes());
                } finally {
                    safeCloseOutputStream(outputStream);
                }

            }
            httpUrlConnection.connect();
            responseCode = httpUrlConnection.getResponseCode();
            retryAfterValue = getRetryAfterValueHeader(httpUrlConnection);
            errorMessage = getErrorStream(httpUrlConnection);

            if (!((responseCode == HttpURLConnection.HTTP_OK) ||
                  (responseCode == HttpURLConnection.HTTP_NO_CONTENT))) {
                throw new HttpException(responseCode, errorMessage, retryAfterValue);
            }

            try {
                // Try to build a response
                inputStream = httpUrlConnection.getInputStream();
                httpResponse = new HttpResponse(responseCode);
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    // Finally set the content
                    httpResponse.setContent(ByteArray.of(readAll(inputStream)));
                }
            } finally {
                safeCloseInputStream(inputStream);
            }

        } catch (KeyManagementException | NoSuchAlgorithmException e) {
            throw new HttpException(e.getMessage(), e);
        } catch (SocketTimeoutException e) {
            throw new HttpException(HttpResponse.SC_REQUEST_TIMEOUT, e.getMessage());
        } catch (SSLException e) {
            throw new HttpException(McbpErrorCode.SSL_ERROR_CODE, e.getMessage());
        } catch (IOException e) {
            throw new HttpException(e.getMessage(), e);
        } finally {
            mLogger.d("----------HTTP END------------");
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();

            }
        }

        return httpResponse;
    }

    /**
     * Return error message
     *
     * @param httpUrlConnection httpUrlConnection
     * @return Error message
     */
    private String getErrorStream(HttpURLConnection httpUrlConnection) throws IOException {

        InputStream errorStream = null;
        try {
            errorStream = httpUrlConnection.getErrorStream();
            if (errorStream != null) {
                return new String(readAll(errorStream));
            }
        } finally {
            safeCloseInputStream(errorStream);
        }
        return null;
    }

    /**
     * Return the retry after time value if Retry-After header present int http header
     *
     * @param httpUrlConnection httpUrlConnection object
     * @return retryAfterValue retry after time
     */
    private int getRetryAfterValueHeader(HttpURLConnection httpUrlConnection)
            throws IOException, HttpException {
        int retryAfterValue = 0;
        if (httpUrlConnection.getHeaderFields() != null) {
            boolean isRetryAfterPresent = httpUrlConnection.getHeaderFields()
                                                           .containsKey(RETRY_AFTER_HEADER);
            if (isRetryAfterPresent) {
                List<String> retryAfter =
                        httpUrlConnection.getHeaderFields().get(RETRY_AFTER_HEADER);
                try {
                    retryAfterValue = Integer.parseInt(retryAfter.get(0));
                } catch (NumberFormatException e) {
                    try {
                        SimpleDateFormat format =
                                new SimpleDateFormat("EEE, dd MM yyyy HH:mm:ss zzz");
                        java.util.Date d = format.parse(retryAfter.get(0));
                        long retryAfterDate = d.getTime();

                        retryAfterValue =
                                Long.valueOf(
                                        (retryAfterDate - System.currentTimeMillis()) / 1000)
                                    .intValue();
                    } catch (Exception e1) {
                        throw new HttpException(httpUrlConnection.getResponseCode(),
                                                "Error in parsing retry after value");
                    }
                }
            }
        }
        return retryAfterValue;
    }


    /**
     * Verify the Host Name.
     *
     * @param sslSession SSLSession
     * @return true if Host Name verified else return false.
     */
    private boolean verifyHost(final SSLSession sslSession) {
        X509Certificate[] certificateChain;

        try {
            certificateChain = (X509Certificate[]) sslSession.getPeerCertificates();
        } catch (SSLPeerUnverifiedException e) {
            mLogger.d(Log.getStackTraceString(e));
            return false;
        }

        if (certificateChain != null) {
            for (int i = 0; i <= certificateChain.length; i++) {
                try {
                    certificateChain[0].checkValidity();
                } catch (CertificateExpiredException e) {
                    mLogger.d(Log.getStackTraceString(e));
                    return false;
                } catch (CertificateNotYetValidException e) {
                    mLogger.d(Log.getStackTraceString(e));
                    return false;
                }
            }

            String clientDN = (certificateChain[0].getSubjectDN()).getName();
            int clientNameIndex = clientDN.indexOf("CN");
            int clientNameEndIndex = clientDN.indexOf(',', clientNameIndex);

            String attributeName;
            if (clientNameEndIndex == -1) {
                attributeName = clientDN.substring(clientNameIndex);
            } else {
                attributeName = clientDN.substring(clientNameIndex, clientNameEndIndex);
            }

            String commonName = attributeName.substring(3);
            if (commonName.equals(mHostname)) {
                return true;
            }
        }
        return false;
    }

    private void safeCloseOutputStream(final OutputStream outputStream) {
        if (outputStream != null)
            try {
                outputStream.close();
            } catch (IOException e) {
                mLogger.e("Error: Closing input stream in HttpConnection.java: " + e.getMessage());
            }

    }

    private void safeCloseInputStream(final InputStream inputStream) {
        if (inputStream != null)
            try {
                inputStream.close();
            } catch (IOException e) {
                mLogger.e("Error: Closing output stream in HttpConnection.java: " + e.getMessage());
            }

    }
}
