package com.comviva.hceservice.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.util.Log;

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
    public static final int TIMEOUT = 1 * 60 * 1000;
    private AssetManager assetManager;
    private final String unknown_exception = "unknown exception";
    private final String server_not_responding = "server not responding";

    private HttpUtil() {
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
        httpsURLConnection.setHostnameVerifier(getHostnameVerifier(url));
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

    public HostnameVerifier getHostnameVerifier(final URL url)
    {
        return new HostnameVerifier() {
            @Override
            public boolean verify(String hostname, SSLSession session) {
               // return true; // verify always returns true, which could cause insecure network traffic due to trusting TLS/SSL server certificates for wrong hostnames
                HostnameVerifier hv = HttpsURLConnection.getDefaultHostnameVerifier();
                Log.d("Hostname Verifier",url.getHost());
                Log.d("Hostname Verifier",url.toString());
                Log.d("Hostname Verifier",String.valueOf(hv.verify(url.getHost(), session)));
                return hv.verify(url.getHost(), session);
            }
        };
    }

    private SSLSocketFactory newSslSocketFactory() {
        try {
            CertificateFactory cf = null;
            try {
                cf = CertificateFactory.getInstance("X.509");
            } catch (CertificateException e) {
                Log.d("CertReading","CertificateFactory Error");

            }
            InputStream caInput = null;
            try {
                assetManager = ComvivaSdk.getInstance(null).getApplicationContext().getAssets();
                caInput =  assetManager.open("paymentAppServer.crt");
                //caInput = new BufferedInputStream(ComvivaSdk.getInstance(null).getApplicationContext().getAssets().open("paymentAppServer.crt"));
            } catch (IOException e) {

                Log.d("Certificate Reading","Error Reading Certificate");

            }
            Certificate ca = null;
            try {
                try {
                    if(null != cf) {
                        ca = cf.generateCertificate(caInput);
                    }
                } catch (CertificateException e) {
                    Log.d("Certificate Reading","Generate Certificate Error");
                }
            } finally {
                try {
                    caInput.close();
                } catch (IOException e) {
                    Log.d("Certificate Reading","Input file close error");

                }
            }

            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = null;
            try {
                keyStore = KeyStore.getInstance(keyStoreType);
            } catch (KeyStoreException e) {
                Log.d("Certificate Reading","Keystore Error");

            }
            try {
                if(null != keyStore) {
                    keyStore.load(null, null);
                }
            } catch (IOException e) {
                Log.d("Error","Keystore Load Error 1");
            } catch (NoSuchAlgorithmException e) {
                Log.d("Certificate Reading","Keystore Load Error 2");
            } catch (CertificateException e) {
                Log.d("Certificate Reading","Keystore Load Error 3");
            }
            try {
                keyStore.setCertificateEntry("ca", ca);
            } catch (KeyStoreException e) {

                Log.d("Certificate Reading","setCertificateEntry Error ");
            }

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = null;
            try {
                tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            } catch (NoSuchAlgorithmException e) {
                Log.d("Certificate Reading","TrustManagerFactory Error ");
            }
            try {
                if(null != tmf) {
                    tmf.init(keyStore);
                }
            } catch (KeyStoreException e) {

                Log.d("Certificate Reading","KeyStoreException Error ");
            }

            // Create an SSLContext that uses our TrustManager
            SSLContext sslContext = null;
            try {
                sslContext = SSLContext.getInstance("TLS");
            } catch (NoSuchAlgorithmException e) {
                Log.d("Certificate Reading","TLS Error ");
            }
            try {
                if(null != sslContext) {
                    sslContext.init(null, tmf.getTrustManagers(), null);
                }
            } catch (KeyManagementException e) {
                Log.d("Certificate Reading","getTrustManagers Error ");
            }
        /*    SSLSocketFactory sslSocketFactory = sslContext.getSocketFactory();
            sslSocketFactory.se*/
            if(null != sslContext.getSocketFactory())
            {
                return sslContext.getSocketFactory();
            }else
            {
                return null;
            }
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
        if (serviceUrl.startsWith("https")) {
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
                responseData = convertStreamToString(in);
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus(server_not_responding);
            } catch (Exception e) {
                httpResponse.setReqStatus(unknown_exception);
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
                    Log.d("Error", ioe.getMessage());
                }
            }
            return httpResponse;
        } else {
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
                responseData = convertStreamToString(in);
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus(server_not_responding);
            } catch (Exception e) {
                httpResponse.setReqStatus(unknown_exception);
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
                    Log.d("Error" , ioe.getMessage());
                }
            }
            return httpResponse;
        }
    }

    public HttpResponse getRequest(String serviceUrl, Map<String, String> queryStrings) {
        if (serviceUrl.startsWith("https")) {

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
                responseData = convertStreamToString(in);
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus(server_not_responding);
            } catch (Exception e) {
                httpResponse.setReqStatus(unknown_exception);
            } finally {
                try {
                    if (null != in) {
                        in.close();
                    }
                    if (null != httpsURLConnection) {
                        httpsURLConnection.disconnect();
                    }
                } catch (IOException ioe) {
                    Log.d("Error" , ioe.getMessage());
                }
            }
            return httpResponse;
        } else {

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
                responseData = convertStreamToString(in);
                httpResponse.setResponse(responseData);
            } catch (IOException ioe) {
                httpResponse.setReqStatus(server_not_responding);
            } catch (Exception e) {
                httpResponse.setReqStatus(unknown_exception);
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


