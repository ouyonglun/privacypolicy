/* Copyright (C) 2016 Tcl Corporation Limited */
package com.tcl.faext;

import android.app.ActionBar;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.tcl.faext.utils.NetworkUtils;
import com.tcl.faext.utils.PushUtils;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shaohua.li on 3/21/16.
 */
public class PrivacyPolicyActivity extends BaseActivity {
    private static final String TAG = "dan";
    //    private static final String DEFAULT_HTML = "file:///android_asset/agreement.html";
    private ActionBar mActionBar;

    private WebView webView;
    private WebSettings webSettings;
    private RelativeLayout webViewParent;
    private ProgressBar loading;
    private TextView errorView;
    private static final int ERROR_CODE = 0;
    private final static int ACTION_AUTO = 0;
    private final static int ACTION_MANUAL = 1;
    private int responseCode;
    private static final String AGREEMENT_URL = "agreement_url";
    private String DEFAULT_HTML = "http://schedulemanag-test.tclclouds.com/arr-private-en.html";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        fullLayout();
        setContentView(R.layout.disgnosticlib_activity_privacy_policy);
        initWebViewProvider();
        if (Build.VERSION.SDK_INT <= 19) {
            setTheme(R.style.consentStyle);
        }
        mActionBar = getActionBar();

        if (mActionBar != null) {
            mActionBar.setDisplayHomeAsUpEnabled(true);
            mActionBar.setTitle(R.string.privacy_policy);
            mActionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#4FA2FE")));
        }

