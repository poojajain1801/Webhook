package com.comviva.hceservice.util;

import android.content.Context;
import android.net.ConnectivityManager;

import com.comviva.hceservice.common.ComvivaSdk;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.Map;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;

/**
 * Contains utility method to send request to server.
 * Created by tarkeshwa r.v on 3/3/2017.
 */
public class HttpUtil {
    private static HttpUtil httpUtil;
    private HttpResponse httpResponse;
    private Boolean isHttpsEnabled = true;
    public static final int TIMEOUT = 20 * 60 * 1000;

    private HttpUtil() {
    }

    private void setProxy() {
        System.setProperty("http.proxyHost", "172.19.7.180");
        System.setProperty("http.proxyPort", "8080");
        System.setProperty("http.proxyUser", "tarkeshwar.v");
        System.setProperty("http.proxyPassword", "feb.2017");

        System.setProperty("https.proxyHost", "172.19.7.180");
        System.setProperty("https.proxyPort", "8080");
        System.setProperty("https.proxyUser", "tarkeshwar.v");
        System.setProperty("https.proxyPassword", "feb.2017");
    }

    private String convertStreamToString(InputStream inStream) throws Exception {
        InputStreamReader inputStream = new InputStreamReader(inStream);
        BufferedReader bReader = new BufferedReader(inputStream);

        StringBuilder sb = new StringBuilder();
        String line;
        while ((line = bReader.readLine()) != null) {
            sb.append(line);
        }
        return sb.toString();
    }

    private HttpsURLConnection createHttpsConnection(URL url) throws IOException {
        HttpsURLConnection httpsURLConnection = (HttpsURLConnection) url.openConnection();
        httpsURLConnection.setConnectTimeout(TIMEOUT);
        httpsURLConnection.setReadTimeout(TIMEOUT);
        httpsURLConnection.setUseCaches(false);
        httpsURLConnection.setRequestProperty("Content-Type", "application/json");
        httpsURLConnection.setSSLSocketFactory(newSslSocketFactory());
        httpsURLConnection.setHostnameVerifier(getHostnameVerifier());
        return httpsURLConnection;
    }

    private HttpURLConnection createHttpConnection(URL url) throws IOException {
        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
        httpURLConnection.setConnectTimeout(TIMEOUT);
        httpURLConnection.setReadTimeout(TIMEOUT);
        httpURLConnection.setUseCaches(false);
        httpURLConnection.setRequestProperty("Content-Type", "application/json");
        return httpURLConnection;
    }

