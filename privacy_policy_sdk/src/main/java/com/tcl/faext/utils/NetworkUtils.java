package com.tcl.faext.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.tcl.faext.net.HttpApi;
import com.tcl.faext.net.TlsOnlySocketFactory;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.SSLSocketFactory;

/**
 * Created by lunou on 18-4-11.
 */

public class NetworkUtils {
    static NetworkStatus mStatus = NetworkStatus.NetworkNotReachable;

    public static enum NetworkStatus{
        NetworkNotReachable,
        NetworkReachableViaWWAN,
        NetworkReachableViaWiFi,
    }

    public NetworkStatus getNetworkStatus(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable())
        {
            mStatus = NetworkStatus.NetworkNotReachable;
        }
        else if (info.getType() == ConnectivityManager.TYPE_MOBILE)
        {
            mStatus = NetworkStatus.NetworkReachableViaWWAN;

        }
        else if (info.getType() == ConnectivityManager.TYPE_WIFI)
        {
            mStatus = NetworkStatus.NetworkReachableViaWiFi;
        }
        return mStatus;
    }

    public static boolean isNetworkAvailable() {
        return !mStatus.equals(NetworkStatus.NetworkNotReachable);
    }

    public static String loadAgreementUrl(Context context) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            connection = generateConnection(HttpApi.PATH_AGREEMENT_URL_V1 + "?packageName=" + context.getPackageName() + "&lang="+ Locale.getDefault().getLanguage());
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            int status = connection.getResponseCode();
            switch (status) {
                case HttpURLConnection.HTTP_OK: {
                    is = connection.getInputStream();
                    JSONObject result = convert(is);

                    return result.getString("data");
                }
                default: {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                is.close();
            } catch (IOException ignore) {
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    private static JSONObject convert(InputStream is) throws IOException, JSONException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int len;
        while ((len = is.read(buffer)) != -1) {
            baos.write(buffer, 0, len);
        }
        String response = baos.toString();
        baos.close();

        return new JSONObject(response);
    }


    private static HttpURLConnection generateConnection(String path) throws IOException {
        if (!path.startsWith("http")) {
            path = HttpApi.DOMAIN_APP_SERVER + path;
        }
        HttpURLConnection connection = (HttpURLConnection) new URL(path).openConnection();

        try {
            if (path.startsWith("https")) {
                SSLSocketFactory tlsOnly = getTlsOnlySocketFactory();
                ((HttpsURLConnection) connection).setSSLSocketFactory(tlsOnly);
                HostnameVerifier hostnameVerifier = new HostnameVerifier() {
                    @Override
                    public boolean verify(String hostname, SSLSession session) {
                        return hostname != null && hostname.contains("tclclouds.com");
                    }
                };
                ((HttpsURLConnection) connection).setHostnameVerifier(hostnameVerifier);
            }
        } catch (Exception ignore) {
        }

        connection.setRequestProperty("Cache-Control", "no-cache");
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoInput(true);

        return connection;
    }

    private static TlsOnlySocketFactory getTlsOnlySocketFactory() {
        SSLContext sslcontext;
        try {
            sslcontext = SSLContext.getInstance("TLSv1");
            sslcontext.init(null, null, null);
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalArgumentException(e);
        } catch (KeyManagementException e) {
            throw new IllegalArgumentException(e);
        }
        return new TlsOnlySocketFactory(sslcontext.getSocketFactory(), true);
    }
}