        findViewById();
        initData();

    }

    private void findViewById() {
        webViewParent = findViewById(R.id.webViewParent);
        errorView = findViewById(R.id.error);
        webView = findViewById(R.id.webview);
        loading = findViewById(R.id.loading);
        webView.setVisibility(View.GONE);
        errorView.setVisibility(View.GONE);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                initData();
            }
        });
    }

    private void initData() {
        try {
            SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
            String url = sharedPreferences.getString(AGREEMENT_URL, "");
            if (TextUtils.isEmpty(url) || url.equalsIgnoreCase("null")) {
                loadUrl(ACTION_MANUAL);
                errorView.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
            } else {
                loadUrl(ACTION_AUTO);
                webSetting(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    int defCount = 0;

    private void webSetting(String url) {
        responseCode = 0;
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                super.onPageStarted(view, url, favicon);
                errorView.setVisibility(View.GONE);
                loading.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                if (webView != null) {
                    webView.setVisibility(View.VISIBLE);
                }
                loading.setVisibility(View.GONE);
                if (responseCode != 0) {
                    if (webView != null) {
                        webView.setVisibility(View.GONE);
                    }

                    if (!url.equals(DEFAULT_HTML) && defCount < 3) {
                        webView.loadUrl(DEFAULT_HTML);
                        defCount++;
                    } else {
                        errorView.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                responseCode = errorCode;
            }
        });
        PushUtils.removeUnsafeJs(webView);
        webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setBuiltInZoomControls(true);
        webSettings.setSupportZoom(true);
        webSettings.setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        if (TextUtils.isEmpty(url) || url.equalsIgnoreCase("null")) {
            webView.loadUrl(DEFAULT_HTML);
        } else {
            Uri uri = Uri.parse(url);
            webView.loadUrl(uri.toString());
        }

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    @Override
    protected void onDestroy() {
        webViewDestroy();
        super.onDestroy();
    }

    private void webViewDestroy() {
        if (webView != null && webViewParent != null) {
            webViewParent.removeView(webView);
            webView.removeAllViews();
            webView.destroy();
            webView = null;
        }
    }

    private static final long AUTO_SYNC_TIMESPAN = 7 * 24 * 60 * 60 * 1000;
    private final static String KEY_LAST_ACTION_LOAD_URL_TIMES = "KEY_LAST_ACTION_LOAD_URL_TIMES";

    private boolean need() {
        SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        long lastSyncTime = sharedPreferences.getLong(KEY_LAST_ACTION_LOAD_URL_TIMES, 0);
        return System.currentTimeMillis() - lastSyncTime > AUTO_SYNC_TIMESPAN;
    }

    boolean mLoading;

    private void loadUrl(int action) {

        if (action == ACTION_AUTO && !need()) {
            return;
        }

        if (mLoading) {
            return;
        }

        if (NetworkUtils.getNetworkStatus(this).equals(NetworkUtils.NetworkStatus.NetworkNotReachable)) {
            webSetting("");
            return;
        }

        mLoading = true;
        AgreementCallBack callBack = null;
        if (action == ACTION_MANUAL) {
            callBack = new AgreementCallBack() {
                @Override
                public void onLoadUrl(String url) {
                    if (!TextUtils.isEmpty(url) && !url.equalsIgnoreCase("null")) {
                        PreferenceManager.getDefaultSharedPreferences(PrivacyPolicyActivity.this).edit().putString(AGREEMENT_URL, url).apply();
                    }
                    runOnUiThread(new TemplateRunnable<String>(url) {
                        @Override
                        protected void doRun(String url) {
                            if (isFinishing()) {
                                return;
                            }
                            webSetting(url);
                        }
                    });
                }
            };
        }

        doLoadUrl(callBack);
    }

    public interface AgreementCallBack {
        void onLoadUrl(String url);
    }

    private void doLoadUrl(final AgreementCallBack callback) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    String url = NetworkUtils.loadAgreementUrl(PrivacyPolicyActivity.this, "privacy");
                    if (url != null) {
                        PreferenceManager.getDefaultSharedPreferences(PrivacyPolicyActivity.this).edit().putLong(KEY_LAST_ACTION_LOAD_URL_TIMES, System.currentTimeMillis()).apply();
                    }
                    if (callback != null) {
                        callback.onLoadUrl(url);
                    }
                } catch (Exception e) {
                    if (callback != null) {
                        callback.onLoadUrl("");
                    }
                } finally {
                    mLoading = false;
                }
            }
        }, "loadAgreementUrl").start();
    }

    private void initWebViewProvider() {
        int uid = android.os.Process.myUid();
        if (!(uid == 0 || uid == android.os.Process.SYSTEM_UID)) {
            return;
        }
        try {
            if (Build.VERSION.SDK_INT < 23 && Build.VERSION.SDK_INT >= 21) {
                Class<?> webViewFactoryClass = Class.forName("android.webkit.WebViewFactory");
                Method loadNativeLibrary = webViewFactoryClass.getDeclaredMethod("loadNativeLibrary");
                loadNativeLibrary.setAccessible(true);
                loadNativeLibrary.invoke(null);
                loadNativeLibrary.setAccessible(false);

                Method getProviderClassMethod = webViewFactoryClass.getDeclaredMethod("getFactoryClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> webViewFactoryProviderClass = (Class<?>) getProviderClassMethod.invoke(null);
                getProviderClassMethod.setAccessible(false);

                Class<?> webViewDelegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> webViewDelegateConstructor = webViewDelegateClass.getDeclaredConstructor();
                webViewDelegateConstructor.setAccessible(true);
                Object webViewDelegate = webViewDelegateConstructor.newInstance();
                webViewDelegateConstructor.setAccessible(false);

                Object webViewFactoryProvider = webViewFactoryProviderClass.getConstructor(webViewDelegateClass).newInstance(webViewDelegate);

                Field providerInstanceField = webViewFactoryClass.getDeclaredField("sProviderInstance");
                providerInstanceField.setAccessible(true);
                providerInstanceField.set(null, webViewFactoryProvider);
                providerInstanceField.setAccessible(false);

            } else if (Build.VERSION.SDK_INT >= 23) {
                Class<?> webViewFactoryClass = Class.forName("android.webkit.WebViewFactory");
                Method getProviderClassMethod = webViewFactoryClass.getDeclaredMethod("getProviderClass");
                getProviderClassMethod.setAccessible(true);
                Class<?> webViewFactoryProviderClass = (Class<?>) getProviderClassMethod.invoke(null);
                getProviderClassMethod.setAccessible(false);

                Class<?> webViewDelegateClass = Class.forName("android.webkit.WebViewDelegate");
                Constructor<?> webViewDelegateConstructor = webViewDelegateClass.getDeclaredConstructor();
                webViewDelegateConstructor.setAccessible(true);
                Object webViewDelegate = webViewDelegateConstructor.newInstance();
                webViewDelegateConstructor.setAccessible(false);

                Object webViewFactoryProvider = webViewFactoryProviderClass.getConstructor(webViewDelegateClass).newInstance(webViewDelegate);

                Field providerInstanceField = webViewFactoryClass.getDeclaredField("sProviderInstance");
                providerInstanceField.setAccessible(true);
                providerInstanceField.set(null, webViewFactoryProvider);
                providerInstanceField.setAccessible(false);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }
}
