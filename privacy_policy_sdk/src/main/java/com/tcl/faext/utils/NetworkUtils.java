package com.tcl.faext.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.tcl.faext.BuildConfig;
import com.tcl.faext.net.HttpApi;
import com.tcl.faext.net.TlsOnlySocketFactory;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;
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
    private static final String TAG = "dan";
    static NetworkStatus mStatus = NetworkStatus.NetworkNotReachable;

    public static enum NetworkStatus {
        NetworkNotReachable,
        NetworkReachableViaWWAN,
        NetworkReachableViaWiFi,
    }

    public static NetworkStatus getNetworkStatus(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = manager.getActiveNetworkInfo();
        if (info == null || !info.isAvailable()) {
            mStatus = NetworkStatus.NetworkNotReachable;
        } else if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
            mStatus = NetworkStatus.NetworkReachableViaWWAN;

        } else if (info.getType() == ConnectivityManager.TYPE_WIFI) {
            mStatus = NetworkStatus.NetworkReachableViaWiFi;
        }
        return mStatus;
    }

    public static boolean isNetworkAvailable() {
        return !mStatus.equals(NetworkStatus.NetworkNotReachable);
    }

    public static String loadAgreementUrl(Context context, String type) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            String keys = type + "_" + Locale.getDefault().getLanguage();
            String pkg = context.getPackageName();
            List<String> list = new ArrayList<>();
            list.add(keys);
            list.add(pkg);
            String sign = SignUtil.generateSign(list);
            connection = generateConnection(HttpApi.PATH_ALL_URL_V1 + "?pkg=" + pkg
                    + "&keys=" + keys + "&sign=" + sign);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            int status = connection.getResponseCode();
            switch (status) {
                case HttpURLConnection.HTTP_OK: {
                    is = connection.getInputStream();
                    JSONObject result = convert(is);
                    JSONObject config = new JSONObject(result.getString("data"));
                    JSONArray array = new JSONArray(config.getString("configuration"));
                    if (array.length() > 0) {
                        JSONObject object = array.getJSONObject(0);
                        return object.getString("value");
                    } else {
                        return "";
                    }
                }
                default: {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException ignore) {
            }
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public static String requestDialogSwitch(Context context) {
        HttpURLConnection connection = null;
        InputStream is = null;
        try {
            String keys = "privacy_policy_sdk_dialog_switch";//默认固定
            String pkg = context.getPackageName();
            List<String> list = new ArrayList<>();
            list.add(keys);
            list.add(pkg);
            String sign = SignUtil.generateSign(list);
            if (BuildConfig.DEBUG) {
                Log.i(TAG, "requestDialogSwitch: path = " + HttpApi.PATH_ALL_URL_V1 + "?pkg=" + pkg
                        + "&keys=" + keys + "&sign=" + sign);
            }
            connection = generateConnection(HttpApi.PATH_ALL_URL_V1 + "?pkg=" + pkg
                    + "&keys=" + keys + "&sign=" + sign);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
            int status = connection.getResponseCode();
            switch (status) {
                case HttpURLConnection.HTTP_OK: {
                    is = connection.getInputStream();
                    JSONObject result = convert(is);
                    JSONObject config = new JSONObject(result.getString("data"));
                    JSONArray array = new JSONArray(config.getString("configuration"));
                    if (array.length() > 0) {
                        JSONObject object = array.getJSONObject(0);
                        return object.getString("value");
                    } else {
                        return "";
                    }
                }
                default: {
                    return null;
                }
            }
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
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