    HostnameVerifier getHostnameVerifier() {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
                //return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                //HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                return true;
            }
        };
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X.509");
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            InputStream caInput = null;
            try {
                caInput = new BufferedInputStream(ComvivaSdk.getInstance(null).getApplicationContext().getAssets().open("paymentAppServer.crt"));
            } catch (IOException e) {
                e.printStackTrace();
            }
            Certificate ca = null;
            try {
                try {
                    ca = cf.generateCertificate(caInput);
                } catch (CertificateException e) {
                    e.printStackTrace();
                }
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance(keyStoreType);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }
            try {
                keyStore.load(null, null);
            } catch (IOException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            }
            try {
                keyStore.setCertificateEntry("ca", ca);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = null;
            try {
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                tmf.init(keyStore);
            } catch (KeyStoreException e) {
                e.printStackTrace();
            }

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
            try {
                sslContext.init(null, tmf.getTrustManagers(), null);
            } catch (KeyManagementException e) {
                e.printStackTrace();
            }
        /*    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            sslSocketFactory.se*/

            return sslContext.getSocketFactory();
        } catch (Exception e) {
            return null;
        }
    }

    public static HttpUtil getInstance() {
        if (null == httpUtil) {
            httpUtil = new HttpUtil();
        }
        return httpUtil;
    }

    public boolean isNetworkAvailable(final Context context) {
        final ConnectivityManager connectivityManager = ((ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE));
        return connectivityManager.getActiveNetworkInfo() != null && connectivityManager.getActiveNetworkInfo().isConnected();
    }

    public HttpResponse postRequest(String serviceUrl, String requestData) {
        if (isHttpsEnabled) {
            //setProxy();
            httpResponse = new HttpResponse();
            HttpsURLConnection httpsURLConnection = null;
            OutputStream out = null;
            InputStream in = null;

            int responseCode;
            String responseData;
            try {
                byte[] postData = requestData.getBytes();
                URL url = new URL(serviceUrl);

                httpsURLConnection = createHttpsConnection(url);
                httpsURLConnection.setRequestMethod("POST");
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));
                out = httpsURLConnection.getOutputStream();
                out.write(postData);
                out.close();
                responseCode = httpsURLConnection.getResponseCode();
                httpResponse.setStatusCode(responseCode);

                in = httpsURLConnection.getInputStream();
                if (responseCode == 200) {
                    responseData = convertStreamToString(in);
                } else {
                    responseData = convertStreamToString(in);
                }
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus("Server is not responding");
            } catch (Exception e) {
                httpResponse.setReqStatus("Unknown exception");
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != out) {
                        out.close();
                    }
                    if (null != httpsURLConnection) {
                        httpsURLConnection.disconnect();
                    }
                } catch (IOException ioe) {
                }
            }
            return httpResponse;
        } else {
            //setProxy();
            httpResponse = new HttpResponse();
            HttpURLConnection httpURLConnection = null;
            OutputStream out = null;
            InputStream in = null;

            int responseCode;
            String responseData;
            try {
                byte[] postData = requestData.getBytes();

                URL url = new URL(serviceUrl);

                httpURLConnection = createHttpConnection(url);
                httpURLConnection.setRequestMethod("POST");
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestProperty("Content-Length", Integer.toString(postData.length));

                out = httpURLConnection.getOutputStream();
                out.write(postData);
                out.close();
                responseCode = httpURLConnection.getResponseCode();
                httpResponse.setStatusCode(responseCode);

                in = httpURLConnection.getInputStream();
                if (responseCode == 200) {
                    responseData = convertStreamToString(in);
                } else {
                    responseData = convertStreamToString(in);
                }
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus("Server is not responding");
            } catch (Exception e) {
                httpResponse.setReqStatus("Unknown exception");
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != out) {
                        out.close();
                    }
                    if (null != httpURLConnection) {
                        httpURLConnection.disconnect();
                    }
                } catch (IOException ioe) {
                }
            }
            return httpResponse;
        }
    }

    public HttpResponse getRequest(String serviceUrl, Map<String, String> queryStrings) {
        if (isHttpsEnabled) {
            //setProxy();
            httpResponse = new HttpResponse();

            HttpsURLConnection httpsURLConnection = null;
            InputStream in = null;

            int responseCode;
            String responseData;
            try {
                // Prepare Query String
                if (queryStrings.size() > 0) {
                    String strQueries = "";
                    boolean isFirstElement = true;
                    for (String key : queryStrings.keySet()) {
                        if (isFirstElement) {
                            strQueries += "?" + key + "=" + queryStrings.get(key);
                            isFirstElement = false;
                        } else {
                            strQueries += "&" + key + "=" + queryStrings.get(key);
                        }
                    }
                    serviceUrl += strQueries;
                }

                URL url = new URL(serviceUrl);
                httpsURLConnection = createHttpsConnection(url);
                httpsURLConnection.setDoOutput(true);
                httpsURLConnection.setRequestMethod("GET");

                httpsURLConnection.connect();
                responseCode = httpsURLConnection.getResponseCode();
                httpResponse.setStatusCode(responseCode);

                in = httpsURLConnection.getInputStream();
                if (responseCode == 200) {
                    responseData = convertStreamToString(in);
                } else {
                    responseData = convertStreamToString(in);
                }
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus("Server is not responding");
            } catch (Exception e) {
                httpResponse.setReqStatus("Unknown exception");
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != httpsURLConnection) {
                        httpsURLConnection.disconnect();
                    }
                } catch (IOException ioe) {
                }
            }
            return httpResponse;
        } else {
            //setProxy();
            httpResponse = new HttpResponse();
            HttpURLConnection httpURLConnection = null;
            InputStream in = null;
            int responseCode;
            String responseData;
            try {
                // Prepare Query String
                if (queryStrings.size() > 0) {
                    String strQueries = "";
                    boolean isFirstElement = true;
                    for (String key : queryStrings.keySet()) {
                        if (isFirstElement) {
                            strQueries += "?" + key + "=" + queryStrings.get(key);
                            isFirstElement = false;
                        } else {
                            strQueries += "&" + key + "=" + queryStrings.get(key);
                        }
                    }
                    serviceUrl += strQueries;
                }

                URL url = new URL(serviceUrl);
                httpURLConnection = createHttpConnection(url);
                httpURLConnection.setDoOutput(true);
                httpURLConnection.setRequestMethod("GET");

                httpURLConnection.connect();
                responseCode = httpURLConnection.getResponseCode();
                httpResponse.setStatusCode(responseCode);

                in = httpURLConnection.getInputStream();
                if (responseCode == 200) {
                    responseData = convertStreamToString(in);
                } else {
                    responseData = convertStreamToString(in);
                }
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus("Server is not responding");
            } catch (Exception e) {
                httpResponse.setReqStatus("Unknown exception");
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != httpURLConnection) {
                        httpURLConnection.disconnect();
                    }
                } catch (IOException ioe) {
                }
            }
            return httpResponse;
        }
    }

}


